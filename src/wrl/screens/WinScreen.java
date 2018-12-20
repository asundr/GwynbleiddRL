package wrl.screens;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;

/**
 * Screen that displays after the player wins the game.
 *
 */
public class WinScreen implements Screen {

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("You Win!", 1, 1);
		terminal.writeCenter("-- press [enter] to restart --", 22);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
	}

}
