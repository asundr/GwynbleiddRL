package wrl;

public abstract class Action {
	
	private int apCost;
	
	/** @param apCost - Additional cost to AP along with cost of act(). */
	public Action(int apCost) {
		this.apCost = apCost;
	}
	
	/** Makes the creature do this action.  */
	public int perform(Creature creature) {
		act(creature);
		creature.modifyAP(-apCost);
		return apCost;
	}
	
	/** Defines the behavior of this action. */
	abstract protected void act(Creature creature);
	
	/** Defines how this scheduled action should respond to an interruption. */
	public void interrupt() {}
	
}