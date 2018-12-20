package wrl.screens;


import wrl.Creature;
import wrl.Grammar;
import wrl.Item;

/**
 * This class extends {@linkplain InventoryBasedScreen}. It allows the player to look at and describe {@linkplain Item}s in their {@linkplain Inventory}.
 * @author Arun Sundaram
 *
 */
public class ExamineScreen extends InventoryBasedScreen {

	public ExamineScreen(Creature player) {
		super(player);
	}

	@Override
	protected String getVerb() {
		return "examine";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return true;
	}

	@Override
	protected Screen use(Item item) {
		player.notify("It's " +Grammar.article(player.nameOf(item))+ " " +player.nameOf(item)+ ". " + item.details());
		return null;
	}

}
