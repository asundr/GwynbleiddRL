package wrl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

import wrl.screens.PlayScreen;

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
	/** Adds a {@linkplain Hazard} to {@linkplain Point} {@code p} and the {@linkplain EventOrganizer}. Hazards can stack at the same location. 
	 * @see #addHazard(Hazard, Point) */
	public void add(Hazard hazard, Point p) {
		addHazard(hazard, p);
		if (hazard.updatePending())
			scheduleUpdate(hazard);
		hazard.start();
	}
	/** Adds a {@linkplain Hazard} to {@linkplain Point} {@code p}. Hazards can stack at the same location. */
	private void addHazard(Hazard hazard, Point p) {
		if (!hazards.containsKey(p))
			hazards.put(p, new ArrayList<Hazard>());
		hazards.get(p).add(hazard);
	}
	/** Removes the {@linkplain Hazard} from the World and {@linkplain EventOrganizer}.
	 * @ see {@link #removeHazard(Hazard)} */
	public void remove(Hazard hazard) {
		removeHazard(hazard);
		if (hazard.updatePending())
			cancelUpdate(hazard);
		hazard.end();
	}
	/** Removes the {@linkplain Hazard} from the World. */
	private void removeHazard(Hazard hazard) {
		Point p = hazard.location();
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
		while(!eventOrganizer.isNext(player) && !player.isDead())
			eventOrganizer.nextUpdate();
	}
	
	/** Updates the queue one Enity at a time. */
	public void singleUpdate(Creature player) {
		if (eventOrganizer.isNext(player))
			player.update();
		else
			eventOrganizer.nextUpdate();
	}
	
	
	/** Updates the {@code player}, then updates everything else until it's the {@code player}'s next turn. */
	public void update(Creature player, PlayScreen playScreen) {
		player.update();
		while(!eventOrganizer.isNext(player) && !player.isDead()) {
			eventOrganizer.updateEachOnce();
			playScreen.displayOutput();
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	/** Updates the the queue until an Entity requests a second Update. */
	public void updateEachOnce() {
		eventOrganizer.updateEachOnce();
	}
	
	/** Adds a new {@linkplain Updatable} to the event organizer. */
	public void scheduleUpdate(Updatable updatable) {
		eventOrganizer.add(updatable);
	}
	
	/** Removes the {@linkplain Updatable} from the event organizer. */
	public void cancelUpdate(Updatable updatable) {
		eventOrganizer.remove(updatable);
	}
	
	/** If a Tile is dig-able, replace with its ground Tile. */
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
		Point p;
		do {
			p = new Point(	(int)(Math.random() * width),	(int)(Math.random() * height),	depth);
		} while (!tile(p).isGround() || creature(p) != null || !creature.canEnter(p));
		addAtLocation(creature, p);
		return p;
	}
	
	/** Relocates the {@linkplain Creature} to the location {@code p}, potentially removing a coterminous creature from the World but not the event organizer. */
	public void addAtLocation(Creature creature, Point p) {
		creature.relocate(p);
//		creatures.put(p, creature);
		eventOrganizer.add(creature);
	}
	
	/** Randomly selects an empty location at the {@code depth} level and relocates the {@code item} there. */
	public Point addAtEmptyLocation(Item item, int depth) {
		Point p;
		do {
			p = new Point( 	(int)(Math.random() * width),  	(int)(Math.random() * height),	 depth);
		} while (!tile(p).isGround() || item(p) != null);
		item.relocate(this, p);
		return p;
	}

	/** Will place the {@linkplain Item} at the {@linkplain Point} closest to the {@code location}. If no location is found, item is removed from the world. */
	public Point addAtEmptySpace(Item item, Point location) {
		Point empty = getShortestPathInRange(location, 100,  p -> item(p) == null );
		if (empty == null)
			creature(location).notify("The %s vanishes forever in the mess.", creature(location).nameOf(item));
		else {
			items[empty.x][empty.y][empty.z] = item;
			Creature c = creature(empty);
			if (c != null)
				c.notify("A %s lands between your feet.", c.nameOf(item));
		}
		return empty;
	}
	
	/** Updates the creature map if when {@linkplain Creature} updates its position. */
	public void updateCreatureLocation(Creature creature, Point newLocation) {
		creatures.remove(creature.location());
		creatures.put(newLocation, creature);
	}
	
	/** Updates the hazards map if when {@linkplain Hazard} updates its position. */
	public void updateHazardLocation(Hazard hazard, Point newLocation) {
		removeHazard(hazard);
		addHazard(hazard, newLocation);
	}
	
	/** Removes the {@linkplain Item} from the world. */
	public void remove(Item item) {
		Point p = item.location();
		items[p.x][p.y][p.z] = null;
	}
	
	/** Removes the {@linkplain Creature} from the world. */
	public void remove(Creature other) {
		creatures.remove(other.location());
		cancelUpdate(other);
	}
	
	/**
	 * Searches for the closest Point that satisfies {@code validPoint} and is reached by a contiguous Path from {@code location} within {@code range}.
	 * Returns null if no valid Point is found within the range.
	 * @param location - starting point to search radially from
	 * @param range - maximum radial distance to search
	 * @param validPoint - returns true if a Point meets the passed condition
	 */
	public Point getShortestPathInRange(Point location, double range, Predicate<Point> validPoint) {
		Map<Point, Integer> distance = new HashMap<Point, Integer>();
		Queue<Point> points = new PriorityQueue<Point>( (p1, p2) -> distance.get(p1) - distance.get(p2) );
		distance.put(location, 0);
		points.add(location);
		
		while (!points.isEmpty()) {
			Point p = points.remove();
			if (validPoint.test(p))
				return p;
			for (Point neighbor : p.neighbors8()) {
				if (!tile(neighbor).isGround() || !isInBounds(neighbor) || location.subtract(neighbor).magnitudeXY() > range)
					continue;
				if (!distance.containsKey(neighbor) || distance.get(neighbor) > distance.get(p) + 1) {
					distance.put(neighbor, distance.get(p) + 1);
					points.add(neighbor);
				}
			}
		}
		return null;
	}
	
	
//	public Point getShortestPathInRange(Point location, double range, Predicate<Point> validPoint) {
//		List<Point> points = new ArrayList<Point>();
//		List<Point> checked = new ArrayList<Point>();
//		points.add(location);
//		
//		// TODO improve me
//		while(!points.isEmpty()) {
//			Point p = points.remove(0);
//			checked.add(p);
//			if (!tile(p).isGround() || location.subtract(p).magnitudeXY() > range)
//					continue;
//			if (validPoint.test(p)) {
//				return p;
//			} else {
//				for (Point neighbor : p.neighbors8())
//					if (!checked.contains(neighbor) && isInBounds(neighbor) && neighbor.subtract(location).magnitudeXY() < range)
//						points.add(neighbor);
//			}
//		}
//		return null;
//	}
	
	/**
	 * Searches radially from {@code location} to find a Point within {@code range} that satisfies {@code validPoint}.
	 * Returns null if no valid Point is found within the range.
	 * @param location - starting point to search radially from
	 * @param range - maximum radial distance to search
	 * @param validPoint - returns true if a Point meets the passed condition
	 */
	public Point getNearestInRange(Point location, double range, Predicate<Point> validPoint) {
		Set<Point> seen = new HashSet<Point>();
		
		java.util.TreeSet<KeyValuePair<Point, Double>> q = new java.util.TreeSet<KeyValuePair<Point, Double>>( (t1, t2) -> (int) Math.signum(t1.value - t2.value));
		q.add(new KeyValuePair<Point, Double>(location, 0.0d));
		while (!q.isEmpty()) {
			java.util.Iterator<KeyValuePair<Point, Double>> it = q.iterator();
			Point curr = it.next().key;
			it.remove();
			seen.add(curr);
			if (validPoint.test(curr))
				return curr;
			for (Point p : curr.neighbors8()) {
				if (seen.contains(p) || !isInBounds(p))
					continue;
				double dist = p.subtract(location).magnitudeXY();
				if (dist < range)
					q.add(new KeyValuePair<Point, Double>(p, dist));
			}
		}
		
		
//		Map<Point, Double> distance = new HashMap<Point, Double>();
//		Queue<Point> boundary = new PriorityQueue<Point>( (p1, p2) -> (int) Math.signum(distance.get(p1) - distance.get(p2)) );
//		boundary.add(location);
//		
//		while(!boundary.isEmpty()) {
//			Point curr = boundary.remove();
//			seen.add(curr);
//			if (validPoint.test(curr))
//				return curr;
//			for (Point p : curr.neighbors8()) {
//				if (seen.contains(p) || !isInBounds(p))
//					continue;
//				double dist = p.subtract(location).magnitudeXY();
//				if (dist < range) {
//					distance.put(p, dist);
//					boundary.add(p);
//				}
//			}
//		}
		return null;
	}
	
	/** Returns {@code true} if the {@linkplain Point} exists in the world. */
	public boolean isInBounds(Point p) {
		return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height && p.z >=0 && p.z < depth;
	}
	
	/** Returns the glyph at {@linkplain Point} {@code p} for displaying as a {@code char}. */
	public char glyph(Point p, Creature player) {
		Tile tile = tile(p);
		if (tile == Tile.WALL)
			return tile.glyph();
		
		Creature creature = creature(p);
		if (creature != null) {
			if (creature.visible() && player.canSee(p))
				return creature.glyph();
			else if (player.canDetect(p))
				return '"';
		}
						
		Item item = item(p);
		if (item != null)
			return item.glyph();
		
		if (hazards.containsKey(p)) {
			List<Hazard> list = hazards.get(p);
			for (int i=list.size()-1; i>=0; i--)
				if (list.get(i).glyph() != (char)0)
					return list.get(i).glyph();
		}
		return tile.glyph();
	}
	
	/** Returns the {@linkplain Color} at {@linkplain Point} {@code p} for displaying the glyph. */
	public Color color (Point p, Creature player) {
		Tile tile = tile(p);
		if (tile == Tile.WALL)
			return tile.color();
		
		Creature creature = creature(p);
		if (creature != null && ( creature.visible() || player.canDetect(p) ) )
			return creature.color();
		
		Item item = item(p);
		if (item != null)
			return tile(p)!=Tile.WATER ? item.color() : Tile.blend(tile(p).color(), item.color(), 0.5);

		if (hazards.containsKey(p)) {
			List<Hazard> list = hazards.get(p);
			for (int i=list.size()-1; i>=0; i--)
				if (list.get(i).color() != null)
					return list.get(i).color();
		}
		return tile.color();
	}
	
	/** Returns the {@linkplain Color} at {@linkplain Point} {@code p} for displaying the background. */
	public Color backgroundColor(Point p) {
		Color bg = (creature(p) == null) ? null : creature(p).background();
		if (bg != null)
			return bg;
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
