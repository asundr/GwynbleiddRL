package wrl;

import java.applet.Applet;
import asciiPanel.AsciiPanel;
import wrl.screens.Screen;
import wrl.screens.StartScreen;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AppletMain extends Applet implements KeyListener {
	private static final long serialVersionUID = 2560255315130084198L;
	
	private AsciiPanel terminal;
	private Screen screen;
	
	public AppletMain() {
		super();
		terminal = new AsciiPanel(80,30);
		add(terminal);
		screen = new StartScreen();
		addKeyListener(this);
		repaint();
	}
	
	public void init() {
		super.init();
		this.setSize(terminal.getWidth() + 10, terminal.getHeight() + 10);
		setFocusable(true);
		requestFocusInWindow();
	}
	
	public void repaint() {
		super.repaint();
		terminal.clear();
		screen.displayOutput(terminal);
		terminal.repaint();
	}
	
	public void keyPressed(KeyEvent e) {
		screen = screen.respondToUserInput(e);
		repaint();
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) { }
	
}
