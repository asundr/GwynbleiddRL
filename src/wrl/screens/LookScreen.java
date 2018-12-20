package wrl.screens;

import wrl.Creature;
import wrl.Grammar;
import wrl.Item;
import wrl.Point;
import wrl.Tile;

/**
 * This class extends {@linkplain TargetBasedScreen}. It allows the player to look and examine {@linkplain Tile}s and entities.
 *
 */
public class LookScreen extends TargetBasedScreen {
	
	/** @see TargetBasedScreen#TargetBasedScreen(Creature, String, int, int) */
	public LookScreen (Creature player, String caption, int sx, int sy) {
		super(player, caption, sx, sy);
	}
	
	public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
		Point location = new Point(x, y, player.z());
		Creature creature = player.creature(location);
		
		if (creature != null) {
			caption = creature.glyph() + " " + creature.name() + " " + creature.details();
			return;
		}
		
		Tile tile = player.tile(location);
		Item item = player.item(location);
		if (item != null) {
			caption = item. glyph()  + " " + Grammar.article(player.nameOf(item)).toUpperCase() + " " + player.nameOf(item) + (tile==Tile.WATER ? " covered by shallow water" : "") + " " + item.details() ;
			return;
		}
		
		caption = tile.glyph() + " " + tile.details();
	}

}
