package wrl;

import java.awt.Color;

/**
 * This class can attach to Creatures and apply changes to their properties and behavior over time.
 * @author Arun Sundaram
 *
 */
public class Effect implements Cloneable {
	
	protected Creature caster;
	/** Sets the caster as the source of this Effect. */
	public void setCaster(Creature caster) { this.caster = caster; }
	/** Returns the caster of this Effect, or {@code null} if the caster is unknown. */
	public Creature caster() { return caster; }
	/** Returns {@code true} if the Effect knows its caster. */
	public boolean hasCaster() { return caster != null; }
	
	protected int duration;
	/** Returns the remaining time for this Effect. */
	public int duration() { return this.duration; }
	/** Returns {@code true} if the duration of the effect has finished. */
	public boolean isDone() { return duration < 1; }
	
//	private boolean stackable;		// if true, target can have multiple effects of this type at once
	
//	/** Sets the stackable flag, indicating if multiple instances of the Effect can affect a {@linkplain Creature} */
//	public void setStackable(boolean value) { stackable = value; }
	
	protected boolean bPoison = false;
	protected boolean bPotionEffect = false;
	
	private static int lastID = 0;
	/** Generates a new unique ID to be used when constructing new instances of the same Effect. */
	public static int newID() {
		return ++lastID;
	}
	
	private int id;
	/** Returns {@code true} if multiple instances of the Effect can affect a {@linkplain Creature} */
	public boolean stackable() { return id == 0; }
	/** Sets the ID of this effect. The ID should be generated using {@link #newID()} and passed to instances of the same Effect. */
	public void setID(int id) {
		this.id  = id;
	}
	
	private boolean detrimental;	// Flags a damaging effect
	/** Returns true if the effect is detrimental to its target. */
	public boolean isDetrimental() { return detrimental; }
	/** Sets the detrimental flag, indicating that this effect is damaging. */
	public void setDetrimental(boolean detrimental) { this.detrimental = detrimental; }
	
	private int radius;
	/** Returns the Effect radius. */
	public int radius() { return radius; }
	/** Sets the Effect radius. Cannot be lower than one. */
	public void setRadius(int amount) { radius = Math.max(1, amount); }
	/** Returns {@code true} if this affects more than one {@linkplain Tile}. */
	public boolean isArea() { return radius > 1; }
	
	protected Color color;
	/** Returns the Color associated with this effect. */
	public Color color() {return color;}
	/** Sets the color associated with this effect. */
	public void setColor(Color color) { this.color = color; }
	
	/** {@code caster} defaults to {@code null}, {@code stackable} defaults to {@code true}.
	 * @see #Effect(Creature, int, boolean) */
	public Effect(int duration) {
		this(null, duration);
	}
	
	/** {@code caster} defaults to {@code null}.
	 * @see #Effect(Creature, int, boolean) */
	public Effect(int duration, int id) {
		this(null, duration, id);
	}
	
	/** {@code stackable} defaults to {@code true}.
	 * @see #Effect(Creature, int, boolean)
	 */
	public Effect(Creature caster, int duration) {
		this(caster, duration, 0);
	}
	
	/**
	 * @param caster - the {@linkplain Creature} that is the source of this Effect
	 * @param duration - The time for the effect to last.
	 * @param stackable - if {@code false}, new Effect of same type will override prior effect.
	 */
	public Effect(Creature caster, int duration, int id) {
		this.caster = caster;
		this.duration = duration;
		this.id = id;
		this.radius = 1;
	}
	
	/** Called after the effect is applied to the target. */
	public void start(Creature cTarget) {}
	
	/** Called by the target to update. By default, reduces the Effect duration by 1 each call. */
	public void applyUpdate(Creature cTarget) {
		duration--;
		update(cTarget);
	}
	
	/** Defines how this Effect should update. */
	protected void update(Creature cTarget) {}
	
	/** Called at the end of the Effect's duration. */
	public void end(Creature cTarget) {}
	
	/** Called when target is hit detrimentally. */
	public void onHit(int amount, String causeOfDeath, Creature aggressor, Creature target) {}
	
	public boolean isPoison() {
		return bPoison;
	}
	
	public boolean isPotionEffect() {
		return bPotionEffect;
	}
	
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if ( !(other instanceof Effect) )
			return false;
		Effect o = (Effect) other;
//		return this.caster == o.caster && this.radius == o.radius && this.stackable == o.stackable;
		return !(this.id ==0 && o.id == 0) && this.id == o.id;
	}
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}
	

}
