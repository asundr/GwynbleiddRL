package rltut.screens;

import rltut.Creature;
import rltut.Item;

/**
 * This class extends {@linkplain InventoryBasedScreen}. It allows the player to equip and unequip weapons and armor.
 * @author Arun Sundaram
 *
 */
public class EquipScreen extends InventoryBasedScreen {
	
	public EquipScreen(Creature player) {
		super(player);
	}

	@Override
	protected String getVerb() {
		return "wear or wield";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return item.attackValue() > 0 || item.defenseValue() > 0;
	}

	@Override
	protected Screen use(Item item) {
		if (player.isEquipped(item))
			player.unequip(item);
		else
			player.equip(item);
		return null;
	}
	
}
