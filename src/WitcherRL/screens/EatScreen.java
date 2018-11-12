package rltut.screens;

import rltut.Creature;
import rltut.Item;

/**
 * This class extends {@linkplain InventoryBasedScreen}. It allows the player to eat edible items in their inventory.
 *
 */
public class EatScreen extends InventoryBasedScreen {
	
	public EatScreen(Creature player) {
		super(player);
	}
	
	protected String getVerb() {
		return "eat";
	}
	
	protected boolean isAcceptable(Item item) {
		return item.foodValue() != 0;
	}
	
	protected Screen use(Item item) {
		player.eat(item);
		return null;
	}

}
