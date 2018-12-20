package wrl;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

/**
 * This class represents an {@linkplain World} entity whose behavior is defined by a {@linkplain CreatureAI}.
 * Creatures have a location in the World and and attributes like health and mana that can be modified by other entities and the environment.
 * The class implements {@linkplain Updatable} and {@linkplain ObserverFOV}.
 * @author Arun Sundaram
 *
 */
public class Creature extends Entity implements ObserverFOV {
	
	private CreatureAI ai;
	/** Sets the {@linkplain CreatureAI} for this creature. */
	public void setCreatureAI (CreatureAI ai) { this.ai = ai; }
	/** Returns the ai controlling this creature. */
	public CreatureAI ai() { return ai; }

	private int maxHP;
	/** Returns the maximum amount of health. */
	public int maxHP() {  return maxHP;	}
	/** Modifies the maximum health, reducing the current health to the new maximum health if necessary. */
	public void modifyMaxHP(int amount, String causeOfDeath, Creature aggressor) {
		this.maxHP += amount;
		if (maxHP < hp)
			modifyHP(amount, causeOfDeath, aggressor);
	}
	
	private int hp;
	/** Returns the current number of health points. */
	public int hp() {  return hp;  }
	
	private int regenHpCooldown;
	private int regenHpPer1000;
	/** Modifies the rate of health regeneration by {@code amount} / 1000 updates. */
	public void modifyRegenHpPer1000(int amount) { regenHpPer1000 += amount; }
	
	private int maxMana;
	/** Returns the maximum number of mana points. */
	public int maxMana() { return maxMana; }
	
	private int mana;
	/** Returns the current number of mana points. */
	public int mana() { return mana; }
	/** Modifies the current number of mana points by {@code amount} clamped between {@code [0, maxMana]. */
	public void modifyMana(int amount) { mana = Math.max(0, Math.min(mana += amount, maxMana)); }
	
    private int regenManaCooldown;
    private int regenManaPer1000;
    /** Modifies the rate of mana regeneration by {@code amount} / 1000 update. */
    public void modifyRegenManaPer1000(int amount) { regenManaPer1000 += amount; }
    
    
    private int maxToxicity;
    /** Returns the maximum toxicity. */
    public int maxToxicity() { return Math.max(0,maxToxicity); }
    /** Modifies the maximum amount of toxicity this can withstand. */
    public void modifyMaxToxicity(int amount) {
    	this.maxToxicity += amount;
    }
    
    private int toxicity;
    /** Returns the current toxicity level. */
    public int toxicity() { return toxicity; }
    /** Modifies the toxicity by the amount.  */
    public void modifyToxicity(int amount) {
    	toxicity += amount;
    	toxicity = Math.max(0, toxicity);
    }
    
    private int signIntensity;
    /** Returns the current sign intensity. */
    public int signIntensity() { return signIntensity; }
    /** Modifies the sign intensity by {@code amount}. */
    public void modifySignIntensity(int amount) { signIntensity += amount; System.out.println("WARNING: not implemented"); }
    
    private int actionPoints;
    public int ap() { return actionPoints; }
    /** Modifies AP by {@code amount} inversely modified by speed. If amount is non-zero the creature is re-added to the {@linkplain EventOrganizer} queue. */
    public void modifyAP(int amount) { 
    	if (amount != 0 ) {
    		world.cancelUpdate(this);
    		amount = (int) ( amount * (100.00 / Math.max(10, speed)) );
    		actionPoints += amount;
    		world.scheduleUpdate(this);
    	}
    }
    public void refreshAP() {
    	actionPoints += 100;
		regenerateHealth();
		regenerateMana();
//		modifyFood(-1);
    }
    
    private Queue<Action> actions;
    /** Adds a scheduled action. */
    public void addAction(Action action) { this.actions.add(action); }
    /** Clears the scheduled actions. */
    public void clearActions() { actions.clear(); }
    /** Returns true if this creatures has an action scheduled. */
    public boolean actionPending() { return !actions.isEmpty(); }
    
	
	private Effect ward;
	/** Sets an effects at a protective ward on this creature. */
	public void setWard(Effect ward) { this.ward = ward;}
	/** Removes the protective ward from this Creature. */
	public void removeWard() { ward = null; }
    
    private int poisonResistance;
    /** Returns the potion resistance of this Creature. */
    public int poisonResistance() {
    	return Math.min(100, poisonResistance);
    }
    /** Modifies the Creature's poison resistance by {@code amount}. */
    public void modifyPoisonResistance(int amount) { this.poisonResistance += amount; }
    
    // create a ModAV map
    public int meleeCost 	= -100;
    public int rangedCost 	= -100;
    public int throwCost 	= -100;
    public int spellCost 	= -100;
    public int equipCost 	= -100;
    public int walkCost		= -100;
    public int pickupCost	= -100;
    public int dropCost		= -100;
    public int eatCost		= -100;
    public int quaffCost	= -100;
    public int digCost		= -100;
    public int waitCost		= -100;
    
    private int speed = 100;
    /** Returns the speed of this creature. */
    public  int speed() { return speed; }
    /** Modifies the speed of the creature.  */
    public void modifySpeed(int amount) { speed += amount; }
    
	private List<Effect> effects;
	/** Returns a {@linkplain List} of the {@linkplain Effect}s currently affecting this creature. */
	public List<Effect> effects() { return effects; }
	public void removeEffects(Predicate<Effect> predicate) {
		for (int i=0; i<effects.size(); i++) {
			if ( predicate.test(effects.get(i)) ){
				effects.remove(i--).end(this);
			}
		}
	}
	
	private int food;
	/** Return the current found count. */
	public int food() { return food; }
	
	private int maxFood;
	/** Return maximum food capacity. */
	public int maxFood() { return maxFood; }
	
	private int attackValue;
	/** Returns the attack value of this creature and its equipment. */
	public int attackValue() {  
		return attackValue
			+ (meleeWeapon == null ? 0 : meleeWeapon.attackValue())
			+ (armor == null ? 0 : armor.attackValue());  
	}
	/** Modifies the attack value of this creature by {@code amount}. */
	public void modifyAttackValue(int amount) { attackValue += amount; }
	
	private int defenseValue;
	/** Returns the defense value of this creature and its equipment. */
	public int defenseValue() {  
		return defenseValue
				+ (meleeWeapon == null ? 0 : meleeWeapon.attackValue())
				+ (armor == null ? 0 : armor.attackValue());
	}
	/** Modifies the defense of this creature by {@code amount}. */
	public void modifyDefenseValue(int amount) { defenseValue += amount; }
	
	private int level;
	/** Return the current creature level. */
	public int level() { return level; }
	
	private int xp;
	/** Returns the current experience. */
	public int xp() { return xp; }
	/** Modifies experience by {@code amount}, potentially leveling up creature. 
	 * @see CreatureAI#onGainLevel() */
	public void modifyXP(int amount) { 
		this.xp += amount; 
		notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);
		
		while (xp > (int) (Math.pow(level, 1.5) * 20)) {
			level++;
			doAction("advance to level %d", level);
			ai.onGainLevel();
			modifyHP(level * 2);
		}
	}
	
	private int visionRadius;
	/** Returns the range at which the creature can see. */
	public int visionRadius() { return visionRadius;  }
	/** Modifies the range at which this creature can see. */
	public void modifyVisionRadius(int amount) { visionRadius = Math.max(0, visionRadius += amount); } ;
	
	private int detectCreatures;
	/** Modifies the degree to which this creature can detect other creatures. */
	public void modifyDetectCreatures(int amount) { detectCreatures += amount; }
	
	private String causeOfDeath;
	/** Returns the name of the last method of damage. */
	public String causeOfDeath() { return causeOfDeath; }
	
	private Inventory inventory;
	/** Returns the creature's {@linkplain Inventory}. */
	public Inventory inventory() { return inventory; }
	
	private Item knownSpells;
	/** Sets the spells inside of a spellbook Item to this Creature's known spells. */
	public void setKnownSpells(Item spellbook) {
		if (spellbook.writtenSpells() != null)
			this.knownSpells = spellbook;
		else
			throw new IllegalArgumentException("The spellbook passed doesn't contain any spells");
	}
	/** Returns the known spells of this Creture as a spellbook item. */
	public Item knownSpells() {
		return knownSpells;
	}
	
	private Item meleeWeapon;
	/** Returns the currently wielded melee weapon {@linkplain Item}. */
	public Item meleeWeapon() {return meleeWeapon; }
	
	private Item rangedWeapon;
	/** Returns the currently wielded ranged weapon {@linkplain Item}. */
	public Item rangedWeapon() {return rangedWeapon; }
	
	private Item armor;
	/** Returns the currently worn armor {@linkplain Item}. */
	public Item armor() { return armor; }
	
	/**
	 * @param world - the {@linkplain World} this creature inhabits
	 * @param name - name of this creature
	 * @param glyph - {@code char} symbol used to display this creature
	 * @param color - the {@linkplain Color} of the glyph
	 * @param maxHP - initial maximum amount of health
	 * @param attack - base damage points
	 * @param defense - base defense points
	 */
	public Creature(World world, String name, char glyph, Color color, int maxHP, int attack, int defense){
		super(world, name, glyph, color, null);
		setLocation(new Point(0, 0, -1));
		effects = new ArrayList<Effect>();
		actions = new ArrayDeque<Action>();
		
	    this.maxHP = maxHP;
	    this.hp = maxHP;
	    this.maxMana = 100; 		// changeme
	    this.mana = maxMana;
	    this.maxToxicity = 100;
	    this.maxFood = 10000000;
	    this.food = maxFood * 2 / 3;
	    this.attackValue = attack;
	    this.defenseValue = defense;
	    this.regenHpPer1000 = 10;
	    this.regenManaPer1000 = 10;
	    this.level = 1;
	    this.inventory = new Inventory(20);
	    this.visionRadius = 9;
	    
	    this.actionPoints = 0;
	    this.speed = 100;
	}
	
	public void update() {
		updateEffects(); // move to refreshAP ?
		if (toxicity > maxToxicity)
			modifyHP(-5, "toxicity", this);
		if (isDead())
			return;
		int ap = ap();
		boolean bUpdate = true;
		while (bUpdate && actionPending()) {
			Action action = actions.remove();
			action.perform(this);
			if (ap != ap()) {
				bUpdate = false;
				break;
			}
		}
//		regenerateHealth();  // move to refreshAP ?
//		regenerateMana();  // move to refreshAP ?
//		modifyFood(-1);  // move to refreshAP ?
//		if (isDead())
//			return;
		if (bUpdate && ap() > 0)
			ai.onUpdate();
	}
	
	public boolean updatePending() { return !isDead(); }
	
	public FieldOfView updateFOV() {
		return null;
	}
	
	/** Counts number of updates, replenishing a point of health after enough have passed. */
	private void regenerateHealth() {
		regenHpCooldown -= regenHpPer1000;
		if (regenHpCooldown < 0){
			modifyHP(1);
//			modifyFood(-1);
			regenHpCooldown += 1000;
		}
	}
	
	/** Counts number of updates, replenishing a point of mana after enough have passed. */
	private void regenerateMana() {
		int amount = regenManaPer1000 / 1000;
		if (regenManaPer1000 > 1000) {
			regenManaCooldown -= regenManaPer1000 - 1000 * amount;;
		} else {
			regenManaCooldown -= regenManaPer1000;
		}
		if(regenManaCooldown < 0) {
			amount += 1;
//			modifyFood(-1);
			regenManaCooldown += 1000;
		}
		modifyMana(amount);
	}
	
	/** Calls {@linkplain Effect#update(Creature)} on all active effects, removing those that have completed. */
	public void updateEffects() {
		List<Effect> done = new ArrayList<Effect>();
//		for (Effect effect : effects){  //TODO check this
//			effect.applyUpdate(this);
//			if (effect.isDone()) {
//				effect.end(this);
//				done.add(effect);
//			}
//		}
//		effects.removeAll(done);
		for (int i=1; effects.size()-i >=0; i++) {
			Effect effect = effects.get(effects.size()-i);
			effect.applyUpdate(this);
			if(effect.isDone())
				done.add(effect);
		}
		for(Effect e : done)
			e.end(this);
		effects.removeAll(done);
	}
	
	/** Returns the {@linkplain CreatureAI}'s identification of the {@linkplain Item}.*/
	public String nameOf(Item item) {
		return ai.getName(item);
	}
	
	/** Teaches the {@linkplain CreatureAI} the correct {@linkplain Item} name. */
	public void learnName(Item item) {
		notify("The " + item.appearance() + " is a " + item.name() + "!");
		ai.setName(item, item.name());
	}
	
	/** Dig away a {@linkplain World} {@linkplain Tile}. Incurs AP cost. 
	 * @see World#dig(Point)*/
	public void dig(int wx, int wy) {
		modifyFood(-10);
		world.dig( new Point(wx, wy, z()));
		doAction("dig");
		modifyAP(digCost);
	}
	
	/** Attack with melee. Incurs AP cost. */
	public void meleeAttack(Creature other){
		modifyAP(meleeCost);
		commonAttack(other, attackValue(), "attack the '%s' for %d damage", other.name());
	}
	
	/** Attack by throwing an {@linkplain Item}, quaff effects are applied. Incurs AP cost. */
	public void throwAttack(Item item, Creature other) {
		modifyAP(throwCost);
		item.setThrower(this);
		commonAttack(other, attackValue/2 + item.thrownAttackValue(), "throw a %s at the %s for %d damage", nameOf(item), other.name());
		other.addEffect(item.quaffEffect());
	}
	
	/** Attack with currently equipped range weapon. Incurs AP cost. */
	public void rangedAttack(Creature other) {
		modifyAP(rangedCost);
		commonAttack(other, attackValue/2 + rangedWeapon.rangedAttackValue(), "fire a %s at the %s for %d damage", nameOf(rangedWeapon), other.name());
	}
	
	/**
	 * Generic attack to deal damage. 
	 * @param other - target creature
	 * @param attack - attacking power
	 * @param action - unformatted action text
	 * @param params - parameters for action text
	 */
	public void commonAttack(Creature other, int attack, String action, Object ... params) {
		modifyFood(-1);
		
		int amount = Math.max(0, attack - other.defenseValue());
		amount = (int) (Math.random() * amount) + 1;
		
		Object[] params2 = new Object[params.length +1];
		for (int i=0; i<params.length; i++) params2[i] = params[i];
		params2[params2.length -1] = amount;
		
		doAction(action, params2);
		
		other.modifyHP(-amount, Grammar.article(name()) + " " + name(), this);
	}
	
	/** Unequips the {@linkplain Item} and removes it from the {@linkplain Inventory}. */
	private void getRidOf(Item item) {
		inventory.remove(item);
		unequip(item);
	}
	
	/** Calculate and gain experience from a creature. */
	public void gainXP(Creature other) {
		int amount = other.attackValue + other.defenseValue - level * 2;
		if (amount > 0)
			modifyXP(amount);
	}
	
	/** Modify food by {@code amount}. If creature eats more than max its max food increases at the expense of health. If food is less than one, the creature dies of starvation. */
	public void modifyFood(int amount) {
		//TODO disabled for balance
		if (true) return;
		
		food += amount;
		if (food > maxFood) {
			maxFood += amount/2;
			food = maxFood;
			modifyHP(-1, "overeating", this);
			notify("Your gluttony expands your stomach but you feel less healthly.");
		} else if (food < 1 && isPlayer()) {
			food = 0;
			modifyHP(-hp, "starvation", this);
		}
	}
	
	/** Modifies incoming damage before it is applied */
	private int preDamage(int amount, String causeOfDeath, Creature aggressor) {
		if (causeOfDeath != null && causeOfDeath.equals("poison")) {
			if (poisonResistance() == 100)
				return 0;
			else
				amount = (int) (amount * ((100 - poisonResistance()) / 100.0));
		}
		if (amount == 0)
			return 0;
		
		if (ward != null) {
			notify("The ward shatters!");
			effects.remove(ward); 
			removeWard();
			return 0;
		}
		return amount;
	}
	
	/**{@code aggressor} and {@code causeOfDeath} default to {@code null}.
	 * @see #modifyHP(int, String, Creature)*/
	public void modifyHP(int amount) {
		modifyHP(amount, null, null);
	}
	
	/**{@code aggressor} defaults to {@code null}.
	 * @see #modifyHP(int, String, Creature)*/
	public void modifyHP(int amount, String causeOfDeath) {
		modifyHP(amount, causeOfDeath, null);
	}

	/**
	 * If health is positive, modify health by {@code amount}.
	 * If new health is less than one, this creature dies and the aggressor gains experience.
	 * @param amount - number of points to modify health by
	 * @param causeOfDeath - how this creature died.
	 * @param aggressor - a creature that inflicted causeOfDeath
	 */
	public void modifyHP(int amount, String causeOfDeath, Creature aggressor) {
		if (hp < 1)
			return;
		
		if (amount == 0)
			return;
		
		amount = preDamage(amount, causeOfDeath, aggressor);
		if (amount == 0)
			return;
		
		onHit(amount, causeOfDeath, aggressor);
		
		hp += amount;
		this.causeOfDeath = causeOfDeath == null ? "unknown" : causeOfDeath;
		if (hp > maxHP)
			hp = maxHP;
		if (hp<1) {
			die();
			if (aggressor != null)
				aggressor.gainXP(this);
		}
	}
	
	/** Calculates and a applies a knockback effect from a sources {@linkplain Point}. */
	public void knockBack(Point source, double distance) {
		if (isDead())
			return;
		Point dif = location().subtract(source);
		Line line = new Line(x(), y(), x() + 2*dif.x, y() + 2*dif.y, z());
		
		Point newLoc = null;
		for (Point p : line) {
			if (p.equals(location()))
				continue;
			if (distance < 0 || !world.tile(p).isGround() || world.creature(p) != null)
				break;
			newLoc = p;
			distance--;
		}
		
		if (newLoc != null)
			relocate(newLoc);
	}
	
	/** Applies a stun effect to this Creature. {@code amount} modifies action points negatively. */
	public void stun(int amount) {
		doAction("stun");
		modifyAP(-amount);
	}
	
	/** Calls behavior of Effects that respond to detrimental hit events. */
	protected void onHit(int amount, String causeOfDeath, Creature aggressor) {
		for (Effect e : effects)
			e.onHit(amount, causeOfDeath, aggressor, this);
	}
	
	/** Creature is killed, leaving a corpse and removing the Creature from the {@linkplain World}. */
	public void die() {
		ai.die();
		ai.deathDrop();
		if ( !(ai instanceof PlantAI) )
			StuffFactory.newBlood(world, location());
		leaveCorpse();
		doAction("die");
		world.remove(this);
		actionPoints = 0;   // prevents event organizer warning for no ap change
	}
	
	/** Drops a corpse {@linkplain Item} at the current location along with this creature's {@linkplain Inventory} as Items. */
	private void leaveCorpse() {
		if ( !(ai instanceof PlantAI) ) {
			Item corpse = new ItemDespawnable(world, name + " corpse", '%', color, 1000);
			corpse.modifyFoodValue(maxHP * 3);
			corpse.relocate(world, location());
		}
		for (Item item : inventory.items()) {
			if (item != null) {
				drop(item);
			}
		}
	}
	
	/** Determines action for movement. If there is no movement, the creature stays. 
	 * If the new location contains a creature, the creature is attacked.  
	 * The {@linkplain CreatureAI} determines other movement cases.
	 * @param mx - amount to move in x
	 * @param my - amount to move in y
	 * @param mz - amount to move in z
	 * @see CreatureAI#onEnter(Point, Tile)
	 */
	public void moveBy(int mx, int my, int mz) {
		if (mx == 0 && my == 0 && mz == 0) {
			stay();
			return;
		}
		
		Point next = location().add(0, 0, mz);
		Tile tile = world.tile(location().add(mx, my, mz));
		
		if (mz == -1){
			if (tile == Tile.STAIRS_DOWN) {
				doAction("walk up the stairs to level %d", next.z);
				ai.onEnter(next, world.tile(next));
			}
			else
				doAction("try to go up but are stopped by the cave ceiling");
		} else if (mz == 1){
			if (tile == Tile.STAIRS_UP) {
				doAction("walk down the stairs to level %d", next.z);
				ai.onEnter(next, world.tile(next));
			}
			else
				doAction("try to go down but are stopped by the floor");
		} 
		else {
			next = location().add(mx, my, 0);
			Creature other = world.creature(next);
			if (other == null) {
				ai.onEnter(next, world.tile(next));
			} else {
				meleeAttack(other);
				return;
			}
		}
	}
	
	/** Walks the creature to the new location, incurring AP cost. */
	public void walk(Point location) {
		modifyAP(walkCost);
		relocate(location);
	}
	
	/** Move this creature to the new location {@code p} and updates the {@linkplain World}'s creature location. */
	public void relocate(Point p) { 
		world.updateCreatureLocation(this, p);
		setLocation(p);
	}
	
	/** Wait a turn. Incurs AP cost. */
	public void stay() {
		modifyAP(waitCost);
	}
	
	/** Attempts to pick up an {@linkplain Item} at the current location. Incurs AP cost. */
	public void pickup() {
		Item item = world.item(location());
		if (item == null)
			doAction("grab at the ground");
		else if (inventory.isFull())
			doAction(String.format("try to pick up the %s but inventory is full", nameOf(item)));
		else {
			doAction(String.format("pickup a %s", nameOf(item)));
			item.relocate(world, this);
		}
		modifyAP(pickupCost);
	}
	
	/** Drop a an {@linkplain Item} at current location. Incurs AP cost. */
	public void drop(Item item) {
		doAction("drop a %s", nameOf(item));
		unequip(item);
		item.relocate(world, location());
		modifyAP(dropCost);
	}
	
	/** Eat an {@linkplain Item}. Incurs AP cost. */
	public void eat(Item item) {
		doAction("eat a %s", nameOf(item));
		consume(item);
		modifyAP(eatCost);
	}
	
	/** Finds the food {@linkplain Item} with the lowest food value and eat it. */
	public void autoEat() {
		int index = -1;
		Item[] inventory = inventory().items();
		for (int i=inventory.length-1; i>=0 ; i--) {
			if (inventory[i] != null && inventory[i].foodValue() > 0) {
				if (index == -1 || inventory[i].foodValue() < inventory[index].foodValue())
					index = i;
			}
		}
		if (index == -1) {
			notify("No food to eat");
		} else {
			eat(inventory[index]);
		}
	}
	
	/** Quaff a potion {@linkplain Item}. Incurs AP cost. */
	public void quaff(Item item) {
		modifyAP(quaffCost);
		doAction("quaff a %s", nameOf(item));
		consume(item);
	}
	
	/** Applies effects and food value to creature, the {@linkplain Item} is then removed. */
	public void consume(Item item) {
		if (item.foodValue() < 0)
			notify("Gross!");
		modifyHP(item.foodValue()/20);
		addEffect(item.quaffEffect());
		modifyFood(item.foodValue());
		inventory.remove(item);
		getRidOf(item);
	}
	
	/** Adds and starts the {@linkplain Effect}. */
	public void addEffect(Effect effect) {
		if (effect == null)
			return;
		if (resists(effect))
			return;
		if (!effect.stackable()) {
			for (int i=0; i<this.effects.size(); i++) {
				Effect curr = this.effects.get(i);
				if (curr.equals(effect)) {
					if (curr.duration() < effect.duration()) {
						curr.end(this);
						effects.remove(i--);
					} else
						return;
				}
			}
		}
		effects.add(effect);
		effect.start(this);
	}
	
	/** Returns {@code true} if this Creature is immune to this {@linkplain Effect}. */
	public boolean resists(Effect effect) {
		return poisonResistance() == 100 &&  effect.isPoison();
	}
	
	/** Unequips the {@linkplain Item}. */
	public void unequip(Item item) {
		if (item == null)
			return;
		if (item == armor) {
			if (!isDead()) doAction("take off the %s", nameOf(armor));
			armor = null;
		} else if (item == meleeWeapon) {
			if (!isDead()) doAction("put away the %s", nameOf(meleeWeapon));
			meleeWeapon = null;
		} else if (item == rangedWeapon) {
			if (!isDead()) doAction("put away the %s", nameOf(rangedWeapon));
			rangedWeapon = null;
		}
	}
	
	/** Attempts to equip an {@linkplain Item}. Will automatically unequip an item already in the same slot. If trying to equip world item when inventory is full, Item drops to the ground. */
	public void equip(Item item) {
		if (!inventory.contains(item)) {
			if (inventory.isFull()) {
				notify("Can't equip %s since you're holding too much stuff.", nameOf(item));
				return;
			} else {
				item.relocate(world, this);
			}
		}
		
		if (item.attackValue() == 0 && item.defenseValue() == 0 && item.rangedAttackValue() == 0) 
			return;
		
		if (item.isRanged()) { //item.rangedAttackValue() >= item.attackValue() && ran) {
			unequip(rangedWeapon);
			rangedWeapon = item;
			doAction("weild a %s", nameOf(item));
		} else if(item.isMelee()) { //item.attackValue() >= item.defenseValue()) {
			unequip(meleeWeapon);
			meleeWeapon = item;
			doAction("weild a %s", nameOf(item));
		} else {
			unequip(armor);
			armor = item;
			doAction("put on a %s", nameOf(item));
		}
	}
	
	/** Throws an {@linkplain Item} towards the location potentially attacking a creature. Will drop Item or break it if it's fragile.*/
	public void throwItem(Item item, Point target) {
		Point end = location();
		for (Point point : new Line(x(), y(), target.x, target.y, target.z)) {
			if (!tile(point).isGround())
				break;
			end = point;
			if (creature(point)!=null && creature(point)!=this)
				break;
		}
		
		Creature targetCreature = creature(end);
		if (targetCreature != null) {
			throwAttack(item, targetCreature);
		} else {
			doAction("throw %s %s", Grammar.article(nameOf(item)), nameOf(item));
		}
		
		unequip(item);
		if (item.isPotion()) {
			inventory.remove(item);
			if (targetCreature == null)
				notify("The " + nameOf(item)+ " shatters on impact with the ground.");
		} else {
			item.relocate(world, end);
		}
	}
	
	/** Defaults {@code splash} to a 1x1 grid of {@code true}.
	 * @see #castSpell(Spell, int, int, Splash) */
	public void castSpell(Spell spell, int x2, int y2) {
		castSpell(spell, x2, y2, new Splash());
	}
	
	/**
	 * Casts spell at target.
	 * @param spell - spell to be cast
	 * @param x2 - target's x position
	 * @param y2 = target's y position
	 * @param splash - grid of splash damage centered at {@code (x2, y2)}.
	 */
	public void castSpell(Spell spell, int x2, int y2, Splash splash) {
		if (spell.manaCost() > mana) {
			doAction("attempt to cast %s but don't have enough mana", spell.name());
			return;
		}
		spell.apply(new Point(x2, y2, z()), splash, world, this);
		modifyMana(-spell.manaCost());
		modifyAP(spellCost);	
	}
	
	/**
	 * Distributes a notification from creature to itself and those who can see it.
	 * @param message - text to display
	 * @param params - parameters to format into message
	 */
	public void doAction(String message, Object ... params) {
		for (Creature other : creaturesWhoSeeMe()) {
			if (other == this)
				notify("You " + message + ".", params);
			else
				other.notify(String.format("The %s %s.", name, Grammar.makeThirddPerson(message)), params);
		}
	}
	
	/**
	 * Distributes a notification from creature to itself and those who can see it.
	 * The item passed will be identified if its name is unknown.
	 * @param item - Item with learnable name
	 * @param message - text to display
	 * @param params - parameters to format into message
	 */
	public void doAction(Item item, String message, Object ... params){
		if (hp < 1)
			return;
		//TODO resolve with overload. Checks if dead because you can't learn effect from dead.
		
		for (Creature other : creaturesWhoSeeMe()) {
			if (other == this)
				notify("You " + message + ".", params);
			else
				other.notify(String.format("The %s %s.", name, Grammar.makeThirddPerson(message)), params);
			if (item != null)
				other.learnName(item);
		}
	}
	
	// TODO rewrite this... maybe use for (Creature c : floor.creatures) if (c.canSee(this.location())) others.add(c);
	/** Returns a list of Creatures  around this creature in a circle of radius 9 */
	public List<Creature> creaturesWhoSeeMe() {
		List<Creature> others = new ArrayList<Creature>();
		int r = 9;
		for (int ox = -r ; ox <= r; ox++) {
			for (int oy = -r; oy <= r; oy++) {
				if (ox*ox + oy*oy > r*r)
					continue;
				
				Creature other = world.creature(location().add(ox, oy, 0));
				if (other == null)
					continue;
				others.add(other);
			}
		}
		return others;
	}
	
	// Level up option methods
	public void gaimMaxHP() {
		maxHP += 10;
		hp += 10;
		doAction("look healthier");
	}
	
	public void gainMaxMana() {
		maxMana += 5;
		mana += 5;
		doAction("look more magical");
	}
	
	public void gainRegenMana() {
		regenManaPer1000 += 1000;
		doAction("you look more energetic");
	}
	
	public void gainAttackValue() {
		attackValue += 1;
		doAction("look stronger");
	}
	
	public void gainDefenseValue() {
		defenseValue += 1;
		doAction("look tougher");
	}
	
	public void gainVision() {
		visionRadius += 1;
	    doAction("look more aware");
	}
	
	/** Returns the {@linkplain World} {@linkplain Tile} */
	public Tile realTile(Point p) {
		return world.tile(p);
	}
	
	/** Returns the World {@linkplain Tile} at this location if it is visible, otherwise {@linkplain CreatureAI} returns the remembered Tile. */
	public Tile tile(Point p) {
		if (canSee(p)) {
			return world.tile(p);
		} else {
			return ai.rememberedTile(p);
		}
	}
	
	/** Returns a Creature at this location if it is visible to this creature */
	public Creature creature(Point p) {
		if (canSee(p) || canDetect(p))
			return world.creature(p);
		else
			return null;
	}
	
	/** Returns an {@linkplain Item} at this location if it is visible to this creature */
	public Item item(Point p) {
		if (canSee(p))
			return world.item(p);
		else
			return null;
	}
	
	/** Returns {@code true} if creature is the player character */
	public boolean isPlayer() {
		return glyph == '@';
	}
	
	/** Returns {@code true} if creature is currently dead. */
	public boolean isDead() {
		return hp < 1;
	}
	
	/** @see CreatureAI#canSee(Point) */
	public boolean canSee(Point p) {
		return ai.canSee(p);
	}
	
	/** Returns {@code true} if creature can detect an entity at this location. */
	public boolean canDetect(Point p) {
//		return detectCreatures > 0 && world.creature(p) != null;
		return ai.distanceTo(p) < detectCreatures && world.creature(p) != null && !(world.creature(p).ai() instanceof PlantAI);
	}
	
	/** Returns {@code true} if could enter this location. */
	public boolean canEnter(Point p) {
		return world.tile(p).isGround() && world.creature(p) == null;
	}

	/** Returns {@code true} if the {@linkplain Item} is equipped by this creature. */
	public boolean isEquipped(Item item) {
		return meleeWeapon == item || rangedWeapon == item ||  armor == item;
	}
	
	/** 
	 * Formats a message as a String that is passed onto {@linkplain CreatureAI#notify(String)} 
	 * @param message - text to display
	 * @param params - parameters to format into message
	 */
	public void notify(String message, Object ... params) {
		ai.onNotify(String.format(message, params));
	}
	
	public Color background() {
		return (ward == null) ? background : Color.yellow;
	}
	
	/** Returns a description of this creature. */
	public String details() {
		return String.format("     level:%d     attack:%d     defense:%d     hp:%d", level, attackValue(), defenseValue(), hp);
	}
	
	public String toString() {
		return name();
	}
	
}
