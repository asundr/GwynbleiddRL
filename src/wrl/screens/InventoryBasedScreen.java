package wrl.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import asciiPanel.AsciiPanel;
import wrl.Creature;
import wrl.Item;

/**
 * This abstract class can be extended by screens that ask the player to select and use an {@linkplain Item} from a filtered {@linkplain Inventory}.
 */
public abstract class InventoryBasedScreen implements Screen {
	
	protected Creature player;
	protected String letters;
	
	/** Returns the verb used to describe the action of this {@linkplain InventoryBasedScreen} */
	protected abstract String getVerb();
	
	/** Returns {@code true} if this Screen should list the {@linkplain Item}. */
	protected abstract boolean isAcceptable(Item item);
	
	/** Performs an action with the selected {@linkplain Item}. */
	protected abstract Screen use(Item item);
	
	public InventoryBasedScreen(Creature player) {
		this.player = player;
		this.letters = "abcdefghijklmnopqrstuvwxyz";
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		ArrayList<String> lines = getList();
		int y = 23 - lines.size();
		int x = 4;
		
		int width = 20;
		for (String line : lines)
			if (line.length() > width)
				width = line.length();
		
		if (lines.size()>0)
			makeBorder(terminal, Color.DARK_GRAY, x-2, y-1, width+3, lines.size()+2);
		
		if (lines.size() > 0)
			terminal.clear(' ', x-1, y, width+1, lines.size());
		for (String line : lines)
			terminal.write(line, x, y++);
		
//		terminal.clear(' ', 0, 23, 80, 1);
		terminal.write("What would you like to " + getVerb() + "?", 2, 3);
		terminal.repaint();
	}
	
	/** Returns an {@linkplain ArrayList} of {@linkplain Item} names prefixed by a letter index. Equipped Items are labeled. */
	protected ArrayList<String> getList(){
		ArrayList<String> lines = new ArrayList<String>();
		Item[] items =  player.inventory().items();
		for (int i=0; i<items.length; i++) {
			if (items[i] == null || !isAcceptable(items[i]))
				continue;
			String line = letters.charAt(i) + " - " + items[i].glyph() + " " +player.nameOf(items[i]);
			if (items[i] == player.armor() || items[i] == player.meleeWeapon() || items[i] == player.rangedWeapon())
				line += " (equipped)";
			lines.add(line);
		}
		return lines;
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		char c = key.getKeyChar();
		int index = letters.indexOf(c);
		Item[] items = player.inventory().items();		
		if (index > -1 
				&& items.length > index
				&& items[index] != null
				&& isAcceptable(items[index]) )
			return use(items[index]);
		else if (key.getKeyCode() == KeyEvent.VK_ESCAPE)
			return null;
		else
			return this;
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
	public void makeBorder(AsciiPanel terminal, Color color,  int left, int top, int width, int height) {
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
