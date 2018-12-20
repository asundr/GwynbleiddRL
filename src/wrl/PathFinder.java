package rltut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * PathFinder will attempt to build a path between two {@linkplain Point}s that a {@linkplain Creature} can navigate.
 * Uses the <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a href>.
 * @author Arun Sundaram
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a href>
 */
public class PathFinder {
	
	private Point end;
	private HashSet<Point> open;
	private HashSet<Point> closed;
	private PriorityQueue<Point> queue;	// TODO use TreeMap for faster remove(x)?
	private HashMap<Point, Point> parents;
	private HashMap<Point, Integer> totalCost;
	
	public PathFinder() {
		this.open = new HashSet<Point>();
		this.closed = new HashSet<Point>();
		this.queue =  new PriorityQueue<Point>( (p1, p2) -> totalCost(p1, end) - totalCost(p2, end) );
		this.parents = new HashMap<Point, Point>();
		this.totalCost = new HashMap<Point, Integer>();
	}
	
	/** returns the number of {@linkplain Point}s along a straight line between {@code from} and {@code to} */
	private int heuristicCost(Point from, Point to) {
		return Math.max(Math.abs(to.x - from.x), Math.abs(to.y - from.y));
	}
	
	/** Counts the number of {@linkplain Point}s between {@code from} and {@code start} by recursively iterating through {@code from}'s parents until {@code null} is reached. */
	private int costToGetTo(Point from) {
		return parents.get(from) == null ? 0 : 1 + costToGetTo(parents.get(from));
	}
	
	/**Finds the sum of {@linkplain #costToGetTo(Point from)} and {@linkplain #heuristicCost(Point from, Point to)}.
	 * The method will return a memoized result or store the result in the memo before returning.  */
	private int totalCost(Point from, Point to) {
		if (totalCost.containsKey(from))
			return totalCost.get(from);
		int cost = costToGetTo(from) + heuristicCost(from, to);
		totalCost.put(from, cost);
		return cost;
	}
	
	/** Finds a path as an {@linkplain ArrayList}<{@linkplain Point}> that the {@code creature} can traverse from {@code start} to {@code end}. 
	 * Will return {@code null} if no path can be made before {@code maxTries} is reached.
	 * @param creature - the entity traversing the path according to {@linkplain Creature#canEnter(Point)}
	 * @param start - first Point in the path
	 * @param end - last Point in the path
	 * @param maxTries - the maximum number of attempts to find a path before returning {@code null}
	 * @return An ArrayList of Points in order from {@code start} to {@code end} or {@code null} if no path found.
	 */
	public ArrayList<Point> findPath(Creature creature, Point start, Point end, int maxTries){
		this.end = end;
		open.clear(); closed.clear(); parents.clear(); totalCost.clear(); queue.clear();
		open.add(start);
		queue.add(start);
		
		for (int tries = 0; tries < maxTries && open.size() > 0; tries++) {
			Point closest = queue.remove();
			open.remove(closest);
			closed.add(closest);
			
			if (closest.equals(end))
				return createPath(start, closest);
			
			checkNeighbors(creature, end, closest);
		}
		return null;
	}

	/** Attempts to parent the current closest {@linkplain Point} to its neighbors. 
	 * If the neighbors are found for the first time and can be entered by the {@code creature}, they are added to the open set.  */
	private void checkNeighbors(Creature creature, Point end, Point closest) {
		for (Point neighbor : closest.neighbors8()) {
			if (closed.contains(neighbor) || !creature.canEnter(neighbor) && !neighbor.equals(end))
				continue;
			
			if (!open.contains(neighbor))
				reparentNeighbor(closest, neighbor);
			else
				reparentNeighborIfNecessary(closest, neighbor);
		}
	}
	
	/** Changes the {@code parent} of the {@code child}, removing the {@code child}'s memoized totalCost since it is no longer valid */
	private void reparent(Point child, Point parent) {
		parents.put(child, parent);
		totalCost.remove(child);
	}
	
	/** Sets the parent of the child and adds the child to the queue */
	private void reparentNeighbor(Point closest, Point neighbor) {
		reparent(neighbor, closest);
		open.add(neighbor);
		queue.add(neighbor);
	}

	/** If the distance from start to neighbor is closer with the new parent, the parent is changed. */
	private void reparentNeighborIfNecessary(Point closest, Point neighbor) {
		Point originalParent = parents.get(neighbor);
		double previousCost = costToGetTo(neighbor);
		reparent(neighbor, closest);
		double reparentCost = costToGetTo(neighbor);
		
		if (reparentCost < previousCost) {
			open.remove(neighbor);
			queue.remove(neighbor);
			// TODO re-add to queue?
		} else {
			reparent(neighbor, originalParent);
		}
	}
	
	/** Returns a path from the {@code start} to the {@code end} through their parents as an {@linkplain ArrayList}<{@linkplain Point}>  */
	private ArrayList<Point> createPath(Point start, Point end){
		ArrayList<Point> path = new ArrayList<Point>();
		while (!end.equals(start)) {
			path.add(end);
			end = parents.get(end);
		}
		
		Collections.reverse(path);
		return path;
	}

}
