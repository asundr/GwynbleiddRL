package wrl;

import java.awt.Color;

public class Signs extends Item {
	
	Creature caster;

	public Signs(World world, Creature caster) {
		super(world, "Signs", '+', Color.white);
		this.caster = caster;
		makeSpells();
	}
	
	private static final int quenID = Effect.newID();
	private static final int igniBurnID = Effect.newID();
	private static final int yrdenSlowID = Effect.newID();
	private static final int aardID = Effect.newID();
	private static final int axiiID = Effect.newID();
	private static final int witcherSensesID = Effect.newID();
	
	private void makeSpells() {
		
		Spell quen = new Spell("Quen", 100, Spell.Delivery.SELF, new Effect(1000, quenID) {
			
			public void start(Creature target) {
				target.setWard(this);
			}
			
			public void end(Creature target) {
				target.removeWard();
			}
			
		});
		addWrittenSpell(quen);
		
		
		Spell igni = new Spell("Igni", 100, 3.5, 90, Spell.Delivery.RADIAL, new Effect(10, igniBurnID) {
			
			public void start(Creature target) {
				target.modifyHP(-15, "igni", caster);
			}
			
			public void update(Creature target) {
				super.update(target);
				target.modifyHP(-5, "flames", caster);
			}
			
		});
		addWrittenSpell(igni);
		
		
		Effect slow = new Effect(10, yrdenSlowID) {
			public void start(Creature target) {
				target.modifySpeed(-50);
			}
			public void end(Creature target) {
				target.modifySpeed(50);
			}
		};
		
		Spell yrden = new Spell("Yrden", 100, 2.5, 360, Spell.Delivery.RADIAL, new Hazard(world, "Yrden", (char) 0, null, Color.MAGENTA.darker(), 20) {
			public void onEntered(Creature creature){
				if (creature != null && creature != caster)
					creature.addEffect((Effect) slow.clone());
			}
			public void update() {
				modifyAP(-100);
				Creature target = world.creature(location());
				if (target != null && target != caster)
					target.addEffect((Effect) slow.clone());
			}
		});
		addWrittenSpell(yrden);
	
		
		Spell aard = new Spell("Aard", 100, 4.5, 150, Spell.Delivery.RADIAL, new Effect(1, aardID) {
			public void start(Creature target) {
				target.knockBack(caster.location(), 3);
				target.stun(200);
			}
		});
		addWrittenSpell(aard);
		
		
		Spell axii = new Spell("Axii", 100, Spell.Delivery.TARGET, new Effect(30, axiiID) {
			public void update(Creature target) {
				target.addAction(new Action(0) {
					
					@Override
					protected void act(Creature creature) {
						creature.ai().wander();
					}
					
				});
			}
		});
		addWrittenSpell(axii);
		
		Spell witcherSenses = new Spell("Witcher Senses", 50, Spell.Delivery.SELF, new Effect(30, witcherSensesID) {
			public void start(Creature target) {
				target.modifyVisionRadius(2);
				target.modifyDetectCreatures(10);
			}
			public void end(Creature target) {
				target.modifyVisionRadius(-2);
				target.modifyDetectCreatures(-10);
			}
		});
		addWrittenSpell(witcherSenses);
	}
}
