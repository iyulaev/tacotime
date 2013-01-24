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

package org.coffeecats.coffeetime.gameobjects;

import java.util.ArrayList;
import java.util.List;

import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gamelogic.Interaction;

import org.coffeecats.coffeetime.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class CustomerQueue extends GameItem {
	//Number of customers that are in states INLINE to SERVED
	public static final int QUEUE_VISIBLE_LENGTH = 2;
	//Time between customer appearance
	public static final int TIME_BETWEEN_CUSTOMERS_MS = 2000;
	private long time_since_last_customer;
	
	//Define position of customer queue
	public static final int X_POS = 40;
	public static final int DISTANCE_TO_QUEUE_TWO = 30;
	//public static final int Y_POS_FROM_GG_BOTTOM = 40;
	public static final int Y_POS_FROM_GG_TOP = 32;
	
	private static final String activitynametag = "CustomerQueue";
	
	int queue_length;
	ArrayList<Customer> customerList;
	//Customers processed indicates how many customers go through the queue
	int customers_processed;
	//Customers_satisfied indicates how many customers had their orders satisfied
	int customers_satisfied;
	
	int queue_number;
	
	/** Create a new CustomerQueue. We will probably only be creating one queue per game level.
	 * 
	 * @param caller The calling Context, for extracting resources and such
	 * @param x_pos The GameGrid X position of the front of the queue
	 * @param y_pos The GameGrid Y position of the front of the queue
	 * @param orientation The orientation of the counter-top, determines where the sensitivity area is placed.
	 * @param queue_length The queue length; this is how many Customers this CustomerQueue will be initialized
	 * with and therefore how many customers will have to be served on this level.
	 * @param point_mult The point multiplier for customer order completions; typically set per-level when each level is loaded.
	 * @param money_mult The money multiplier for customer order completions; typically set per-level when each level
	 * @param impatience How quickly each customer in this CustomerQueue's mood will degrade as they 
	 * wait longer for order fulfillment
	 * @param max_items_in_order The maximum number of items that can be in any Customer's order from the game menu
	 * @param foodItemMenu A List of FoodItems that the Customer's can create their random order from
	 * @param queue_number The queue identifier for this CustomerQueue; used to handle multiple queues existing in the level.
	 */
	public CustomerQueue(Context caller, int x_pos, int y_pos, int orientation, 
			int queue_length, float point_mult, float money_mult, 
			float impatience, int max_items_in_order, 
			List<GameFoodItem> foodItemMenuGlobal, int queue_number) {
		//public GameItem(Context caller, String name, int r_bitmap, int x_pos, int y_pos, int orientation, int gg_width, int gg_height)
		super(caller, "CustomerQueue" + Integer.toString(queue_number), R.drawable.countertop, x_pos, y_pos, orientation, 20, 17);
		
		//Create and fill up the CustomerQueue
		this.queue_length = queue_length;
		this.queue_number = queue_number;
		
		List<GameFoodItem> foodItemMenu = new ArrayList<GameFoodItem>(foodItemMenuGlobal.size());
		for(GameFoodItem gfi : foodItemMenuGlobal) foodItemMenu.add(gfi);
		List<Integer> foodItemCounts = generateFoodItemCounts(foodItemMenu, max_items_in_order * queue_length);
		
		String foodICDebug = "CustomerQueue generated foodItemCounts {";
		for(Integer count : foodItemCounts) { foodICDebug += count; foodICDebug += ", "; }
		foodICDebug += "}";
		
		Log.d(activitynametag, foodICDebug);
		
		foodICDebug = "CustomerQueue corresponding items were {";
		for(GameFoodItem gfi : foodItemMenu) { foodICDebug += gfi; foodICDebug += ", "; }
		foodICDebug += "}";
		
		Log.d(activitynametag, foodICDebug);
		
		customerList = new ArrayList<Customer>(queue_length);
		for(int i = 0; i < queue_length; i++) {
			customerList.add(new Customer(caller, 
					Customer.DEFAULT_CUSTOMER_MOVERATE, 
					i, 
					point_mult, 
					money_mult, 
					impatience, 
					max_items_in_order, 
					foodItemMenu,
					queue_number,
					foodItemCounts));
		}
		
		for(int i = 0; i < queue_length; i++) {
			Log.v(activitynametag, customerList.get(i).toString());
		}
		
		time_since_last_customer = 0;
		
		customers_satisfied = 0;
		customers_processed = 0;
	}
	
	/** Used to determine how many of each food item can be served from a particular CustomerQueue. The math is pretty 
	 * straight-forward: given a maximum number of items (usually queue_length * max_order_size), determine how many items
	 * should be ordered based on GameFoodItem.orderProbability. We create a List of integers that reflects the item 
	 * count for each item in foodItemMenu
	 */
	public ArrayList<Integer> generateFoodItemCounts(List<GameFoodItem> foodItemMenu, int total_number_of_items) {
		float total_probability = 0.0f;
		
		for(GameFoodItem fi : foodItemMenu) total_probability += fi.orderProbability;
		
		ArrayList<Integer> returned = new ArrayList<Integer>(foodItemMenu.size());
		
		for(GameFoodItem fi : foodItemMenu) {
			returned.add( ((int) ((fi.orderProbability/total_probability) * ((float) total_number_of_items))) + 1);
		}
		
		//The zero-th index is food item "nothing" so it should never get picked
		returned.set(0, 0);
		
		return(returned);
	}
	
	public CustomerQueue(Context caller, int x_pos, int y_pos, int orientation, 
			int queue_length, float point_mult, float money_mult, 
			float impatience, int max_items_in_order, 
			List<GameFoodItem> foodItemMenu) {
		this(caller, x_pos,  y_pos, orientation, queue_length, point_mult, money_mult, impatience, max_items_in_order, foodItemMenu, 1);
	}
	
	/** Used to set the food item order for a particular customer. Tutorial uses this method to set 
	 * the customer orders to something predictable.
	 * 
	 */
	public void setCustomerOrder(int customer_index, GameFoodItem theItem) {
		customerList.get(customer_index).setCustomerOrder(theItem);
	}
	
	/** Decrement the queue position for each customer, effective advancing the line */
	private void advanceQueue() {
		for(int i = 0; i < queue_length; i++) {
			customerList.get(i).decQueuePosition();
		}
		
		customers_processed++;
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
		
		//Happens when the queue is empty
		return null;
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
		if(head() != null && (head().getState() == Customer.STATE_ANGRY || head().getState() == Customer.STATE_SERVED || head().getState() == Customer.STATE_FINISHED)) {
			if(head().getState() == Customer.STATE_SERVED) {
				customers_satisfied++;
				Log.d(activitynametag, this.getName() + " has served " + customers_satisfied + " customers.");
			}
			advanceQueue(); 
		}
				
		
		//Toggle customer visibility
		for(int i = 0; i < queue_length; i++) {
			//Hide everything with queue position <0
			if(customerList.get(i).getQueuePosition() < 0 &&
					customerList.get(i).getState() == Customer.STATE_FINISHED) 
				customerList.get(i).setVisible(false);
					
			
			//Set the next customer that should be visible to visible IF sufficient time has passed
			//and the customer's position is between 0 (front of the line) and the last visible position
			if(!customerList.get(i).isVisible() && 
					(customerList.get(i).getQueuePosition()>=0 && 
					customerList.get(i).getQueuePosition()<QUEUE_VISIBLE_LENGTH) && 
					GameInfo.currentTimeMillis() > time_since_last_customer+TIME_BETWEEN_CUSTOMERS_MS){
				customerList.get(i).setVisible(true);
				time_since_last_customer = GameInfo.currentTimeMillis();
				
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
		if(head() != null)
			return(head().onInteraction(foodItem));
		else
			return new Interaction();
	}
	
	/** Return true if the last Customer (and by extension every other customer) in this queue has been served
	 * 
	 * @return True if the last Customer has been served, otherwise false.
	 */
	public boolean isFinished() {
		if(customerList.get(queue_length-1).orderSatisfied()) return true;
		return false;
	}
	/** Returns the number of customers that left the queue not because their order was satisfied, but because
	 * they lost patience 
	 * @return
	 */
	public int numberOfCustomersIgnored() { return (customers_processed - customers_satisfied); }
	
	/** Return the number of customers that were satisfied */
	public int numberOfCustomersServed() { return (customers_satisfied); }
	
	/** Return how many customers are remaining in the queue */
	public int numberOfCustomersLeft() { return queue_length - customers_processed; }
}
