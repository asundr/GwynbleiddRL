package rltut;

/**
 * Determines how the player will respond to input and serves as the player's memory.
 * @author Arun Sundaram
 *
 */
public class PlayerAI extends CreatureAI {
	
	private MessageHistory messageHistory;
	private FieldOfView fov;
	
	public PlayerAI (Creature creature, FieldOfView fov, MessageHistory messageHistory) {
		super(creature);
		this.fov = fov;
		this.messageHistory = messageHistory;
	}
	
	public void onEnter(Point p, Tile tile) {
		if (tile.isGround()) {
			creature.walk(p);
		}
		else if (tile.isDiggable()) {
			creature.dig(p.x, p.y);
		}
	}
	
	/** Sends notifications to the {@linkplain MessageHistory}. */
	public void onNotify(String message) {
		messageHistory.add(message);
	}
	
	public boolean canSee(Point p) {
		return fov.isVisible(p);
	}
	
	public void onGainLevel() { }
	
	public Tile rememberedTile(Point p) {
		return fov.tile(p);
	}	

}
