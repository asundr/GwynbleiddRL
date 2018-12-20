package wrl.screens;

import java.util.ArrayList;

import wrl.Creature;
import wrl.Item;
import wrl.Potion;

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
	
	protected ArrayList<String> getList(){
		ArrayList<String> lines = new ArrayList<String>();
		Item[] items =  player.inventory().items();
		for (int i=0; i<items.length; i++) {
			if (items[i] == null || !isAcceptable(items[i]))
				continue;
			String line = letters.charAt(i) + " - " + items[i].glyph() + " " +player.nameOf(items[i]) + " (" + ((Potion)items[i]).toxicity() + ")";
			if (items[i] == player.armor() || items[i] == player.meleeWeapon() || items[i] == player.rangedWeapon())
				line += " (equipped)";
			lines.add(line);
		}
		return lines;
	}

}
