package wrl;

import java.awt.Color;

/**
 * This class should be extended by objects that inhabit the {@linkplain World}. It handles the location of the child classes.
 * @author Arun Sundaram
 *
 */
public abstract class Entity implements Updatable  {
	
	/* @param world - the {@linkplain World} this Entity inhabits
	 * @param name - name of this creature
	 * @param glyph - {@code char} symbol used to display this Entity
	 * @param color - the {@linkplain Color} of the glyph
	 * @param background - {@linkplain Color} of background
	 */
	protected Entity(World world, String name, char glyph, Color color, Color background) {
		this.world = world;
		this.name = name;
		this.glyph = glyph;
		this.color = color;
		this.background = background;
	}
	
	/** The world this Entity inhabits. */
	protected World world;
	/** Returns the {@linkplain World} this entity exists in. */
	public World world() { return world; }
	
	protected String name;
	/** Return name of this hazard. */
	public String name() { return name; }
	
	protected char glyph;
	/** Return {@code char} symbol for display. */
	public char glyph() { return glyph; }
	
	protected Color color;
	/** Return {@linkplain Color} of the glyph. */
	public Color color() { return color; }
	/** Sets the color of the glyph. */
	public void setColor(Color color) { this.color = color; }
	
	protected Color background;
	/** Return {@linkplain Color} of the glyph background. */
	public Color background() { return background; }
	/** Sets the color of the glyph background. */
	public void setBackground(Color background) { this.background = background; }
	
	protected boolean visible = true;
	/** Returns {@code true} if this Entity is directly visible. */
	public boolean visible() { return visible; }
	/** Sets the visible state of this Entity. */
	public void setVisible(boolean visible) { this.visible = visible; }
	
	/** The location of this Entity on the {@code XY}-plane at depth {@code z} */
	private Point location;
	
	/** Sets the location to {@linkplain Point} {@code p}. */
	protected void setLocation(Point p) { this.location = p == null ? null : new Point(p); }
	
	/** Sets the vertical position to {@code x}. */
	protected void setX(int x) { this.location = new Point(x, location.y, location.z); }
	
	/** Sets the horizontal position to {@code y}. */
	protected void setY(int y) { this.location = new Point(location.x, y, location.z); }
	
	/** Sets the depth to {@code z}. */
	protected void setZ(int z) { this.location = new Point(location.x, location.y, z); }
	
	/** The location of this Entity on the {@code XY}-plane */
	public Point locationXY() {
		return new Point(location.x, location.y, 0);
	}
	
	/** Returns the location of the Entity as a {@linkplain Point}. */
	public Point location() {
		return (location == null) ? null : new Point(location);
	}
	
	/** Returns the horizontal location of this Entity. */
	public int x() { return location.x; }
	
	/** Returns the vertical location of this Entity. */
	public int y() { return location.y; }
	
	/** Returns the depth of this Entity. */
	public int z() { return location.z; }

	/** Move this Entity to the new location {@code p}. */
	abstract public void relocate(Point p);
	
	/** {@code depth} defaults to {@code this.depth}
	 * @param wx - world x
	 * @param wy - world y
	 * @see World#isInBounds(Point)
	 */
	public boolean isInBounds(int wx, int wy) {
		return isInBounds(new Point(wx, wy, z()));
	}
	
	/** @see World#isInBounds(Point) */
	public boolean isInBounds(Point p) {
		return world.isInBounds(p);
	}
	
}
