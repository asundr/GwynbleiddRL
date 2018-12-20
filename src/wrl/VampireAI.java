package wrl;

/**
 * Defines the behavior of a higher vampire.
 * @author Arun Sundaram
 *
 */
public class VampireAI extends CreatureAI {
	
	protected Creature player;
	private int invisibilityCharge;
	
	private static final double STRAFE_RANGE = 3.5;
	
	private boolean strafeLeft;
	private boolean attacking;
	private int strafeCount = 0;
	private int retreatCount = 0;
	
	public VampireAI(Creature creature, Creature player) {
		super(creature);
		this.player = player;
		invisibilityCharge = (int) ( Math.random() * 100 );
		creature.modifySpeed(25);
	}
	
	
	// attack -> (pass, retreat) -> invis -> strafe -> attack
	
	public void onUpdate() {
		invisibilityCharge = Math.min(invisibilityCharge + 1, 100);
		
		if (retreatCount < 1 && player.z() == creature.z() && distanceTo(player.location()) < 2) {
			if (attacking || Math.random() < 0.25) {
				attacking = false;
				hunt(player.location());
			} else 
				pass(player);
		} else if (creature.canSee(player.location())) {
			if (attacking) {
				hunt(player.location());
			} else if (invisibilityCharge == 100) {
				destination = player.location();
				turnInvisible();
			} else if (retreatCount > 0) 
				retreat(player);
			else if (strafeCount > 0)
				strafe(player);
			else {
				hunt(player.location());
			}
		} else if (destination != null) {
			hunt(destination);
		} else {
		    wander();
		}
	}
	
	/** Moves in a direction perpendicular to the target. */
	public void strafe(Creature target) {
		strafeCount--;
		destination = target.location();
		double angle = new Line(creature.location(), target.location()).radialAngle();
		int targetOctant = (int) Math.round(angle / 45.0)%8;
		int strafeOctant = strafeLeft ? 2 : -2;
		Point next = creature.location().neighbor(targetOctant + strafeOctant);
		
		if (target.ai().distanceTo(next) > STRAFE_RANGE )
			next = creature.location().neighbor(targetOctant + (int) Math.signum(strafeOctant) );
		
		if ( !canEnter(next) ) {
			strafeOctant = -strafeOctant;
			next = creature.location().neighbor(targetOctant + strafeOctant);
		}
		
		if (target.ai().distanceTo(next) > STRAFE_RANGE )
			next = creature.location().neighbor(targetOctant + (int) Math.signum(strafeOctant) );
		
		if ( !canEnter(next) ) {
			strafeCount = 0;
			retreat(target);
		} else {
			creature.walk(next);		
		}
		if (strafeCount <= 0)
			attacking = true;
	}
	
	/** Creature moves in the direction away from the target. */
	public void retreat(Creature target) {
		retreatCount--;
		destination = target.location();
		double angle = new Line(creature.location(), target.location()).radialAngle() + 180;
		Point next = creature.location().neighbor(angle);
		if ( canEnter(next) ) {
			creature.walk(next);
		} else {
			retreatCount = 0;
			creature.stay();
		}
		if (retreatCount == 0) {
			strafeCount = 3;
			strafeLeft = Math.random() > 0.5;
		}
	}
	
	/** Moves past the target before preparing to retreat. */
	public void pass(Creature target) {
		attacking = false;
		destination = target.location();
		double angle = new Line(creature.location(), target.location()).radialAngle();
		int dAngle = Math.random() < 0.5 ? 45 : -45;
		Point next = creature.location().neighbor(angle + dAngle);
		if (!canEnter(next)) 
			next = creature.location().neighbor(angle - dAngle);
		if (!canEnter(next))
			hunt(target.location());
		else {
			creature.walk(next);
			retreatCount = 3;
		}
	}
	
	private static final int invisibleID = Effect.newID();
	/** Applies invisibility to the Creature. */
	public void turnInvisible() {
		invisibilityCharge = 0;
		creature.addEffect( new Effect(20, invisibleID) {
			public void start(Creature target) {
				target.setVisible(false);
			}
			public void end(Creature target) {
				target.setVisible(true);
			}
		});
		creature.doAction("turn invisible");
		creature.modifyAP(-100);
	}

}
