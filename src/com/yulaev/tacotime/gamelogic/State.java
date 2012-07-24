package com.yulaev.tacotime.gamelogic;

import android.graphics.Bitmap;

/** State is simply a container class representing State information for GameItems and other things in the TacoTime
 * game engine. In particular we store the Bitmap associated with the state, the minimum delay before transitioning
 * to the next state, and whether or not the GameItem must get an interaction event before we can transition to the
 * next state.
 * @author ivany
 *
 */

public class State {
	/**the name of this state*/
	public String stateName; 
	
	/**the delay that we must wait until advancing to the next state; this is only observed
	if time_sensitive is also set to true. */
	public int state_delay_ms; 
	
	/**The Bitmap that the Object uses to draw itself while in this state*/
	public Bitmap bitmap; 
	
	/**input_sensitive: when set to true, the state can only advance once interaction has cocured
	/time_sensitive: when set to true, the state can only advance when state_delay_ms have passed
	both of these may be set to true*/
	public boolean input_sensitive, time_sensitive;
	
	/** The required "input" that the GameItem in this State must receive to advance to the next state
	in more specific terms this is name of the GameFoodItem that CoffeeGirl must provide to the
	GameItem that is in this State in order to advance to the next state
	Ignored if input_sensitive is set to false
	Ignored if set to "null" */
	public String requiredInput;
	
	/** The index of this state within the GameItem state list */
	public int state_idx;
	
	/*No methods or constructor - this is purely a data storage class! Like a C struct */
}
