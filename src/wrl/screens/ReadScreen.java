package wrl.screens;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import wrl.Creature;
import wrl.Item;

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
	protected ArrayList<String> getList(){
		ArrayList<String> list = super.getList();
		Item spellbook = player.knownSpells();
		if (spellbook != null) {
			String line = "1" + " - " + spellbook.glyph() + " " +player.nameOf(spellbook);
			list.add(0, line);
		}
		return list;
	}
	
	@Override
	public Screen respondToUserInput(KeyEvent key) {
		char c = key.getKeyChar();
		if (c == '1' && player.knownSpells() != null)
			return use(player.knownSpells());
		else
			return super.respondToUserInput(key);
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
