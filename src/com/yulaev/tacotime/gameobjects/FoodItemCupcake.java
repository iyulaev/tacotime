package com.yulaev.tacotime.gameobjects;

import com.yulaev.tacotime.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FoodItemCupcake extends GameFoodItem {
	
	private static boolean bitmaps_initialized = false;
	private static Bitmap bitmapInactive;
	private static Bitmap bitmapActive;
	
	private static final String activitynametag = "FoodItemCupcake";
	
	public FoodItemCupcake() {
		super("cupcake");
	}
	
	public FoodItemCupcake(Context caller) {
		super("cupcake");
		
		if(!bitmaps_initialized) {
			bitmaps_initialized = true;
			bitmapInactive = BitmapFactory.decodeResource(caller.getResources(), R.drawable.fooditem_cupcake_grey);
			bitmapActive = BitmapFactory.decodeResource(caller.getResources(), R.drawable.fooditem_cupcake);
			
			Log.d(activitynametag, "Initializing Bitmaps for " + activitynametag);
		}
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
	
	public Bitmap getBitmapInactive() { return bitmapInactive; }
	public Bitmap getBitmapActive() { return bitmapActive; }
}
