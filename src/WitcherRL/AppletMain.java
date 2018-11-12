package rltut;

import java.applet.Applet;
import asciiPanel.AsciiPanel;

import rltut.screens.Screen;
import rltut.screens.StartScreen;

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
		this.setSize(terminal.getWidth() + 20, terminal.getHeight() + 20);
	}
	
	public void repaint() {
		terminal.clear();
		screen.displayOutput(terminal);
		super.repaint();
	}
	
	public void keyPressed(KeyEvent e) {
		screen.respondToUserInput(e);
		repaint();
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) { }
	
}
