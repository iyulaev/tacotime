package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;
import java.util.HashMap;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gamelogic.State;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class CoffeeGirl implements ViewObject {
	private static final String activitynametag = "CoffeeGirl";
	
	private Bitmap bitmap;
	
	private static int DEFAULT_COFFEEGIRL_MOVERATE = 10;
	
	//represents current position
	private int x;
	private int y;
	//represents the position we move towards
	private int target_x, target_y;
	//Define the move rate, in pixels per 100ms
	private int move_rate;
	//Represents the last time we performed an onUpdate() operation
	private long time_of_last_update;
	//lock
	boolean locked;
	//Calling context, for getting resources later on
	Context caller;
	
	//Defines for states that CoffeeGirl can be in
	public static final int STATE_NORMAL = 0;
	public static final int STATE_CARRYING_COFFEE = 1;
	public static final int STATE_CARRYING_CUPCAKE = 2;
	public static final int STATE_CARRYING_BLENDEDDRINK = 3;
	
	public CoffeeGirl(Context caller, Rect canvas) {
		//Set starting position to middle of canvas
		x = canvas.width()/2;
		y = canvas.height()/2;
		target_x=x; target_y=y;
		time_of_last_update = -1;
		locked = false;
		this.caller = caller;
		
		move_rate = DEFAULT_COFFEEGIRL_MOVERATE;
		
		bitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl);
		
		this.addState("default", R.drawable.coffeegirl);
		this.addState("carrying_coffee", R.drawable.coffeegirl_w_coffee);
		this.addState("carrying_cupcake", R.drawable.coffeegirl_w_cupcake);
		this.addState("carrying_blended_drink", R.drawable.coffeegirl_w_blended_drink);
	}
	
	//TODO: Update for GameGrid, document!
	public void handleTap(int new_x, int new_y) {
		int new_x_gg = GameGrid.gameGridX(new_x);
		int new_y_gg = GameGrid.gameGridY(new_y);
		
		if(new_x_gg > GameGrid.GAMEGRID_WIDTH-GameGrid.GAMEGRID_PADDING)
			new_x_gg = (GameGrid.GAMEGRID_WIDTH-GameGrid.GAMEGRID_PADDING);
		if(new_x_gg < GameGrid.GAMEGRID_PADDING) new_x_gg = GameGrid.GAMEGRID_PADDING;
		
		if(new_y_gg > GameGrid.GAMEGRID_HEIGHT-GameGrid.GAMEGRID_PADDING)
			new_y_gg = (GameGrid.GAMEGRID_HEIGHT-GameGrid.GAMEGRID_PADDING);
		if(new_y_gg < GameGrid.GAMEGRID_PADDING) new_y_gg = GameGrid.GAMEGRID_PADDING;
		
		setLocked();
		target_x = new_x_gg;
		target_y = new_y_gg;
		unLock();
	}
	
	//TODO: Update for GameGrid, document!
	public void draw(Canvas canvas) {
		int drawn_x = GameGrid.canvasX(x);
		int drawn_y = GameGrid.canvasY(y);
		canvas.drawBitmap(bitmap, drawn_x - (bitmap.getWidth() / 2), drawn_y - (bitmap.getHeight() / 2), null);
	}
	
	/** These methods are used to lock and unlock the CoffeeGirl's internal variables, like position */
	public synchronized boolean setLocked(){ while(locked); locked = true; return(locked); }	
	public synchronized void unLock() { locked = false; }
	
	public void onUpdate() {
		
		setLocked();
		int target_x = this.target_x;			
		int target_y = this.target_y;
		unLock();
		
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
	
	/** For documentation see ViewObject interface */
	public boolean isActor() {return true;}
	public boolean isItem() {return false;}
	public String getName() {return "CoffeeGirl";}
	public int getPositionX() { return x; }
	public int getPositionY() { return y; }
	
	//represents the state of coffeegirl
	private ArrayList<State> validStates;
	private HashMap<String, State> itemToStateMap;
	private int current_state_idx;
	private State currentState;
	private String itemHolding; //the item that CoffeeGirl holds
	
	public synchronized void setItemHoldingToStateAssoc(String item, int state) {
		if(itemToStateMap == null) itemToStateMap = new HashMap<String, State>();
		itemToStateMap.put(item, validStates.get(state));
	}
	
	public synchronized void setItemHolding(String newItem) {
		itemHolding = newItem;
		setState(itemToStateMap.get(newItem).state_idx);
	}
	
	public synchronized String getItemHolding() {
		return(itemHolding);
	}
	
	/** Used when this GameItem is constructed, to add states to this GameItem 
	 * Assumption is that this is called during construction not from all of the various threads*/
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
	private synchronized void setState(int new_state) {
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
