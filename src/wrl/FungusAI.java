package rltut;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the behavior for a fungus.
 * @author Arun Sundaram
 *
 */
public class FungusAI extends CreatureAI{
	
	private StuffFactory factory;
	FungusAI parent;
	List<FungusAI> children;
	
	private int maxCreatures = 8;
	private double spawnChance = 0.02;
	
	public FungusAI(Creature creature, StuffFactory factory, FungusAI parent) {
		super(creature);
		this.factory = factory;
		this.parent = parent;
		if (parent == null)
			this.children = new ArrayList<FungusAI>();
		else
			parent.children.add(this);
	}
	
	public void onUpdate() {
		if (parent != null) {
			creature.modifyAP(-100);
			parent.spread();
		}
		else  {
			creature.modifyAP(-100);
			spread();
		}
			
	}
	
	/** Attempts to create a child fungus. */
	private void spread() {
		if (children.size() >= maxCreatures || Math.random() > spawnChance)
			return;
		Point p  = new Point(
			creature.x() + (int)(Math.random()*5) - 1,
			creature.y() + (int)(Math.random()*5) - 1,
			creature.z() );
		
		if (!canEnter(p))
			return;
		
		factory.newFungus(this, p.x, p.y, p.z);
		creature.doAction("spawn child");
	}
	
	public void die() {
		if (parent != null) {
			parent.children.remove(this);
		} else if (children.size() > 0) {
			FungusAI child = children.get(0);
			children.remove(child);
			child.parent = null;
			child.children = children;
			for(FungusAI ai : children) {
				ai.parent = child;
			}
		}
	}
	
	public boolean canEnter(Point p) {
		return super.canEnter(p) && creature.tile(p) != Tile.WATER;
	}

}
