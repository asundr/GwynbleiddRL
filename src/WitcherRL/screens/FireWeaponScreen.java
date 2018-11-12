package rltut.screens;

import rltut.Creature;
import rltut.Line;
import rltut.Point;

/**
 * This class extends {@linkplain TargetBasedScreen}. It allows the player to fire a ranged weapon at a target.
 * @author Arun Sundaram
 *
 */
public class FireWeaponScreen extends TargetBasedScreen {

	/** @see TargetBasedScreen#TargetBasedScreen(Creature, String, int, int)  */
	public FireWeaponScreen(Creature player, int sx, int sy) {
		super(player, "Fire " + player.nameOf(player.rangedWeapon()) + " at?", sx, sy);
	}
	
	public boolean isAcceptable(int dx, int dy) {
		if (!super.isAcceptable(dx, dy))
			return false;
		
		Point target = new Point(dx + player.x(), dy + player.y(), player.z());
		if (!player.canSee(target))
			return false;
		
		for (Point p : new Line(player.x(), player.y(), target.x, target.y, player.z())) {
			if (!player.realTile(p).isGround()) {
				return false;
			}
		}
		return true;
	}
	
	public void selectWorldCoordinate(int wx, int wy, int screenX, int screenY) {
		Creature other = player.creature( new Point(wx, wy, player.z()) );
		if (other == null) {
			player.notify("There is no one to fire at");
		} else {
			player.rangedAttack(other);
		}
	}

}
