package rltut.screens;

import rltut.Creature;
import rltut.Item;

/**
 * This class extends {@linkplain InventoryBasedScreen}. Allows the player to select a potion to drink.
 * @author Arun Sundaram
 *
 */
public class QuaffScreen extends InventoryBasedScreen {

	public QuaffScreen(Creature player) {
		super(player);
	}

	@Override
	protected String getVerb() {
		return "quaff";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return item.isPotion();
	}

	@Override
	protected Screen use(Item item) {
		player.quaff(item);
		return null;
	}

}
