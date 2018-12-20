package wrl;

import java.util.function.Consumer;


//TODO remove this class

/**
 * Defines the behavior for a fungus.
 * @author Arun Sundaram
 * @deprecated
 *
 */
public class FungusAI extends PlantAI {
	
	public FungusAI(Creature creature, StuffFactory factory, int maxCreatures, double spawnChance, Consumer<Creature> makeInventory) {
		super(creature, factory, maxCreatures, spawnChance );
	}
	
	public FungusAI(Creature creature, StuffFactory factory, FungusAI parent) { 
		super(creature, factory, parent);
	}

	public boolean canEnter(Point p) {
		return super.canEnter(p) && creature.tile(p) != Tile.WATER;
	}

}
