package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gamelogic.State;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

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
	
	/** These methods are used to lock and unlock the GameActor's internal variables, like position 
	 * TODO Should be done using wait() and notifyAll() */
	public synchronized boolean setLocked(){ while(locked); locked = true; return(locked); }	
	public synchronized void unLock() { locked = false; }
	
	public GameActor(Context caller, Rect canvas, int move_rate) {
		//Set starting position to middle of canvas
		x = canvas.width()/2;
		y = canvas.height()/2;
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
		int max_dist_moved; //the maximum distance we can move this onUpdate()
		
		//If we haven't even moved yet then just assume we can only move move_rate pixels
		if(time_of_last_update < 0)	max_dist_moved = move_rate;
		//Otherwise, assume we can move move_rate * (time since last update / 100ms) pixels
		else {
			double unit_intervals_since_moved = ( (double)System.currentTimeMillis() - time_of_last_update ) / 100.0;
			max_dist_moved = (int) ( unit_intervals_since_moved * ((double)move_rate) );
		}
		time_of_last_update = System.currentTimeMillis();
		
		//If we're not at our target position move towards it at move_rate
		if(target_x != x || target_y != y) {
			//Calculate distance to target
			int distance = (int) Math.sqrt((target_x - x)*(target_x - x) + (target_y - y)*(target_y - y));
			
			//Calculate the length of our vector motion
			int vector_length = Math.min( max_dist_moved, distance );
			
			if(vector_length < max_dist_moved) {
				x = target_x;
				y = target_y;
			} else {
				int vector_x, vector_y;
				//Scale vectors by the distance that we are to traverse
				vector_x = (target_x - x) * vector_length/distance;
				vector_y = (target_y - y) * vector_length/distance;
				
				x+=vector_x;
				y+=vector_y;
			}
		}
	}

	/** handleTap() is called by InputThread on user input, it does nothing for GameActor */
	public void handleTap(int x, int y) {
		;
	}

	/** Called by the ViewThread when the CoffeeGirl is to be drawn on the canvas 
	 * @param canvas The canvas that this CoffeeGirl is to be drawn.
	 * */
	public void draw(Canvas canvas) {
		if(visible) {
			int drawn_x = GameGrid.canvasX(x);
			int drawn_y = GameGrid.canvasY(y);
			canvas.drawBitmap(bitmap, drawn_x - (bitmap.getWidth() / 2), drawn_y - (bitmap.getHeight() / 2), null);
		}
	}
	
	
	
	
	

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

}
