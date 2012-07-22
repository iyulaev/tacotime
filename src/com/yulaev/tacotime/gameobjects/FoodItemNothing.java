package com.yulaev.tacotime.gameobjects;

public class FoodItemNothing extends GameFoodItem {

	public FoodItemNothing() {
		super("nothing");
	}
	
	@Override
	public int pointsOnInteraction(String interactedWith, int waitTime) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int moneyOnInteraction(String interactedWith, int waitTime) {
		// TODO Auto-generated method stub
		return 0;
	}

}
