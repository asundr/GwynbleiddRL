package wrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class describes an ability that exclusively applies {@linkplain Effect}s.
 * @author Arun Sundaram
 *
 */
public class Spell {
	
	private String name;
	/** Returns the name of this Spell. */
	public String name() { return name;}
	
	private int manaCost;
	/** Returns the amount of mana that will be spent if this Spell is cast. */
	public int manaCost() { return manaCost; }
	
	private double radius;
	/** Returns the radius of this spell. Minimum 1. Defaults to 1. */
	public double radius() {
		return Math.max(radius, 1);
	}
	/** Sets the radius of this Spell. */
	public void setRadius(double radius) {
		this.radius = radius;
	}
	/** Returns {@code true} if the spell can affect more than just the target Tile */
	public boolean isArea() { return radius() > 1; }
	
	private int degree;
	/** Returns the degree of splash area centered on the target direction. Clamps between [0,360]. Defaults to 360. */
	public int degree() {
		return Math.min(360, Math.max(0, degree));
	}
	/** Sets the degree of this Spell. */
	public void setDegree(int degree) {
		this.degree = degree;
	}
	
	private Delivery delivery;
	/** Returns the delivery method of this Spell. */
	public Delivery delivery() { return delivery; }
	
	private List<Effect> effects;
	/** Defaults {@code caster} to {@code null}.
	 * @see #effects(Creature) */
	public List<Effect> effects() { return effects(null); }
	/** Returns a fresh list of {@linkplain Effect}s created by the {@code caster}
	 * @param caster - the {@linkplain Creature} that created the {@linkplain Effect}s. If no caster, pass {@code null}. */
	public List<Effect> effects(Creature caster) {
		if (this.effects == null)
			return null;
		List<Effect> effects = new ArrayList<Effect>();
		for (Effect e : this.effects) {
			Effect clone = (Effect) e.clone();
			clone.setCaster(caster);
			effects.add(clone);
		}
		return effects;
	}
	
	private List<Hazard> hazards;
	/** Defaults {@code caster} to {@code null}.
	 * @see #hazards(Creature) */
	public List<Hazard> hazards() { return hazards(null); }
	/** Returns a fresh list of {@linkplain Hazard}s created by the {@code caster}. */
	public List<Hazard> hazards(Creature caster) {
		if (this.hazards == null)
			return null;
		List<Hazard> hazards = new ArrayList<Hazard>();
		for (Hazard h : this.hazards) {
			Hazard clone = (Hazard) h.clone();
			clone.setOwner(caster);
			hazards.add(h);
		}
		return hazards;
	}
	
	/** The unique effect ID for the HazardLink if it is not stackable. */
	private int hazardLinkID;
	
	/** @see #Spell(String, int, double, int, Delivery, Hazard) */
	public Spell(String name, int manaCost, Delivery delivery, Hazard hazard) {
		this(name, manaCost, 1, 360, delivery, hazard);
	}
	
	/** @see #Spell(String, int, double, int, Delivery, List, List) */
	public Spell(String name, int manaCost, double radius , int degree, Delivery delivery, Hazard hazard) {
		this(name, manaCost, radius, degree, delivery, null, Arrays.asList(hazard));
	}
	
	/** @see {@link #Spell(String, int, double, int, Delivery, Effect)} */
	public Spell(String name, int manaCost, Delivery delivery, Effect effect) {
		this(name, manaCost, 1, 360, delivery, effect);
	}
	
	/** @see #Spell(String, int, double, int, Delivery, List, List)*/
	public Spell(String name, int manaCost, double radius, int degree, Delivery delivery, Effect effect) {
		this(name, manaCost, radius, degree, delivery, Arrays.asList(effect), null);
	}
	
	/** @see #Spell(String, int, double, int, Delivery, List, List)*/
	public Spell(String name, int manaCost, double radius, int degree, Delivery delivery, Effect effect, Hazard hazard) {
		this(name, manaCost, radius, degree, delivery, Arrays.asList(effect), Arrays.asList(hazard));
	}
	
	/**
	 * @param name - The name of the spell
	 * @param manaCost - the amount of mana spent when cast
	 * @param radius - the splash radius from the target
	 * @param degree - the degrees [0, 360] that are affected by splash
	 * @param delivery - the way this spell is targeted
	 * @param effects - the effects that this spell applies to a target
	 * @param hazards - the hazards that this spell places at the target
	 */
	public Spell(String name, int manaCost, double radius, int degree, Delivery delivery, List<Effect> effects, List<Hazard> hazards) {
		this.name = name;
		this.manaCost = manaCost;
		this.radius = radius;
		this.degree = degree;
		this.delivery = delivery;
		this.effects = effects;
		this.hazards = hazards;
		if (hazards != null)
			this.hazardLinkID = Effect.newID();
		if (effects != null)
			for (Effect e : effects) {
				if (e.stackable()) {
					e.setID(Effect.newID());
				}
			}
	}
	
	/** Applies the {@linkplain Effect}s and {@linkplain Hazard}s over the pre-calculated {@linkplain Splash} region of this spell. */
	public void apply(Point location, Splash splash, World world, Creature caster) {
		if (effects != null && !effects.isEmpty())
			splash.applyEffects(world, location, effects(caster));
		if (hazards != null && !hazards.isEmpty()) {
			List<Hazard> newHazards = hazards(caster);
			caster.addEffect( new HazardLink(world, newHazards, location, splash) );
		}
	}
	
	/** Returns a {@linkplain Splash} object representing the shape of this Spell's splash damage range.
	 * @param aimedDeg - the degree from from the +x axis that the Spell is aimed towards.*/
	public Splash areaOfEffect(int aimedDegree) {
		return new Splash(radius, degree, aimedDegree, delivery);
	}
	

	/** Enumerates the conditions for which spells can be delivered. */
	public enum Delivery{
		SELF,		// Can target caster
		RADIAL,		// Area affect w/o center. Can be partial wedge of circle
		AIMED, 		// Can fire at any tile in LOS
		TARGET, 	// Can fire at any detected creature
	}
	
	
	/** Effect used to link {@linkplain Hazard}s to their caster to ensure the Spell's hazards wear off. */
	private class HazardLink extends Effect {
		
		private List<Hazard> hazards;
		private World world;
		private Point location;
		private Splash splash;
		
		public HazardLink(World world, List<Hazard> hazards, Point location, Splash splash) {
			super(null, 0, hazardLinkID);
			this.world = world;
			this.hazards = hazards;
			this.location = location;
			this.splash = splash;
			for (Hazard h : hazards) if (duration < h.duration()) duration = h.duration();
		}
		
		public void start(Creature cTarget) {
			caster = cTarget;
			splash.applyHazards(world, location, hazards);
		}
		
		public void end(Creature cTarget) {
			splash.cleanUp(world);
		}
	}

}
