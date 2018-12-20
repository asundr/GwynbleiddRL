package rltut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates the {@linkplain Tile}s of the {@linkplain World}.
 *
 */
public class WorldBuilder {
	
	private int width;
	private int height;
	private int depth;
	private Tile[][][] tiles;
	private int[][][] regions;
	private int nextRegion = 1;
	
	public WorldBuilder(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.tiles = new Tile[width][height][depth];
	}
	
	/** Returns the generated world. */
	public World build() {
		return new World(tiles);
	}
	
	/** Generates the caves for the {@linkplain World}. */
	public WorldBuilder makeCaves() {
		return randomizeTiles()
				.smooth(8)
				.createRegions()
				.connectRegions()
				.addExitStairs().water();
	}
	
	/** Generates a {@linkplain Tile}[][][] with Tiles that randomly alternate between {@linkplain Tile.FLOOR} and {@linkplain Tile.WALL}. */
	private WorldBuilder randomizeTiles() {
		for (int z = 0; z < depth; z++) {
			//System.out.println("constructing floor " + z);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++)
					tiles[x][y][z] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
			}
		}
		return this;
	}
	
	// TODO build with random chance of .46 for floor vs wall
	/** Generates water, replacing floor tiles. */
	private WorldBuilder water() {
		for (int z=0; z<depth; z++) {
			for (int x=0; x < width; x++) {
				for (int y=0; y<height; y++) {
					if (tiles[x][y][z] == Tile.FLOOR && tiles[x][y][(z+5)%depth].isGround())
						tiles[x][y][z] = Tile.WATER;
				}
			}
		}
		return this;
	}
	
	/** Creates smooth areas of walls and floor from randomly placed {@linkplain Tile}s. */
	private WorldBuilder smooth(int times) {
		Tile[][][] tiles2 = new Tile[width][height][depth];  // TODO move this into loops?
		for (int time = 0; time < times; time++) {
			
			for (int z = 0; z < depth; z++) {
				//System.out.println("smoothing floor " + z);
				for (int x = 0; x<width ; x++) {
					for (int y=0; y<height; y++) {
						int floors = 0;
						int rocks = 0;
						
						for (int ox = -1; ox < 2; ox++) {
							for (int oy = -1; oy < 2; oy++) {
								if (x + ox < 0 || x + ox >= width || y + oy < 0
				                        || y + oy >= height)
									continue;
								else if (tiles[x + ox][y + oy][z] == Tile.FLOOR)
									floors++;
								else
									rocks++;
							}
						}
						tiles2[x][y][z] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
					}
				}
			}
			tiles = tiles2;
		}
		return this;
	}
	
	/** Uniquely labels continuous regions of {@linkplain Tile.FLOOR}. */
	public WorldBuilder createRegions() {
//		System.out.println("create regions");
		regions = new int[width][height][depth];
		
		for (int z = 0; z < depth; z++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (tiles[x][y][z] != Tile.FLOOR && regions[x][y][z] == 0) {
						int size = fillRegion(nextRegion++, x, y, z);
						
						if (size < 25)
							removeRegion(nextRegion - 1, z);
					}
				}
			}
		}
		return this;
	}
	
	/** Removes region label and converts it to {@linkplain Tile.WALL} */
	public void removeRegion(int region, int depth) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (regions[x][y][depth] == region) {
					regions[x][y][depth] = 0;
					tiles[x][y][depth] = Tile.WALL;
				}
			}
		}
	}
	
	/** Labels all continuous {@linkplain Tile.FLOOR} with {@code region} and returns the size of the region. */
	public int fillRegion(int region, int x, int y, int z) {
		int size = 1;
		ArrayList<Point> open = new ArrayList<Point>();
		open.add(new Point(x,y,z));
		regions[x][y][z] = region;
		
		while(!open.isEmpty()) {
			Point p = open.remove(0);
			for(Point n : p.neighbors8()) {
				if (n.x < 0 || n.x >= width || n.y < 0 || n.y >= height)
					continue;
				if (tiles[n.x][n.y][n.z] == Tile.WALL || regions[n.x][n.y][n.z] > 0)
					continue;
				regions[n.x][n.y][n.z] = region;
				open.add(n);
				size++;
			}
		}
		return size;
	}
	
	/** Links together of each floor of the {@linkplain World}. */
	public WorldBuilder connectRegions() {
		for (int z=0; z<depth-1; z++) {
			connectRegionsDown(z);
		}
		return this;
	}
	
	/** Connects regions in level {@code z} to the regions they overlap in level {@code z+1} */
	private void connectRegionsDown(int z) {
		List<String> connected = new ArrayList<String>();
		for (int x=0; x < width; x++) {
			for (int y=0; y<height; y++) {
				String region = regions[x][y][z] + "," + regions[x][y][z+1];
				if (tiles[x][y][z] == Tile.FLOOR && tiles[x][y][z+1] == Tile.FLOOR && !connected.contains(region)) {
					connected.add(region);
					connectRegionsDown(z,  regions[x][y][z],  regions[x][y][z+1]);
				}
			}
		}
	}
	
	/** Connects levels {@code z} and {@code z+1} by adding stairs that connect regions {@code r1} and {@code r2}. */
	private void connectRegionsDown(int z, int r1, int r2) {
		List<Point> candidates = findRegionOverlaps(z, r1, r2);
		int stairs = 0;
		do {
			Point p = candidates.remove(0);
			tiles[p.x][p.y][p.z] = Tile.STAIRS_DOWN;
			tiles[p.x][p.y][p.z+1] = Tile.STAIRS_UP;
			stairs++;			
		} while (candidates.size() / stairs > 250);
	}
	
	/** Creates a shuffled {@linkplain List} of {@linkplain Point}s at depth {@code z} that overlap in regions {@code r2} and {@code r2}.  */
	public List<Point> findRegionOverlaps(int z, int r1, int r2){
		ArrayList<Point> candidates = new ArrayList<Point>();
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				if (tiles[x][y][z] == Tile.FLOOR && tiles[x][y][z+1] == Tile.FLOOR
						&& regions[x][y][z] == r1 && regions[x][y][z+1] == r2){
					candidates.add(new Point(x,y,z));
				}
			}
		}
		Collections.shuffle(candidates);
		return candidates;
	}
	
	/** Adds a set of stairs on the top level to exit the dungeon. */
	public WorldBuilder addExitStairs() {
		int x = -1, y = -1;
		do {
			x = (int) (Math.random() * width);
			y = (int) (Math.random() * height);
		} while (tiles[x][y][0] != Tile.FLOOR);
		
		tiles[x][y][0] = Tile.STAIRS_UP;
		return this;
	}
	

}
