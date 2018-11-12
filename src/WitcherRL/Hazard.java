package rltut;

import java.awt.Color;

/**
 * This class represents an {@linkplain World} entity that applies {@linkplain Effect}s to entities located in World {@linkplain Tile}s.
 * Hazard implements  {@linkplain Updatable} and {@linkplain ObserverFOV}.
 * @author Arun Sundaram
 *
 */
public abstract class Hazard implements Updatable, ObserverFOV {
	
	private String name;
	/** Return name of this hazard. */
	public String name() { return name; }
	
	private char glyph;
	/** Return {@code char} symbol for display. */
	public char glyph() { return glyph; }
	
	private Color color;
	/** Return {@linkplain Color} of the glyph. */
	public Color color() { return color; }
	
	private Color background;
	/** Return {@linkplain Color} of the glyph background. */
	public Color background() { return background; }
	
	private int actionPoints;
	public int ap() { return actionPoints; }
	public void refreshAP() { actionPoints += 100; }
	
	
	/**
	 * @param name - name of hazard
	 * @param glyph - {@code char} symbol used to display
	 * @param color - {@linkplain Color} of glyph
	 * @param background - {@linkplain Color} of background
	 */
	public Hazard(String name, char glyph, Color color, Color background) {
		this.name = name;
		this.glyph = glyph;
		this.color = color;
		this.background = background;
	}
	
	public FieldOfView updateFOV() { System.out.println("Observer " + name() + " has no FOV."); return null; }
	
	/** Defines action for when a {@linkplain Creature} enters the location of this Hazard */
	public void onEntered(Creature creature) {}
	
	public void update() {}
	
	public boolean updatePending() { return true; }
	
}