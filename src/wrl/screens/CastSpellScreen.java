package wrl.screens;

import java.awt.Color;

import asciiPanel.AsciiPanel;
import wrl.Creature;
import wrl.Line;
import wrl.Point;
import wrl.Spell;
import wrl.Splash;

/**
 * This class extends {@linkplain TargetBasedScreen}. It allows the player to target {@linkplain Spell}s.
 * @author Arun Sundaram
 *
 */
public class CastSpellScreen extends TargetBasedScreen {

	private Spell spell;
	private Splash splash;
	
	/**
	 * @param player - Entity from which this targets from
	 * @param caption - text to display
	 * @param sx - distance of player from the left of the screen
	 * @param sy - distance of player from top of the screen
	 * @param spell - spell to be cast
	 */
	public CastSpellScreen(Creature player, String caption, int sx, int sy, Spell spell) {
		super(player, caption, sx, sy);
		this.spell = spell;
	}
	
	//TODO cleanup
	@Override
	public void displayOutput(AsciiPanel terminal) {  
		super.displayOutput(terminal);
		Point target = player.location().add( new Point(x, y, 0));
		if(spell.isArea() && player.canSee(target)) {
			double direction = new Line(0, 0, x, y).radialAngle();
			splash = spell.areaOfEffect((int)direction);
			int top = sy - splash.size()/2 + (spell.delivery()==Spell.Delivery.RADIAL  ? 0 : y);
			int left = sx - splash.size()/2 + (spell.delivery()==Spell.Delivery.RADIAL  ? 0 : x);
			for (int i=0; i<splash.size(); i++) {
				for (int j=0; j < splash.size(); j++) {
					if (!isInScreen(top + i - sx, top + j - sy)) 
						continue;
					Point p = new Point(player.x() + left + i - sx, player.y() + top - sy + j, player.z());
					if (splash.get(i,j) && player.tile(p).isGround() && player.canSee(p)) {
						char c = (player.creature(p) == null) ? '*' : player.creature(p).glyph();
						Color col = i==splash.size()/2 && j==splash.size()/2 ? Color.MAGENTA : Color.RED;
						terminal.write(c, left + i, top+j, col);
					}
				}
			}
		}
	}
	
	@Override
	public void selectWorldCoordinate(int wx, int wy, int screenX, int screenY) {
		if(!canSelect(wx, wy))
			return;
		
		if (!spell.isArea()) {
			player.castSpell(spell, wx, wy);
			return;
		}

		if (spell.delivery()==Spell.Delivery.RADIAL)
			player.castSpell(spell, player.x(), player.y(), splash);
		else
			player.castSpell(spell, wx, wy, splash);

	}
	
	@Override
	public boolean isAcceptable(int dx, int dy) {
		Point location = player.location().add(new Point(dx, dy, 0));
		if (!isInScreen(dx, dy))
			return false;
		
		if (spell.delivery() == Spell.Delivery.SELF)
			return dx==0 && dy==0;
		else if (spell.delivery() == Spell.Delivery.RADIAL)
			return (dx==0 && dy==0) || player.location().neighbors8().contains(location); 
		else if (spell.delivery() == Spell.Delivery.AIMED) {
			return player.canSee(location);
		}
		
		return true;
	}
	
	/**
	 * Returns {@code true} if world coordinate is a valid target for this Spell.
	 * @param wx - horizontal world coordinate of target
	 * @param wy - vertical world coordinate of target
	 */
	public boolean canSelect(int wx, int wy) {
		if (spell.delivery() == Spell.Delivery.TARGET)
			return player.creature(new Point(wx, wy, player.z())) != null || player.canSee(new Point(wx, wy, player.z()));
		return true;
	}

}
