package wrl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

/**
 * The {@code EventOrganizer} class schedules updates to objects that implement the {@linkplain Updatable} interface.
 * It prioritizes items that have the highest AP value and the least recent update
 * @author Arun Sundaram
 *
 */
public class EventOrganizer {
	
	private static final int TICK_MAX = 1000000;
	
	private int tick;
	private Map<Updatable, Integer> tickMap;
	private TreeSet<Updatable> queue;
	
	public EventOrganizer() {
		this.tick = 0;
		this.tickMap = new HashMap<Updatable, Integer>();
		this.queue = new TreeSet<Updatable>( (u1, u2) -> (u1==u2) ? 0 : (u1.ap() != u2.ap()) ? u2.ap() - u1.ap() : tickMap.get(u1) - tickMap.get(u2)  );
	}
	
	/** 
	 * Updates the next entity in the queue, removing it afterwards if non requiring a new update
	 * Will update the AP of all the entities in the queue if max AP falls below zero.
	 * If a non-player entity updates without spending AP, will print a warning to the console.
	 */
	public void nextUpdate() {
		Updatable curr = queue.first(); 
		if (curr.ap() <= 0)
			refreshAP();
		if (tick > 2*TICK_MAX)
			resetTicks();
		
		int ap = curr.ap();
		curr.update();
		
		if ( !curr.updatePending() )
			remove(curr);
		else if (ap == curr.ap() && curr instanceof Entity &&  !((Entity)curr).isPlayer() ) {
			System.out.println("WARNING: " + curr.getClass().getSimpleName() + " " + ((Entity) curr).name() + " has same AP = " + ap);
		}
	}
	
	/** Continues to update the queue until an Updatable is seen twice. */
	public void updateEachOnce() {
		HashSet<Updatable> seen = new HashSet<Updatable>();
		Updatable curr;
		while ( !seen.contains(curr = queue.first()) ) {
			seen.add(curr);
			nextUpdate();
		}
	}
	
	/** Refreshes the AP of each updatable. Can be thought of as iterating to the next turn */
	private void refreshAP() {
		for (Updatable u : queue)
			u.refreshAP();
	}
	
	/** Reduces the current tick associated with each update to prevent overflow. */
	private void resetTicks() {
		tick -= TICK_MAX;
		for (Updatable u : queue)
			tickMap.put(u, tickMap.get(u) - TICK_MAX);
	}
	
	/** Adds an {@linkplain Updatable} item to the queue. */
	public void add(Updatable updatable) {
		tickMap.put(updatable, tick++);
		queue.add(updatable);
	}
	
	/** Removes an {@linkplain Updatable} item from the queue. */
	public void remove(Updatable updatable) {
		if (tickMap.containsKey(updatable)) {
			queue.remove(updatable);
			tickMap.remove(updatable);
		}
	}

	/** Returns {@code true} if the passed {@linkplain Updatable} will be updated the next time {@linkplain #nextUpdate(Creature)} is called. */
	public boolean isNext(Updatable updatable) {
		return !queue.isEmpty() && queue.first() == updatable;
	}
	
}