package wrl.screens;

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
		terminal.write("Alpha", 1 ,0);
		terminal.writeCenter("-- press [enter] to start --", 24);
		terminal.writeCenter("-- press [?] or [ESC] in game for help --", 26);
		writeTitle(terminal);
	}
	
	public void writeTitle(AsciiPanel terminal) {
		int x = 12;
		int y = 10;
		
		terminal.write(" __        __    _       _              ______ _      ", x, y++);
		terminal.write("/ /   /\\   \\ \\ _| |     | |             | ___ \\ |     ", x, y++);
		terminal.write("\\ \\  /  \\  / /(_) |_ ___| |__   ___ _ __| |_/ / |     ", x, y++);
		terminal.write(" \\ \\/ /\\  / / | | __/ __| '_ \\ / _ \\ '__|    /| |     ", x, y++);
		terminal.write("  \\  /  \\  /  | | || (__| | | |  __/ |  | |\\ \\| |____ ", x, y++);
		terminal.write("   \\/    \\/   |_|\\__\\___|_| |_|\\___|_|  \\_| \\_\\_____/ ", x, y++);
		
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
	}
		
}

/*

 __        __    _       _              ______ _      
/ /   /\   \ \ _| |     | |             | ___ \ |     
\ \  /  \  / /(_) |_ ___| |__   ___ _ __| |_/ / |     
 \ \/ /\  / / | | __/ __| '_ \ / _ \ '__|    /| |     
  \  /  \  /  | | || (__| | | |  __/ |  | |\ \| |____ 
   \/    \/   |_|\__\___|_| |_|\___|_|  \_| \_\_____/ 
   
 __        __    _       _              ______ _      
/ /        \ \ _| |     | |             | ___ \ |     
\ \   /\   / /(_) |_ ___| |__   ___ _ __| |_/ / |     
 \ \_/  \_/ / | | __/ __| '_ \ / _ \ '__|    /| |     
  \   /\   /  | | || (__| | | |  __/ |  | |\ \| |____ 
   \_/  \_/   |_|\__\___|_| |_|\___|_|  \_| \_\_____/ 
                    
                 
*/