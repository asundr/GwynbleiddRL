package wrl;

/**
 * This class manages a collection of {@linkplain Item}s for a container class.
 *
 */
public class Inventory {
	
	private Item[] items;
	/** Returns the {@linkplain Item}s as an array. */
	public Item[] items() { return items; }
	/** Returns the {@linkplain Item} at index {@code i}, or {@linkplain null} if no Item at {@code i}. */
	public Item getItem(int i) { return items[i]; }
	
	/** @param max - maximum inventory spaces */
	public Inventory(int max) {
		items = new Item[max];
	}
	
	/** Adds the {@linkplain Item} to the inventory if there is space. */
	public void add(Item item) {
		for (int i=0; i<items.length; i++) {
			if (items[i] == null) {
				items[i] = item;
				break;
			}
		}
	}
	
	/** Removes the {@linkplain Item} if it is found in the inventory. */
	public void remove(Item item) {
		for (int i=0; i<items.length; i++) {
			if (items[i] == item) {
				items[i] = null;
				return;
			}
		}
	}
	
	/** Returns true if the inventory contains a reference to the passed {@linkplain Item}. */
	public boolean contains(Item item) {
		for (Item curr : items) {
			if (curr == item)
				return true;
		}
		return false;
	}
	
	/** Returns the number of items that have this name */
	public int count(String name) {
		int count = 0;
		for (Item curr : items)
			if (curr != null && curr.name().equals(name))
				count++;
		return count;
	}
	
	/** Returns {@linkplain true} if there are no empty spaces in the inventory. */
	public boolean isFull() {
		for (int i=0; i<items.length; i++) {
			if (items[i] == null)
				return false;
		}
		return true;
	}

}
