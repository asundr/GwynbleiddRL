package wrl;

import java.util.ArrayList;
import java.util.List;

import wrl.Spell.Delivery;

public class Splash {
	
	private static final boolean[][] SINGLE = {{true}};
	
	private boolean[][] splash;
	private double radius;
	private int spread;
	private int aimedDegree;
	private Spell.Delivery delivery;
	
	private List<Hazard> spawnedHazards;
	
	/** Creates a Splash that is comprised of only the target Tile. */
	public Splash() {
		this.splash = SINGLE;
	}
	
	/**
	 * @param radius - the splash radius from the target
	 * @param spread - the degrees [0, 360] that are affected by splash
	 * @param aimedDegree - the degree from from the +x axis that the Spell is aimed towards
	 * @param delivery - the way this spell is targeted
	 */
	public Splash(double radius, int spread, int aimedDegree, Spell.Delivery delivery) {
		this.radius = radius;
		this.spread = spread;
		this.aimedDegree = aimedDegree;
		this.delivery = delivery;
		makeSplash();
	}
	
	/** Returns the width {@code n} for the {@code n*n} grid that describes the splash region. */
	public int size() { return splash.length; }
	
	/** Returns {@code true} if index of splash is in range. */
	public boolean get(int x, int y) { return splash[x][y]; }
	
	/** Builds a grid representing the shape of this Spell's splash damage range. Grid will always have odd dimensions, ensuring the target is at the center. */
	private void makeSplash() {
		int r = (int) Math.ceil(radius + 1);
		int len = 2*r - 1;
		
		splash = new boolean[len][len];
		for (int x=0; x<len; x++) {
			for (int y=0; y<len; y++) {
				if ((x-r+1)*(x-r+1) + (y-r+1)*(y-r+1) > radius*radius)
					continue;
				if (spread == 360 || isValidAngle(aimedDegree, 0, 0, x-r+1, y-r+1))
					splash[x][y] = true;
			}
		}
		
		if (delivery == Delivery.RADIAL)
			splash[r-1][r-1] = false;
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
		int leftDeg = aimedDeg + spread/2;
		int rightDeg = aimedDeg - spread/2;
		double angle = new Line(cx, cy, x, y).radialAngle();
		
		if (leftDeg > 360)
			return angle >= rightDeg || angle <= leftDeg-360;
		else if (rightDeg < 0)
			return angle <= leftDeg || angle >= rightDeg+360;
		else
			return angle <= leftDeg && angle >= rightDeg;
	}
	
	/**
	 * Adds the Effects to {@linkplain Creature}s in the Splash region.
	 * @param world - the {@linkplain World} in which this exists
	 * @param location - the location of the target this centers around
	 * @param effects - the {@linkplain Effect}s to be applied to Creatures in the region
	 */
	public void applyEffects(World world, Point location, List<Effect> effects) {
		if (effects == null || effects.isEmpty())
			return;
		for (int i=0; i<splash.length; i++) {
			for (int j=0; j < splash.length; j++) {
				if ( !splash[i][j] )
					continue;
				Point p = new Point(location.x - splash.length/2 + i, location.y - splash.length/2 + j, location.z);
				Creature c = world.creature(p);
				if (c != null)
					for (Effect e : effects)			
						c.addEffect( (Effect) e.clone() );
			}
		}
	}
	
	/**
	 * Adds the Hazards to the Tile in the Splash region.
	 * @param world - the {@linkplain World} in which this exists
	 * @param location - the location of the target this centers around
	 * @param hazards - the {@linkplain Hazard}s to be applied to Tiles in the region
	 */
	public void applyHazards(World world, Point location, List<Hazard> hazards) {
		if (hazards == null || hazards.isEmpty())
			return;
		spawnedHazards = new ArrayList<Hazard>();
		for (int i=0; i<splash.length; i++) {
			for (int j=0; j < splash.length; j++) {
				if ( !splash[i][j] )
					continue;
				Point p = new Point(location.x - splash.length/2 + i, location.y - splash.length/2 + j, location.z);
				for (Hazard h : hazards) {
					Hazard h2 = (Hazard) h.clone();
					spawnedHazards.add(h2);
					h2.relocate(p);
				}
			}
		}
	}
	
	/** Removes any temporary Entities this splash has added to the world.  */
	public void cleanUp(World world) {
		for (Hazard h : spawnedHazards)
			world.remove(h);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if ( !(other instanceof Splash) )
			return false;
		Splash o = (Splash) other;
		return Math.abs(o.radius-this.radius) < 0.0001 && this.spread == o.spread && ( spread==360 || this.aimedDegree == o.aimedDegree );
	}

}
