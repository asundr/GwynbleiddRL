package rltut.screens;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;

/**
 * This is the first Screen displayed when the game is launched. It allows the player to start the game.
 * @author Arun Sundaram
 *
 */
public class StartScreen implements Screen{

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("Roguelike Pre-Alpha", 1 ,1);
		terminal.writeCenter("-- press [enter] to start --", 20);
		terminal.writeCenter("-- press [?] in game for help --", 22);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
	}
		
}
