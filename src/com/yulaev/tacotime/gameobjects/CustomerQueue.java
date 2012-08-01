/** CustomerQueue represents the "line" of customers waiting to be served. It is responsible for signaling to 
 * customers whether they should be visible or not and updating their queue positions (although the Customers 
 * do quite a bit of their own management, like determining whether CoffeeGirl has satisifed their order's 
 * requirements). Some of CustomerQueue's methods, like onInteraction(), pass the interaction event onto the
 * customer at the front of the queue 
 * 
 * The ViewObject representation of CustomerQueue is as a counter. In a sense, the CustomerQueue is itself
 * the countertop, and passes some interactions and update events between the queue of customers and the CoffeeGirl
 * GameActor.
 * 
 * */

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
	public static final int QUEUE_VISIBLE_LENGTH = 2;
	//Time between customer appearance
	public static final int TIME_BETWEEN_CUSTOMERS_MS = 3000;
	private long time_since_last_customer;
	
	private static final String activitynametag = "CustomerQueue";
	
	int queue_length;
	ArrayList<Customer> customerList;
	
	/** Create a new CustomerQueue. We will probably only be creating one queue per game level.
	 * 
	 * @param caller The calling Context, for extracting resources and such
	 * @param x_pos The GameGrid X position of the front of the queue
	 * @param y_pos The GameGrid Y position of the front of the queue
	 * @param orientation The orientation of the counter-top, determines where the sensitivity area is placed.
	 * @param queue_length The queue length; this is how many Customers this CustomerQueue will be initialized
	 * with and therefore how many customers will have to be served on this level.
	 * @param foodItemMenu A List of FoodItems that the Customer's can create their random order from
	 */
	public CustomerQueue(Context caller, int x_pos, int y_pos, 
			int orientation, int queue_length, float point_mult, float money_mult, float impatience, List<GameFoodItem> foodItemMenu) {
		//public GameItem(Context caller, String name, int r_bitmap, int x_pos, int y_pos, int orientation, int gg_width, int gg_height)
		super(caller, "CustomerQueue", R.drawable.countertop, x_pos, y_pos, orientation, 10, 10);
		
		//Create and fill up the CustomerQueue
		this.queue_length = queue_length;
		customerList = new ArrayList<Customer>(queue_length);
		for(int i = 0; i < queue_length; i++) {
			customerList.add(new Customer(caller, Customer.DEFAULT_CUSTOMER_MOVERATE, i, point_mult, money_mult, impatience, foodItemMenu));
		}
		
		Log.v(activitynametag, "Created new CustomerQueue with " + queue_length + " customers.");
		for(int i = 0; i < queue_length; i++) {
			Log.v(activitynametag, customerList.get(i).toString());
		}
		
		time_since_last_customer = 0;		
	}
	
	/** Decrement the queue position for each customer, effective advancing the line */
	private void advanceQueue() {
		for(int i = 0; i < queue_length; i++) {
			customerList.get(i).decQueuePosition();
		}
	}
	
	/** Get the customer at the front of the queue
	 * 
	 * @return Customer with queue position 0.
	 */
	private Customer head() {
		for(int i = 0; i < queue_length; i++) {
			if(customerList.get(i).getQueuePosition() == 0)
				return customerList.get(i);
		}
		
		//Should never happen?
		return customerList.get(queue_length-1);
	}
	
	/** Called by ViewThread when we are to update the state of this CustomerQueue. Aside from doing the typical
	 * GameItem updates (like trying to change state; since the CustomerQueue itself is stateless this is moot) it
	 * will also update queue positions and visibility for the Customers in the queue.
	 * 
	 */
	public void onUpdate() {
		super.onUpdate();
		
		//Call update for all of the customers
		for(int i = 0; i < queue_length; i++) {
			customerList.get(i).onUpdate();
		}
		
		//Advance the queue if customer at position 0 has finished
		if(head().getState() == Customer.STATE_SERVED || head().getState() == Customer.STATE_FINISHED) {
			advanceQueue();
		}
				
		
		for(int i = 0; i < queue_length; i++) {
			//Hide everything with queue position <0
			if(customerList.get(i).getQueuePosition() < 0 &&
					customerList.get(i).getState() == Customer.STATE_FINISHED) 
				customerList.get(i).setVisible(false);
			
			//Set the next customer that should be visible to visible IF sufficient time has passed
			//and the customer's position is between 0 (front of the line) and the last visilbe position
			if(!customerList.get(i).isVisible() && 
					(customerList.get(i).getQueuePosition()>=0 && 
					customerList.get(i).getQueuePosition()<QUEUE_VISIBLE_LENGTH) && 
					System.currentTimeMillis() > time_since_last_customer+TIME_BETWEEN_CUSTOMERS_MS){
				customerList.get(i).setVisible(true);
				time_since_last_customer = System.currentTimeMillis();
				
				i = queue_length; //end loop
			}
		}
		
	}
	
	/** Draws this CustomerQueue and also calls draw for all Customers in Queue*/
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		for(int i = 0; i < queue_length; i++) {
			customerList.get(i).draw(canvas);
		}
	}
	
	/** Called when CoffeeGirl interacts with this GameItem; basically she interacts directly with the 
	 * head of the queue 
	 * 
	 * @return The Interaction object describing the result of CoffeeGirl's interaction with this CustomerQueue
	 * 
	 * */
	public Interaction onInteraction(String foodItem) {
		return(head().onInteraction(foodItem));
	}
}
