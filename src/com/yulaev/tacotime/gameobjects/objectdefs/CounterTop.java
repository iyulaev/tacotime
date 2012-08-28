/** CounterTop implements a GameItem that is a counter top. CoffeeGirl can placed prepared
 * food items here without losing points. Then, CoffeeGirl can pick the prepared items up 
 * and serve them. This allows food items to be made out-of-turn with respect to the customers
 * without having to use the trash can and thereby lose points.
 * 
 * CounterTop is typically accessed/acquired through the CounterTopUpgrade.
 */

package com.yulaev.tacotime.gameobjects.objectdefs;

import android.content.Context;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.Interaction;
import com.yulaev.tacotime.gameobjects.GameItem;


public class CounterTop extends GameItem {
	//Define all of the state indices
	public static final int STATE_IDLE=0;
	public static final int STATE_HOLDING_COFFEE=1;
	public static final int STATE_HOLDING_CUPCAKE=2;
	public static final int STATE_HOLDING_BLENDEDDRINK=3;
	
	//keep track of how many coffee machines are instantiated
	public static int instanceCount = 0;
	
	/** Constructor for CoffeeMachine mostly mimics a game items, except it sets the name by itself. Also it sets up
	 * all of the CoffeeMachine states and the associated bitmaps; the bitmap provided as an argument is just a "default" bitmap
	 * that probably never gets used.
	 * @param caller
	 * @param r_bitmap
	 * @param x_pos
	 * @param y_pos
	 * @param orientation
	 */
	public CounterTop(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "CounterTop" + (++instanceCount), r_bitmap, x_pos, y_pos, orientation, 25, 25);
		
		//Add states that describe behavior of coffee machine
		//super.addState(String stateName, int state_delay_ms, int r_bitmap, boolean input_sensitive, boolean time_sensitive)
		this.addState("idle", 0, R.drawable.countertop_grey, true, false);
		this.addState("holding_coffee", 2000, R.drawable.countertop_grey_w_coffee, true, false);
		this.addState("holding_cupcake", 2000, R.drawable.countertop_grey_w_cupcake, true, false);
		this.addState("holding_blendeddrink", 2000, R.drawable.countertop_grey_w_blendeddrink, true, false);
	}
	
	/** Called by onInteraction only. Used to (try) to transition states. We define the state machine for CounterTop explicitly 
	 * here since the state transitions will be based on what GameFoodItem the CoffeeGirl is holding. Thus the tryChangeState() for
	 * GameItem is overridden.
	 * 
	 * @param has_interacted true if tryChangeState() was called as a response to a user interaction else false
	 * @param input A String representing the name of the current GameFoodItem that CoffeeGirl is holding (if any)
	 * @return The previous state if state changed, otherwise (-1)
	 */
	//@Override
	private synchronized Interaction tryChangeState(boolean has_interacted, String input) {
		//If we haven't even added any states, return that state changed from 0 to 0
		//This is for "stateless" things like TrashCan
		if(validStates == null) return(new Interaction(0));
		//by default we do not change state; we first check to see if all conditions for changing state were met
		
		if(!has_interacted) return(new Interaction(-1));
		
		//If CoffeeGirl provides is with "nothing" but we are holding something, we now hold nothing
		//GLT is responsible for changing CoffeeGirl state to now be holding something
		if(input.equals("nothing")) {
			if( !(current_state_idx == STATE_IDLE) ) {
				int old_state = current_state_idx;
				setState(STATE_IDLE);
				return(new Interaction(old_state));
			}
		}
		//If we hold nothing but coffeegirl holds something, we now will hold that something
		//GLT is expected to update CoffeeGirl state appropriately
		else {
			if(input.equals("coffee")) {
				if( current_state_idx == STATE_IDLE ) {
					int old_state = current_state_idx;
					setState(STATE_HOLDING_COFFEE);
					return(new Interaction(old_state));
				}
			}
			if(input.equals("cupcake")) {
				if( current_state_idx == STATE_IDLE ) {
					int old_state = current_state_idx;
					setState(STATE_HOLDING_CUPCAKE);
					return(new Interaction(old_state));
				}
			}
			if(input.equals("blended_drink")) {
				if( current_state_idx == STATE_IDLE ) {
					int old_state = current_state_idx;
					setState(STATE_HOLDING_BLENDEDDRINK);
					return(new Interaction(old_state));
				}
			}
		}
		
		//Default case - invalid/no interaction
		return(new Interaction(-1));
	}
	
	/** Override onUpdate, onInteraction() so that our custom tryChangeState() state machine is employed */
	@Override
	public void onUpdate() { tryChangeState(false, "null"); }
	
	/** Called when we determine that an interaction between the Actor and this GameItem 
	 * has occured (by GameLogicThread) 
	 * @param coffeeGirlHendItem The name of the GameFoodItem that CoffeeGirl currently holds
	 * @return The previous state IF we transitioned to a new state, else (-1). */
	@Override
	public Interaction onInteraction(String coffeeGirlHeldItem) { return tryChangeState(true, coffeeGirlHeldItem); }
}
