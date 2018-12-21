package wrl.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

/**
 * This screen shows the game controls to the player.
 * @author Arun Sundaram
 *
 */
public class HelpScreen implements Screen {

	private int page = 0;
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		switch (page) {
		case 0: showControls(terminal); break;
		case 1: showSymbols(terminal); break;
		}
		
	}
	
	public void showControls(AsciiPanel terminal) {
		terminal.clear();
		terminal.writeCenter("Controls", 1);
		terminal.write("Move deeper into the caves to find the Monster Trophy then get to", 1, 3);
		terminal.write("the surface to win. Use what you find to avoid dying.", 1, 4);
		
		int y = 6;
		
		terminal.write("To melee attack, walk into another creature.", 1, y++);
		y++;
		terminal.write("[g] or [,] to pick up", 2, y++);
		terminal.write("[d] to drop", 2, y++);
		terminal.write("[e] to eat", 2, y++);
		terminal.write("[a] to auto-eat", 2, y++);
		terminal.write("[q] to quaff", 2, y++);
		terminal.write("[z] to make potions", 2, y++);
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
		
		
		terminal.writeCenter("                        -- press [ESC] to continue --          Symbols >>> ", 27);
		terminal.repaint();
	}
	
	public void showSymbols(AsciiPanel terminal) {
		terminal.clear();
		terminal.writeCenter("Symbols", 1);
		
		int y = 3;
		terminal.write("@ player    < Stars up     > Stairs down     [ armor     ) weapon     , rock", 2, y++);
		y++;
		terminal.write("  [b]  bats will ignore you but can bump into you.", 2, y++);
		terminal.write("  [g]  goblins can use weapons and armor and throw items.", 2, y++);
		terminal.write("[d][D] drowners will not stray too far from water.", 2, y++);
		terminal.write("[r][R] rotfiends can spit poison and will expplode when they die.", 2, y++);
		terminal.write("[v][V] vampires can turn invisible and will quickly evade around you.", 2, y++);
		terminal.write("[t][T] trolls are strong and slow but quickly charge in lines and throw rocks", 2, y++);
		y++;
		terminal.write("These monsters can drop items useful for alchemy.", 2, y++);
		y++;
		terminal.write(String.format("[%c][%c][%c][%c][%c][%c] Plants that spread and are used for alchemy.", (char)231, (char)244, (char)157, (char)233, (char)235, (char)234), 2, y++);
		y++;
		terminal.write("An alchemy recipe combines three of these ingredients to make a potion:", 2, y++);
		y++;
		terminal.write("Black blood: hurts [v][r][d] when they hit you         Cat: Improves vision.", 2, y++);
		terminal.write("White Rafford's Decotion: heals immediately.       Swallow: heals over time.", 2, y++);
		terminal.write("Golden Oriole: cures and prevents poision.      Full Moon: increases max HP.", 2, y++);
		terminal.write("Blizzard: Increaes speed afer a kill.         Thunderbolt: increases damage.", 2, y++);
		terminal.write("Tawny Owl: Increases MP regen.             White Honey: dispels all potions.", 2, y++);
		terminal.write("Petri's Philter: increase sign intensity [not implemented].", 2, y++);
		
		
		terminal.writeCenter(" <<< Controls           -- press [ESC] to continue --                      ", 27);
		terminal.repaint();
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch(key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: 
			return null;
		case KeyEvent.VK_LEFT:
			page = 0; break;
		case KeyEvent.VK_RIGHT:
			page = 1; break;
		}
		return this;
	}

}
