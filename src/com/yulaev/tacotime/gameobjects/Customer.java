package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.Interaction;
import com.yulaev.tacotime.gamelogic.GameGrid;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

/** Customer implements a single customer that requests one or more items out of the possible foodItems list.
 * CoffeeGirl gets these items for Customer and the requirements are marked off. When all requirements are done, 
 * the customer transitions to finished and the next customer approaches the counter.
 * 
 * @author ivany
 *
 */

public class Customer extends GameActor {
	
	public static int DEFAULT_CUSTOMER_MOVERATE = 3;
	
	public static final String activitynametag = "Customer";
	
	//Define states for customer
	public static int STATE_HIDDEN = 0;
	public static int STATE_INLINE = 1;
	public static int STATE_SERVED = 2;
	public static int STATE_FINISHED = 3;
	
	//Define queue position (customer's position in CustomerQueue)
	private static int queue_position;
	
	public static int MAX_ORDER_SIZE = 1;

	/** Initialize a new Customer.
	 * 
	 * @param caller The calling Context for fetching bitmaps and such.
	 * @param move_rate The moverate for this customer (default should be Customer.DEFAULT_CUSTOMER_MOVERATE)
	 * @param starting_queue_position The (initial) queue position of this customer; will be decremented by 
	 * 	CustomerQueue as the Customer queue is advanced
	 * @param foodItemChoices The menu of GameFoodItem choices that this customer may select from
	 */
	public Customer(Context caller, int move_rate, int starting_queue_position, List<GameFoodItem> foodItemChoices) {
		super(caller, move_rate);
		
		queue_position = starting_queue_position;
		visible = false;
		
		//Generate this customer's "point multiplier" and food item order
		Random random = new Random();
		customerOrder = new ArrayList<GameFoodItem>();
		customerOrderSize = 1+random.nextInt(MAX_ORDER_SIZE);
		for(int i = 0; i <= customerOrderSize; i++) {
			//add 1 at the beginning because "nothing" is not a valid choice ;)
			int item_choice = 1 + random.nextInt(foodItemChoices.size()-1);
			customerOrder.add(foodItemChoices.get(item_choice).clone());
		}
		moneyMultiplier = random.nextFloat() + 1.0f;
		pointsMultiplier = random.nextFloat() + 1.0f;
		
		//Initialize all of the states that this Customer can have
		this.addState("hidden", R.drawable.customer_waiting);
		this.addState("inline", R.drawable.customer_waiting);
		this.addState("served", R.drawable.customer_happy);
		this.addState("finished", R.drawable.customer_waiting);
		setState(STATE_HIDDEN);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "customer";
	}
	
	/** These methods have to do with getting the position in the queue and advancing the queue */
	public int getQueuePosition() { return queue_position; }
	public void decQueuePosition() { queue_position--; } 
	
	//Define the starting location of customers, the position if they're first, second, of third, 
	//and the exit location of customers
	private int location_start_x = 40;
	private int location_start_y = GameGrid.GAMEGRID_HEIGHT - 5;
	private int locations_queue_x[] = {40,40,40};
	private int locations_queue_y[] = {GameGrid.GAMEGRID_HEIGHT - 35, GameGrid.GAMEGRID_HEIGHT - 25, GameGrid.GAMEGRID_HEIGHT - 15};
	private int locations_exit_x = GameGrid.GAMEGRID_WIDTH - 5;
	private int locations_exit_y = GameGrid.GAMEGRID_HEIGHT - 35;
	
	/** Sets the state of this customer
	 * @param new_state The State to put this customer into
	 */
	protected synchronized void setState(int new_state) {
		super.setState(new_state);
		
		if(new_state == STATE_INLINE) {
			x = location_start_x;
			y = location_start_y;
		}
		
		else if(new_state == STATE_SERVED) {
			setLocked();
			this.target_x = locations_exit_x;	
			this.target_y = locations_exit_y;
			unLock();
		}
	}
	
	/** Called when the customer needs to be updated
	 * Not only does it update the position (through super.onUpdate()) but it also runs the
	 * state machine for this customer!
	 */
	public void onUpdate() {
		super.onUpdate();
		
		if(this.getQueuePosition() < CustomerQueue.QUEUE_VISIBLE_LENGTH && getState() == STATE_HIDDEN) {
			setState(STATE_INLINE);
			visible = true;
			Log.d(activitynametag, "Customer advanced to STATE_INLINE");
		}
		
		//If state is "in line" make sure that we are standing in the appropriate part of the line
		if(this.getState() == STATE_INLINE) {
			setLocked();
			this.target_x = locations_queue_x[getQueuePosition()];			
			this.target_y = locations_queue_y[getQueuePosition()];
			unLock();
			
			//Log.d(activitynametag, "Customer is STATE_INLINE");
		}
		
		//If we are presently in line and at the front of the queue, check if our dependencies have been met
		//if they have then we can transition to served
		if(this.getState() == STATE_INLINE) {
			if(orderSatisfied()) setState(STATE_SERVED);
		}
		
		//If we have been served and have advanced to the exit location then set state to finished
		if(this.getState() == STATE_SERVED) {
			if(x == locations_exit_x && y == locations_exit_y) setState(STATE_FINISHED);
		}
		
		if(this.getState() == STATE_FINISHED) {
			visible = false;
			
		}
	}
	
	int customerOrderSize;
	ArrayList<GameFoodItem> customerOrder;
	float moneyMultiplier;
	float pointsMultiplier;
	
	/** 
	 * @return true if the interaction filled a dependency, otherwise false
	 */
	public Interaction onInteraction(String itemInteracted) {
		for(int i = 0; i < customerOrderSize; i++) {
			if(customerOrder.get(i).getName().equals(itemInteracted) && 
					(! customerOrder.get(i).isSatisfied())) {
				customerOrder.get(i).setSatisfied();
				
				//return CustomerInteraction object specifying that the interaction was successful, 
				//and also specifying the number of points and money that results
				Interaction retval = new Interaction();
				retval.was_success = true;
				retval.money_result = (int) (moneyMultiplier * ((float) customerOrder.get(i).moneyOnInteraction("Customer", 0)));
				retval.point_result = (int) (pointsMultiplier * ((float) customerOrder.get(i).pointsOnInteraction("Customer", 0)));
				
				return(retval);
			}
		}
		
		return(new Interaction());
	}
	
	/** Checks to see if this Customer's order has been satisfied
	 * 
	 * @return true if this Customer's order has been satisfies (and he can GTFO), false otherwise
	 */
	public boolean orderSatisfied() {
		for(int i = 0; i < customerOrderSize; i++) {
			if(! customerOrder.get(i).isSatisfied()) return(false);
		}
		return(true);
	}
	
	/** Returns a simple string representation of this Customer and his/her order.
	 * @return a simple string representation of this Customer and his/her order
	 */
	public String toString() {
		StringBuilder retvalBuilder = new StringBuilder();
		
		retvalBuilder.append("Customer #");
		retvalBuilder.append(queue_position);
		retvalBuilder.append(", has ");
		retvalBuilder.append(customerOrderSize);
		retvalBuilder.append(" items in order: {");
		
		for(int i = 0; i < customerOrderSize; i++) {
			retvalBuilder.append(customerOrder.get(i).getName());
			if(i != customerOrderSize-1) retvalBuilder.append(",");
		}
		
		retvalBuilder.append("}");
		
		return(retvalBuilder.toString());
	}

}
