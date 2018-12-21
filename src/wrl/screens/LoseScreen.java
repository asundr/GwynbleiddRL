package wrl.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import wrl.Creature;
import wrl.FieldOfView;
import wrl.Hazard;
import wrl.Point;
import wrl.World;

/**
 * Screen displayed when the player loses the game. Allows the player to start a new game.
 * @author Arun Sundaram
 *
 */
public class LoseScreen implements Screen {
	
	private Creature player;
	private World world;
	private Thread loopOnDeath;
	private boolean loop;
	private PlayScreen playScreen;
	private AsciiPanel terminal;
	
//	private int dx = 0;
//	private int dy = 0;
	private int x;
	private int y;
	private int left, top;
//	Point pLoc;
	
	public LoseScreen(World world, Creature player, PlayScreen playScreen) {
		this.world = world;
		this.player = player;
		this.playScreen = playScreen;
		this.terminal = playScreen.terminal();
		x = left + world.width()/2;
		y = top + world.height()/2;
		left = playScreen.getScrollX(x);
		top = playScreen.getScrollY(y);
		
		new Hazard(world, "death view", (char)0, null, null, 100) {
			int radius  = player.visionRadius();
			FieldOfView f = new FieldOfView(world);
			public FieldOfView updateFOV() {
				f.update(location(), radius);
				return f;
			}
			public void start() { playScreen.addObserver(this); }
			public void end() {playScreen.removeObserver(this); }
			public boolean updatePending() { return false; }
		}.relocate(player.location());
		
		
		player.relocate(new Point());
		world.remove(player);
		player.modifyVisionRadius(-player.visionRadius());
		loop = true;
		playBackground();
	}
	
	/** Starts a new Thread to run updates independently. */
	private void playBackground() {
		loopOnDeath = new Thread() {
			public void run() {
				while (loop) {
					world.updateEachOnce();
					displayOutput(terminal);
					terminal.repaint();
					try {
						sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		loopOnDeath.start();
	}
	/** Modifies the current screen X position if valid. */
	protected void modifyX(int amount) {
		int sx = playScreen.getScrollX(x + amount);
		if (left != sx) {
			left = sx;
			x += amount;
		}
	}
	
	/** Modifies the current screen Y position if valid. */
	protected void modifyY(int amount) {
		int sy = playScreen.getScrollY(y + amount);
		if ( top != sy ) {
			top = sy;
			y += amount;
		}
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		playScreen.displayOutput(terminal, left, top);
		
		terminal.write("You were killed by " + player.causeOfDeath() + ".", 1, 1);
		terminal.writeCenter("-- press [enter] to restart --", 19);
		terminal.writeCenter("-- press [?] in game for help --", 21);

	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		
		switch(key.getKeyCode()) {
		case KeyEvent.VK_LEFT: modifyX(-1); break;
		case KeyEvent.VK_RIGHT: modifyX(1); break;
		case KeyEvent.VK_UP: modifyY(-1); break;
		case KeyEvent.VK_DOWN: modifyY(1); break;
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_PAGE_DOWN:
		case KeyEvent.VK_HOME:
		case KeyEvent.VK_END: playScreen.respondToUserInput(key); break;
		case KeyEvent.VK_ENTER: 
			loop = false;
			return new PlayScreen();
		}
		
		switch(key.getKeyChar()) {
		case '+': case'-':
			playScreen.respondToUserInput(key); break;
		}
		return this;
	}

}
