package rltut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rltut.screens.PlayScreen;

import asciiPanel.AsciiPanel;

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
	
	public StuffFactory (World world, PlayScreen playScreen) {
		this.world = world;
		this.playScreen = playScreen;
		setUpPotionAppearances();
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
		Creature player = new Creature(world, "player", '@', AsciiPanel.brightWhite, 10000, 20, 5);
		world.addAtEmptyLocation(player, depth);
		new PlayerAI(player, fov, messageHistory);
		return player;
	}
	
	
	/** Places a fungus on an empty location at {@code depth}. */
	public Creature newFungus(FungusAI parent, int depth) {
		return newFungus(parent, -1, -1, depth);
	}
	
	/** Places a fungus at the location {@code (wx, wy, wz)}. */
	public Creature newFungus(FungusAI parent, int wx, int wy, int wz) {
		Creature fungus = new Creature(world, "fungus", (char)231, AsciiPanel.green, 10, 0, 0);
		new FungusAI(fungus, this, parent);
		if (wx == -1 || wy == -1)
			world.addAtEmptyLocation(fungus, wz);
		else
			world.addAtLocation(fungus, new Point(wx, wy, wz));
		return fungus;
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
		Item rock = new Item("rock", ',', AsciiPanel.yellow);
		world.addAtEmptyLocation(rock, depth);
		return rock;
	}
	
	/** Places a Monster Trophy on an empty location at {@code depth}. */
	public Item newVictoryItem(int depth) {
		Item victoryItem = new Item("Monster trophy", '*', AsciiPanel.brightWhite);
		world.addAtEmptyLocation(victoryItem, depth);
		return victoryItem;
	}
	
	/** Places a dagger on an empty location at {@code depth}. */
	public Item newDagger(int depth){
	    Item item = new Item("dagger", ')', AsciiPanel.white);
	    item.modifyAttackValue(5);
	    world.addAtEmptyLocation(item, depth);
	    return item;
	  }

	/** Places a sword on an empty location at {@code depth}. */
	public Item newSword(int depth){
		Item item = new Item("sword", ')', AsciiPanel.brightWhite);
		item.modifyAttackValue(10);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	/** Places a staff on an empty location at {@code depth}. */
	public Item newStaff(int depth){
		Item item = new Item("staff", ')', AsciiPanel.yellow);
		item.modifyAttackValue(5);
		item.modifyDefenseValue(3);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a bow on an empty location at {@code depth}. */
	public Item newBow(int depth) {
		Item item = new Item("bow", ')', AsciiPanel.yellow);
		item.modifyRangedAttackValue(5);
		item.modifyAttackValue(1);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	/** Places a tunic on an empty location at {@code depth}. */
	public Item newLightArmor(int depth){
		Item item = new Item("tunic", '[', AsciiPanel.green);
		item.modifyDefenseValue(2);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	/** Places a chainmail on an empty location at {@code depth}. */
	public Item newMediumArmor(int depth){
		Item item = new Item("chainmail", '[', AsciiPanel.white);
		item.modifyDefenseValue(4);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a platemail on an empty location at {@code depth}. */
	public Item newHeavyArmor(int depth){
		Item item = new Item("platemail", '[', AsciiPanel.brightWhite);
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
	
	/** Places a Potion of Health on an empty location at {@code depth}. */
	public Item newPotionOfHealth(int depth) {
		String appearance = potionAppearances.get(0);
		Item item = new Item("health potion", '!', potionColors.get(appearance), appearance);
		item.setQuaffEffect(new Effect(1){
		 public void start(Creature creature) {
			 if (creature.hp() == creature.maxHP())
				 return;
			 creature.modifyHP(15, "health potion", caster);
			 creature.doAction(item, "look healthier");
		 } 
		});
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a Potion of Mana on an empty location at {@code depth}. */
	public Item newPotionOfMana(int depth) {
		String appearance = potionAppearances.get(1);
		Item item = new Item("mana potion", '!', potionColors.get(appearance), appearance);
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
		Item item = new Item("poison potion", '!', potionColors.get(appearance), appearance);
		item.setQuaffEffect(new Effect(20) {
			public void start(Creature creature) {
				creature.doAction(item, "look sick");
			}
			
			public void update(Creature creature) {
				super.update(creature);
				creature.modifyHP(-1, "poison", caster);
			}
		});
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	/** Places a potion of warrior on an empty location at {@code depth}. */
	public Item newPotionOfWarrior(int depth) {
		String appearance = potionAppearances.get(3);
		Item item = new Item("warrior potion", '!', potionColors.get(appearance), appearance);
		item.setQuaffEffect(new Effect(20){
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
		case 0: return newPotionOfHealth(depth);
		case 1: return newPotionOfPoison(depth);
		case 2: return newPotionOfMana(depth);
		default: return newPotionOfWarrior(depth);
		}
	}
	
	/** Adds a spellbook with restoration {@linkplain Spell}s to an empty space at {@code depth}. */
	public Item newWhiteMageSpellbook(int depth) {
		Item item = new Item("white mage's spellbook", '+', AsciiPanel.brightWhite);
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
  
		item.addWrittenSpell("slow heal", 12, Spell.Delivery.TARGET, new Effect(50) {
			public void update(Creature creature) {
				super.update(creature);
				creature.modifyHP(2, "slow heal");
			}
		});

		item.addWrittenSpell("inner strength", 16, Spell.Delivery.TARGET, new Effect(50) {
			public void start(Creature creature) {
				creature.modifyAttackValue(2);
				creature.modifyDefenseValue(2);
				creature.modifyVisionRadius(1);
				creature.modifyRegenHpPer1000(10);
				creature.modifyRegenManaPer1000(-10);
				creature.doAction("seem to glow with inner strength");
			}
  
			public void update(Creature creature) {
				super.update(creature);
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
		Item item = new Item("blue mage's spellbook", '+', AsciiPanel.brightBlue);
		
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
				creature.modifyDetectCreatures(1);
			}
			
			public void end(Creature creature) {
				creature.modifyDetectCreatures(-1);
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
				super.update(creature);
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
		
		item.addWrittenSpell("scrying orb", 15, Spell.Delivery.AIMED, new Effect(100) {
			FieldOfView f = new FieldOfView(world);
			Point p = new Point(0, 0, 0);
			Hazard orb = new Hazard("scrying orb", 'O', Color.pink, null) {
				int radius = 15;
				public void onEntered(Creature creature) {}
				public void update() {}
				public FieldOfView updateFOV() {
					f.update(p, radius);
					return f;
				}
			};
			public void start(Creature creature) {
				p.x = creature.x();
				p.y = creature.y();
				p.z = creature.z();
				world.add(orb, p);
				playScreen.addObserver(orb);
			}
			public void end(Creature creature) {
				playScreen.removeObserver(orb);
				world.remove(orb, p);
			}
		});
		
		item.addWrittenSpell("DegreeTest", 5, 5.5, 75, Spell.Delivery.RADIAL, new Effect(1) 
				{public void start(Creature creature) { creature.doAction("hit into radial");} } );
		
		item.addWrittenSpell("SplashTest", 5, 3.5, 360, Spell.Delivery.TARGET, new Effect(1) 
				{public void start(Creature creature) { creature.doAction("hit into splash");} } );
		
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	

}
