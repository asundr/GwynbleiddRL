package wrl.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;
import wrl.Creature;
import wrl.Effect;
import wrl.FieldOfView;
import wrl.Item;
import wrl.MessageHistory;
import wrl.ObserverFOV;
import wrl.Point;
import wrl.StuffFactory;
import wrl.Tile;
import wrl.World;
import wrl.WorldBuilder;

/**
 * This screen handles player input, world updates and display during gameplay. 
 * Player input is interpreted by {@linkplain #respondToUserInput(KeyEvent)} and then the game {@linkplain World} updates.
 * It also manages entities that contribute to the player's {@linkplain FieldOfView}.
 * When this class writes its display to {@linkplain AsciiPanal} it updates any active {@linkplain ObseverFOV}s.
 * @author Arun Sundaram
 *
 */
public class PlayScreen implements Screen {
	
	private World world;
	private int screenWidth;
	private int screenHeight;
	private int depth = 10;
	
	private AsciiPanel terminal;
	public AsciiPanel terminal() { return terminal; }
	
	private Creature player;
	
	private FieldOfView fov;
	
	private List<ObserverFOV> observers;
	/** Adds an  {@linkplain ObserverFOV} to be updated and displayed. */
	public void addObserver(ObserverFOV o) {
		observers.add(o);
	}
	/** Remove an {@linkplain ObserverFOV} from being updated and displayed. */
	public void removeObserver(ObserverFOV o) {
		observers.remove(o);
	}
	
	private MessageHistory messageHistory;
	private int historyIndex = 0;
	public int historyLines = 6;
	
	private Screen subscreen;
	
	
	public PlayScreen() {
		screenWidth = 80;
		screenHeight = 21;
		observers = new ArrayList<ObserverFOV>();
		messageHistory = new MessageHistory();
		createWorld();
		StuffFactory creatureFactory = new StuffFactory(world, this);
		createCreatures(creatureFactory);
	}
	
	/** Fills the {@linkplain World} with {@linkplain Item}s and {@linkplain Creature}s. */
	private void createCreatures(StuffFactory factory) {
		fov = new FieldOfView(world);
		factory.newVictoryItem(depth - 1);
		for (int z=0; z<depth; z++) {
			
			factory.randomArmor(z);
			factory.randomWeapon(z);

			for (int i=0; i< world.width()*world.height()/20; i++)
				factory.newRock(z);
			
//			for (int i=0; i<4; i++) {
				factory.newWolfsbane(z);
				factory.newHornwort(z);
				factory.newWhiteMyrtle(z);
				factory.newCrowsEye(z);
				factory.newBlowall(z);
				factory.newSewantMushroom(8, 0.02, z);
//			}
			
		}
		
		for (int i=0; i<10000; i++)
			world.singleUpdate(null);
		
		player = factory.newPlayer(messageHistory, fov, 0);
		player.notify("You enter the cave...");
//		player.equip(factory.newBlueMageSpellbook(0));
//		player.equip(factory.newWhiteMageSpellbook(0));
//		player.equip(factory.newPotionWhiteRafford(0));
//		player.equip(factory.newPotionSwallow(0));
//		player.equip(factory.newPotionGoldenOriole(0));
//		player.equip(factory.newPotionBlackBlood(0));
//		player.equip(factory.newPotionThunderbolt(0));
//		player.equip(factory.newPotoinBlizzard(0));
//		player.equip(factory.newPotionCat(0));
//		player.equip(factory.newPotionTawnyOwl(0));
//		player.equip(factory.newPotionFullMoon(0));
//		player.equip(factory.newPotionWhiteHoney(0));
		
//		for (int i=0; i<10; i++) player.equip(factory.newPotionOfPoison(0));
		
		
		for (int z=0; z<depth; z++) {
			for (int i=0; i<10; i++)
				factory.newBat(z);
			
//			for (int i=0; i< 3; i++)
//				factory.newZombie(z, player);
			
			if (Math.random() > 0.5)
				factory.newRockTroll(z, player);
			else
				factory.newIceTroll(z, player);
			
			if (Math.random() > 0.5)
				factory.newRotfiend(z, player);
			else
				factory.newDevourer(z, player);
			
			if (Math.random() > 0.5)
				factory.newAlp(z, player);
			else
				factory.newBruxa(z, player);
			
			for (int i=0; i<3; i++)
				factory.newDrowner(z, player);
			
			for (int i=0; i<1; i++) 
				factory.newDrownedDead(z,  player);
			
//			for (int i=0; i<3; i++)
//				factory.randomPotion(z);
			
			factory.newGoblin(z, player);
			
		}
//		for (int i=0; i<10; i++) factory.newBlueMageSpellbook(0);
//		boolean first = Math.random() < 0.5;
//		if (first) {
//			factory.newBlueMageSpellbook(  (int)(Math.random()*3)  );
//			factory.newWhiteMageSpellbook( (int)(Math.random()*3) + 3  );
//		} else {
//			factory.newBlueMageSpellbook(  (int)(Math.random()*3) + 3  );
//			factory.newWhiteMageSpellbook( (int)(Math.random()*3)  );
//		}
	}
	
	/** Constructs the {@linkplain World}.
	 * @see WorldBuilder */
	private void createWorld() {
		world = new WorldBuilder(90,31, depth)
				.makeCaves()
				.build();
	}
	
	/** Writes the lines stored in the {@linkplain MessageHistory} that are currently in focus. */
	private void displayMessages(AsciiPanel terminal) {
		List<String> visibleMessage = messageHistory.get(historyIndex, historyLines);
		int newMessages =  messageHistory.newMessages();
		int notifyWidth = 59;
		for (int i=0; i<visibleMessage.size(); i++) {
			if (visibleMessage.get(i).length() >= notifyWidth)
				visibleMessage.set(i, visibleMessage.get(i).substring(0, notifyWidth));
			boolean recent = historyIndex + visibleMessage.size() - i <= newMessages;
			Color color = recent ? AsciiPanel.white : Color.GRAY;		//AsciiPanel.brightWhite : AsciiPanel.white;
			terminal.write(visibleMessage.get(i), 20, 23+i, color);
		}
		
		if (messageHistory.size() > historyIndex + historyLines) {
			boolean bright = historyIndex + historyLines < newMessages;
			terminal.write((char)174, 76, 22, bright ? AsciiPanel.brightWhite : Color.DARK_GRAY);
		}
		
		if (historyIndex > 0) {
			boolean bright = newMessages > 0 && messageHistory.size() > historyLines && historyIndex > 0;
			terminal.write((char)175, 77, 22, bright ? AsciiPanel.brightWhite : Color.DARK_GRAY);
		}
	}
	
	/** Writes the border that separates regions of the PlayScreen to the {@linkplain AsciiPanel}. */
	public void displayBorder(AsciiPanel terminal) {
		for (int i=0; i<80; i++) {
			terminal.write((char)205, i, 22, Color.DARK_GRAY);
			terminal.write((char)205, i, 29, Color.DARK_GRAY);
		}
		terminal.write((char)203, 18, 22, Color.DARK_GRAY);
		for (int i=23; i<29; i++) {
			terminal.write((char)186, 18, i, Color.DARK_GRAY);
		}
		terminal.write((char)202, 18, 29, Color.DARK_GRAY);
	}
	
	/** Writes the player's current state to the {@linkplain AsciiPanel}. */
	private void displayPlayerStats(AsciiPanel terminal) {
		int y = 23;

//		terminal.write(hunger(), 1, y++, Color.ORANGE);
		terminal.write(String.format("hp %4d/%4d", player.hp(), player.maxHP()), 1, y++);
		terminal.write(String.format("mp %4d/%4d", player.mana(), player.maxMana()), 1, y++);
		terminal.write(String.format("tox %3d/%3d", player.toxicity(), player.maxToxicity()), 1, y++);
		if (player.armor() != null)
			terminal.write(player.armor().name(), 1, y++);
		if (player.meleeWeapon() != null)
			terminal.write(player.meleeWeapon().name(), 1, y++);
		if (player.rangedWeapon() != null)
			terminal.write(player.rangedWeapon().name(), 1, y++);
		
		int count = 0, x = 17;
		for (Effect e : player.effects()) {
			if (e.isPotionEffect() && e.color() != null) {
				terminal.write(""+(char)168, x-count/6, 23 + count%6, e.color());
				count++;
			}
		}
	}
	
	/** Returns the world position of the left of the screen. */
	public int getScrollX() {
//		return Math.max(0, Math.min(player.x() - screenWidth/2, world.width() - screenWidth));
		return getScrollX(player.x());
	}
	
	/** Returns the world position of the top of the screen. */
	public int getScrollY() {
//		return Math.max(0,	Math.min(player.y() - screenHeight/2, world.height() - screenHeight));
		return getScrollY(player.y());
	}
	
	public int getScrollX(int x) {
		return Math.max(0, Math.min(x - screenWidth/2, world.width() - screenWidth));
	}
	
	public int getScrollY(int y) {
		return Math.max(0,	Math.min(y - screenHeight/2, world.height() - screenHeight));
	}
	
	/**
	 * Updates active {@linkplain FieldOfView}s and writes the {@linkplain Tile}s and entities in the bounds of the screen to the {@linkplain AsciiPanel}.
	 * @param terminal - displaying class
	 * @param left - world coordinate of left of screen
	 * @param top - world coordinate of top of screen
	 * @param depth - world level
	 */
	private void displayTiles(AsciiPanel terminal, int left, int top, int depth) {
		fov.update(player.location(), player.visionRadius());
		for (ObserverFOV obs : observers) {
			if (obs.location().z == player.z())
				fov.addFOV(obs.updateFOV(), player.z());
//			fov.addFOV(obs.updateFOV(), obs.location().z);
		}
		Point location = new Point(-1, -1, depth);
		for(int x = 0; x < screenWidth; x++) {
			for (int y=0; y<screenHeight; y++) {
				
				location = new Point(x + left, y + top, location.z);
				
				if (player.canSee(location)) {
					terminal.write(world.glyph(location, player),x, y, fov.visibleColor(location, player), world.backgroundColor(location));
				} else if (player.canDetect(location)) {
					terminal.write(world.glyph(location, player), x, y, Color.darkGray);
				} else {
					terminal.write(fov.tile(location).glyph(), x, y, Color.darkGray.darker());
				}
			}
		}
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		if (this.terminal == null)
			this.terminal = terminal;
		int left = getScrollX();
		int top = getScrollY();
		displayTiles(terminal, left, top, player.z());
		displayPlayerStats(terminal);
	    displayBorder(terminal);
	    displayMessages(terminal);
	    
	    if (subscreen != null)
	    	subscreen.displayOutput(terminal);
	}
	
	/** Displays tiles for other screens. */
	public void displayOutput(AsciiPanel terminal, int left, int top) {
		displayTiles(terminal, left, top, player.z());
		displayPlayerStats(terminal);
	    displayBorder(terminal);
	    displayMessages(terminal);
	}
	
	/** Returns a short description of the player's hunger. */
	private String hunger() {
		if (player.food() < player.maxFood() * 0.1)
			return "Starving";
		else if (player.food() < player.maxFood() * 0.2)
			return "Hungry";
		else if (player.food() > player.maxFood() * 0.9)
			return "Stuffed";
		else if (player.food() > player.maxFood() * 0.8)
			return "Full";
		return "";
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		int ap = player.ap();
		int level = player.level();
		int invalidCount = 0;
		int invalidMax = 2;
		if (subscreen != null) {
			subscreen =  subscreen.respondToUserInput(key);
			invalidCount = invalidMax;
		} else {
			switch(key.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_H: player.moveBy(-1,0, 0); break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_L: player.moveBy(1,0, 0); break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_K: player.moveBy( 0,-1,0); break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_J: player.moveBy( 0, 1, 0); break;
			case KeyEvent.VK_Y: player.moveBy(-1,-1, 0); break;
			case KeyEvent.VK_U: player.moveBy( 1,-1, 0); break;
			case KeyEvent.VK_B: player.moveBy(-1, 1, 0); break;
			case KeyEvent.VK_N: player.moveBy( 1, 1, 0); break;
			case KeyEvent.VK_A: player.autoEat(); break;
			case KeyEvent.VK_D: subscreen = new DropScreen(player); break;
			case KeyEvent.VK_E: subscreen = new EatScreen(player); break;
			case KeyEvent.VK_Q: subscreen = new QuaffScreen(player); break;
			case KeyEvent.VK_W: subscreen = new EquipScreen(player); break;
			case KeyEvent.VK_X: subscreen = new ExamineScreen(player); break;
			case KeyEvent.VK_R: subscreen = new ReadScreen(player, player.x() - getScrollX(), player.y() - getScrollY()); break;
			case KeyEvent.VK_T: subscreen = new ThrowScreen(player, player.x() - getScrollX(), player.y() - getScrollY()); break;
			case KeyEvent.VK_Z: subscreen = new AlchemyScreen(player); break;
			case KeyEvent.VK_ESCAPE: subscreen = new HelpScreen(); break;
			case KeyEvent.VK_F: if (player.rangedWeapon() == null) {
									player.notify("You don't have a ranged weapon to shoot with");
									invalidCount = invalidMax;
								} else {
									subscreen = new FireWeaponScreen(player, player.x() - getScrollX(), player.y() - getScrollY());
								}
								break;
			case KeyEvent.VK_SEMICOLON: subscreen = new LookScreen(player, player.glyph() + " " + player.name() + " " + player.details(),  player.x() - getScrollX(), player.y() - getScrollY());
										break;
			case KeyEvent.VK_PAGE_UP: historyIndex = Math.min(historyIndex+historyLines, Math.max(messageHistory.size()-historyLines, 0));
										invalidCount=invalidMax; break;
			case KeyEvent.VK_PAGE_DOWN: historyIndex = Math.max(historyIndex-historyLines, 0); 
										invalidCount=invalidMax; break;
			case KeyEvent.VK_HOME: historyIndex = Math.max(messageHistory.size()-historyLines, 0); 
										invalidCount=invalidMax; break;
			case KeyEvent.VK_END: historyIndex = 0; 
										invalidCount=invalidMax; break;
			default: invalidCount++;
			}	
				
			switch(key.getKeyChar()) {
			case 'g':
			case ',': player.pickup(); break;
			case '<': if (userIsTryingToExit())
							return userExits();
						else 
							player.moveBy(0, 0, -1); 
						break;
			case '>': player.moveBy(0, 0, 1); break;
			case '?': subscreen = new HelpScreen(); 
						break;
			case '-': historyIndex = Math.min(historyIndex+1, Math.max(messageHistory.size()-historyLines, 0));
						invalidCount=invalidMax; break;
			case '+': historyIndex = Math.max(historyIndex-1, 0);
						invalidCount=invalidMax; break;
			default: invalidCount ++;
			}
		}
		
		
		
		if (subscreen == null && (player.ap() < ap || invalidCount < invalidMax))
			world.update(player);
		
		if (player.hp() < 1 && invalidCount < invalidMax)
			return new LoseScreen(world, player, this);
		
		if (player.level() > level)
			subscreen = new LevelUpScreen(player, player.level() - level);
		
		return this;
	}
	
	/** Returns true if player is positioned at an exit square on the top level. */
	private boolean userIsTryingToExit() {
		return player.z() == 0 && world.tile(player.location()) == Tile.STAIRS_UP ;
	}
	
	/** Checks to see if win conditions are met, then sets the Win or Lose screen. */
	private Screen userExits() {
		for (Item item : player.inventory().items())
			if (item != null && item.name().equals("Monster trophy"))
				return new WinScreen();
		player.modifyHP(0, "cowardice");
		return new LoseScreen(world, player, this);
	}
	
}
