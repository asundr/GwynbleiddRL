package wrl.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asciiPanel.AsciiPanel;
import wrl.Creature;
import wrl.Item;
import wrl.Recipe;

public class AlchemyScreen implements Screen {

	protected Creature player;
	protected String letters = "abcdefghijklmnopqrstuvwxyz";
	protected ArrayList<Recipe> recipes;
	protected Item[][] found;
	
	public AlchemyScreen(Creature player) {
		this.player = player;
		this.recipes = ((wrl.PlayerAI)player.ai()).recipes();
		this.found = new Item[recipes.size()][];
		for (int i=0; i<recipes.size(); i++)
			found[i] = recipes.get(i).findIngredients(player.inventory());
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		List<String> lines = getList();
		int y = 23 - lines.size();
		int x = 4;
		
		int width = 20;
		for (String line : lines)
			if (line.length() > width)
				width = line.length();
		width = Math.min(75, width +10);
		
		if (recipes.size()>0)
			makeBorder(terminal, Color.DARK_GRAY, x-2, y-1, width+3, lines.size()+2);
		
		if (lines.size() > 0)
			terminal.clear(' ', x-1, y, width+1, lines.size());
		for (int i = 0; i< lines.size(); i++) {
			String line = lines.get(i);
			
			char a = symbol.get( recipes.get(i).ingredients().get(0) );
			char b = symbol.get( recipes.get(i).ingredients().get(1) );
			char c = symbol.get( recipes.get(i).ingredients().get(2) );
			if (line.length() > 65)
				line = line.substring(0,  65);
			if (canUse(i)) {
				terminal.write(line, x, y, AsciiPanel.brightGreen);
				terminal.write("[" + a + ", " + b + ", " + c + "]", width-6, y, AsciiPanel.brightGreen);
			} else {
				terminal.write(line.charAt(0) + " ", x, y, Color.DARK_GRAY);
				terminal.write(line.substring(2), x + 2, y);
				int dx = width - 11;
				terminal.write(" [", x + dx, y); dx +=2;
				terminal.write (a, x + dx, y,  (found[i][0]==null) ? Color.DARK_GRAY : AsciiPanel.white); dx++;
				terminal.write(", ", x + dx, y); dx += 2;
				terminal.write (b, x + dx, y,  (found[i][1]==null) ? Color.DARK_GRAY : AsciiPanel.white); dx++;
				terminal.write(", ", x + dx, y); dx += 2;
				terminal.write (c, x + dx, y,  (found[i][2]==null) ? Color.DARK_GRAY : AsciiPanel.white); dx++;
				terminal.write("]", x + dx, y);
			}
			y++;
		}
		
		terminal.write("What would you like to make?", 2, 3);
		terminal.repaint();
	}
	
	/** Returns a List of string to print to the terminal. */
	protected List<String> getList() {
		List<String> lines = new ArrayList<String>();
		for (int i=0; i<recipes.size(); i++) {
			String line = "" + letters.charAt(i) + " ";
			line += recipes.get(i).name() + " ";
			lines.add(line);
		}
		return lines;
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		char c = key.getKeyChar();
		int index = letters.indexOf(c);
		if (index > -1 	&& recipes.size() > index  )
			return use(index);
		else if (key.getKeyCode() == KeyEvent.VK_ESCAPE)
			return null;
		else
			return this;
	}
	
	/** Returns {@code true} if ingredients from recipe match. */
	public boolean canUse(int index) {
		for (Item item : found[index])
			if (item == null)
				return false;
		return true;
	}
	
	/** Attempts to create the product of this recipe. */
	public Screen use(int index) {
		Item[] invItems = found[index];
		if (!canUse(index)) {
			player.notify("You're missing some ingredients for " + recipes.get(index).name() + ".");
			return this;
		} else {
			for (Item item : invItems)
				player.inventory().remove(item);
			recipes.get(index).create().relocate(player);
			player.doAction("make a " + recipes.get(index).name() + "!");
		}
		return null;	
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
	
	
	protected static final Map<String, Character> symbol;
	static {
		Map<String, Character> map = new HashMap<String, Character>();
		map.put("sewant mushroom", (char)231);
		map.put("wolfsbane flower", (char)244 );
		map.put("hornwort", (char)233 );
		map.put("white myrtle flower", (char)157 );
		map.put("crow's eye root", (char)235 );
		map.put("blowball flower", (char)234 );
		map.put("drowner tongue", (char)251 );
		map.put("troll liver", (char)229 );
		map.put("necrophage blood vial", (char)154 );
		map.put("vampire fang", '!' );
		symbol = map;
	}

}
