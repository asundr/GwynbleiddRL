package rltut;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by a {@linkplain Creature} to select a {@linkplain LevelUpOption} after reaching a new level.
 *
 */
public class LevelUpController {
	
	private static LevelUpOption[] options = new LevelUpOption[] {
		new LevelUpOption("Increased hitpoints")  {
			public void invoke (Creature creature) { creature.gaimMaxHP(); } 
		},
		
		new LevelUpOption("Increased mana points") {
			public void invoke (Creature creature) {creature.gainMaxMana(); }
		},
		
		new LevelUpOption("Increased mana regeneration") {
			public void invoke (Creature creature) {creature.gainRegenMana(); }
		},
		
		new LevelUpOption("Increased attack value") {
			public void invoke (Creature creature) {creature.gainAttackValue(); }
		},
		
		new LevelUpOption("Increased defense value") {
			public void invoke (Creature creature) {creature.gainDefenseValue(); }
		},
		
		new LevelUpOption("Increased vision") {
			public void invoke (Creature creature) {creature.gainVision(); }
		}
	};
	
	/** Automatically select a level up bonus at random. */
	public void autoLevelUp(Creature creature) {
		options[(int) (Math.random() * options.length)].invoke(creature);
	}
	
	/** Return option names as a {@linkplain List}. */
	public List<String> getLevelUpOptions() {
		List<String> out = new ArrayList<String>();
		for (LevelUpOption option : options)
			out.add(option.name());
		return out;
	}
	
	/** Returns the {@linkplain LevelUpOption} at the {@code index}. */
	public LevelUpOption getLevelUpOption(int index) {
		return options[index];
	}
	
	/** Returns the total number of {@linkplain LevelUpOption}s */
	public int optionCount() {
		return options.length;
	}
}
