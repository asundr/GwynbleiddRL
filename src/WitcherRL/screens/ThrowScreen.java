package rltut.screens;

import rltut.Creature;
import rltut.Item;

/**
 * This class extends {@linkplain InventoryBasedScreen}. Allows player to select an {@linkplain Item} to throw.
 *
 */
public class ThrowScreen extends InventoryBasedScreen {
	
	private int sx;
	private int sy;

	/**
	 * @param player
	 * @param sx - distance of player from the left of the screen
	 * @param sy - distance of player from top of the screen
	 */
	public ThrowScreen(Creature player, int sx, int sy) {
		super(player);
		this.sx = sx;
		this.sy = sy;
	}

	@Override
	protected String getVerb() {
		return "throw";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return true;
	}

	@Override
	protected Screen use(Item item) {
		return new ThrowAtScreen(player, sx, sy, item);
	}

}
