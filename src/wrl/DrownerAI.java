package wrl;

/**
 * Defines the behavior for Drowners which are territorial about their homes but prefer not to venture to far from water.
 * @author Arun Sundaram
 *
 */
public class DrownerAI extends CreatureAI {
	
	private double WATER_RANGE;
	private Point lastWater;
	private Creature player;
	
	/** @param waterRange - the range from which this will venture away from water before returning. */
	public DrownerAI(Creature c, Creature player, double waterRange) {
		super(c);
		this.player = player;
		this.WATER_RANGE = waterRange;
	}
	
	public void onUpdate() {
		Point newLastWater = creature.world().getShortestPathInRange(creature.location(), WATER_RANGE, p -> creature.world().tile(p) == Tile.WATER && !player.canSee(p) );
		if (newLastWater != null) lastWater = newLastWater;
		
		if (canAttack(player))
			hunt(player.location());
		else if (destination != null)
			hunt(destination);
		else {
			if (lastWater != null && newLastWater == null) {  //if lastwater remembered and currently cant see water hidden from player
				destination = lastWater;
				hunt(destination);
			} else
				wander();
		}
	}
	
	/** Returns true if there is a {@linkplain Path} to a visible {@code target} that remains in range of water.  */
	private boolean canAttack(Creature target) {
		if (!canSee(target.location()))
			return false;
		Path path = new Path(creature, target.x(), target.y());
		if (path.points() == null)
			return false;
		int size = path.points().size();
		if (path.points() == null || size == 0)
			return false;
		if (size == 1)
			return true;
		for (int i=0; i<size-1; i++)
			if ( findNearestWater(path.points().get(i)) == null)
				return false;
		return true;
	}
	
	/** Returns the closest {@linkplain Point} to {@code location} that is a water tile, or {@code null} if not in range. */
	private Point findNearestWater(Point location) {
		return creature.world().getShortestPathInRange(location, WATER_RANGE, p -> creature.world().tile(p) == Tile.WATER);
	}

}
