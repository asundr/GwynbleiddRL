package rltut;

/**
 * Determines the behavior of a zombie.
 * @author Arun Sundaram
 *
 */
public class ZombieAI extends CreatureAI {
	private Creature player;
	
	public ZombieAI(Creature creature, Creature player) {
		super(creature);
		this.player = player;
	}
	
	public void onUpdate() {
		if (Math.random() < 0.25) {
			creature.stay();
		} else if (canSee(player.location())) {
			hunt(player);
		} else {
			wander();
		}
	}

}
