package rltut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code World} class encapsulates the information and behavior about the {@linkplain Tile}s and entities in the game world.
 * @author Arun Sundaram
 *
 */
public class World {
	private int width;
	public int width() { return width; }
	
	private int height;
	public int height() { return height; }
	
	private int depth;
	public int depth() { return depth; }
	
	private Tile[][][] tiles;
	/** Returns the {@linkplain Tile} of the passed {@linkplain Point} if {@linkplain #isInBounds(Point)} returns {@code true} otherwise returns {@code Tile.BOUNDS}. */
	public Tile tile(Point p) { return isInBounds(p) ? tiles[p.x][p.y][p.z] : Tile.BOUNDS; }
	
	private Item[][][] items;
	/** Returns the {@linkplain Item} of the passed {@linkplain Point} if {@linkplain #isInBounds(Point)} returns {@code true} otherwise returns {@code null}.
	 * Will also return {@code null} if there is no Item at the location. */
	public Item item(Point p) { return isInBounds(p) ? items[p.x][p.y][p.z] : null; }
	
	private Map<Point, List<Hazard>> hazards;
	/** Adds a {@linkplain Hazard} to {@linkplain Point} {@code p}. Hazards can stack at the same location. */
	public void add(Hazard hazard, Point p) {
		if (!hazards.containsKey(p))
			hazards.put(p, new ArrayList<Hazard>());
		hazards.get(p).add(hazard);
	}
	/** Removes the {@linkplain Hazard} from {@linkplain Point} {@code p}. */
	public void remove(Hazard hazard, Point p) {
		if (!hazards.containsKey(p))
			return;
		hazards.get(p).remove(hazard);
		if (hazards.get(p).isEmpty())
			hazards.remove(p);
	}
	
	private Map<Point, Creature> creatures;
	/** Returns the {@linkplain Creature} of the passed {@linkplain Point}. Will return {@code null} if there is no Creature at the location.  */
	public Creature creature(Point p) { return creatures.get(p); }
	
	private EventOrganizer eventOrganizer;
	
	public World (Tile[][][] tiles) {
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
		this.depth = tiles[0][0].length;
		this.items = new Item[width][height][depth];
		this.creatures = new HashMap<Point, Creature>();
		this.hazards = new HashMap<Point, List<Hazard>>();
		this.eventOrganizer = new EventOrganizer();
	}
	
	/** Updates the {@code player}, then updates everything else until it's the {@code player}'s next turn. */
	public void update(Creature player) {
		player.update();
		while(!eventOrganizer.isNext(player))
			eventOrganizer.nextUpdate(player);
	}
	
	/** Updates the item's position in the event queue. */
	public void updateQueue(Updatable updatable) {
		eventOrganizer.updateQueue(updatable);
	}
	
	/** Adds a new {@linkplain Updatable} to the event organizer. */
	public void scheduleUpdate(Updatable updatable) {
		eventOrganizer.add(updatable);
	}
	
	/** Removes the {@linkplain Updatable} from the event organizer. */
	public void removeUpdate(Updatable updatable) {
		eventOrganizer.remove(updatable);
	}
	
	/** If a Tile is diggable, replace with its ground Tile. */
	public void dig(Point p) {
		if (tile(p).isDiggable())
			tiles[p.x][p.y][p.z] = tiles[p.x][p.y][(p.z+5)%depth].isGround() ? Tile.WATER : Tile.FLOOR;
	}
	
	/** Replaces the Tile with Tile.WALL if it is in bounds and not stairs. */
	public void caveIn(Point p) {
		if (tiles[p.x][p.y][p.z] == Tile.BOUNDS || tiles[p.x][p.y][p.z] == Tile.STAIRS_DOWN || tiles[p.x][p.y][p.z] == Tile.STAIRS_UP)
			return;
		tiles[p.x][p.y][p.z] = Tile.WALL;
	}
	
	/** Randomly selects an empty location at the {@code depth} level and relocates the {@code creature} there. */
	public Point addAtEmptyLocation(Creature creature, int depth) {
		Point p = new Point(-1, -1, depth);
		do {
			p.x = (int) (Math.random() * width);
			p.y = (int) (Math.random() * height);
		} while (!tile(p).isGround() || creature(p) != null);
		addAtLocation(creature, p);
		return p;
	}
	
	/** Relocates the {@linkplain Creature} to the location {@code p}, potentially removing a coterminous creature from the World but not the event organizer. */
	public void addAtLocation(Creature creature, Point p) {
		creature.relocate(p);
		creatures.put(p, creature);
		eventOrganizer.add(creature);
	}
	
	/** Randomly selects an empty location at the {@code depth} level and relocates the {@code item} there. */
	public Point addAtEmptyLocation(Item item, int depth) {
		Point p = new Point(-1, -1, depth);
		do {
			p.x = (int) (Math.random() * width);
			p.y = (int) (Math.random() * height);
		} while (!tile(p).isGround() || item(p) != null);
		item.relocate(this, p);
		return p;
	}

	/** Will place the {@linkplain Item} at the {@linkplain Point} closest to the {@code location}. If no location is found, item is removed from the world. */
	public Point addAtEmptySpace(Item item, Point location) {
		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();
		points.add(location);
		
		// TODO improve me
		while(!points.isEmpty()) {
			Point p = points.remove(0);
			checked.add(p);
			
			if (!tile(p).isGround())
					continue;
			
			if (item(p) == null) {
				items[p.x][p.y][p.z] = item;
				Creature c = creature(p);
				if (c != null)
					c.notify("A %s lands between your feet.", c.nameOf(item));
				return p;
			} else {
				List<Point> neighbors = p.neighbors8();
				neighbors.removeAll(checked);
				points.addAll(neighbors);
			}
		}
		creature(location).notify("The %s vanishes forever in the mess.", creature(location).nameOf(item));	
		return null;
	}
	
	/** Updates the creature map if when {@linkplain Creature} updates its position. */
	public void updateCreatureLocation(Creature creature, Point newLocation) {
		creatures.remove(creature.location());
		creatures.put(newLocation, creature);
	}
	
	/** Updates the hazerd map if when {@linkplain Hazard} updates its position. */
	public void updateHazardLocation(Hazard hazard, Point newLocation) {
		// TODO implement method
		System.out.println("updateHazardLocation unimplemented");
	}
	
	/** Removes the {@linkplain Item} from the world. */
	public void remove(Item item) {
		Point p = item.location();
		items[p.x][p.y][p.z] = null;
	}
	
	/** Removes the {@linkplain Creature} from the world. */
	public void remove(Creature other) {
		creatures.remove(other.location());
		eventOrganizer.remove(other);
	}
	
	/** Returns {@code true} if the {@linkplain Point} exists in the world. */
	public boolean isInBounds(Point p) {
		return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height && p.z >=0 && p.z < depth;
	}
	
	/** Returns the glyph at {@linkplain Point} {@code p} for displaying as a {@code char}. */
	public char glyph(Point p) {
		Tile tile = tile(p);
		if (tile == Tile.WALL)
			return tile.glyph();
		Creature creature = creature(p);
		if (creature != null)
			return creature.glyph();
		
		Item item = item(p);
		if (item != null)
			return item.glyph();
		
		if (hazards.containsKey(p)) {
			List<Hazard> list = hazards.get(p);
			for (int i=list.size()-1; i>=0; i++)
				if (list.get(i).glyph() != (char)0)
					return list.get(i).glyph();
		}
		return tile.glyph();
	}
	
	/** Returns the {@linkplain Color} at {@linkplain Point} {@code p} for displaying the glyph. */
	public Color color (Point p) {
		Tile tile = tile(p);
		if (tile == Tile.WALL)
			return tile.color();
		
		Creature creature = creature(p);
		if (creature != null)
			return creature.color();
		
		Item item = item(p);
		if (item != null)
			return tile(p)!=Tile.WATER ? item.color() : Tile.blend(tile(p).color(), item.color(), 0.5);

		if (hazards.containsKey(p)) {
			List<Hazard> list = hazards.get(p);
			for (int i=list.size()-1; i>=0; i++)
				if (list.get(i).color() != null)
					return list.get(i).color();
		}
		return tile.color();
	}
	
	/** Returns the {@linkplain Color} at {@linkplain Point} {@code p} for displaying the background. */
	public Color backgroundColor(Point p) {
		if (!hazards.containsKey(p))
			return new Color(0, 0, 0, 0); // return Color.BLACK;
		int r = 0, b = 0, g = 0, count = 0;
		for (Hazard h : hazards.get(p)) {
			if (h.background() == null)
				continue;
			count++;
			r += h.background().getRed();
			g += h.background().getGreen();
			b += h.background().getBlue();
		}
		return count == 0 ? new Color(0, 0, 0, 0) : new Color(r/count, g/count, b/count);
	}
	

}	
