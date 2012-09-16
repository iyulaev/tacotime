package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;
import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gamelogic.State;
import com.yulaev.tacotime.utility.CircularList;
import com.yulaev.tacotime.utility.DirectionBitmapMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.SystemClock;

/** A GameActor is any ViewObject in the Game that moves about and can interact with (or through) GameItems. GameActors
 * are for now limited to CoffeeGirl (the player character) and Customers, who interact with CoffeeGirl through the
 * CustomerQueue structure/GameItem.
 * 
 * GameActor uses a DirectionBitmapMap, containing multiple CircularLists, to draw the sprite representing this GameActor. 
 * The drawn sprite will vary depending on the heading of this GameActor. 
 * 
 * @author ivany
 *
 */

public abstract class GameActor implements ViewObject {
	//Bitmap of this CoffeeGirl
	protected DirectionBitmapMap bitmapmap;

	//represents (interpolated) current position (on the GameGrid)
	protected int x, y; 
	//represents a more precise current position on the GameGrid
	private float x_real, y_real;
	//represents the position we move towards
	protected int target_x, target_y;
	
	//Define the move rate, in pixels per UNIT_INTERVAL_MS ms
	protected int move_rate;
	final double UNIT_INTERVAL_MS = 200.0; 
	//Represents the last time we performed an onUpdate() operation
	protected long time_of_last_update;
	
	//Represents the last time that we DREW this GameActor
	protected long last_time_drawn;
	//Number of ms between sprite frames
	protected long SPRITE_FRAME_PERIOD_MS = 250;
	
	
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
		x_real = starting_x;
		y = starting_y;
		y_real = starting_y;
		
		target_x=x; target_y=y;
		
		time_of_last_update = -1;
		locked = false;
		this.caller = caller;
		
		this.move_rate = move_rate;
		visible = true;
		last_time_drawn = -1;
		
		bitmapmap = new DirectionBitmapMap(false);
		bitmapmap.setDirectionList(0, new CircularList<Bitmap>(1,
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl)));
	}

	/** onUpdate() gets called when GameActor needs to be updated by ViewThread
	 * 
	 */
	@SuppressLint("FloatMath")
	public void onUpdate() {
		//If we aren't visible there's no need to do motion calculation, just exit
		if(!visible) return;
		
		setLocked();
		int target_x = this.target_x;			
		int target_y = this.target_y;
		unLock();
		
		/* =-=-= CALCULATE MOTION VECTOR AND APPLY IT TO CHANGE POSITION! =-=-= */
		float max_dist_moved; //the maximum distance we can move this onUpdate()
		
		//If we haven't even moved yet then just assume we can't move and calculate again on the next frame
		if(time_of_last_update < 0)	{
			max_dist_moved = 0.0f;
		}
		//Otherwise, assume we can move move_rate * (time since last update / 100ms) pixels
		else {
			double unit_intervals_since_moved = ( (double)(SystemClock.uptimeMillis() - time_of_last_update) ) / UNIT_INTERVAL_MS;
			max_dist_moved = (float) ( unit_intervals_since_moved * ((float)move_rate) );
		}
		
		//If we're not at our target position move towards it at move_rate
		if(target_x != x || target_y != y) {
			//Calculate distance to target
			float distance = (float) Math.sqrt((target_x - x_real)*(target_x - x_real) + (target_y - y_real)*(target_y - y_real));
			
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
				vector_x = ((float)(target_x - x_real)) * vector_length/distance;
				vector_y = ((float)(target_y - y_real)) * vector_length/distance;
				
				x_real += vector_x;
				y_real += vector_y;
				x = (int) Math.round(x_real); 
				y = (int) Math.round(y_real);
			}
		}
		
		time_of_last_update = SystemClock.uptimeMillis();
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
			Bitmap bitmap;
			
			if(SystemClock.uptimeMillis() > last_time_drawn + SPRITE_FRAME_PERIOD_MS ) {
				bitmap = bitmapmap.getDirectionList(target_x - x, target_y - y).getNext();
				last_time_drawn = SystemClock.uptimeMillis();
			} else {
				bitmap = bitmapmap.getDirectionList(target_x - x, target_y - y).getCurrent();
			}
			
			this.draw(canvas, bitmap);
		}
	}
	
	protected void draw(Canvas canvas, Bitmap bitmap) {
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
	protected ArrayList<State<DirectionBitmapMap>> validStates; //An ArrayList of valid states for CoffeeGirl
	protected int current_state_idx; //the index of the current state
	protected State <DirectionBitmapMap> currentState; //The actual State object
	
	/** Used when this GameItem is constructed, to add states to this GameItem 
	 * Assumption is that this is called during construction not from all of the various threads
	 * 
	 * @param stateName the name of the state to add
	 * @param r_bitmap the resource representing the Bitmap that is to be drawn to represent CoffeeGirl 
	 * when she is in the state named stateName.
	 * */
	public void addState(String stateName, DirectionBitmapMap bitmapList) {
		if(validStates == null) validStates = new ArrayList<State<DirectionBitmapMap>>();
		
		State <DirectionBitmapMap> newState = new State<DirectionBitmapMap>();
		newState.stateName = stateName;
		newState.bitmap = bitmapList;
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
		this.bitmapmap = currentState.bitmap;
		
		unLock();
	}
	
	/** Accessor method for current_state_idx; return index of the current state.
	 * 
	 * @return Index of the current CoffeeGirl State
	 */
	public int getState() {
		return(current_state_idx);
	}
	
	/** Set whether this GameActor is visible.
	 * 
	 * @param visibility new visibility setting
	 */
	public void setVisible(boolean visibility) {
		visible = visibility;
	}
	
	/** Determine whether this GameActor is currently visible (and will be drawn to the canvas)
	 * 
	 * @return Whether this GameActor is visible.
	 */
	public boolean isVisible() {
		return(visible);
	}

}
