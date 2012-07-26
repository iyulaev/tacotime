package com.yulaev.tacotime.gameobjects;

import android.util.Log;

public class FoodItemCupcake extends GameFoodItem {
	public FoodItemCupcake() {
		super("cupcake");
	}

	@Override
	public int pointsOnInteraction(String interactedWith, int waitTime) {
		if(interactedWith.equals("TrashCan")) return(-5);
		if(interactedWith.equals("Customer")) return(5);
		
		Log.e("FoodItemCoffee", "Tried to do pointsOnInteraction but interactedWith not recognized => " + interactedWith);
		return(0);
	}

	@Override
	public int moneyOnInteraction(String interactedWith, int waitTime) {
		if(interactedWith.equals("TrashCan")) return(0);
		if(interactedWith.equals("Customer")) return(5);
		
		Log.e("FoodItemCoffee", "Tried to do moneyOnInteraction but interactedWith not recognized => " + interactedWith);
		return(0);
	}
	
	public FoodItemCupcake clone() { return new FoodItemCupcake(); }
}
