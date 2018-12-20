package rltut.screens;

import rltut.Creature;
import rltut.Item;

/**
 * This class extends {@linkplain InventoryBasedScreen}. It allows the player to drop an item they own.
 *
 */
public class DropScreen extends InventoryBasedScreen {

	public DropScreen(Creature player) {
		super(player);
	}

	@Override
	protected String getVerb() {
		return "drop";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return true;
	}

	@Override
	protected Screen use(Item item) {
		player.drop(item);
		return null;
	}

}
