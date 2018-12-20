package wrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A point in 3-dimensional space.
 * @author Arun Sundaram
 *
 */
public class Point {
	
	public final int x;
	public final int y;
	public final int z;
	
	/** Constructs a point at the origin. */
	public Point() {
		this(0, 0, 0);
	}
	
	/**
	 * Constructs a point at the passed coordinates.
	 * @param x - horizontal distance
	 * @param y - vertical distance
	 * @param z - depth
	 */
	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Constructs a duplicate point from {@code p}.
	 * @param p - an existing Point
	 */
	public Point(Point p) {
		this(p.x, p.y, p.z);
	}
	
	private static final Map<Integer, Point> neighborMap;
    static {
        Map<Integer, Point> map = new HashMap<Integer, Point>(); 
        map.put(0, new Point(1, 0, 0));
        map.put(1, new Point(1, 1, 0));
        map.put(2, new Point(0, 1, 0));
        map.put(3, new Point(-1, 1, 0));
        map.put(4, new Point(-1, 0, 0));
        map.put(5, new Point(-1, -1, 0));
        map.put(6,  new Point(0, -1, 0));
        map.put(7, new Point(1, -1, 0));
        neighborMap = Collections.unmodifiableMap(map);
    }
    
    /** Returns the neighboring point corresponding to the passed octant modulo 8. 
     * @see <a href"https://en.wikipedia.org/wiki/Octant_(plane_geometry)">Wikipedia - Octant</a href>
     */
    public Point neighbor(int octant) {
    	return this.add( neighborMap.get((octant+8)%8) );
    }
    
    /** Returns the neighboring point corresponding to the passed degree. */
    public Point neighbor(double degree) {
    	return neighbor( (int) Math.round(degree/45.0) );
    }
	
	/**
	 * Returns eight neighboring Points in random order
	 * @return an {@linkplain ArrayList} of eight Points.
	 */
	public List<Point> neighbors8(){
		List<Point> neighbors = new ArrayList<Point>();
		for (int i=-1; i<2; i++) {
			for (int j=-1; j<2; j++) {
				if (i==0 && j==0)
					continue;
				neighbors.add(new Point(x+i, y+j, z));
			}
		}
		Collections.shuffle(neighbors);
		return neighbors;
	}
	
	/** Returns a new Point equal to subtracting the components of {@code p} from {@code this}. */
	public Point subtract(Point p) {
		return new Point(x - p.x, y - p.y, z - p.z);
	}
	
	/** @see #subtract(Point) */
	public Point subtract(int dx, int dy, int dz) {
		return subtract(new Point(dx, dy, dz));
	}
	
	/** Returns a new Point equals to adding the components of {@code p} to {@code this}.*/
	public Point add(Point p) {
		return new Point(x + p.x, y + p.y, z + p.z);
	}
	
	/** @see #add(Point) */
	public Point add(int dx, int dy, int dz) {
		return add(new Point (dx , dy, dz));
	}
	
	/** Returns the magnitude of a line from the origin to this point in the XY plane. */
	public double magnitudeXY() {
		return Math.sqrt(x*x + y*y);
	}
	
	/** Returns the dot product of lines from {@code (0,0)}  to {@code this} and {@code p} in the XY plane.  */
	public int scalarProductXY(Point p) {
		return x*p.x + y*p.y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Point))
			return false;
		Point other = (Point) obj;
		return x == other.x && y == other.y && z == other.z;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
}
