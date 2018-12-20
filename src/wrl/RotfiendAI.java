package wrl;

import java.awt.Color;

/**
 * Defines the behavoir of rotfiends. They are able to spit poisonous acid.
 * When low on health, the rotfiend will bloat at explode when health reaches zero.
 * @author Arun Sundaram
 *
 */
public class RotfiendAI extends CreatureAI {
	
	protected Creature player;
	protected int spitCooldown = 20;
	
	public RotfiendAI(Creature creature, Creature player) {
		super(creature);
		this.player = player;
	}
	
	public void onUpdate() {
		if (creature.hp() / ((double) creature.maxHP()) < 0.3 ) {
			creature.setColor(Color.orange);
			creature.setColor(Color.orange.darker());
			creature.modifyHP( (int) (creature.maxHP() * -0.1) );
			if (!creature.isDead())
				creature.doAction("bulge");
			creature.modifyAP(-200);
			return;
		}
		spitCooldown = Math.min(20, spitCooldown + 1);
		double playerDistance = player.location().subtract(creature.location()).magnitudeXY();
		if (playerDistance < 5 && spitCooldown == 20 && Math.random() < 0.1 && canSee(player.location())) {
			spit(player);
			destination = player.location();
		} else if (creature.canSee(player.location())) {
			hunt(player.location());
		} else if (destination != null) {
			hunt(destination);
		} else {
		    wander();
		}
	}
	
	private static final int acidID = Effect.newID();
	/** A ranged attack that poisons the target. */
	public void spit(Creature player) {
		player.notify("The %s spits at %s", creature.name(), player.name());
		creature.castSpell(new Spell("acid spit", 0, Spell.Delivery.AIMED, new Effect(20, acidID) {
			public void update(Creature target) {	
				target.modifyHP(-2, "poison", creature);
			}
		}), player.x(), player.y());
	}
	
	public void die() {
		creature.doAction("explode");
		Spell explosion = new Spell("necrophage explosion", 0, 2.0, 360, Spell.Delivery.RADIAL, new Effect(1) {
			public void start(Creature target) {
				int damage = (int) Math.max(0, ( Math.random() * 50) - target.defenseValue() ) + 1;
//				target.commonAttack(creature, -damage, "%s took %d damage in the %s's explosion", target.name(), damage, creature.name()); //TODO fix damage notificaiton
				target.knockBack(creature.location(), 2);
				target.modifyHP(-damage, "explosion", creature);
				
			}
		}, StuffFactory.newBlood(creature.world()) ); //factory.newBlood(creature.world()));
		creature.castSpell(explosion, creature.x(), creature.y(), explosion.areaOfEffect(0) );
	}

}
