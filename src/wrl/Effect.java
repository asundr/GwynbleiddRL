package rltut;
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
	/** Returns {@code true} if the duration of the effect has finished. */
	public boolean isDone() { return duration < 1; }
	
	private boolean stackable;		// if true, target can have multiple effects of this type at once
	/** Returns {@code true} if multiple instances of the Effect can affect a {@linkplain Creature} */
	public boolean stackable() { return stackable; }
	/** Sets the stackable flag, indicating if multiple instances of the Effect can affect a {@linkplain Creature} */
	public void setStackable(boolean value) { stackable = value; }
	

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
	
	/** {@code caster} defaults to {@code null}, {@code stackable} defaults to {@code true}.
	 * @see #Effect(Creature, int, boolean) */
	public Effect(int duration) {
		this(null, duration);
	}
	
	/** {@code caster} defaults to {@code null}.
	 * @see #Effect(Creature, int, boolean) */
	public Effect(int duration, boolean stackable) {
		this(null, duration, stackable);
	}
	
	/** {@code stackable} defaults to {@code true}.
	 * @see #Effect(Creature, int, boolean)
	 */
	public Effect(Creature caster, int duration) {
		this(caster, duration, true);
	}
	
	/**
	 * @param caster - the {@linkplain Creature} that is the source of this Effect
	 * @param duration - The time for the effect to last.
	 * @param stackable - if {@code false}, new Effect of same type will override prior effect.
	 */
	public Effect(Creature caster, int duration, boolean stackable) {
		this.caster = caster;
		this.duration = duration;
		this.stackable = stackable;
		this.radius = 1;
	}
	
	/** Called after the effect is applied to the target. */
	public void start(Creature cTarget) {}
	
	/** Called by the target to update. By default, reduces the Effect duration by 1 each call. */
	public void update(Creature cTarget) {
		duration--;
	}
	
	/** Called at the end of the Effect's duration. */
	public void end(Creature cTarget) {}
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}

}
