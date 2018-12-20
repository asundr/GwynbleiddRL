package wrl;

/**
 * This describes the behavior of a troll. The troll is a slow but heavy hitting creature that can charge at enemies.
 * It has an affinity for rocks which it will throw at fleeing opponents.
 * @author Arun Sundaram
 *
 */
public class TrollAI extends CreatureAI {
	
	private static final int CHARGE_DISTANCE = 10;
	protected Creature player;
	
	public TrollAI(Creature creature, Creature player) {
		super(creature);
		creature.modifySpeed(-50);
		this.player = player;
	}
	
	
	public void onUpdate() {
		double playerDistance = distanceTo(player.location());
		if (playerDistance > 2 && destination != null && playerFurtherAway() && canThrowAt(player)) {
			creature.throwItem(getWeaponToThrow(), player.location());
			destination = player.location();
		} else if (playerDistance > 3 && canPickup() && creature.item(creature.location()).name().equals("rock") && creature.inventory().count("rock") == 0 ) {
			creature.pickup();
		} else if (creature.canSee(player.location())) {
			if (playerDistance > 3 && Math.random() < 0.1)
				charge(player);
			else
				hunt(player.location());
		} else if (destination != null) {
			hunt(destination);
		} else {
		    wander();
		}
	}
	
	/** Returns true if the player has moved away from the troll. */
	protected boolean playerFurtherAway() {
		return distanceTo(player.location()) > distanceTo(destination);
	}
	
	/** Applies a charging state to this creature. */
	public void charge(Creature target) {
		creature.doAction("charge");
		Point dif = target.location().subtract(creature.location());
		int scale = (int) Math.ceil( (CHARGE_DISTANCE) / dif.magnitudeXY() ) + 1;
		Line chargeLine = new Line(creature.x(), creature.y(), creature.x() + scale*dif.x, creature.y() + scale*dif.y, creature.z());
		
		for(int i=1; i < CHARGE_DISTANCE + 1; i++) {
			int index = i;
			creature.addAction( new Action(12) {
				protected void act(Creature creature) {
					World world = creature.world();
					Point p = chargeLine.getPoints().get(index);
					if (!world.tile(p).isGround()) {
						creature.clearActions();
						creature.stun(400);
						return;
					}
					
					Creature c = world.creature(p);
					if (c != null ) {
						c.knockBack(creature.location(), 2);
						creature.commonAttack(c, creature.attackValue()*2, "charge into the %s for %d damage", c.name());
					}
					
					if (canEnter(p))
						creature.relocate(p);
					else
						creature.clearActions();
				}
			});
		}
		creature.modifyAP(-50);
	}

}
