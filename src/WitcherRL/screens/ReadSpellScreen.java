package rltut.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import asciiPanel.AsciiPanel;
import rltut.Creature;
import rltut.Item;
import rltut.Spell;

/**
 * This screen displays the {@linkplain Spell}s that an {@linkplain Item} is capable of casting and lets the player select one to cast.
 * @see CastSpellScreen
 * @see ReadScreen
 * @author Arun Sundaram
 *
 */
public class ReadSpellScreen implements Screen {
	
	protected Creature player;
	private String letters;
	private Item item;
	private int sx;
	private int sy;

	/**
	 * @param player - player reading the spellbook
	 * @param sx - distance of player from the left of the screen
	 * @param sy - distance of player from top of the screen
	 * @param item - The spellbook
	 */
	public ReadSpellScreen(Creature player, int sx, int sy, Item item) {
		this.player = player;
		this.letters = "abcdefghijklmnopqrstuvwxyz";
		this.item = item;
		this.sx = sx;
		this.sy = sy;
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		ArrayList<String> lines = getList();
		String title = "What spell would you like to use?";
		
		int width = title.length();
		for (String line : lines)
			if (line.length() > width)
				width = line.length();
		
		int y = 23 - lines.size();
		int x = 4;

		if (lines.size() > 0)
			terminal.clear(' ', x, y-2, width, lines.size() + 2);
		
		makeTitleBorder(terminal, Color.DARK_GRAY, x-1, y-3, width+2, lines.size()+4); 
		terminal.write(title, x, y-2);

		for (String line : lines){
		    terminal.write(line, x, y++);
		}
		terminal.repaint();
	}
	
	/** Returns an indexed list of the Item's spells names and their mana costs. */
	private ArrayList<String> getList() {
		ArrayList<String> lines = new ArrayList<String>();
		for(int i=0; i<item.writtenSpells().size(); i++) {
			Spell spell = item.writtenSpells().get(i);
			String line = letters.charAt(i) + " " + spell.name() + " (" + spell.manaCost() + ")";
			lines.add(line);
		}
		return lines;
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		char c = key.getKeyChar();
		if (letters.indexOf(c) > -1 && letters.indexOf(c) < item.writtenSpells().size()) {
			return use(item.writtenSpells().get(letters.indexOf(c)));
		} else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
			return null;
		} else {
			return this;
		}
	}
	
	/** Display the targeting screen for the selected {@linkplain Spell}. */
	protected Screen use(Spell spell) {
		return new CastSpellScreen(player, "", sx, sy, spell);
	}
	
	/** @see #makeBorder(AsciiPanel, Color, int, int, int, int) */
	protected void makeTitleBorder(AsciiPanel terminal, Color color,  int left, int top, int width, int height) {
		terminal.clear(' ', left+1, top+1, width-2, height-2);
		makeBorder(terminal, color, left, top, width, height);
		for (int i=left+1; i<left+width-1; i++)
			terminal.write((char) 196, i, top+2, color);
		terminal.write((char)199, left, top+2, color);
		terminal.write((char)182, left+width-1, top+2, color);
	}
	
	/** 
	 * Writes a border to the {@linkplain AsciiPanel}.
	 * @param terminal - output to write to
	 * @param color - {@linkplain Color} of the border
	 * @param left - the left-most char coordinate
	 * @param top - the highest char coordinate
	 * @param width - char width of the border
	 * @param height - char height of the border
	 */
	protected void makeBorder(AsciiPanel terminal, Color color,  int left, int top, int width, int height) {
		for (int i= left+1; i<left+width-1; i++) {
			terminal.write((char)205, i, top, color);
			terminal.write((char)205, i, top+height-1, color);
		}
		for (int i=top+1; i<top+height-1; i++) {
			terminal.write((char)186, left, i, color);
			terminal.write((char)186, left+width-1, i, color);
		}
		terminal.write((char)201, left, top, color);
		terminal.write((char)200, left, top+height-1, color);
		terminal.write((char)187, left+width-1, top, color);
		terminal.write((char)188, left+width-1, top+height-1, color);
	}

}
