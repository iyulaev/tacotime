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
	public String stateName;
	public int state_delay_ms;
	public Bitmap bitmap;
	public boolean input_sensitive, time_sensitive;
	public String requiredInput;
	public int state_idx;
	
	//No methods or constructor - this is purely a data storage class! Like a C struct
}
