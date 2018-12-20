package wrl.screens;

import wrl.Creature;
import wrl.Item;
import wrl.Line;
import wrl.Point;

/**
 * This class extends {@linkplain TargetBasedScreen}. It allows the player to select a target to throw an {@linkplain Item} at.
 * @author Arun Sundaram
 *
 */
public class ThrowAtScreen extends TargetBasedScreen {
	
	Item item;

	/**
	 * @see TargetBasedScreen#TargetBasedScreen(Creature, String, int, int)
	 */
	public ThrowAtScreen(Creature player, int sx, int sy, Item item) {
		super(player, "Throw " + player.nameOf(item) + " at?", sx, sy);
		this.item = item;
	}
	
	public boolean isAcceptable(int dx, int dy) {
		if (!super.isAcceptable(dx, dy))
			return false;
		Point location = new Point(dx + player.x(), dy + player.y(), player.z());
		if (!player.canSee(location))
			return false;
		
		for (Point p : new Line(player.x(), player.y(), location.x, location.y)) {
			if (!player.realTile( new Point(p.x, p.y, player.z()) ).isGround()) {
				return false;
			}
		}
		return true;
	}
	
	public void selectWorldCoordinate(int wx, int wy, int screenX, int screenY) {
		player.throwItem(item, new Point(wx, wy, player.z()));
	}

}
