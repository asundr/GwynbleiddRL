package wrl;

import java.awt.Color;

/**
 * This class describes an {@linkplain Item} that will despawn after a certain amount of time in the world.
 * @author Arun Sundaram
 *
 */
public class ItemDespawnable extends Item {
	
	private int despawnAP;
	
	public ItemDespawnable(World world, String name, char glyph, Color color) {
		this(world, name, glyph, color, null, 1000);
	}
	
	public ItemDespawnable(World world, String name, char glyph, Color color, int despawnTurns) {
		this(world, name, glyph, color, null, despawnTurns);
	}
	
	public ItemDespawnable(World world, String name, char glyph, Color color, String appearance, int despawnTurns) {
		super(world, name, glyph, color, appearance);
		this.despawnAP = -100 * despawnTurns;
	}
	
	/** Schedules update if not in container or if {@code super.updatePending()} is satisfied. */
	public boolean updatePending() {
		return !isInContainer() && ap() < 0;
	}
	/** Resets AP when corpse is dropped into World. */
	public void onContainerChange(Creature newContainer) {
		if (newContainer != null) {
			world.cancelUpdate(this);
			return;
		} else {
			setAP(despawnAP);
			world.scheduleUpdate(this);					
		}
	}
	
	/** Deletes corpse if AP is positive from {@linkplain Item#refreshAP()}. */
	public void update() {
		world.remove(this);
		world.cancelUpdate(this);
	}

}
