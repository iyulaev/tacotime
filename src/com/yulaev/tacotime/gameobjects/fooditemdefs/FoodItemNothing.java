package com.yulaev.tacotime.gameobjects.fooditemdefs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gameobjects.GameFoodItem;

/** Describes the "Nothing" food item. Mostly we just define the number of points and amount of money that
 * this FoodItem is worth (zero lol!); for a detailed description of the methods and their purpose please 
 * see the GameFoodItem class documentation.*/

public class FoodItemNothing extends GameFoodItem {
	
	private static boolean bitmaps_initialized = false;
	private static Bitmap bitmapInactive;
	private static Bitmap bitmapActive;
	
	private static final String activitynametag = "FoodItemNothing";
	
	public FoodItemNothing() {
		super("nothing");
	}

	public FoodItemNothing(Context caller) {
		super("nothing");
		
		if(!bitmaps_initialized) {
			bitmaps_initialized = true;
			//bitmapInactive = BitmapFactory.decodeResource(caller.getResources(), R.drawable.fooditem_nothing);
			bitmapActive = BitmapFactory.decodeResource(caller.getResources(), R.drawable.fooditem_nothing);
			
			Log.d(activitynametag, "Initializing Bitmaps for " + activitynametag);
		}
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

	public FoodItemNothing clone() { return (new FoodItemNothing()); }
	
	public Bitmap getBitmapInactive() { return bitmapActive; }
	public Bitmap getBitmapActive() { return bitmapActive; }
}
