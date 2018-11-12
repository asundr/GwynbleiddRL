package rltut.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.List;

import rltut.Creature;
import rltut.LevelUpController;
import asciiPanel.AsciiPanel;

/**
 * This screen displays the level-up bonus option of the player and allows they to select one for increase in level.
 * @author Arun Sundaram
 *
 */
public class LevelUpScreen implements Screen {
	
	private LevelUpController controller;
	private Creature player;
	private int picks;
	
	/** 
	 * @param player - The player {@linkplain Creature}.
	 * @param picks - The number of options the player can pick
	 */
	public LevelUpScreen(Creature player, int picks) {
		this.controller = new LevelUpController();
		this.player = player;
		this.picks = picks;
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		List<String> options = controller.getLevelUpOptions();
		String title = "Choose a level up bonus";
		int width = 0;
		for (String s : options)
			if (s.length() > width)
				width = s.length();
		width += 4;
		int y = 5;
		
		int top = y;

		terminal.clear(' ', 4, y, width + 2, options.size() + 2);
		terminal.write(title, 5, y++);
		y++;

		for (int i = 0; i < options.size(); i++){
			terminal.write(String.format("[%d] %s", i+1, options.get(i)), 5, y++);
		}
		
		makeBorder(terminal, Color.DARK_GRAY, 3, top-1, width + 4, options.size() + 4);
		for (int i=4; i<width+6; i++) terminal.write((char)196, i, top+1, Color.DARK_GRAY);
		terminal.write((char)199, 3, top+1, Color.DARK_GRAY);
		terminal.write((char)182, width+6, top+1, Color.DARK_GRAY);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		char in = key.getKeyChar();
		if (!Character.isDigit(in))
			return this;
		
		int option = Integer.parseInt("" + in);
		if (option < 1 || option > controller.optionCount())
			return this;
		
		controller.getLevelUpOption(option-1).invoke(player);
		
		if (--picks < 1)
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
