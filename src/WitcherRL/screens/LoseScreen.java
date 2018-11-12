package rltut.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import rltut.Creature;

/**
 * Screen displayed when the player loses the game. Allows the player to start a new game.
 * @author Arun Sundaram
 *
 */
public class LoseScreen implements Screen {
	
	private Creature player;
	
	public LoseScreen(Creature player) {
		this.player = player;
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("You were killed by " + player.causeOfDeath() + ".", 1, 1);
		terminal.writeCenter("-- press [enter] to restart --", 19);
		terminal.writeCenter("-- press [?] in game for help --", 21);

	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
	}

}
