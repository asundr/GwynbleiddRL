package rltut;

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
		List<Effect> effects = new ArrayList<Effect>();
		for (Effect e : this.effects) {
			Effect clone = (Effect) e.clone();
			clone.setCaster(caster);
			effects.add(clone);
		}
		return effects;
	}
	
	/** @see {@link #Spell(String, int, double, int, Delivery, Effect)} */
	public Spell(String name, int manaCost, Delivery delivery, Effect effect) {
		this(name, manaCost, 1, 360, delivery, effect);
	}
	
	/** @see #Spell(String, int, double, int, Delivery, List)*/
	public Spell(String name, int manaCost, double radius, int degree, Delivery delivery, Effect effect) {
		this(name, manaCost, radius, degree, delivery, Arrays.asList(effect));
	}
	
	/**
	 * @param name - The name of the spell
	 * @param manaCost - the amount of mana spent when cast
	 * @param radius - the splash radius from the target
	 * @param degree - the degrees [0, 360] that are affected by splash
	 * @param delivery - the way this spell is targeted
	 * @param effects - the effects that this spell applies to a target
	 */
	public Spell(String name, int manaCost, double radius, int degree, Delivery delivery, List<Effect> effects) {
		this.name = name;
		this.manaCost = manaCost;
		this.effects = effects;
		this.delivery = delivery;
		this.radius = radius;
		this.degree = degree;
	}
	
	/** Returns a grid representing the shape of this Spell's splash damage range. Grid will always have odd dimensions, ensuring the target is at the center.
	 * @param aimedDeg - the degree from from the +x axis that the Spell is aimed towards.*/
	public boolean[][] areaOfEffect(int aimedDeg){
		int r = (int) Math.ceil(radius + 1);
		int len = 2*r - 1;
		
		boolean[][] splash = new boolean[len][len];
		for (int x=0; x<len; x++) {
			for (int y=0; y<len; y++) {
				if ((x-r+1)*(x-r+1) + (y-r+1)*(y-r+1) > radius*radius)
					continue;
				if (degree == 360 || isValidAngle(aimedDeg, 0, 0, x-r+1, y-r+1))
					splash[x][y] = true;
			}
		}
		
		if (delivery == Delivery.RADIAL)
			splash[r-1][r-1] = false;
		
		return splash;
	}
	
	/**
	 * Returns {@code true} if the line from (cx,cy) to (x,y) is within degree/2 of the aimedDegree
	 * @param aimedDeg - direction this spell is being cast
	 * @param cx - x coordinate of the radial center
	 * @param cy - y coordinate of the radial center
	 * @param x - x coordinate of a point that terminates a line from the center
	 * @param y - y coordinate of a point that terminates a line from the center
	 * @return
	 */
	private boolean isValidAngle(int aimedDeg, int cx, int cy, int x, int y) {
		int leftDeg = aimedDeg + degree/2;
		int rightDeg = aimedDeg - degree/2;
		double angle = new Line(cx, cy, x, y).radialAngle();
		
		if (leftDeg > 360)
			return angle >= rightDeg || angle <= leftDeg-360;
		else if (rightDeg < 0)
			return angle <= leftDeg || angle >= rightDeg+360;
		else
			return angle <= leftDeg && angle >= rightDeg;
	}
	
	
	/** Enumerates the conditions for which spells can be delivered. */
	public enum Delivery{
		SELF,		// Can target caster
		RADIAL,		// Area affect w/o center. Can be partial wedge of circle
		AIMED, 		// Can fire at any tile in LOS
		TARGET, 	// Can fire at any detected creature
	}

}
