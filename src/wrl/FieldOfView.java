package wrl;

import java.awt.Color;
/**
 * The {@code FieldOfView} class is used to describe the visibility of {@linkplain Tile}s surrounding a {@linkplain Point} in the {@linkplain World} 
 * and as a memory of previously seen but not currently visible {@code Tile}s.
 * @author Arun Sundaram
 *
 */

public class FieldOfView {
	
	private World world;
	private int depth;
	int range;

	private int[][] visibility;
	/** Returns the degree to which a given {@linkplain Point} is visible. */
	public int visibility(Point p) {
		if ( p.z == depth && world.isInBounds(p))
			return visibility[p.x][p.y];
		return 0;
	}
	
	/** Returns whether a given {@linkplain Point} is visible. */
	public boolean isVisible(Point p) {
		return visibility(p) > 0;
	}
	
	// TODO replace with Tile static method blend() 
	/**
	 * If a given {@linkplain Point} is visible it will return the {@linkplain Color} of the corresponding {@linkplain Tile}.
	 * The most visible tiles have their full color while less visible tiles are proportionally more grey.
	 * Tiles that are not visible at all will determine their color from {@linkplain World#color(Point)}.
	 * @param p - The point that wants to know its color
	 * @return color of tile adjusted for visibility
	 */
	public Color visibleColor(Point p, Creature player) {
		if (range == 0)
			return Color.DARK_GRAY.darker();
		Color cw = world.color(p, player);
		if (!isVisible(p)) {
			return cw;
		}
		Color gray = Color.DARK_GRAY.darker();
		int red = (cw.getRed() - gray.getRed()) * visibility[p.x][p.y] / range;
		int green = (cw.getGreen() - gray.getGreen()) * visibility[p.x][p.y] / range;
		int blue = (cw.getBlue() - gray.getBlue()) * visibility[p.x][p.y] / range;
		
		return new Color(
				Math.min(255, Math.max(0, red 	+ 	gray.getRed())), 
				Math.min(255, Math.max(0, green + 	gray.getRed())), 
				Math.min(255, Math.max(0, blue 	+ 	gray.getBlue())));
	}
	
	private Tile[][][] tiles;
	/** Returns the {@linkplain Tile} at the passed {@linkplain Point} from the {@code FieldOfView}'s memory. Returns {@linkplain Tile#BOUNDS} if out of bounds.*/
	public Tile tile(Point p) {
		if (!world.isInBounds(p))
			return Tile.BOUNDS;
		return tiles[p.x][p.y][p.z];
	}
	
	/** @param world - The {@linkplain World} that this {@code FieldOfView} describes */
	public FieldOfView(World world) {
		this.world = world;
		tiles = new Tile[world.width()][world.height()][world.depth()];
		for (int x=0; x<tiles.length; x++) {
			for (int y=0; y<tiles[0].length; y++) {
				for (int z=0; z<tiles[0][0].length; z++) {
					tiles[x][y][z] = Tile.UNKNOWN;
				}
			}
		}
	}
	
	public void update() {}
	
	/** Adds information from another {@code FieldOfView} by retaining the maximum visibility and updating known {@linkplain Tile}s for every {@linkplain Point}.
	 * @param other - the {@code FieldOfView} that will update  {@code this}
	 * @param z - depth */
	public void addFOV(FieldOfView other, int z) {
		if (other.visibility == null) {
			return;
		}
		// TODO restrict loop to isVisible in r*r square
		for (int x= 0; x<visibility.length; x++) {
			for (int y=0; y<visibility[0].length; y++) {
				visibility[x][y] = Math.max(visibility[x][y], other.visibility[x][y]);
				if (other.tiles[x][y][z] != Tile.UNKNOWN)
					tiles[x][y][z] = other.tiles[x][y][z];
			}
		}
	}
	
	/** Updates the {@code FieldOfView} from a {@linkplain Point} {@code location} and its radius {@code r}.
	 * Visibility values are calculated by retaining the maximum visibility of lines of sight radiating from {@code location}.
	 * The lines of sight are considered visible for a radius {@code r} unless obstructed by a non-ground tile.
	 * Previously discovered tile are remembered as they were even if they are changed out of sight. 
	 * @param location - the Point at which the visibility will be updated
	 * @param r - the radial distance from (@code location) that can be viewed
	*/
	public void update(Point location, int r) {
		if (r==0)
			return;
		depth = location.z;
		visibility = new int[world.width()][world.height()];
		range = r;
		for (int x=-r; x<=r; x++) {
			for (int y=-r; y<=r; y++) {
				if (x*x + y*y > r*r)
					continue;
				if (location.x + x < 0 || location.x + x >= world.width() || location.y + y < 0 || location.y + y >= world.height())
					continue;
				int strength = r;
				for (Point p : new Line(location.x, location.y, location.x+x, location.y+y)) {
					Tile tile = world.tile( new Point(p.x, p.y, location.z) );
					visibility[p.x][p.y] =  Math.max(visibility[p.x][p.y], (int ) (8 * Math.log(strength--)) );
					tiles[p.x][p.y][location.z] = tile;
					if (!tile.isGround())
						break;
				}
				
			}
		}
	}

}
