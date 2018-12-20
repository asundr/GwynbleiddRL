package rltut.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

/**
 * This screen shows the game controls to the player.
 * @author Arun Sundaram
 *
 */
public class HelpScreen implements Screen {

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.clear();
		terminal.writeCenter("Help", 1);
		terminal.write("Pre-Alpha: Move deeper into the games to find the Monster Trophy then get to", 1, 3);
		terminal.write("the surface to win. Use what you find to avoid dying.", 1, 4);
		
		int y = 6;
		terminal.write("[g] or [,] to pick up", 2, y++);
		terminal.write("[d] to drop", 2, y++);
		terminal.write("[e] to eat", 2, y++);
		terminal.write("[a] to auto-eat", 2, y++);
		terminal.write("[q] to quaff", 2, y++);
		terminal.write("[w] to wear or wield", 2, y++);
		terminal.write("[t] to throw", 2, y++);
		terminal.write("[f] to fire", 2, y++);
		terminal.write("[r] to read", 2, y++);
		terminal.write("[?] for help", 2, y++);
		terminal.write("[x] to examine your items", 2, y++);
		terminal.write("[;] to look around", 2, y++);
		y++;
		terminal.write("[Home] [PgUp] [-] to scroll texbox up", 2, y++);
		terminal.write("[End]  [PgDn] [+] to scroll texbox down", 2, y++);
		
		
		terminal.writeCenter("-- press any key to continue --", 27);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return null;
	}

}
