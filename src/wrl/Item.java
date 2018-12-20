package wrl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the properties and behaviors of entities that can exist in the {@linkplain World} and inside {@linkplain Inventory}s.
 * Depending on an Item's properties, it can be used by {@linkplain Creature}s for various purposes including eating, wielding and throwing.
 * The class implments the {@linkplain Updatable} interface.
 * @author Arun Sundaram
 *
 */
public class Item extends Entity {
	
	/** Returns the location of this item in the world, the container it's contained in or {@code null} if neither. */
//	public Point location() { return container == null ? ( super.location() == null ? null : super.location() ) : container.location(); }
	
	private Creature container;
	/** Returns the container this Item is located in or {@code null} if not located in a container. */
	protected Creature container() { return container; }
	/** Returns {@code true} if Item is in a container. */
	public boolean isInContainer() { return container != null; }
	
	private int actionPoints;
	/** Sets the AP of this item without re-adding to the {@linkplain EventOrganizer} queue. */
	protected void setAP(int amount) { this.actionPoints = amount; }
	public int ap() { return actionPoints; }
	public void  refreshAP() { actionPoints += 100; }
	
	/** Moves this Item to the passed {@code location}. Calls {@link #onContainerChange(Creature)} with a {@code null} argument. */
	public void relocate(Point location) {
		onContainerChange(null);
		if (isInContainer())
			this.container.inventory().remove(this);
		else if (location() != null) {
			world.remove(this);
		}
		setLocation( world.addAtEmptySpace(this, location) );
		this.container = null;
	}
	
	public void relocate(World world, Point location) { relocate(location); }
	
	public void relocate(World world, Creature container) { relocate(container); }
	
	/** Moves this Item to the passed {@code container}. Calls {@link #onContainerChange(Creature)} with {@code container}. */
	public void relocate(Creature container) {
		onContainerChange(container);
		if (isInContainer()) {
			this.container.inventory().remove(this);
		} else if (location() != null) {
			world.remove(this);
		}
		setLocation(null);
		container.inventory().add(this);
		this.container = container;
	}
	
	/** Describes the behavior of this object just before it moves to a location or container. */
	public void onContainerChange(Creature newContainer) {}
	
	private int stackMax;
	/** Returns the maximum number of times this Item can be stacked into the same location. */
	public int stackMax() {return stackMax;}
	
	private int stackCount;
	/** Returns the current number of this Item in the stack. */
	public int stackCount() {return stackCount;}
	
	private int foodValue;
	/** Returns the food value. */
	public int foodValue() { return foodValue; }
	/** Modifies the food value by {@code amount}. */
	public void modifyFoodValue(int value) { foodValue += value; }
	
	private Effect quaffEffect;
	/** Returns the {@linkplain Effect} that this Item applies when quaffed. */
	public Effect quaffEffect() {return quaffEffect; }
	/** Sets the {@linkplain Effect} this Item applies when this Item is quaffed. */
	public void setQuaffEffect(Effect effect) { quaffEffect = effect; }
	
	private int attackValue;
	/** Returns the melee attack value. */
	public int attackValue() { return attackValue; }
	/** Modifies the base melee attack value by {@code amount}. */
	public void modifyAttackValue(int amount) { attackValue += amount; }
	
	private int thrownAttackValue;
	/** Returns the thrown attack value. */
	public int thrownAttackValue() { return thrownAttackValue; }
	/** Modifies the base thrown attack value by {@code amount}. */
	public void modfyThrownAttackValue(int amount) { thrownAttackValue += amount; }
	
	private int defenseValue;
	/** Returns the defense value. */
	public int defenseValue() { return defenseValue; }
	/** Modifies the base defense value by {@code amount}. */
	public void modifyDefenseValue(int amount) { defenseValue += amount; }
	
	private int rangedAttackValue;
	/** Returns the ranged attack value. */
	public int rangedAttackValue() {return rangedAttackValue; }
	/** Modifies the base ranged attack value by {@code amount}. */
	public void modifyRangedAttackValue(int amount) {rangedAttackValue += amount; }
	
	private List<Spell> writtenSpells;
	/** Returns a {@linkplain List} of the {@linkplain Spell}s castable from this Item. */
	public List<Spell> writtenSpells() { return writtenSpells; }
	/** Adds a {@linkplain Spell} to this item that can be cast. */
	public void addWrittenSpell(String name, int manaCost, Spell.Delivery delivery, Effect effect) {
		addWrittenSpell(name, manaCost, 1, 360, delivery, effect);
	}
	public void addWrittenSpell(String name, int manaCost, double radius, int degree, Spell.Delivery delivery, Effect effect) {
		writtenSpells.add(new Spell(name, manaCost, radius, degree, delivery, effect));
	}
	
	public void addWrittenSpell(String name, int manaCost, Spell.Delivery delivery, Hazard hazard) {
		addWrittenSpell(name, manaCost, 1, 360, delivery, hazard);
	}
	public void addWrittenSpell(String name, int manaCost, double radius, int degree, Spell.Delivery delivery, Hazard hazard) {
		writtenSpells.add(new Spell(name, manaCost, radius, degree, delivery, hazard));
	}
	public void addWrittenSpell(Spell spell) {
		writtenSpells.add(spell);
	}
	
	private String appearance;
	/** Returns the appearance of this Item. */
	public String appearance() { return appearance == null ? name() : appearance; }
	
	/** {@code appearance} defaults to {@code null}.
	 * @see #Item(String, char, Color, String) */
	public Item(World world, String name, char glyph, Color color) {
		this(world, name, glyph, color, null);
	}
	
	/**
	 * @param world - the World this Item inhabits
	 * @param name - The name of this Item.
	 * @param glyph - the {@code char} symbol for display
	 * @param color - the {@linkplain Color} of the glyph
	 * @param appearance - The description of this Item.
	 */
	public Item(World world, String name, char glyph, Color color, String appearance) {
		super(world, name, glyph, color, null);
		this.writtenSpells = new ArrayList<Spell>();
		this.appearance = appearance;
		this.thrownAttackValue = 1;
	}
	
	public void update() {}
	
	public void modifyAP(int amount) {
		if (amount != 0) {
			world.cancelUpdate(this);
			actionPoints += amount;
			world.scheduleUpdate(this);
		}
	}
	
	public boolean updatePending() {
		return actionPoints != 0;
	}

	/** Sets the {@linkplain Creature} who threw this Item. Used to identify the caster this Item's {@linkplain Effect}s. */
	public void setThrower(Creature thrower) {
		if (isPotion())
			quaffEffect.setCaster(thrower);
	}
	
	/** Returns description of Item. */
	public String details() {
		String details = "";
		
		if (attackValue != 0)
		    details += "     attack:" + attackValue;
		
		if (rangedAttackValue != 0)
		    details += "     ranged:" + rangedAttackValue;
		
		if (defenseValue != 0)
		    details += "     defense:" + defenseValue;
		
		if (foodValue != 0)
		    details += "     food:" + foodValue;
		
		return details;
	}
	
	/** Rates melee quality of Item. */
	public int meleeRating() {
		return attackValue();
	}
	
	/** Rates ranged quality of Item. */
	public int rangedRating() {
		return rangedAttackValue();
	}
	
	/** Rates armor quality of Item. */
	public int armorRating() {
		return defenseValue();
	}
	
	/** Returns {@code true} if this is armor */
	public boolean isArmor() {
		return meleeRating() + rangedRating() < armorRating();
	}
	
	/** Returns {@code true} if this is a melee weapon */
	public boolean isMelee() {
		return meleeRating() > rangedRating() && meleeRating() > armorRating();
	}
	
	/** Returns {@code true} if the this is a ranged weapon. */
	public boolean isRanged() {
		return rangedRating() > meleeRating() && rangedRating() > armorRating();
	}
	
	/** Returns {@code true} if the Item is a potion. */
	public boolean isPotion() {
		return quaffEffect != null;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if ( !(other instanceof Item) )
			return false;
		Item o = (Item) other;
		return this.name.equals(o.name);
	}
	
}
