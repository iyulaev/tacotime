package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;
import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gamelogic.State;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

/** A GameActor is any ViewObject in the Game that moves about and can interact with (or through) GameItems. GameActors
 * are for now limited to CoffeeGirl (the player character) and Customers, who interact with CoffeeGirl through the
 * CustomerQueue structure/GameItem.
 * @author ivany
 *
 */

public abstract class GameActor implements ViewObject {
	//Bitmap of this CoffeeGirl
	protected Bitmap bitmap;

	//represents current position (on the GameGrid)
	protected int x;
	protected int y;
	//represents the position we move towards
	protected int target_x, target_y;
	//Define the move rate, in pixels per 100ms
	protected int move_rate;
	//Represents the last time we performed an onUpdate() operation
	protected long time_of_last_update;
	//lock
	private boolean locked;
	//Calling context, for getting resources later on
	protected Context caller;
	
	//define whether this GameActor should be drawn
	protected boolean visible;
	
	/** These methods are used to lock and unlock the GameActor's internal variables, like position */
	protected synchronized boolean setLocked(){ while(locked); locked = true; return(locked); }	
	protected synchronized void unLock() { locked = false; }
	
	/** Creates a new GameActor. Strating location is set to the center of the game grid
	 * 
	 * @param caller The calling Context, for getting resources like Bitmap ids
	 * @param move_rate The move rate, in terms of grid length per second, for this GameActor
	 */
	public GameActor(Context caller, int move_rate) {
		this(caller, move_rate, GameGrid.GAMEGRID_WIDTH/2, GameGrid.GAMEGRID_HEIGHT/2);
	}
	
	/** Creates a new GameActor
	 * 
	 * @param caller The calling Context, for getting resources like Bitmap ids
	 * @param move_rate The move rate, in terms of grid length per second, for this GameActor
	 * @param starting_x The starting x location on the GameGrid
	 * @param starting_y The starting y location on the GameGrid
	 */
	public GameActor(Context caller, int move_rate, int starting_x, int starting_y) {
		//Set starting position to middle of canvas
		x = starting_x;
		y = starting_y;
		target_x=x; target_y=y;
		time_of_last_update = -1;
		locked = false;
		this.caller = caller;
		
		this.move_rate = move_rate;
		visible = true;
		
		bitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl);
	}

	/** onUpdate() gets called when GameActor needs to be updated by ViewThread
	 * 
	 */
	public void onUpdate() {
		//If we aren't visible there's no need to do motion calculation, just exit
		if(!visible) return;
		
		setLocked();
		int target_x = this.target_x;			
		int target_y = this.target_y;
		unLock();
		
		/* =-=-= CALCULATE MOTION VECTOR AND APPLY IT TO CHANGE POSITION! =-=-= */
		float max_dist_moved; //the maximum distance we can move this onUpdate()
		
		final double UNIT_INTERVAL_MS = 100.0; 
		
		//If we haven't even moved yet then just assume we can only move move_rate pixels
		if(time_of_last_update < 0)	max_dist_moved = move_rate;
		//Otherwise, assume we can move move_rate * (time since last update / 100ms) pixels
		else {
			double unit_intervals_since_moved = ( (double)(SystemClock.uptimeMillis() - time_of_last_update) ) / UNIT_INTERVAL_MS;
			max_dist_moved = (float) ( unit_intervals_since_moved * ((float)move_rate) );
		}
		
		//If we're not at our target position move towards it at move_rate
		if(target_x != x || target_y != y) {
			//Calculate distance to target
			float distance = (int) Math.sqrt((target_x - x)*(target_x - x) + (target_y - y)*(target_y - y));
			
			//Calculate the length of our vector motion
			float vector_length = Math.min( max_dist_moved, distance );
			
			//If the distance to the target is less than the maximum distance we're allowed to move, just
			//move straight to the target
			if(distance <= max_dist_moved) {
				x = target_x;
				y = target_y;
			} else {
				float vector_x, vector_y;
				//Scale vectors by the distance that we are to traverse
				vector_x = ((float)(target_x - x)) * vector_length/distance;
				vector_y = ((float)(target_y - y)) * vector_length/distance;
				
				/*if(vector_x==0 && vector_y==0 ) {
					if(Math.abs(target_x - x) > Math.abs(target_y - y)) {
						vector_x = (target_x - x) > 0 ? 1 : -1;
					} else {
						vector_y = (target_y - y) > 0 ? 1 : -1;
					}
				}*/
				
				x+=(int) vector_x;
				y+=(int) vector_y;
				
				//If we moved the actor, then log this update as having occured
				//If the actor hasn't moved (because not enough time has elapsed) then don't move the actor!
				if(! ( ((int) vector_x == 0) && ((int) vector_y == 0) ))
					time_of_last_update = SystemClock.uptimeMillis();
			}
		}
		else 
			time_of_last_update = -1;
	}

	/** handleTap() is called by InputThread on user input, it does nothing for GameActor */
	public void handleTap(int x, int y) {
		;
	}

	/** Called by the ViewThread when the CoffeeGirl is to be drawn on the canvas 
	 * @param canvas The canvas that this CoffeeGirl is to be drawn.
	 * */
	public void draw(Canvas canvas) {
		if(isVisible()) {
			int drawn_x = GameGrid.canvasX(x);
			int drawn_y = GameGrid.canvasY(y);
			canvas.drawBitmap(bitmap, drawn_x - (bitmap.getWidth() / 2), drawn_y - (bitmap.getHeight() / 2), null);
		}
	}
	
	
	
	
	

	//See ViewObject for documentation for these members
	public boolean isActor() { return true; }
	public boolean isItem() { return false;	}
	public abstract String getName();
	public int getPositionX() {	return x; }
	public int getPositionY() {	return y; }
	
	
	
	
	
	
	//represents the state of GameActor
	protected ArrayList<State> validStates; //An ArrayList of valid states for CoffeeGirl
	protected int current_state_idx; //the index of the current state
	protected State currentState; //The actual State object
	
	/** Used when this GameItem is constructed, to add states to this GameItem 
	 * Assumption is that this is called during construction not from all of the various threads
	 * 
	 * @param stateName the name of the state to add
	 * @param r_bitmap the resource representing the Bitmap that is to be drawn to represent CoffeeGirl 
	 * when she is in the state named stateName.
	 * */
	public void addState(String stateName, int r_bitmap) {
		if(validStates == null) validStates = new ArrayList<State>();
		
		State newState = new State();
		newState.stateName = stateName;
		newState.bitmap = BitmapFactory.decodeResource(caller.getResources(), r_bitmap);
		newState.state_delay_ms = 0; //all coffeegirl states are interaction-sensitive only
		newState.input_sensitive = true; //all states are input sensitive only for coffeegirl
		newState.time_sensitive = false; //all coffeegirl states are interaction-sensitive only
		newState.state_idx = validStates.size();
		
		validStates.add(newState);
		
		if(currentState == null) setState(0);	
	}
	
	/** Change the state to something else. CoffeeGirl's state is controlled through setItemHolding().
	 * 
	 * @param new_state The index of the new state to set this GameItem's State to.
	 */
	protected synchronized void setState(int new_state) {
		setLocked(); 
		
		currentState = validStates.get(new_state);
		current_state_idx = new_state;
		this.bitmap = currentState.bitmap;
		
		unLock();
	}
	
	/** Accessor method for current_state_idx; return index of the current state.
	 * 
	 * @return Index of the current CoffeeGirl State
	 */
	public int getState() {
		return(current_state_idx);
	}
	
	public void setVisible(boolean visibility) {
		visible = visibility;
	}
	
	public boolean isVisible() {
		return(visible);
	}

}
