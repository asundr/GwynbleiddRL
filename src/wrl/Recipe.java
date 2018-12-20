package wrl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class encapsulates a list of ingredients and can return an {@linkplain Item} as a product of those ingredients.
 * @author Arun Sundaram
 *
 */
public class Recipe {
	
	private String name;
	public String name() { return name; }
	
	private ArrayList<String> ingredients;
	public ArrayList<String> ingredients() { return ingredients; }
	private Supplier<Item> crafter;
	
	/**
	 * @param name - name of the Recipe
	 * @param ingredients - item names that this Recipe requires
	 * @param crafter - {@linkplain Supplier} that returns the product
	 */
	public Recipe(String name, ArrayList<String> ingredients, Supplier<Item> crafter) {
		this.name = name;
		this.ingredients = ingredients;
		this.crafter = crafter;
	}
	
	/** Returns the product of this recipe without checking for ingredients. */
	public Item create() {
		return crafter.get();
	}
	
	/** Returns true if {@code items} is a valid combination of this Recipe's ingredients. */
	public boolean matchIngredients(List<String> items){
		if (ingredients.size() != items.size())
			return false;
		boolean[] used = new boolean[ingredients.size()];
		for (String item : items) {
			boolean matched = false;
			for (int i=0; i<used.length; i++) {
				if (used[i])
					continue;
				if (ingredients.get(i).equals(item)) {
					used[i] = true;
					matched = true;
					break;
				}
			}
			if (!matched)
				return false;
		}
		return true;
	}
	
	/** Returns an array of Items or {@code null} values corresponding to the ingredients of this recipe if found. */
	public Item[] findIngredients(Inventory inventory) {
		Item[] has = new Item[ingredients.size()];
		for (int inv = inventory.items().length-1; inv >=0; inv--) {
			for (int i=0; i<has.length; i++) {
				if (has[i] != null)
					continue;
				Item item = inventory.items()[inv];
				if (item != null && ingredients.get(i).equals( item.name() ))
					has[i] = inventory.items()[inv];
			}
		}
		return has;
	}
	
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if ( !(other instanceof Recipe) )
			return false;
		Recipe o = (Recipe) other;
		return this.name.equals(o.name) && this.ingredients.equals(o.ingredients);
	}

}
