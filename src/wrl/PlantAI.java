package wrl;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a passive plant creature that can spread itself.
 * @author Arun Sundaram
 *
 */
public class PlantAI extends CreatureAI {
	
	protected StuffFactory factory;
	protected PlantAI parent;
	protected List<PlantAI> children;
	
	protected int maxCreatures = 8;
	protected double spawnChance = 0.02;
	
	/** Constructs an AI for a parent plant. */
	public PlantAI(Creature creature, StuffFactory factory, int maxCreatures, double spawnChance) {
		this(creature, factory, null);
		this.maxCreatures = maxCreatures;
		this.spawnChance = spawnChance;
	}
	
	/** Constructs an ai for a child plant. */
	public PlantAI(Creature creature, StuffFactory factory, PlantAI parent) {
		super(creature);
		this.factory = factory;
		this.parent = parent;
		if (parent == null)
			this.children = new ArrayList<PlantAI>();
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
	
	/** Creates a {@linkplain Creature} from {@code this} to be added to the world. */
	protected Creature makeChild() {
		Creature child = new Creature(creature.world, creature.name(), creature.glyph(), creature.color(), creature.maxHP(), creature.attackValue(), creature.defenseValue());
		new PlantAI(child, factory, this) {};
		child.ai().setDeathDrop(deathDrop);
		return child;
	}
	
	
	/** Attempts to create a child plant. Checks {@link #canEnter(Point)} for valid spawn location. */
	protected void spread() {
		if (children.size() >= maxCreatures || Math.random() > spawnChance)
			return;
		Point p  = new Point(
			creature.x() + (int)(Math.random()*5) - 3,
			creature.y() + (int)(Math.random()*5) - 3,
			creature.z() );
		
//		p = creature.location().add((int)(Math.random()*5) - 3, (int)(Math.random()*5) - 3, 0);
		
		if (!canEnter(p))
			return;
		
		factory.newPlantChild(this, p);
		creature.doAction("spawn child");
	}
	
	public void die() {
		if (parent != null) {
			parent.children.remove(this);
		} else if (children.size() > 0) {
			PlantAI sacrifice = children.remove(0);
			Creature nextCreature = sacrifice.creature;
			
			sacrifice.creature = creature;
			creature = nextCreature;
			
			creature.setCreatureAI(this);
			sacrifice.creature.setCreatureAI(sacrifice);
		}
	}
	
}
