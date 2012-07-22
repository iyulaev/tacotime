package com.yulaev.tacotime.gameobjects;

/** GameFoodItem is simply a container class that describes a food item and how many points it
 * is worth when given to a customer, or when thrown out
 * @author ivany
 *
 */
public abstract class GameFoodItem {
	public String foodItemName;
	
	public GameFoodItem(String name) {
		foodItemName = name;
	}
	
	public String getName() { return foodItemName; }
	
	/** Calculate the number of points when coffeeGirl interacts with interactedWith, after
	 * having waited for waitTime seconds.
	 * @param interactedWith
	 * @param waitTime
	 * @return
	 */
	public abstract int pointsOnInteraction(String interactedWith, int waitTime);
	
	public abstract int moneyOnInteraction(String interactedWith, int waitTime);
}
