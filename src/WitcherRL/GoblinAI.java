package rltut;

/**
 * Determines the behavior for a goblin.
 * @author Arun Sundaram
 *
 */
public class GoblinAI extends CreatureAI {
	Creature player;

	public GoblinAI(Creature creature, Creature player) {
		super(creature);
		this.player = player;
	}
	
	public void onUpdate(){
		if (canRangedWeaponAttack(player)) {
			creature.rangedAttack(player);
		}
		else if (canThrowAt(player)) {
			creature.throwItem(getWeaponToThrow(), player.location());
		}
		else if (creature.canSee(player.location())) {
			hunt(player);
		}
		else if (canPickup()) {
			creature.pickup();
		} else {
		    wander();
		}
	}
	
}
