package wrl;

/**
 * Defines the behavior for a bat.
 * @author Arun Sundaram
 *
 */
public class BatAI extends CreatureAI {

	public BatAI(Creature creature) {
		super(creature);
		creature.modifySpeed(100);
	}
	
	public void onUpdate() {
		wander();
	}

}
