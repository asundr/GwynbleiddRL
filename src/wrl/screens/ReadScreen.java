package rltut.screens;

import rltut.Creature;
import rltut.Item;

/**
 * This class extends {@linkplain InventoryBasedScreen}. Allows player to select a spellbook to read.
 * @author Arun Sundaram
 * @see ReadSpellScreen
 * @see CastSpellScreen
 *
 */
public class ReadScreen extends InventoryBasedScreen {
	
	private int sx;
	private int sy;

	/**
	 * @param player - {@linkplain Creature} that is accessing the menu.
	 * @param sx - distance of player from the left of the screen
	 * @param sy - distance of player from the top of the screen
	 */
	public ReadScreen(Creature player, int sx, int sy) {
		super(player);
		this.sx = sx;
		this.sy = sy;
	}

	@Override
	protected String getVerb() {
		return "read";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return !item.writtenSpells().isEmpty();
	}

	@Override
	protected Screen use(Item item) {
		return new ReadSpellScreen(player, sx, sy, item);
	}

}
