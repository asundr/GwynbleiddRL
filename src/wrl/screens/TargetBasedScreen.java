package wrl.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import wrl.Creature;
import wrl.Line;
import wrl.Point;

public class TargetBasedScreen implements Screen {
	
	private int screenWidth = 80;
	private int screenHeight = 21;
	
	protected Creature player;
	protected String caption;
	/** Distance of player from left of screen. */
	protected int sx;
	/** Distance of player from top of screen. */
	protected int sy;
	/** Horizontal displacement from player to target. */
	protected int x;
	/** Vertical displacement from player to target. */
	protected int y;
	
	/**
	 * @param player - Entity from which this targets from
	 * @param caption - text to display
	 * @param sx - distance of player from the left of the screen
	 * @param sy - distance of player from top of the screen
	 */
	public TargetBasedScreen(Creature player, String caption, int sx, int sy) {
		this.player = player;
		this.caption = caption;
		this.sx = sx;
		this.sy = sy;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		if (x==0 && y==0) {
			terminal.write('*', x, y, AsciiPanel.brightMagenta);
			return;
		}
		for( Point p : new Line(sx, sy, sx + x, sy + y)) {
			if (!isInScreen(x, y) || new Point(sx, sy, 0).equals(p))
				continue;
			terminal.write('*', p.x, p.y, AsciiPanel.brightMagenta);
		}
		terminal.write(caption, 0, 21);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		int px = x;
		int py = y;
		
		switch (key.getKeyCode()){
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_H: x--; break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_L: x++; break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_J: y--; break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_K: y++; break;
		case KeyEvent.VK_Y: x--; y--; break;
		case KeyEvent.VK_U: x++; y--; break;
		case KeyEvent.VK_B: x--; y++; break;
		case KeyEvent.VK_N: x++; y++; break;
		case KeyEvent.VK_ENTER: selectWorldCoordinate(player.x() + x, player.y() + y, sx + x, sy + y); return null;
		case KeyEvent.VK_ESCAPE: return null;
		}
		
		if (!isAcceptable(x, y)) {
		    x = px;
		    y = py;
		}
		
		enterWorldCoordinate(player.x() + x, player.y() + y, sx + x, sy + y);
		return this;
	}
	
	/**
	 * Returns {@code true} if {@code dx} and {@code dy} represent a valid target within the region's bounds.
	 * @param dx = horizontal displacement from player to target
	 * @param dy = vertical displacement from player to target */
	protected boolean isAcceptable(int dx, int dy) {
		return isInScreen(dx, dy);
	}
	
	/**
	 * Returns {@code true} if target is within the bounds displayed by the screen.
	 * @param dx - horizontal displacement from player to target
	 * @param dy - vertical displacement from player to target
	 */
	protected boolean isInScreen(int dx, int dy) {
		dx += sx;
		dy += sy;
		return dx >=0 && dx < screenWidth && dy >= 0 && dy < screenHeight;
	}
	
	/** Called when the targeted coordinate changes.
	 * @param wx - horizontal world coordinate of target
	 * @param wy - vertical world coordinate of target
	 * @param screenX - distance from left edge of screen
	 * @param screenY - distance from top edge of screen
	 */
	public void enterWorldCoordinate(int wx, int wy, int screenX, int screenY) { }
	
	/** Called when player confirms action.
	 * @param wx - horizontal world coordinate of target
	 * @param wy - vertical world coordinate of target
	 * @param screenX - distance from left edge of screen
	 * @param screenY - distance from top edge of screen
	 */
	public void selectWorldCoordinate(int wx, int wy, int screenX, int screenY) { }
	
}
