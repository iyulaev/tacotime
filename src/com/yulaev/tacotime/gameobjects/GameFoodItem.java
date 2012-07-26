package com.yulaev.tacotime.gameobjects;

/** GameFoodItem is simply a container class that describes a food item and how many points it
 * is worth when given to a customer, or when thrown out
 * @author ivany
 *
 */
public abstract class GameFoodItem {
	/**The name for this GameFoodItem*/
	private String foodItemName;
	
	/** Constructor for this GameFoodItem; just set the name.
	 * 
	 * @param name The name of this GameFoodItem.
	 */
	public GameFoodItem(String name) {
		foodItemName = name;
		is_satisfied = false;
	}
	public GameFoodItem(String name, boolean n_is_satisfied) {
		foodItemName = name;
		is_satisfied = n_is_satisfied;
	}
	
	/** Return the name of this GameFoodItem */
	public String getName() { return foodItemName; }
	
	/** Calculate the number of points when coffeeGirl interacts with interactedWith, after
	 * having waited for waitTime seconds.
	 * @param interactedWith The GameItem that CoffeGirl interacted with while holding this GameFoodItem
	 * @param waitTime The amount of time that the GameItem has been "waiting" - really only applicable if 
	 * the GameItem is a Customer
	 * @return The number of points resulting from this interaction.
	 */
	public abstract int pointsOnInteraction(String interactedWith, int waitTime);
	
	/** Calculate the number of dollars when coffeeGirl interacts with interactedWith, after
	 * having waited for waitTime seconds.
	 * @param interactedWith The GameItem that CoffeGirl interacted with while holding this GameFoodItem
	 * @param waitTime The amount of time that the GameItem has been "waiting" - really only applicable if 
	 * the GameItem is a Customer
	 * @return The number of moneys resulting from this interaction.
	 */
	public abstract int moneyOnInteraction(String interactedWith, int waitTime);
	
	//The below variables are used when this GameFoodItem is part of a customer's order
	private boolean is_satisfied;
	public void setSatisfied() { is_satisfied = true; }
	public boolean isSatisfied() { return is_satisfied; }
	
	public abstract GameFoodItem clone() ;
}
