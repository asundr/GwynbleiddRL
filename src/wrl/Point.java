package rltut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A point in 3-dimensional space.
 * @author Arun Sundaram
 *
 */
public class Point {
	
	public int x;
	public int y;
	public int z;
	
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
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
	
	/** Returns a new Point equal to subtracting the components of {@code p} from {@code this}. */
	public Point subtract(Point p) {
		return new Point(x - p.x, y - p.y, z - p.z);
	}
	
	/** Returns a new Point equals to adding the components of {@code p} to {@code this}.*/
	public Point add(Point p) {
		return new Point(x + p.x, y + p.y, z + p.z);
	}
	
	/** Returns the magnitude of a line from the origin to this point in the XY plane. */
	public double magnitudeXY() {
		return Math.sqrt(x*x + y*y);
	}
	
	/** Returns the dot product of lines from {@code (0,0)}  to {@code this} and {@code p} in the XY plane.  */
	public int scalarProductXY(Point p) {
		return x*p.x + y*p.y;
	}
	
}
