package com.yulaev.tacotime.gameobjects;

import android.util.Log;

public class FoodItemBlendedDrink extends GameFoodItem {
	public FoodItemBlendedDrink() {
		super("blended_drink");
	}

	@Override
	public int pointsOnInteraction(String interactedWith, int waitTime) {
		if(interactedWith.equals("TrashCan")) return(-10);
		if(interactedWith.equals("Customer")) return(10);
		
		Log.e("FoodItemCoffee", "Tried to do pointsOnInteraction but interactedWith not recognized => " + interactedWith);
		return(0);
	}

	@Override
	public int moneyOnInteraction(String interactedWith, int waitTime) {
		if(interactedWith.equals("TrashCan")) return(0);
		if(interactedWith.equals("Customer")) return(15);
		
		Log.e("FoodItemCoffee", "Tried to do moneyOnInteraction but interactedWith not recognized => " + interactedWith);
		return(0);
	}
}
