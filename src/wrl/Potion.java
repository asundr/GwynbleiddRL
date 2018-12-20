package wrl;

import java.awt.Color;

/**
 * This class is an Item that applies toxicity to the Creature that consumes it in addition to its quaff effect.
 * @author Arun Sundaram
 *
 */
public class Potion extends Item {
	
	private int toxicity;
	public int toxicity() { return toxicity; }
	
	/**
	 * @param world - the World this Item inhabits
	 * @param name - The name of this Item.
	 * @param glyph - the {@code char} symbol for display
	 * @param color - the {@linkplain Color} of the glyph
	 * @param appearance - The description of this Item.
	 * @param toxicity - how much consuming this potion will increase toxicity
	 */
	public Potion(World world, String name, char glyph, Color color, String appearance, int toxicity) {
		super(world, name, glyph, color, appearance);
		this.toxicity = toxicity;
	}
	
	
	public void setQuaffEffect(Effect quaffEffect) {
		quaffEffect.setColor(color);
		int duration = (int) Math.max(quaffEffect.duration() * 1.5, toxicity);
		super.setQuaffEffect(new Effect( duration ) {
			public void start(Creature target) {
				this.bPotionEffect = true;
				target.modifyToxicity(toxicity);
				Effect clone = (Effect) quaffEffect.clone();
				clone.bPotionEffect = true;
				target.addEffect( clone );
			}
			
			public void end(Creature target) {
				target.modifyToxicity(-toxicity);
			}
		});
	}
}
