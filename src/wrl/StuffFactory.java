package wrl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import asciiPanel.AsciiPanel;
import wrl.screens.PlayScreen;

/**
 * This class is used to define entities that can be created and placed into the {@linkplain World} via function call.
 * @author Arun Sundaram
 *
 */
public class StuffFactory {
	
	private World world;
	private PlayScreen playScreen;
	
	private Map<String, Color> potionColors;
	private List<String> potionAppearances;
	private ArrayList<Recipe> recipes;
	
	public StuffFactory (World world, PlayScreen playScreen) {
		this.world = world;
		this.playScreen = playScreen;
		setUpPotionAppearances();
		setUpPotionRecipes();
	}
	
	
	/** Creates a new blood Hazard at passed {@linkplain Point}. */
	public static void newBlood(World world, Point point) {
		newBlood(world).relocate(point);
	}
	
	/** Returns a new blood Hazard. */
	public static Hazard newBlood(World world) {
		return new Hazard(world, "blood", (char) 0, null, AsciiPanel.red, 200) {
			public void update() {
				this.duration--;
				if (duration%50 == 0)
					this.background = background.darker();
				modifyAP(-100);
			}
			public boolean updatePending() { return duration > 0; }
		};
	}
	
	/** Creates recipes for the witcher potions using drops from plants and creatures. */
	public void setUpPotionRecipes() {
		recipes = new ArrayList<Recipe>();
		List<String> monsterIngredients = Arrays.asList("drowner tongue", "troll liver", "necrophage blood vial", "vampire fang");
		List<String> plantIngredients = Arrays.asList("sewant mushroom", "wolfsbane flower", "hornwort", "white myrtle flower", "crow's eye root", "blowball flower");
		Map<String, Supplier<Item>> potions = new HashMap<String, Supplier<Item>>();
				potions.put("White Rafford's Decoction" , () -> newPotionWhiteRafford(0) );
				potions.put("Swallow" , () -> newPotionSwallow(0) );
				potions.put("Full Moon" , () -> newPotionFullMoon(0) );
				potions.put("Tawny Owl" , () -> newPotionTawnyOwl(0) );
				potions.put("Cat" , () -> newPotionCat(0) );
				potions.put("White Honey" , () -> newPotionWhiteHoney(0) );
				potions.put("Golden Oriole" , () -> newPotionGoldenOriole(0) );
				potions.put("Thunderbolt" , () -> newPotionThunderbolt(0) );
				potions.put("Blizzard" , () -> newPotoinBlizzard(0) );
				potions.put("Black Blood" , () -> newPotionBlackBlood(0) );
				potions.put("Petri's Philter" , () -> newPotoinPetrisPhilter(0) );
		
		if (potions.size() > plantIngredients.size() * (plantIngredients.size()/2  -1  )  )
				System.out.println("WARNING: There aren't enough plants to guaruntee unique potion ingredients. n plants can support n*(n/2 -1) potions.");
		
		List<ArrayList<String>> ingredients = new ArrayList<ArrayList<String>>();
		
		Collections.shuffle(monsterIngredients);
		for (int i=0; i<potions.keySet().size(); i++) {
			ArrayList<String> ig = new ArrayList<String>();
			ig.add(monsterIngredients.get(i%monsterIngredients.size()));
			ingredients.add(ig);
		}
		Collections.shuffle(ingredients);
		
		Collections.shuffle(plantIngredients);
		int first = 0, dif = 1, plantSize = plantIngredients.size();
		for (List<String> ig : ingredients) {
			if (first == plantSize) {
				first = 0; dif++;
			}
			ig.add(plantIngredients.get(first));
			ig.add(plantIngredients.get( (first+dif)%plantSize ));
			first++;
		}
		Collections.shuffle(ingredients);
		
		int index = 0;
		for (String name : potions.keySet()) {
			recipes.add( new Recipe(name, ingredients.get(index++), potions.get(name)) );
		}
		
	}
	
	
	/** Initializes a map of color to be used by unidentified colored objects. */
	private void setUpPotionAppearances(){
		potionColors = new HashMap<String, Color>();
		potionColors.put("red potion", AsciiPanel.brightRed);
		potionColors.put("yellow potion", AsciiPanel.brightYellow);
		potionColors.put("green potion", AsciiPanel.brightGreen);
		potionColors.put("cyan potion", AsciiPanel.brightCyan);
		potionColors.put("blue potion", AsciiPanel.brightBlue);
		potionColors.put("magenta potion", AsciiPanel.brightMagenta);
		potionColors.put("dark potion", AsciiPanel.brightBlack);
		potionColors.put("grey potion", AsciiPanel.white);
		potionColors.put("light potion", AsciiPanel.brightWhite);
		
		potionAppearances = new ArrayList<String>(potionColors.keySet());
		Collections.shuffle(potionAppearances);
	}
	
	/** Creates a new player {@linkplain Creature} and places that at a random location at {@code depth}.} */
	public Creature newPlayer(MessageHistory messageHistory, FieldOfView fov, int depth) {
		Creature player = new Creature(world, "Witcher", '@', AsciiPanel.brightWhite, 100, 20, 5);
		player.setKnownSpells(new Signs(world, player));
		player.modifyRegenManaPer1000(10000);
		world.addAtEmptyLocation(player, depth);
		PlayerAI ai = new PlayerAI(player, fov, messageHistory);
		ai.setRecipes(recipes);
		return player;
	}
	
	/** Creates a new creature from an existing Plant at {@code p}. */
	public Creature newPlantChild(PlantAI parent, Point p) {
		Creature plant = parent.makeChild();
		world.addAtLocation(plant, p);
		return plant;
	}
	
	public Item newSewantMushroomItem(int depth) {
		Item item = new ItemDespawnable(world, "sewant mushroom", (char)145, AsciiPanel.green);
		item.modifyFoodValue(30);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	/** Places a fungus on an empty location at {@code depth}. */
	public Creature newSewantMushroom(int maxCreatures, double spawnChance, int depth) {
		return newSewantMushroom(maxCreatures, spawnChance, -1, -1, depth);
	}
	
	/** Places a fungus at the location {@code (wx, wy, wz)}. */
	public Creature newSewantMushroom(int maxCreatures, double spawnChance, int wx, int wy, int wz) {
		Creature fungus = new Creature(world, "sewant mushroom", (char)231, AsciiPanel.green, 10, 0, 0);
		new PlantAI(fungus, this, maxCreatures, spawnChance) {
			public boolean canEnter(Point p) {
				return super.canEnter(p) && world.tile(p) != Tile.WATER;
			}
		};
		fungus.ai().setDeathDrop( c -> newSewantMushroomItem(0).relocate(c));
		if (wx == -1 || wy == -1)
			world.addAtEmptyLocation(fungus,  wz);
		else
			world.addAtLocation(fungus, new Point(wx, wy, wz));
		return fungus;
	}
	
	/** Places a wolfsbane flower at {@code depth}. */
	public Item newWolfsbaneItem(int depth) {
		Item item = new ItemDespawnable(world, "wolfsbane flower", (char)145, AsciiPanel.blue);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a new wolfsbane plant at a random space at {@code depth}. */
	public Creature newWolfsbane(int depth) {
		Creature plant = new Creature(world, "wolfsbane", (char)244, AsciiPanel.blue, 10, 0, 0);
		new PlantAI(plant, this, 3, 0.01) {
			public boolean canEnter(Point p) { return super.canEnter(p) && world.tile(p) != Tile.WATER; }
		};
		plant.ai().setDeathDrop(c -> newWolfsbaneItem(0).relocate(c.location()));
		world.addAtEmptyLocation(plant,  depth);
		return plant;
	}
	
	/** Places a hornwort at {@code depth}. */
	public Item newHornwortItem(int depth) {
		Item item = new ItemDespawnable(world, "hornwort", (char)145, AsciiPanel.brightGreen);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a new wolfsbane plant at a random space at {@code depth}. */
	public Creature newHornwort(int depth) {
		Creature plant = new Creature(world, "hornwort patch", (char)233, AsciiPanel.green, 10, 0, 0);
		new PlantAI(plant, this, 3, 0.01) {
			public boolean canEnter(Point p) { return super.canEnter(p) && world.tile(p) == Tile.WATER; }
		};
		plant.ai().setDeathDrop(c -> newHornwortItem(0).relocate(c.location()));
		world.addAtEmptyLocation(plant,  depth);
		return plant;
	}
	
	/** Places a  white myrtle flower at {@code depth}. */
	public Item newWhiteMyrtleItem(int depth) {
		Item item = new ItemDespawnable(world, "white myrtle flower", (char)145, AsciiPanel.white);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a new white myrtle plant at a random space at {@code depth}. */
	public Creature newWhiteMyrtle(int depth) {
		Creature plant = new Creature(world, "white myrtle tree", (char)157 , AsciiPanel.white, 10, 0, 0);
		new PlantAI(plant, this, 3, 0.01) {
			public boolean canEnter(Point p) {return super.canEnter(p) && world.tile(p) != Tile.WATER; }
		};
		plant.ai().setDeathDrop(c -> newWhiteMyrtleItem(0).relocate(c.location()));
		world.addAtEmptyLocation(plant, depth);
		return plant;
	}
	
	/** Places a  white crow's eye root at {@code depth}. */
	public Item newCrowsEyeItem(int depth) {
		Item item = new ItemDespawnable(world, "crow's eye root", (char)145, Color.orange.darker());
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a new white myrtle plant at a random space at {@code depth}. */
	public Creature newCrowsEye(int depth) {
		Creature plant = new Creature(world, "crow's eye bush", (char)235 , Color.orange.darker(), 10, 0, 0);
		new PlantAI(plant, this, 3, 0.01) {
			public boolean canEnter(Point p) {return super.canEnter(p) && world.tile(p) != Tile.WATER; }
		};
		plant.ai().setDeathDrop(c -> newCrowsEyeItem(0).relocate(c.location()));
		world.addAtEmptyLocation(plant, depth);
		return plant;
	}
	
	/** Places a  white myrtle flower at {@code depth}. */
	public Item newBlowballItem(int depth) {
		Item item = new ItemDespawnable(world, "blowball flower", (char)145, AsciiPanel.yellow);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a new white myrtle plant at a random space at {@code depth}. */
	public Creature newBlowall(int depth) {
		Creature plant = new Creature(world, "blowball", (char)234 , AsciiPanel.yellow, 10, 0, 0);
		new PlantAI(plant, this, 3, 0.01) {
			public boolean canEnter(Point p) {return super.canEnter(p) && world.tile(p) != Tile.WATER; }
		};
		plant.ai().setDeathDrop(c -> newBlowballItem(0).relocate(c.location()));
		world.addAtEmptyLocation(plant, depth);
		return plant;
	}
	
	///
	
	/** Places a drowner tongue at {@code p}. */
	public Item newDrownerTongue(Point p) {
		Item item = new Item(world, "drowner tongue", (char)251, AsciiPanel.brightBlue);
		item.relocate(p);
		return item;
	}
	
	/** Places a drowner at a random space at {@code depth}. */
	public Creature newDrowner(int depth, Creature player) {
		Creature creature = new Creature(world, "drowner", 'd', AsciiPanel.brightBlue, 50, 10, 10);
		new DrownerAI(creature, player, 2.0d);
		creature.ai().setDeathDrop(c -> { if (Math.random() < 0.4) newDrownerTongue(c.location()); } );
		world.addAtEmptyLocation(creature, depth);
		return creature;
	}
	
	/** Places a drowned dead at a random space at {@code depth}. */
	public Creature newDrownedDead(int depth, Creature player) {
		Creature creature = new Creature(world, "drowned dead", 'D', AsciiPanel.blue, 100, 20, 12);
		new DrownerAI(creature, player, 3.5d);
		creature.ai().setDeathDrop(c -> { if (Math.random() < 0.8) newDrownerTongue(c.location()); } );
		world.addAtEmptyLocation(creature, depth);
		return creature;
	}
	
	/** Places a troll liver at {@code p}. */
	public Item newTrollLiver(Point p) {
		Item item = new Item(world, "troll liver", (char)229, AsciiPanel.red);
		item.relocate(p);
		return item;
	}
	
	/** Places a rock troll at a random space at {@code depth}. */
	public Creature newRockTroll(int depth, Creature player) {
		Creature creature = new Creature(world, "rock troll", 't', AsciiPanel.green.darker(), 100, 40, 20);
		new TrollAI(creature, player);
		creature.ai().setDeathDrop( c -> newTrollLiver(c.location()) );
		world.addAtEmptyLocation(creature, depth);
		return creature;
	}
	
	/** Places an ice troll at a random space at {@code depth}. */
	public Creature newIceTroll(int depth, Creature player) {
		Creature creature = new Creature(world, "ice troll", 'T', Color.cyan, 150, 50, 20);
		new TrollAI(creature, player);
		creature.ai().setDeathDrop( c -> newTrollLiver(c.location()) );
		world.addAtEmptyLocation(creature, depth);
		return creature;
	}
	
	/** Places an necrophage blood at {@code p}. */
	public Item newNecrophageBlood(Point p) {
		Item item = new Item(world, "necrophage blood vial", (char)154, AsciiPanel.red.brighter());
		item.relocate(p);
		return item;
	}
	
	/** Places a rotfiend at a random space at {@code depth}. */
	public Creature newRotfiend(int depth, Creature player) {
		Creature creature = new Creature(world, "rotfiend", 'r', Color.red.brighter(), 70, 15, 10);
		new RotfiendAI(creature, player);
		creature.ai().setDeathDrop( c -> { newNecrophageBlood(c.location()); } );
		world.addAtEmptyLocation(creature, depth);
		return creature;
	}
	
	/** Places a devourer at a random space at {@code depth}. */
	public Creature newDevourer(int depth, Creature player) {
		Creature creature = new Creature(world, "devourer", 'R', Color.magenta.darker().darker(), 120, 20, 10);
		new RotfiendAI(creature, player);
		creature.ai().setDeathDrop( c -> { newNecrophageBlood(c.location()); } );
		world.addAtEmptyLocation(creature, depth);
		return creature;
	}
	
	/** Places a vampire fang at {@code p} */
	public Item newVampireFang(Point p) {
		Item item = new Item(world, "vampire fang", '!', AsciiPanel.white);
		item.relocate(p);
		return item;
	}
	
	/** Places an alp at a random space at {@code depth}. */
	public Creature newAlp(int depth, Creature player) {
		Creature creature = new Creature(world, "alp", 'v', Color.white, 70, 15, 10);
		new VampireAI(creature, player);
		creature.ai().setDeathDrop( c -> { newVampireFang(c.location()); } );
		world.addAtEmptyLocation(creature, depth);
		return creature;
	}
	
	/** Places a bruxa at a random space at {@code depth}. */
	public Creature newBruxa(int depth, Creature player) {
		Creature creature = new Creature(world, "bruxa", 'V', Color.red.darker().darker(), 120, 20, 10);
		new VampireAI(creature, player);
		creature.ai().setDeathDrop( c -> { newVampireFang(c.location()); } );
		world.addAtEmptyLocation(creature, depth);
		return creature;
	}
	
	public Creature newBat(int depth) {
		return newBat(-1, -1, depth);
	}
	
	/** Places a bat on an empty location at {@code depth}. */
	public Creature newBat(int wx, int wy, int wz) {
		Creature bat = new Creature(world, "bat", 'b', AsciiPanel.yellow, 15, 5, 0);
		new BatAI(bat);
		if (wx == -1 || wy ==-1)
			world.addAtEmptyLocation(bat, wz);
		else
			world.addAtLocation(bat, new Point(wx, wy, wz));
		return bat;
	}
	
	/** Places a Zombie on an empty location at {@code depth}. */
	public Creature newZombie(int depth, Creature player) {
		Creature zombie = new Creature(world, "zombie", 'z', AsciiPanel.white, 50, 10, 10);
		world.addAtEmptyLocation(zombie, depth);
		new ZombieAI(zombie, player);
		return zombie;
	}
	
	/** Places a goblin on an empty location at {@code depth}. */
	public Creature newGoblin(int depth, Creature player) {
		Creature goblin = new Creature(world, "goblin", 'g', AsciiPanel.brightGreen, 66, 15, 5);
		new GoblinAI(goblin, player);
		goblin.equip(randomWeapon(depth));
        goblin.equip(randomArmor(depth));
        world.addAtEmptyLocation(goblin, depth);
        return goblin;
	}
	
	/** Places a rock on an empty location at {@code depth}. */
	public Item newRock(int depth) {
		Item rock = new Item(world, "rock", ',', AsciiPanel.yellow);
		world.addAtEmptyLocation(rock, depth);
		return rock;
	}
	
	/** Places a Monster Trophy on an empty location at {@code depth}. */
	public Item newVictoryItem(int depth) {
		Item victoryItem = new Item(world, "Monster trophy", '*', AsciiPanel.brightWhite);
		world.addAtEmptyLocation(victoryItem, depth);
		return victoryItem;
	}
	
	/** Places a dagger on an empty location at {@code depth}. */
	public Item newDagger(int depth){
	    Item item = new Item(world, "dagger", ')', AsciiPanel.white);
	    item.modifyAttackValue(5);
	    world.addAtEmptyLocation(item, depth);
	    return item;
	  }

	/** Places a sword on an empty location at {@code depth}. */
	public Item newSword(int depth){
		Item item = new Item(world, "sword", ')', AsciiPanel.brightWhite);
		item.modifyAttackValue(10);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	/** Places a staff on an empty location at {@code depth}. */
	public Item newStaff(int depth){
		Item item = new Item(world, "staff", ')', AsciiPanel.yellow);
		item.modifyAttackValue(5);
		item.modifyDefenseValue(3);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a bow on an empty location at {@code depth}. */
	public Item newBow(int depth) {
		Item item = new Item(world, "bow", ')', AsciiPanel.yellow);
		item.modifyRangedAttackValue(5);
		item.modifyAttackValue(1);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	/** Places a tunic on an empty location at {@code depth}. */
	public Item newLightArmor(int depth){
		Item item = new Item(world, "tunic", '[', AsciiPanel.green);
		item.modifyDefenseValue(2);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	/** Places a chainmail on an empty location at {@code depth}. */
	public Item newMediumArmor(int depth){
		Item item = new Item(world, "chainmail", '[', AsciiPanel.white);
		item.modifyDefenseValue(4);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a platemail on an empty location at {@code depth}. */
	public Item newHeavyArmor(int depth){
		Item item = new Item(world, "platemail", '[', AsciiPanel.brightWhite);
		item.modifyDefenseValue(6);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	/** Places a random weapon on an empty location at {@code depth}. */
	public Item randomWeapon(int depth){
		switch ((int)(Math.random() * 4)){
		case 0: return newDagger(depth);
		case 1: return newSword(depth);
		case 2: return newBow(depth);
		default: return newStaff(depth);
		}
	}
	
	/** Places a random armor on an empty location at {@code depth}. */
	public Item randomArmor(int depth){
		switch ((int)(Math.random() * 3)){
		case 0: return newLightArmor(depth);
		case 1: return newMediumArmor(depth);
		default: return newHeavyArmor(depth);
		}
	}
	
//	/** Places a Potion of Health on an empty location at {@code depth}. */
//	public Item newPotionOfHealth(int depth) {
//		String appearance = potionAppearances.get(0);
//		Item item = new Item(world, "health potion", '!', potionColors.get(appearance), appearance);
//		item.setQuaffEffect(new Effect(1){
//		 public void start(Creature creature) {
//			 if (creature.hp() == creature.maxHP())
//				 return;
//			 creature.modifyHP(15, "health potion", caster);
//			 creature.doAction(item, "look healthier");
//		 } 
//		});
//		world.addAtEmptyLocation(item, depth);
//		return item;
//	}
	
	/** Places a Potion of Mana on an empty location at {@code depth}. */
	public Item newPotionOfMana(int depth) {
		String appearance = potionAppearances.get(1);
		Item item = new Item(world, "mana potion", '!', potionColors.get(appearance), appearance);
		item.setQuaffEffect(new Effect(1){
		 public void start(Creature creature) {
			 if (creature.mana() == creature.maxMana())
				 return;
			 creature.modifyMana(10);
			 creature.doAction(item, "look more energetic");
		 } 
		});
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a Potion of Poison on an empty location at {@code depth}. */
	public Item newPotionOfPoison(int depth) {
		String appearance = potionAppearances.get(2);
		Item item = new Item(world, "poison potion", '!', potionColors.get(appearance), appearance);
		item.setQuaffEffect(new Effect(20) {
			public void start(Creature creature) {
				creature.doAction(item, "look sick");
			}
			
			public void update(Creature creature) {
				creature.modifyHP(-1, "poison", caster);
			}
		});
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	private static final int warriorPotionID = Effect.newID();
	/** Places a potion of warrior on an empty location at {@code depth}. */
	public Item newPotionOfWarrior(int depth) {
		String appearance = potionAppearances.get(3);
		Item item = new Item(world, "warrior potion", '!', potionColors.get(appearance), appearance);
		item.setQuaffEffect(new Effect(20, warriorPotionID){
			public void start(Creature creature) {
				creature.modifyAttackValue(5);
				creature.modifyDefenseValue(5);
				creature.doAction(item, "look stronger");
			 } 
			 
			public void end(Creature creature) {
				creature.modifyAttackValue(-5);
				creature.modifyDefenseValue(-5);
				creature.doAction("look less strong");
			}
		});
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a randomly chosen potion on an empty location at {@code depth}. */
	public Item randomPotion(int depth) {
		switch ((int) (Math.random() * 4)) {
//		case 0: return newPotionOfHealth(depth);
		case 1: return newPotionOfPoison(depth);
		case 2: return newPotionOfMana(depth);
		default: return newPotionOfWarrior(depth);
		}
	}
	
	
	
	/** Places a White Rafford's Decoction on an empty location at {@code depth}. This potion heals immediately.*/
	public Potion newPotionWhiteRafford(int depth) {
		Potion potion = new Potion(world, "White Rafford's Decoction", (char)168, AsciiPanel.brightWhite, "White Rafford's Decoction", 30);
		potion.setQuaffEffect(new Effect(1) {
			public void start(Creature target) {
				 if (target.hp() == target.maxHP())
					 return;
				 target.modifyHP(15, "White Rafford's Decoction", caster);
				 target.doAction(potion, "look healthier");
			}
		});
		world.addAtEmptyLocation(potion, depth);
		return potion;
	}
	
	private static final int swallowID  = Effect.newID();
	/** Places a Swallow on an empty location at {@code depth}. Swallow heals over time. */
	public Potion newPotionSwallow(int depth) {
		Potion potion = new Potion(world, "Swallow", (char)168, Color.green, "Swallow", 20);
		potion.setQuaffEffect(new Effect(30, swallowID) {
			public void update(Creature target) {
				target.modifyHP(2, "Swallow", caster);
			}
		});
		return potion;
	}
	
	
	private static final int fullmoonID = Effect.newID();
	/** Places a Full Moon Potion on an empty location at {@code depth}. Full Moon increases max HP. */
	public Potion newPotionFullMoon(int depth) {
		Potion potion = new Potion(world, "Full Moon", (char)168, Color.MAGENTA, "Full Moon", 25);
		potion.setQuaffEffect( new Effect(60, fullmoonID) {
			public void start(Creature target) {
				target.modifyMaxHP(50, "Full Moon", caster);
				target.modifyHP(50);
			}
			public void end(Creature target) {
				target.modifyMaxHP(-50, "Full Moon", caster);
			}
		});
		return potion;
	}
	
	private static final int tawnyOwlID = Effect.newID();
	/** Places a Tawny Owl Potion on an empty location at {@code depth}. Tawny Owl increases MP regeneration. */
	public Potion newPotionTawnyOwl(int depth) {
		Potion potion = new Potion(world, "Tawny Owl", (char)168, Color.BLUE, "Tawny Owl", 20);
		potion.setQuaffEffect( new Effect(30, tawnyOwlID) {
			public void start(Creature target) {
				target.modifyRegenManaPer1000(5000);
			}
			public void end(Creature target) {
				target.modifyRegenManaPer1000(-5000);
			}
		});
		return potion;
	}
	
	private static final int catID = Effect.newID();
	/** Places a Cat Potion on an empty location at {@code depth}. Cat increases vision range. */
	public Potion newPotionCat(int depth) {
		Potion potion = new Potion(world, "Cat", (char)168, Color.BLUE, "Cat", 15);
		potion.setQuaffEffect(new Effect(120, catID) {
			public void start(Creature target) {
				target.modifyVisionRadius(5);
			}
			public void end(Creature target) {
				target.modifyVisionRadius(-5);
			}
		});
		return potion;
	}
	
	/** Places a White Honey Potion on an empty location at {@code depth}. White honey clears all potion effects and toxicity. */
	public Potion newPotionWhiteHoney(int depth) {
		Potion potion = new Potion(world, "White Honey", (char)168, Color.white.brighter(), "White Honey", 0);
		potion.setQuaffEffect(new Effect(0) {
			public void start(Creature target) {
				target.removeEffects( e -> e.isPotionEffect());
			}
		});
		return potion;
	}
	
	private static final int goldenOrioleID = Effect.newID();
	/** Places a Golden Oriole on an empty location at {@code depth}. Golden Oriole provides poison immunity and neutralizes current poison. */
	public Potion newPotionGoldenOriole(int depth) {
		Potion potion = new Potion(world, "Golden Oriole", (char)168, Color.ORANGE, "Golden Oriole", 20);
		potion.setQuaffEffect(new Effect(120, goldenOrioleID) {
			public void start(Creature target) {
				target.removeEffects( e -> e.isPoison() );
				target.modifyPoisonResistance(100);
			}
			public void end(Creature target) {
				target.modifyPoisonResistance(-100);
			}
		});
		return potion;
	}
	
	private static final int thunderboltID = Effect.newID();
	/** Places a Thunderbolt Potion on an empty location at {@code depth}. Thunderbolt increases damage. */
	public Potion newPotionThunderbolt(int depth) {
		Potion potion = new Potion(world, "Thunderbolt", (char)168, Color.YELLOW, "Thunderbolt", 25);
		potion.setQuaffEffect(new Effect(60, thunderboltID) {
			public void start(Creature target) {
				target.modifyAttackValue(10);
			}
			public void end(Creature target) {
				target.modifyAttackValue(-10);
			}
		});
		return potion;
	}
	
	private static final int blizzardID = Effect.newID();
	private static final int blizzardSlowID = Effect.newID();
	/** Places a Blizzard Potion on an empty location at {@code depth}. Blizzard increases speed after a kill. */
	public Potion newPotoinBlizzard(int depth) {
		Potion potion = new Potion(world, "Blizzard", (char)168, Color.GRAY, "Blizzard", 25);
		potion.setQuaffEffect(new Effect(60, blizzardID) {
			int xp;
			public void start(Creature target) {
				this.xp = target.xp();
			}
			public void update(Creature target) {
				if (xp != target.xp())
					target.addEffect(new Effect(5, blizzardSlowID){
						public void start(Creature target) { bPotionEffect = true; target.modifySpeed(50); }
						public void end(Creature target) { target.modifySpeed(-50); }
					});
				xp = target.xp();
			}
			
		});
		return potion;
	}
	
	private static final int blackBloodID = Effect.newID();
	/** Places a Black Blood Potion on an empty location at {@code depth}. Black Blood damages vampires and necrophages when they hit the target. */
	public Potion newPotionBlackBlood(int depth) {
		Potion potion = new Potion(world, "Black Blood", (char)168, Color.GRAY.darker(), "Black Blood", 15);
		potion.setQuaffEffect(new Effect(60, blackBloodID) {
			List<String> names = Arrays.asList("drowner", "drowned dead", "bruxa", "alp");
			public void onHit(int amount, String causeOfDeath, Creature aggressor, Creature target) {
				if ( aggressor == null || target == aggressor || !names.contains(aggressor.name()))
					return;
				aggressor.modifyHP(-5, "Black Blood", target);
			}
		});
		return potion;
	}
	
	private static final int petrisPhilterID = Effect.newID();
	/** Places a Petri's Philter Potion on an empty location at {@code depth}. Petri's Philter increases sign intensity. */
	public Potion newPotoinPetrisPhilter(int depth) {
		Potion potion = new Potion(world, "Petri's Philter", (char)168, Color.MAGENTA.darker(), "Petri's Philter", 25);
		potion.setQuaffEffect(new Effect(60, petrisPhilterID) {
			public void start(Creature target) {
				target.modifySignIntensity(50);
			}
			public void end(Creature target) {
				target.modifySignIntensity(-50);
			}
		});
		return potion;
	}
	
	private static final int slowHealID = Effect.newID();
	private static int innerStrengthID = Effect.newID();
	/** Adds a spellbook with restoration {@linkplain Spell}s to an empty space at {@code depth}. */
	public Item newWhiteMageSpellbook(int depth) {
		Item item = new Item(world, "white mage's spellbook", '+', AsciiPanel.brightWhite);
		item.addWrittenSpell("minor heal", 4, Spell.Delivery.TARGET, new Effect(1) {
			public void start(Creature creature) {
				if (creature.hp() == creature.maxHP())
					return;
				creature.modifyHP(20, "minor heal");
				creature.doAction("loook healthier");
			}
		});

		item.addWrittenSpell("major heal", 8, Spell.Delivery.TARGET, new Effect(1) {
			public void start(Creature creature) {
				if (creature.hp() == creature.maxHP())
					return;
				creature.modifyHP(50, "major heal");
				creature.doAction("loook healthier");
			}
		});
  
		item.addWrittenSpell("slow heal", 12, Spell.Delivery.TARGET, new Effect(50, slowHealID) {
			public void update(Creature creature) {
				creature.modifyHP(2, "slow heal");
			}
		});
		
		item.addWrittenSpell("inner strength", 16, Spell.Delivery.TARGET, new Effect(50, innerStrengthID) {
			public void start(Creature creature) {
				creature.modifyAttackValue(2);
				creature.modifyDefenseValue(2);
				creature.modifyVisionRadius(1);
				creature.modifyRegenHpPer1000(10);
				creature.modifyRegenManaPer1000(-10);
				creature.doAction("seem to glow with inner strength");
			}
  
			public void update(Creature creature) {
				if (Math.random() < 0.25)
					creature.modifyHP(1, "inner strength");
			}
  
			public void end(Creature creature) {
				creature.modifyAttackValue(-2);
				creature.modifyDefenseValue(-2);
				creature.modifyVisionRadius(-1);
				creature.modifyRegenHpPer1000(-10);
				creature.modifyRegenManaPer1000(10);
			}
		});
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Adds a spellbook containing strange spells to an empty {@linkplain Tile} at {@code depth}. */
	public Item newBlueMageSpellbook(int depth) {
		Item item = new Item(world, "blue mage's spellbook", '+', AsciiPanel.brightBlue);
		
		item.addWrittenSpell("blood to mana", 1, Spell.Delivery.SELF, new Effect(1) {
			public void start(Creature creature) {
				int amount = Math.min(creature.hp() - 1, creature.maxMana() - creature.mana());
				creature.modifyHP(-amount, "converting health to mana");
				creature.modifyMana(amount);
			}
		});
		
		item.addWrittenSpell("blink", 6, Spell.Delivery.TARGET, new Effect(1) {
			public void start(Creature creature) {
				creature.doAction("fade out");
				int mx = 0, my = 0;
				do {
					mx = (int) (Math.random() * 11) - 5;
					my = (int) (Math.random() * 11) - 5;
				} while (!creature.canEnter(new Point(creature.x() + mx, creature.y() + my, creature.z()))
						&& creature.canSee( new Point(creature.x() +mx, creature.y() + my, creature.z())));
				
				creature.moveBy(mx, my, 0);
				creature.doAction("fade in");
			}
		});
		
		item.addWrittenSpell("summon bats", 11, Spell.Delivery.TARGET, new Effect(1) {
			public void start(Creature creature) {
				Creature tempBat = newBat(0);
				for (int ox = -1; ox<2; ox++) {
					for (int oy=-1; oy<2; oy++) {
						Point location = new Point(
								creature.x() + ox,
								creature.y() + oy,
								creature.z() );
						if (tempBat.canEnter(location))
							newBat(location.x, location.y, location.z);
					}
				}
				world.remove(tempBat);
			}
		});
		
		item.addWrittenSpell("detect creatures", 16, Spell.Delivery.TARGET, new Effect(75) {
			public void start(Creature creature) {
				creature.doAction("feel your senses sharpen");
				creature.modifyDetectCreatures(40);
			}
			
			public void end(Creature creature) {
				creature.modifyDetectCreatures(-40);
			}
		});
		
		item.addWrittenSpell("cave in", 25, Spell.Delivery.AIMED, new Effect(300) {
			public void start(Creature creature) {
				creature.modifyMaxHP(-30, "a cave in", caster);
				creature.notify("You are crushed beneath the rocks!");
				
				for (int dx=-2; dx<3; dx++) {
					for (int dy=-2; dy<3; dy++) {
						Point location = new Point(creature.x()+dx, creature.y()+dy, creature.z());
						if (dx==0 && dy==0 || !creature.isInBounds(location))
							continue;
						int dist = dx*dx + dy*dy;
						if (dist <= 2.1 && Math.random() > 0.25 || dist > 2.1 && Math.random() >.75) {
							Creature other = creature.creature(location);
							if (other != null)
								other.modifyHP(-other.hp(), "a cave in", caster);
							world.caveIn(location);
						}
					}
				}
			}
			
			public void update(Creature creature) {
				if (duration%10 == 1) {
					creature.modifyMaxHP(1, "", null);
				}
			}
		});
		
//		FieldOfView f = new FieldOfView(world);
//		Creature orb = new Creature(world,"scrying orb", 'O', Color.PINK, -1, 0, 0) {
//			public FieldOfView fov = f;
//			int radius = 15;
//			private CreatureAI ai = new CreatureAI(this);
//			public void update() {}
//			public FieldOfView updateFOV() {
//				fov.update(x, y, z, radius);
//				return fov;
//			}
//		};
//		item.addWrittenSpell("scrying orb", 15, new Effect(100) {
//			public void start(Creature creature) {
//				playScreen.addObserver(orb);
//				world.creatures().add(orb);
//				orb.x = creature.x;
//				orb.y = creature.y;
//				orb.z = creature.z;
//			}
//			public void end(Creature creature) {
//				playScreen.removeObserver(orb);
//				orb.effects().clear();
//				world.remove(orb);
//			}
//		});
		
//		item.addWrittenSpell("scrying orb", 15, Spell.Delivery.AIMED, new Effect(100) {
//			FieldOfView f = new FieldOfView(world);
//			Point p = new Point(0, 0, 0);
//			Hazard orb = new Hazard(world, "scrying orb", 'O', Color.pink, null, 100) {
//				int radius = 15;
//				public void onEntered(Creature creature) {}
//				public void update() {}
//				public FieldOfView updateFOV() {
//					f.update(p, radius);
//					return f;
//				}
//			};
//			public void start(Creature creature) {
//				p.x = creature.x();
//				p.y = creature.y();
//				p.z = creature.z();
////				world.add(orb, p);
//				orb.relocate(p);
//				playScreen.addObserver(orb);
//			}
//			public void end(Creature creature) {
//				playScreen.removeObserver(orb);
//				world.remove(orb);
//			}
//		});
		
		
		item.addWrittenSpell("scrying orb", 15, Spell.Delivery.AIMED, 
				new Hazard(world, "scrying orb", 'O', Color.pink, null, 100) {
					int radius  = 15;
					FieldOfView f = new FieldOfView(world);
					public FieldOfView updateFOV() {
						f.update(location(), radius);
						return f;
					}
					public void start() { playScreen.addObserver(this); }
					public void end() {playScreen.removeObserver(this); }
					public boolean updatePending() { return false; }
				}
		);
				
				
		item.addWrittenSpell("DegreeTest", 5, 5.5, 75, Spell.Delivery.RADIAL, new Effect(1) 
				{public void start(Creature creature) { creature.doAction("hit into radial");} } );
		
		item.addWrittenSpell("SplashTest", 5, 3.5, 360, Spell.Delivery.TARGET, new Effect(1) 
				{public void start(Creature creature) { creature.doAction("hit into splash");} } );
		
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	

}
