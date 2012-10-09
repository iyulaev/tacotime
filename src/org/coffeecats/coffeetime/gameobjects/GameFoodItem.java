package org.coffeecats.coffeetime.gameobjects;

import android.graphics.Bitmap;

/** GameFoodItem is simply a container class that describes a food item and how many points it
 * is worth when given to a customer, or when thrown out
 * @author ivany
 *
 */
public abstract class GameFoodItem {
	
	/**The name for this GameFoodItem*/
	private String foodItemName;
	
	/**The (weighed) probability of this food item being ordered; normalized to 1
	 */
	protected float orderProbability;
	
	/** Constructor for this GameFoodItem; just set the name.
	 * 
	 * @param name The name of this GameFoodItem.
	 * @param n_is_satisfied Whether this GameFoodItem has been satisfied (in the context of a Customer
	 * order that has been satisfied by CoffeeGirl).
	 */
	public GameFoodItem(String name) {
		this(name, false);
	}
	public GameFoodItem(String name, boolean n_is_satisfied) {
		foodItemName = name;
		is_satisfied = n_is_satisfied;
		orderProbability = 1.0f;
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
	
	/** Create a deep copy of the existing GameFoodItem */
	public abstract GameFoodItem clone() ;
	
	/** Returns a bitmap representation of this food item (for drawing to the screen, f.ex.) when
	 * the food item is "active", i.e. when the colors are not desaturated 
	 * @return Bitmap to use when drawing this food item
	 */
	public abstract Bitmap getBitmapActive();
	
	/** Returns a bitmap representation of this food item (for drawing to the screen, f.ex.) when
	 * the food item is "inactive", i.e. when the colors are desaturated and the icon is ghosted 
	 * @return Bitmap to use when drawing this food item, when it is inactive
	 */
	public abstract Bitmap getBitmapInactive();
	
	/**Return this GameFoodItem's order probability */
	public float getOrderProbability() { return orderProbability; }
}
