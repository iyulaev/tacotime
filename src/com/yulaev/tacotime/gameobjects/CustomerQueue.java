package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;
import java.util.List;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.Interaction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class CustomerQueue extends GameItem {
	//Number of customers that are in states INLINE to SERVED
	public static final int QUEUE_VISIBLE_LENGTH = 3;
	
	private static final String activitynametag = "CustomerQueue";
	
	int queue_length;
	ArrayList<Customer> customerList;
	
	public CustomerQueue(Context caller, int x_pos, int y_pos, 
			int orientation, int queue_length, List<GameFoodItem> foodItemMenu) {
		//public GameItem(Context caller, String name, int r_bitmap, int x_pos, int y_pos, int orientation, int gg_width, int gg_height)
		super(caller, "CustomerQueue", R.drawable.countertop, x_pos, y_pos, orientation, 10, 10);
		
		//Create and fill up the CustomerQueue
		this.queue_length = queue_length;
		customerList = new ArrayList<Customer>(queue_length);
		
		for(int i = 0; i < queue_length; i++) {
			customerList.add(new Customer(caller, Customer.DEFAULT_CUSTOMER_MOVERATE, i, foodItemMenu));
		}
		
		Log.v(activitynametag, "Created new CustomerQueue with " + queue_length + " customers.");
		for(int i = 0; i < queue_length; i++) {
			Log.v(activitynametag, customerList.get(i).toString());
		}
		
	}
	
	private void advanceQueue() {
		for(int i = 0; i < queue_length; i++) {
			customerList.get(i).decQueuePosition();
		}
	}
	
	private Customer head() {
		return customerList.get(0);
	}
	
	public void onUpdate() {
		super.onUpdate();
		
		for(int i = 0; i < queue_length; i++) {
			customerList.get(i).onUpdate();
		}
		
		if(head().getState() == Customer.STATE_FINISHED) advanceQueue();
	}
	
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		for(int i = 0; i < queue_length; i++) {
			customerList.get(i).draw(canvas);
		}
	}
	
	/** Called when CoffeeGirl interacts with this GameItem; basically she interacts directly with the 
	 * head of the queue */
	public Interaction onInteraction(String foodItem) {
		return(head().onInteraction(foodItem));
	}
}
