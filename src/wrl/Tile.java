package wrl;

import java.awt.Color;
import asciiPanel.AsciiPanel;

/**
 * This enumerator contains the Tiles that are used to construct, display and interact with the world.
 * @author Arun Sundaram
 *
 */
public enum Tile {
	FLOOR((char)250, AsciiPanel.yellow, "A dirt and rock cave floor."),
	WALL((char)177, AsciiPanel.yellow, "A dirt and rock cave wall."),
	BOUNDS('x', AsciiPanel.brightBlack, "Beyond the edge of the world."),
	STAIRS_DOWN('>', AsciiPanel.white, "A stone staircase that goes down."),
	STAIRS_UP('<', AsciiPanel.white, "A stone staircase that goes up."),
	WATER((char)250, AsciiPanel.blue.brighter(), "A floor covered in shallow water."),
//	DEEP_WATER((char)58, AsciiPanel.blue, "Waist deep water."),
	UNKNOWN(' ', AsciiPanel.white, "(unknown)");
	
	private char glyph;
	/** Returns the glyph labeling this tile as a {@code char}. */
	public char glyph() { return glyph; }
	
	private Color color;
	/** Returns the {@linkplain Color} of this tile's glyph. */
	public Color color() { return color; }
	
	private String details;
	/** Returns a description of this tile. */
	public String details() { return details; }
	
	/**
	 * 
	 * @param glyph - a symbol to display this tile
	 * @param color - the color of the glyph
	 * @param details - a description of this tile
	 */
	Tile (char glyph, Color color, String details){
		this.glyph = glyph;
		this.color = color;
		this.details = details;
	}
	
	/** Returns true if the tile can be dug. */
	public boolean isDiggable() {
		return this == WALL;
	}
	
	/** Returns true if the tile is in bound and not an obstruciton. */
	public boolean isGround() {
		return this != WALL && this != BOUNDS;
	}
	
	// higher percent means more of c1
	/**
	 * Combines two {@linkplain Color}s. {@code percent} determines the bias with {@code 0.0} equal to {@code c1} and {@code 1.0} equal to {@code c2}.
	 * The RGB values of the blend are clamped within the {@code int} range [0, 256).
	 * @param c1 - first Color
	 * @param c2 - second Color
	 * @param percent - how much of {@code c1} is present. Range from (0,1)
	 * @return blended Color
	 */
	public static Color blend(Color c1, Color c2, double percent) {
		if (percent <= 0.0)
			return c2;
		if (percent >= 1.0)
			return c1;
		int red = (int) ((c1.getRed() - c2.getRed()) * percent);
		int green = (int)  ((c1.getGreen() - c2.getGreen()) * percent);
		int blue = (int) ((c1.getBlue() - c2.getBlue()) * percent);
		return new Color(
				Math.min(255, Math.max(0, red 	+ 	c2.getRed())), 
				Math.min(255, Math.max(0, green + 	c2.getRed())), 
				Math.min(255, Math.max(0, blue 	+ 	c2.getBlue()))
			);
	}
	
}