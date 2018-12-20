package rltut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A straight line of consecutive {@linkplain Point}s 
 *
 */
public class Line implements Iterable<Point> {
	
	private List<Point> points;
	public List<Point> getPoints(){ return points; }
	
	/**
	 * {@code depth} defaults to 0.
	 * @see #Line(int x0, int y0, int x1, int y1, int depth)
	 */
	public Line(int x0, int y0, int x1, int y1) {
		this(x0,y0,x1,y1, 0);
	}
	
	/**
	 * Creates a line of {@linkplain Point}s between two coordinates on the XY plane at {@code depth}
	 * @param x0 horizontal value of point 1
	 * @param y0 vertical value of point 1
	 * @param x1 horizontal value of point 2
	 * @param y1 vertical value of point 2
	 * @param depth of the XY plane
	 */
	public Line(int x0, int y0, int x1, int y1, int depth) {
		makeLine(x0,y0,x1,y1, depth);
	}
	
	/**
	 * Creates a line of {@linkplain Points} between two coordinates on the XY plane using 
	 * <a href="https://en.wikipedia.org/wiki/Bresenham's_line_algorithm">Bresenham's line algorithm</a href>.
	 * Works for lines in all quadrants.
	 * @param x0 horizontal value of point 1
	 * @param y0 vertical value of point 1
	 * @param x1 horizontal value of point 2
	 * @param y1 vertical value of point 2
	 * @param depth of the XY plane
	 * @see <a href="https://en.wikipedia.org/wiki/Bresenham's_line_algorithm">Bresenham's line algorithm</a href>
	 */
	private void makeLine(int x0, int y0, int x1, int y1, int depth) {
		points = new ArrayList<Point>();
		int dx = Math.abs(x0-x1);
		int dy = Math.abs(y0-y1);
		int err = dx-dy;
		
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;
		
		while (true) {
			points.add(new Point(x0, y0, depth));
			if (x0 == x1 && y0 == y1)
				break;
			
			int e2 = err * 2;
			if (e2 > -dx) {
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}
	
	/** Returns the angle made by this line starting from an origin and the unit vector {@code (1,0,0)}. */
	public double radialAngle() {
		if (points.size() < 2)
			return 0;
		
		Point p1 = points.get(points.size()-1).subtract(points.get(0));
		Point p2 = new Point(1, 0, 0);
		
		double cos = p1.scalarProductXY(p2) / (p1.magnitudeXY() * p2.magnitudeXY());
		 // = p1.x / p1.magnitude();
		double acos = Math.acos(cos) * 180 / Math.PI;
		
		return  p1.y < 0 ? 360 - acos : acos;
	}
	
	public Iterator<Point> iterator(){
		return points.iterator();
	}
	
	

}
