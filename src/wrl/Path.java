package rltut;

import java.util.List;

/**
 * A continuous path a creature can take to reach a given ({@code x}, {@code y}) location it the same XY plane.
 * @author Arun Sundaram
 *
 */
public class Path {
	
	private static PathFinder pf = new PathFinder();
	
	private List<Point> points;
	public List<Point> points() { return points; }
	
	public Path(Creature creature, int x, int y) {
		points = pathFind(creature, x, y, 300);
	}
	
	/** Returns a {@linkplain List} of {@linkplain Point}s that form a continuous path from a {@linkplain Creature} to a location ({@code x}, {@code y}). 
	 * <br>Will return {@code null} if no path is found.
	 * @param creature - creature looking for a path to navigate
	 * @param x - horizontal coordinate 
	 * @param y - vertical coordinate
	 * @param tries - maximum attempts to make continuous path
	 * @see PathFinder#findPath(Creature, Point, Point, int)
	 */
	public List<Point> pathFind(Creature creature, int x, int y, int tries){
		return pf.findPath(creature, creature.location(), new Point(x, y, creature.z()), tries);
	}

}
