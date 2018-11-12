package rltut;

/**
 * This abstract class can be extended to define a bonus to a {@linkplain Creature} for leveling up.
 *
 */
public abstract class LevelUpOption {
	
	private String name;
	/** Returns the name of this option. */
	public String name() { return name;}
	
	public LevelUpOption(String name) {
		this.name = name;
	}
	
	/** Applies the option's effect to {@code creature}. */
	public abstract void invoke(Creature creature);

}
