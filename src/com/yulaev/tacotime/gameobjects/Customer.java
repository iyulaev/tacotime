package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.Interaction;
import com.yulaev.tacotime.gamelogic.GameGrid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
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
	public static int STATE_INLINE_HAPPY = 1;
	public static int STATE_INLINE_OK = 2;
	public static int STATE_INLINE_ANGRY = 3;
	public static int STATE_SERVED = 4;
	public static int STATE_FINISHED = 5;
	
	//Define waiting time and correpsonding mood
	private int seconds_between_pissed_off;
	public static int SECONDS_BW_PO_STARTING = 25;
	private int curr_mood;
	private long mood_last_updated; //time (in seconds) when mood was last updated
	
	//Define queue position (customer's position in CustomerQueue)
	private int queue_position;
	
	public static int MAX_ORDER_SIZE = 2;

	/** Initialize a new Customer.
	 * 
	 * @param caller The calling Context for fetching bitmaps and such.
	 * @param move_rate The moverate for this customer (default should be Customer.DEFAULT_CUSTOMER_MOVERATE)
	 * @param starting_queue_position The (initial) queue position of this customer; will be decremented by 
	 * 	CustomerQueue as the Customer queue is advanced
	 * @param point_mult = level-dependent multiplier for points
	 * @param money_mult = level-dependent multiplier for money
	 * @param impatience How quickly this customer becomes impatient
	 * @param foodItemChoices The menu of GameFoodItem choices that this customer may select from
	 */
	public Customer(Context caller, int move_rate, int starting_queue_position, 
			float point_mult, float money_mult, float impatience, List<GameFoodItem> foodItemChoices) {
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
		moneyMultiplier = money_mult * random.nextFloat() + 1.0f;
		pointsMultiplier = point_mult * random.nextFloat() + 1.0f;
		
		//Generate impatience with a random factor
		float seconds_between_po_divisor = (random.nextFloat() + 1.0f) * impatience;
		seconds_between_pissed_off = (int) (((float)SECONDS_BW_PO_STARTING) / seconds_between_po_divisor);
		
		//Initialize all of the states that this Customer can have
		this.addState("hidden", R.drawable.customer_waiting);
		this.addState("inline_happy", R.drawable.customer_waiting);
		this.addState("inline_ok", R.drawable.customer_waiting_ok);
		this.addState("inline_angry", R.drawable.customer_waiting_angry);
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
	
	//Define the starting location of customers, the position if they're first or second,  
	//and the exit location of customers
	private int location_start_x = 40;
	private int location_start_y = GameGrid.GAMEGRID_HEIGHT - 5;
	private int locations_queue_x[] = {40,40};
	private int locations_queue_y[] = {GameGrid.GAMEGRID_HEIGHT - 30, GameGrid.GAMEGRID_HEIGHT - 12};
	private int locations_exit_x = GameGrid.GAMEGRID_WIDTH - 5;
	private int locations_exit_y = GameGrid.GAMEGRID_HEIGHT - 35;
	
	/** Sets the state of this customer
	 * @param new_state The State to put this customer into
	 */
	protected synchronized void setState(int new_state) {
		super.setState(new_state);
		
		if(new_state == STATE_INLINE_HAPPY) {
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
		
		/* Customer state machine */
		//If customer has been set visible (by the CustomerQueue) and it is currently in the hidden state
		//then advance to the in-line state
		if(this.isVisible() && getState() == STATE_HIDDEN) {
			setState(STATE_INLINE_HAPPY);
			mood_last_updated = System.currentTimeMillis() / 1000;
			//Log.d(activitynametag, "Customer advanced to STATE_INLINE");
		}
		
		//If state is "in line" make sure that we are standing in the appropriate part of the line
		if(this.getState() == STATE_INLINE_HAPPY || this.getState() == STATE_INLINE_OK || this.getState() == STATE_INLINE_ANGRY) {
			setLocked();
			this.target_x = locations_queue_x[getQueuePosition()];			
			this.target_y = locations_queue_y[getQueuePosition()];
			unLock();
			
			//If we are presently in line and at the front of the queue, check if our dependencies have been met
			//if they have then we can transition to served
			if(orderSatisfied()) setState(STATE_SERVED);
			
			//Update mood?
			if(mood_last_updated + seconds_between_pissed_off < (System.currentTimeMillis()/1000)) {
				mood_last_updated = System.currentTimeMillis()/1000;
				
				if(this.getState() == STATE_INLINE_HAPPY) setState(STATE_INLINE_OK);
				else if(this.getState() == STATE_INLINE_OK) setState(STATE_INLINE_ANGRY);
			}
		}
		
		//If we have been served and have advanced to the exit location then set state to finished
		if(this.getState() == STATE_SERVED) {
			if(x == locations_exit_x && y == locations_exit_y) setState(STATE_FINISHED);
		}
		
		//If the customer has advanced to the finished state then do nothing
		if(this.getState() == STATE_FINISHED) ;
		
	}
	
	/** Called when the customer needs to be drawn. Apart from drawing the customer icon we also
	 * draw the customer's order, using icons and a speech bubble (which is a 9patch drawing)
	 */
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		//draw the order using a 9patch speech bubble, if this Customer is visible and is waiting for their order
		//to be fulfilled
		if(isVisible() && (this.getState() == STATE_INLINE_HAPPY || this.getState() == STATE_INLINE_OK || this.getState() == STATE_INLINE_ANGRY)) {
			int ICON_WIDTH = 20 + 10; //10 is for padding
			int BUBBLE_WIDTH = 54;
			int BUBBLE_HEIGHT = 32;
			
			//TODO: this might not be great for performance (due to GC performance?)
			NinePatchDrawable speechBubble = (NinePatchDrawable)caller.getResources().getDrawable(R.drawable.speech_bubble_sm);

			int bubble_left =  GameGrid.canvasX(this.x) + this.bitmap.getWidth()/2 + 2; //+2 at the end for padding :)
			int bubble_top = GameGrid.canvasY(this.y) - speechBubble.getMinimumHeight()/2;
			int bubble_right = bubble_left + BUBBLE_WIDTH + (ICON_WIDTH*(customerOrderSize-1));
			int bubble_bottom = bubble_top + BUBBLE_HEIGHT;
			
			speechBubble.setBounds(bubble_left, bubble_top, bubble_right, bubble_bottom);
			speechBubble.draw(canvas);
			
			//Now draw the actual food items in the bubble!
			int foodicon_x = bubble_left + 34 - 6; //this puts is right in the middle of the area for the left-most icon
				//within the speech bubble; I don't know why the (-6) looks better but it does
			int foodicon_y = (bubble_top + bubble_bottom) / 2;
			
			//Draw an icon for each food item on the customer's order
			for(int i = 0; i < customerOrderSize; i++) {
				Bitmap foodBitmap;
				
				if(customerOrder.get(i).isSatisfied()) 
					foodBitmap = customerOrder.get(i).getBitmapInactive();
				else 
					foodBitmap = customerOrder.get(i).getBitmapActive();
				
				int foodicon_adjusted_x = foodicon_x - customerOrder.get(i).getBitmapInactive().getWidth()/2;
				int foodicon_adjusted_y = foodicon_y - customerOrder.get(i).getBitmapInactive().getHeight()/2;				
				canvas.drawBitmap(foodBitmap,foodicon_adjusted_x,foodicon_adjusted_y,null);

				foodicon_x += ICON_WIDTH;
			}
			
		}
	}
	
	int customerOrderSize;
	ArrayList<GameFoodItem> customerOrder;
	float moneyMultiplier;
	float pointsMultiplier;
	
	/** 
	 * @return true if the interaction filled a dependency i.e. fufilled a customer's order request,
	 * otherwise false
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
	 * 
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
