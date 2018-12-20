package wrl;

import java.awt.Color;

/**
 * This class represents an {@linkplain World} entity that applies {@linkplain Effect}s to entities located in World {@linkplain Tile}s.
 * Hazard implements  {@linkplain Updatable} and {@linkplain ObserverFOV}.
 * @author Arun Sundaram
 *
 */
public abstract class Hazard extends Entity implements Cloneable, ObserverFOV {
	
	protected int duration;
	/** Returns the number of this turns this hazard exists for. */
	public int duration() {return this.duration; }
	
	private int actionPoints;
	public int ap() { return actionPoints; }
	public void refreshAP() { actionPoints += 100; }
	public void modifyAP(int amount) {
		if (amount != 0) {
			world.cancelUpdate(this);
			actionPoints += amount;
			world.scheduleUpdate(this);
		}
	}
	
	private Creature owner;
	/** Sets the {@linkplain Creature} that owns this Hazard of this Hazard. Returns {@code null} if no owner.  */
	public Creature owner() {
		return owner;
	}
	/** Sets the {@linkplain Creature} that owns this Hazard. Used to attribute the aggressor of damage cased by Hazard. */
	public void setOwner(Creature owner) {
		this.owner = owner;
	}
	
	/**
	 * @param world - the {@linkplain World} this hazard inhabits
	 * @param name - name of hazard
	 * @param glyph - {@code char} symbol used to display
	 * @param color - {@linkplain Color} of glyph
	 * @param background - {@linkplain Color} of background
	 * @param duraiton - the number of updates this hazard will remain
	 */
	public Hazard(World world, String name, char glyph, Color color, Color background, int duration) {
		super(world, name, glyph, color, background);
		this.duration = duration;
	}
	
	/** Moves this hazard to the {@linkplain Point} {@code p} in the {@linkplain World}. */
	public void relocate(Point p) {
		if (location() == null)
			world.add(this, p);
		else
			world.updateHazardLocation(this, p);
		setLocation(p);
	}
	
	public FieldOfView updateFOV() { System.out.println("Observer " + name() + " has no FOV."); return null; }
	
	/** Defines action for when a {@linkplain Creature} enters the location of this Hazard */
	public void onEntered(Creature creature) {}
	
	public void start() {}
	
	public void end() {}
	
	public void update() {}
	
	public boolean updatePending() { return true; }
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}
}