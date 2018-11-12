package rltut;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;


/**
 * The {@code EventOrganizer} class schedules updates to objects that implement the {@linkplain Updatable} interface.
 * It prioritizes items that have the highest AP value and the least recent update
 * @author Arun Sundaram
 *
 */
public class EventOrganizer {
	
	private static final int TICK_MAX = 1000000;
	
	private int tick;
	private PriorityQueue<Updatable> updateQueue;
	private Map<Updatable, Integer> lastTick;
	
	public EventOrganizer() {
		this.tick = 0;
		this.lastTick = new HashMap<Updatable, Integer>();
//		this.updateQueue = new PriorityQueue<Updatable>((c1, c2) -> c1.ap()!=c2.ap() ? c2.ap() - c1.ap() : lastTick.get(c1) - lastTick.get(c2) );		
		this.updateQueue = new PriorityQueue<Updatable>(new Comparator<Updatable>() {
			@Override public int compare(Updatable c1, Updatable c2) {
				if (c1.ap() != c2.ap())
					return c2.ap() - c1.ap();
				else {
					int out = 0;
					if (lastTick.get(c1) != null)
						out +=lastTick.get(c1);
					if (lastTick.get(c2) != null)
						out += -lastTick.get(c2);
					return out;
				}
			}
		});
	}
	
	/** Updates the next entity in the queue, removing it afterwards if non requiring a new update
	 * Will update the AP of all the entities in the queue if max AP falls below zero.
	 * If a non-player entity updates without spending AP, will print a warning to the console.
	 */
	public void nextUpdate(Creature player) {
		Updatable curr = updateQueue.element();
		if (curr.ap() <= 0)
			refreshAP();
		if (tick > 2*TICK_MAX)
			resetTicks();
		
		lastTick.put(curr, tick++);
		int ap = curr.ap();
		curr.update();
		
		if (ap == curr.ap() && curr != player && curr instanceof Creature) {
			System.out.println("WARNING: " + ((Creature) curr).name() + " has same AP");
		}
		
		if ( !curr.updatePending() ) {
			remove(curr);
		}
	}
	
	/** Will reorder the passed item in the queue. This must be called if the item's AP is modified such as when {@linkplain Creature#modifyAP(int)} is called. */
	public void updateQueue(Updatable updatable) {
		updateQueue.remove(updatable);
		updateQueue.add(updatable);
	}
	
	/** Refreshes the AP of each updatable. Can be thought of as iterating to the next turn */
	private void refreshAP() {
		Iterator<Updatable> it = updateQueue.iterator();
		while (it.hasNext())
			it.next().refreshAP();
	}
	
	/** Reduces the current tick associated with each update to prevent overflow. */
	private void resetTicks() {
		tick -= TICK_MAX;
		for (Updatable c : lastTick.keySet()) {
			lastTick.put(c, lastTick.get(c) - TICK_MAX);
		}
	}
	
	/** Adds an {@linkplain Updatable} item to the queue. */
	public void add(Updatable updatable) {
		lastTick.put(updatable, tick);
		updateQueue.add(updatable);
	}
	
	/** Removes an {@linkplain Updatable} item from the queue. */
	public void remove(Updatable updatable) {
		updateQueue.remove(updatable);
		lastTick.remove(updatable);
	}
	
	/** Returns {@code true} if the passed {@linkplain Updatable} will be updated the next time {@linkplain #nextUpdate(Creature)} is called. */
	public boolean isNext(Updatable updatable) {
		return updateQueue.peek() == updatable;
	}
	
}



