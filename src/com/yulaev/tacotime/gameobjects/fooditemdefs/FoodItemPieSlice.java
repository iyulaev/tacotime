package com.yulaev.tacotime.gameobjects.fooditemdefs;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gameobjects.GameFoodItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/** Describes the Pie Slice food item. Mostly we just define the number of points and amount of money that
 * this FoodItem is worth; for a detailed description of the methods and their purpose please see the GameFoodItem 
 * class documentation.*/

public class FoodItemPieSlice extends GameFoodItem {
	
	private static boolean bitmaps_initialized = false;
	private static Bitmap bitmapInactive;
	private static Bitmap bitmapActive;
	
	private static final String activitynametag = "FoodItemCupcake";
	
	public FoodItemPieSlice() {
		super("pieslice");
	}
	
	public FoodItemPieSlice(Context caller) {
		super("pieslice");
		
		if(!bitmaps_initialized) {
			bitmaps_initialized = true;
			bitmapInactive = BitmapFactory.decodeResource(caller.getResources(), R.drawable.fooditem_cake_slice_grey);
			bitmapActive = BitmapFactory.decodeResource(caller.getResources(), R.drawable.fooditem_cake_slice);
			
			Log.d(activitynametag, "Initializing Bitmaps for " + activitynametag);
		}
	}

	@Override
	public int pointsOnInteraction(String interactedWith, int waitTime) {
		if(interactedWith.equals("TrashCan")) return(-5);
		if(interactedWith.equals("Customer")) return(8);
		
		Log.e("FoodItemCoffee", "Tried to do pointsOnInteraction but interactedWith not recognized => " + interactedWith);
		return(0);
	}

	@Override
	public int moneyOnInteraction(String interactedWith, int waitTime) {
		if(interactedWith.equals("TrashCan")) return(0);
		if(interactedWith.equals("Customer")) return(8);
		
		Log.e("FoodItemCoffee", "Tried to do moneyOnInteraction but interactedWith not recognized => " + interactedWith);
		return(0);
	}
	
	public FoodItemPieSlice clone() { return new FoodItemPieSlice(); }
	
	public Bitmap getBitmapInactive() { return bitmapInactive; }
	public Bitmap getBitmapActive() { return bitmapActive; }
}
