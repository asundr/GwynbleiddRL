package wrl.screens;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;

/**
 * This interface outlines basic input and output for screens.
 */
public interface Screen {
	
	/** Writes information from this screen to the {@linkplain AsciiPanel} to be displayed. */
	public void displayOutput(AsciiPanel terminal);
	
	/** Determines what the Screen should do in response to a user's {@linkplain KeyEvent}. */
	public Screen respondToUserInput(KeyEvent key);

}
