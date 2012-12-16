package org.coffeecats.coffeetime.gameobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gamelogic.Interaction;
import org.coffeecats.coffeetime.utility.CircularList;
import org.coffeecats.coffeetime.utility.DirectionBitmapMap;

import org.coffeecats.coffeetime.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.SystemClock;
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
	public static int STATE_ANGRY = 3;
	public static int STATE_SERVED = 4;
	public static int STATE_FINISHED = 5;
	
	//Define waiting time and correpsonding mood
	private int seconds_between_pissed_off;
	public static int SECONDS_BW_PO_STARTING = 15;
	private int curr_mood;
	private long mood_last_updated; //time (in seconds) when mood was last updated
	
	//Define queue position (customer's position in CustomerQueue)
	private int queue_position;
	
	public static int DEFAULT_MAX_ORDER_SIZE = 2;
	private int max_order_size; //The maximum order size that a customer can have
	private static final int [] order_weights = {3,2,1};
	int queue_number; //the queue that this customer resides in (of the two possible queues in later levels)
	
	//Define Bitmap used to represent tear drop
	private static Bitmap tearDropBMP = null;
	
	//Instance counter
	private static int instanceCount = 0;
	private int instance_index;


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
			float point_mult, float money_mult, float impatience, int max_order_size,
			List<GameFoodItem> foodItemChoices, int queue_number) {
			
		super(caller, move_rate, location_start_x + ((queue_number==2) ? CustomerQueue.DISTANCE_TO_QUEUE_TWO : 0), location_start_y, true);
		
		queue_position = starting_queue_position;
		visible = false;
		
		this.queue_number = queue_number;
		
		//Generate this customer's order size
		Random random = new Random();
		this.max_order_size = max_order_size;

		int order_weight_sum = 0;
		for(int i = 0; i < ((max_order_size<order_weights.length)?max_order_size:order_weights.length); i++) 
			order_weight_sum += order_weights[i];
		
		int order_bin = random.nextInt(order_weight_sum);
		
		int order_cumulative_size = 0;
		customerOrderSize = -1;
		for(int i = 0; i < order_weights.length; i++) {
			order_cumulative_size += order_weights[i];
			
			if(order_bin < order_cumulative_size) {
				customerOrderSize = i+1;
				break;
			}
		}
		
		if(customerOrderSize == -1 || customerOrderSize > max_order_size) {
			Log.w(activitynametag, "Customer order size calculation went astray!");
			customerOrderSize = max_order_size;
		}
		
		
		//Generate this customer's food item order
		customerOrder = new ArrayList<GameFoodItem>();
		
		//We set up the "bins" for determining each menu item
		//The size of the bin will be the orderProbability of each GameFoodItem
		//If we pick a random integer, and it is between bin[i-1] and bin[i], we pick foodItemChoices.get(i)
		//Thus, items with higher orderProbability will be more likely to be chosen
		float [] order_bins = new float[foodItemChoices.size()];
		for(int i = 0; i < foodItemChoices.size(); i++) {
			if(i == 0)
				order_bins[i] = 0 + foodItemChoices.get(i).getOrderProbability();
			else
				order_bins[i] = order_bins[i-1] + foodItemChoices.get(i).getOrderProbability();
		}
		
		for(int i = 0; i <= customerOrderSize; i++) {
			//Normalize the random number we pick to the range of the bins
			float item_choice_f = random.nextFloat() * order_bins[order_bins.length-1];
			
			int item_choice = 0;
			while(order_bins[item_choice] < item_choice_f && item_choice < order_bins.length) item_choice++;
			customerOrder.add(foodItemChoices.get(item_choice).clone());
		}
		
		//Generate this customer's "point multiplier"
		moneyMultiplier = money_mult * random.nextFloat() + 1.0f;
		pointsMultiplier = point_mult * random.nextFloat() + 1.0f;
		
		//Generate impatience with a random factor
		float seconds_between_po_divisor = (random.nextFloat() + 1.0f) * impatience;
		seconds_between_pissed_off = (int) (((float)SECONDS_BW_PO_STARTING) / seconds_between_po_divisor);
		
		
		
		
		
		//Initialize all of the states that this Customer can have
		Bitmap tempBitmap;
		
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_waiting);
		this.addState("hidden", new DirectionBitmapMap(new CircularList<Bitmap>(1,tempBitmap)));
		
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_waiting);
		this.addState("inline_happy", new DirectionBitmapMap(new CircularList<Bitmap>(1,tempBitmap)));
		
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_waiting_ok);
		this.addState("inline_ok", new DirectionBitmapMap(new CircularList<Bitmap>(1,tempBitmap)));
		
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_waiting_angry);
		this.addState("inline_angry", new DirectionBitmapMap(new CircularList<Bitmap>(1,tempBitmap)));
		
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_happy);
		this.addState("served", new DirectionBitmapMap(new CircularList<Bitmap>(1,tempBitmap)));
		
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_waiting);
		this.addState("finished", new DirectionBitmapMap(new CircularList<Bitmap>(1,tempBitmap)));
		
		setState(STATE_HIDDEN);
		
		
		
		
		//Load a new CustomerSprite and set this instance's gAS to that
		gameActorSprite = new CustomerSprite(caller);
		
		//Load the teardrop
		if(tearDropBMP == null)
			tearDropBMP = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_tear_drop);
		
		instance_index = instanceCount++;
	}

	@Override
	public String getName() {
		return "customer";
	}
	
	/** These methods have to do with getting the position in the queue and advancing the queue */
	public synchronized int getQueuePosition() { return queue_position; }
	public synchronized void decQueuePosition() { queue_position--; } 
	
	//Define the starting location of customers, the position if they're first or second,  
	//and the exit location of customers
	private static int location_start_x = 40;
	private static int location_start_y = 0;
	private static int locations_queue_x[] = {40,40};
	private static int locations_queue_y[] = {19, 8};
	private static int locations_exit_x = GameGrid.GAMEGRID_WIDTH - 5;
	private static int locations_exit_y = locations_queue_y[0];
	
	/** Sets the state of this customer
	 * @param new_state The State to put this customer into
	 */
	protected synchronized void setState(int new_state) {
		int old_state = getState();
		
		super.setState(new_state);
		
		if(new_state == STATE_INLINE_HAPPY && old_state != STATE_INLINE_HAPPY) {
			x = location_start_x + ((queue_number==2) ? CustomerQueue.DISTANCE_TO_QUEUE_TWO : 0);
			y = location_start_y;
		}
		
		else if(new_state == STATE_SERVED || new_state == STATE_ANGRY) {
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
			mood_last_updated = GameInfo.currentTimeMillis() / 1000;
		}
		
		//If state is "in line" make sure that we are standing in the appropriate part of the line
		if(this.getState() == STATE_INLINE_HAPPY || this.getState() == STATE_INLINE_OK) {
			setLocked();
			this.target_x = locations_queue_x[getQueuePosition()] + ((queue_number==2) ? CustomerQueue.DISTANCE_TO_QUEUE_TWO : 0);			
			this.target_y = locations_queue_y[getQueuePosition()];
			unLock();
			
			//Log.d(activitynametag, "setting y position for customer " + instance_index + " to " + this.target_y + " (currently is " + this.y + ")");
			
			//If we are presently in line and at the front of the queue, check if our dependencies have been met
			//if they have then we can transition to served
			if(orderSatisfied()) setState(STATE_SERVED);
			
			//If we are not at the front of the line, don't get angry
			else if(getQueuePosition() > 0) {
				mood_last_updated = GameInfo.currentTimeMillis()/1000;
			}
			
			//Update mood? If we are at the front of the line...and it's been that long...
			else if(mood_last_updated + seconds_between_pissed_off < GameInfo.currentTimeMillis()/1000) {
				mood_last_updated = GameInfo.currentTimeMillis()/1000;
				
				if(this.getState() == STATE_INLINE_HAPPY) {
					setState(STATE_INLINE_OK);
					/*Log.d(activitynametag, "Customer state transitioned to OK, will update at " + (mood_last_updated + seconds_between_pissed_off) + 
							", it is currently " + (GameInfo.currentTimeMillis()/1000));*/
				}
				else if(this.getState() == STATE_INLINE_OK) {
					//setState(STATE_ANGRY);
					Log.d(activitynametag, "Customer state transitioned to ANGRY");
				}
			}
		}
		
		//If we have been served and have advanced to the exit location then set state to finished
		if(this.getState() == STATE_SERVED || this.getState() == STATE_ANGRY) {
			if(x == locations_exit_x && y == locations_exit_y) {
				setState(STATE_FINISHED);
			}
		}
		
		//If the customer has advanced to the finished state then do nothing
		//if(this.getState() == STATE_FINISHED) ;
	}
	
	/** Called when the customer needs to be drawn. Apart from drawing the customer icon we also
	 * draw the customer's order, using icons and a speech bubble (which is a 9patch drawing)
	 */
	public void draw(Canvas canvas) {		
		super.draw(canvas);
		
		int drawn_x = GameGrid.canvasX(x);
		int drawn_y = GameGrid.canvasY(y);
		
		//if(isVisible()) Log.d(activitynametag, "Drawing customer " + instance_index + " positions = " + target_x + ", " + target_y);
		
		//Hack to draw in hands correctly for customers
		if(this.USING_NEW_SPRITES) {
			int vector_x = target_x - x;
			int vector_y = target_y - y;
			this.draw(canvas, gameActorSprite.getHandsBitmap(vector_x, vector_y, 0));
		}
		
		//Draw a "tear drop" if the customer is getting unhappy
		if(isVisible() && (this.getState() == STATE_INLINE_OK || this.getState() == STATE_ANGRY)) {
			canvas.drawBitmap(tearDropBMP, GameGrid.canvasX(this.x) - 18, GameGrid.canvasY(this.y) - 4,null);
		}
		
		
		//draw the order using a 9patch speech bubble, if this Customer is visible and is waiting for their order
		//to be fulfilled
		if(isVisible() && (this.getState() == STATE_INLINE_HAPPY || this.getState() == STATE_INLINE_OK)) {
			int ICON_WIDTH = 20 + 10; //10 is for padding
			int BUBBLE_WIDTH = 54;
			int BUBBLE_HEIGHT = 32;
			
			/*TODO: this might not be great for performance (due to GC performance?)
			We can probably avoid re-drawing the speechBubble if no order items have been fulfilled AND the Customer
			hasn't moved since the last time draw() was called.*/
			NinePatchDrawable speechBubble = (NinePatchDrawable)caller.getResources().getDrawable(R.drawable.speech_bubble_sm);

			int bubble_left =  GameGrid.canvasX(this.x) + 32/2 + 2; //32 is bitmap width, +2 at the end for padding :) 
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
				
				//Calculate adjusted positions based on the size of the icons
				int foodicon_adjusted_x = foodicon_x - customerOrder.get(i).getBitmapInactive().getWidth()/2;
				int foodicon_adjusted_y = foodicon_y - customerOrder.get(i).getBitmapInactive().getHeight()/2;				
				canvas.drawBitmap(foodBitmap,foodicon_adjusted_x,foodicon_adjusted_y,null);

				foodicon_x += ICON_WIDTH;
			}
			
		}
	}
	
	//Customer order tracking variables
	int customerOrderSize;
	ArrayList<GameFoodItem> customerOrder;
	float moneyMultiplier;
	float pointsMultiplier;
	
	/** Used to set the customerOrder to a single particular menu option. This will be used by the game tutorial
	 * in order to set the Customer orders to something predictable
	 */
	public void setCustomerOrder(GameFoodItem theItem) {
		customerOrder.clear();
		customerOrder.add(theItem);
		customerOrderSize = 1;
	}
	
	/** Called when an interaction occurs with this Customer
	 * @param itemInteracted the name of the FoodItem that the interacter (CoffeeGirl) held when interacting with
	 * this customer.
	 * @return true if the interaction filled a dependency i.e. fufilled a customer's order request, otherwise false
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
		
		//If no dependency was filled (we haven't returned yet) return a new, failed (by default) Interaction
		return(new Interaction());
	}
	
	/** Checks to see if this Customer's order has been satisfied
	 * 
	 * @return true if this Customer's order has been satisfies (and he can GTFO), false otherwise
	 */
	public synchronized boolean orderSatisfied() {
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
