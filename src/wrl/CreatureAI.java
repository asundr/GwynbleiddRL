package wrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class should be extended to define the behavior for different types of {@linkplain Creature}s.
 * It determines the Creature's actions on updates and serves as a Creature's memory.
 * @author Arun Sundaram
 *
 */
public class CreatureAI {
	protected Creature creature;
	protected Point destination;
	protected  Consumer<Creature> deathDrop;
	
	private Map<String, String> itemNames;
	
	/** @param creature - the {@linkplain Creature} that this controls. */
	public CreatureAI (Creature creature) {
		this.creature = creature;
	    itemNames = new HashMap<String, String>();
		creature.setCreatureAI(this);
	}
	
	/** Returns the identified name of the {@linkplain Item}, otherwise its description. */
	public String getName(Item item) {
		String name = itemNames.get(item.name());
		return name == null ? item.appearance() : name;
	}
	
	/** Sets the name of an identified {@linkplain Item}. */
	public void setName(Item item, String name) {
		itemNames.put(item.name(), name);
	}
	
	/** Determines what action the {@linkplain Creature} will perform when deciding to move to a {@linkplain Tile}. */
	public void onEnter(Point p, Tile tile) {
		if (tile.isGround()) {
			creature.walk(p);
		} else {
			creature.stay();
		}
	}
	
	/** Randomly chooses to move or stay, avoids moving into {@linkplain Creature}s of same type. */
	public void wander() {
		int mx = (int) (Math.random() * 3) - 1;
		int my = (int) (Math.random() * 3) - 1;
		Creature other = creature.creature(new Point(creature.x()+mx, creature.y() + my, creature.z()));
		if (other != null && other.glyph() == creature.glyph()) {
			creature.stay();
		} else {
			creature.moveBy(mx,my,0);
		}
	}
	
	/** Determines what to do when the {@linkplain Creature} updates. */
	public void onUpdate() { }
	
	/** Determines what occurs when this is notified. */
	public void onNotify(String message) { }
	
	/** Determines what the creature does once it levels up.
	 * @see LevelUpController */
	public void onGainLevel() { 
		new LevelUpController().autoLevelUp(creature);
	}
	
	/** Behavior to be performed when the {@linkplain Creature} dies. */
	public void die() { }
	
	public void deathDrop() { if (deathDrop != null) deathDrop.accept(creature); }
	
	public void setDeathDrop( Consumer<Creature> deathDrop)  {
		this.deathDrop = deathDrop;
	}
	
	/** Return true if location {@code p} can be seen unobstructed within the vision radius. */
	public boolean canSee(Point p) {
		if (creature.z() != p.z)
			return false;
		if ((creature.x()-p.x)*(creature.x()-p.x) + (creature.y()-p.y)*(creature.y()-p.y) > creature.visionRadius()*creature.visionRadius()) 
			return false;
		
		for (Point point : new Line(creature.x(), creature.y(), p.x, p.y).getPoints()) {
			if (creature.realTile( new Point(point.x, point.y, p.z) ).isGround() || (point.x == p.x && point.y == p.y) )
				continue;
			return false;
		}
		return true;
	}
	
	/** Returns the {@linkplain Tile} at {@code p} from {@linkplain FieldOfView} memory. */
	public Tile rememberedTile(Point p) {
		return Tile.UNKNOWN;
	}
	
	/** Returns the radial distance from this creature. */
	protected double distanceTo(Point other) {
		return other.subtract(creature.location()).magnitudeXY();
	}
	
	/** Return true if {@code this} has a ranged weapon equipped and can see its {@code target}.  */
	protected boolean canRangedWeaponAttack(Creature target) {
		return creature.rangedWeapon() != null && creature.canSee(target.location());
	}
	
	/** Return true if the {@code target} can be seen and {@code this} has an {@linkplain Item} to throw. */
	protected boolean canThrowAt(Creature target) {
		return creature.canSee(target.location()) && getWeaponToThrow() != null;
	}

	/** Returns and {@linkplain Item} with the highest thrown attack value that can't be equipped. */
	protected Item getWeaponToThrow() {
		Item toThrow = null;
		for (Item item : creature.inventory().items()) {
			if (item == null || item == creature.meleeWeapon() || item == creature.rangedWeapon() || item == creature.armor())
				continue;
			
			if (toThrow == null || item.thrownAttackValue() > item.attackValue())
				toThrow = item;
		}
		return toThrow;
	}

	/** Return {@code true} if standing above an {@linkplain Item} with an {@linkplain Inventory} that isn't full. */
	protected boolean canPickup() {
		return creature.item(creature.location()) != null && !creature.inventory().isFull();
	}
	
	/** Move towards a {@code target} to attack. */
	public void hunt (Point target) {
		List<Point> points = new Path(creature, target.x, target.y).points();
		if (points == null || points.isEmpty()) {
			if (target.equals(destination))
					destination = null;
			wander();
			return;
		}
		int mx = points.get(0).x - creature.x();
		int my = points.get(0).y - creature.y();
		
		creature.moveBy(mx, my, 0);
		
		if (creature.location().equals(target))
			destination = null;
		else
			destination = new Point(target);
			
	}
	
	/** Returns {@code true} if {@linkplain Creature} owns better equipment than is currently equipped. */
	protected boolean canUseBetterEquipment() {
		int currentMeleeRating = creature.meleeWeapon() == null ? 0 : creature.meleeWeapon().meleeRating();
		int currentRangedRating = creature.rangedWeapon() == null ? 0 : creature.rangedWeapon().rangedRating();
		int currentArmorRating = creature.armor() == null ? 0 : creature.armor().armorRating();
		
		for (Item item : creature.inventory().items()) {
			if (item == null)
				continue;
			if (item.meleeRating() > currentMeleeRating || item.rangedRating() > currentRangedRating || item.isArmor() && item.armorRating() > currentArmorRating)
				return true;
		}
		
		return false;
	}
	
	/** Search through {@linkplain Inventory} to equip the best owned {@linkplain Item}s. */
	protected void useBetterEquipment() {
		int currentMeleeRating = creature.meleeWeapon() == null ? 0 : creature.meleeWeapon().meleeRating();
		int currentRangedRating = creature.rangedWeapon() == null ? 0 : creature.rangedWeapon().rangedRating();
		int currentArmorRating = creature.armor() == null ? 0 : creature.armor().armorRating();
		
		for (Item item : creature.inventory().items()) {
			if (item == null)
				continue;
			if (item.meleeRating() > currentMeleeRating) {
				creature.equip(item);
 				currentMeleeRating = item.meleeRating();
			} else if ( item.rangedRating() > currentRangedRating) {
				creature.equip(item);
				currentRangedRating = item.rangedRating();
			} else if (item.isArmor() && item.armorRating() > currentArmorRating) {
				creature.equip(item);
				currentArmorRating = item.armorRating();
			}
		}
	}
	
	/** Returns {@code true} if the {@linkplain Creature} can enter.
	 * @see Creature#canEnter(Point) */
	public boolean canEnter(Point p) {
		return creature.canEnter(p);
	}

}
