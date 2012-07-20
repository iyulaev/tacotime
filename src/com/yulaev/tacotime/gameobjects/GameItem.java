package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;

import com.yulaev.tacotime.gamelogic.State;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class GameItem implements ViewObject {
	//Enum for the orientations
	public static final int ORIENTATION_NORTH = 0;
	public static final int ORIENTATION_SOUTH = 1;
	public static final int ORIENTATION_EAST = 2;
	public static final int ORIENTATION_WEST = 3;
	
	//Enum for event types
	public static final int EVENT_NULL = 0;
	public static final int EVENT_DEFAULT = 1;
	
	static final String activitynametag = "GameItem";
	
	//The "name" of this game item
	private String itemName;
	//Represents the orientation; NORTH will have the item's sensitivity area to the SOUTH of the item's bitmap position
	private int orientation;
	//represents current position
	private int x;
	private int y;
	//represents the width and height of the item; this is taken from the bitmap provided in the constructor
	private int width, height;
	//represents the "sensitivity area" of this object; if a user clicks this sensitivity area then an event
	//is queued. If the user enters the sensitivity area AND an event is queued then an interaction, determined
	//by the GameLogic engine, will occur!
	private int sensitivity_xmin, sensitivity_xmax;
	private int sensitivity_ymin, sensitivity_ymax;
	//represents the interaction queue
	private int [] interactionQueue;
	private int interactionQueueLength;
	static final int INTERACTION_QUEUE_SIZE = 8;
	
	//bitmap (what this GameItem looks like) (may be modified when state changes)
	Bitmap bitmap;
	
	//Context where we were called from, only used to load bitmaps and other resources
	Context caller;
	
	//lock
	boolean locked;
	
	/** This constructor builds a new gameitem with a provided name and an int representing
	 * a bitmap resource.
	 */
	public GameItem(Context caller, String name, int r_bitmap, int x_pos, int y_pos, int orientation) {
		//Initialize variables to some values
		current_state_idx = 0;
		//Default bitmap is the one provided
		bitmap = BitmapFactory.decodeResource(caller.getResources(), r_bitmap);
		//Save caller, for loading future resources
		this.caller = caller;
		//Set GameItem ID
		itemName = name;
		//Determine starting position and dimensions
		x = x_pos;
		y = y_pos;
		
		Log.v(activitynametag, "Got to here 1");
		
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		
		Log.v(activitynametag, "Bitmap width = " + width + ", height = " + height);
		
		this.setOrientation(orientation);
		//Calculate sensitivity area
		sensitivity_xmin = x - width/2;
		sensitivity_ymin = y - height/2;
		sensitivity_xmax = x + width/2;
		sensitivity_ymax = y + height/2;
		if(orientation == ORIENTATION_SOUTH) sensitivity_ymin -= height;
		else if(orientation == ORIENTATION_NORTH) sensitivity_ymax += height;
		else if(orientation == ORIENTATION_EAST) sensitivity_xmin -= width;
		else if(orientation == ORIENTATION_WEST) sensitivity_xmax += width;
		
		//Initialize the interaction queue
		interactionQueue = new int[INTERACTION_QUEUE_SIZE];
		interactionQueueLength = 0;
	}
	
	/** Used to determine if a given point location is within this GameItem's "sensitivity 
	 * area", i.e. the area within which interaction between an Actor and this GameItem 
	 * can occur
	 * @param x x-coordinate to check
	 * @param y y-coordinate to check
	 * @return True if (x,y) is in this GameItem's sensitivity area, else false
	 */
	public boolean inSensitivityArea(int x, int y) {
		if(x <= sensitivity_xmax && 
				x >= sensitivity_xmin && 
				y <= sensitivity_ymax && 
				y >= sensitivity_ymin) 
			return true;
		
		return false;
	}
	
	/** Used to determine if a given ViewObject is within this GameItem's "sensitivity 
	 * area", i.e. the area within which interaction between an Actor and this GameItem 
	 * can occur
	 * @param vO The ViewObject to look at.
	 * @return True if vO is in this GameItem's sensitivity area, else false
	 */
	public boolean inSensitivityArea(ViewObject vO) {
		return inSensitivityArea(vO.getPositionX(), vO.getPositionY());
	}
	
	/**Accessor method for class variable orientation */
	public int getOrientation() { return orientation; }
	
	/**Accessor (setter) method for class variable orientation */
	private void setOrientation(int n_orientation) { orientation = n_orientation; }	
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
	}
	
	public void handleTap(int new_x, int new_y) {		
		setLocked();
		
		/*if(inSensitivityArea(new_x, new_y)) 
			Log.d(activitynametag+"."+this.getName(), "Saw tap at (" + new_x + ", " + new_y + ") this was in the sensitivity area");
		else
			Log.d(activitynametag+"."+this.getName(), "Saw tap at (" + new_x + ", " + new_y + ") this was NOT in the sensitivity area");*/
		
		if(inSensitivityArea(new_x, new_y)) queueEvent();
		else clearEvents();
		unLock();
	}
	
	/** Called when an interaction event is inserted into this GameItem's event queue */
	private void queueEvent() {
		interactionQueue[interactionQueueLength] = EVENT_DEFAULT;
		interactionQueueLength++;
		
		Log.v(activitynametag+"."+this.getName(), "Queued interaction event.");
	}
	
	/** Clears the events in this GameItem's event queue; typically occurs when the user taps 
	 * somewhere else in the screen causing the Actor to go elsewhere
	 */
	private void clearEvents() {
		interactionQueueLength = 0;
		
		Log.v(activitynametag+"."+this.getName(), "Cleared interaction events.");
	}
	
	/** 
	 * Should be called when an Actor enter's this GameItem's sensitivity area. If an event 
	 * exists in this GameItem's sensitivity queue the value of that event will be returned.
	 * @return The value of an event queued. If no interaction event has been queued we return 
	 * EVENT_NULL signifying that no event has been queued.
	 */
	public int consumeEvent() {
		
		
		if(interactionQueueLength > 0) {
			interactionQueueLength--;
			Log.v(activitynametag+"."+this.getName(), "Consumed interaction event (not null).");
			//return(interactionQueue[interactionQueueLength + 1]); TODO why doesn't this work??
			return(EVENT_DEFAULT);
		}
		else return EVENT_NULL;
	}
	
	/** These methods are used to lock and unlock the GameItem's internal variables, like position */
	public synchronized boolean setLocked(){ while(locked); locked = true; return(locked); }	
	public synchronized void unLock() { locked = false; }
	
	/** For documentation see ViewObject interface */
	public boolean isActor() {return false;}
	public boolean isItem() {return true;}
	public String getName() {return itemName;}
	public int getPositionX() { return x; }
	public int getPositionY() { return y; }
	
	/** Called when a ViewUpdate occurs. Since the ViewThread is responsible for detecting and signaling
	 * interactions the onUpdate() for a GameItem, right now, does nothing. Later it might change bitmaps
	 * or something like that
	 */
	public void onUpdate() { tryChangeState(false); }
	
	/* THIS SECTION USED TO DEFINE THE STATE MACHINE FOR THIS GAMEITEM
	 * The state machine is accessed by the GameLogic thread, to attempt to change this GameItem's state
	 * 
	 */
	
	
	/** Called when we determine that an interaction between the Actor and this GameItem 
	 * has occured (by GameLogicThread) 
	 * @return The previous state IF we transitioned to a new state, else (-1). */
	public int onInteraction() { return tryChangeState(true); }
	
	//represents the state transition times
	private ArrayList<State> validStates;
	private int current_state_idx;
	private State currentState;
	private long time_of_state_transition;
	
	/** Used when this GameItem is constructed, to add states to this GameItem 
	 * Assumption is that this is called during construction not from all of the various threads*/
	protected void addState(String stateName, int state_delay_ms, int r_bitmap, boolean input_sensitive, boolean time_sensitive) {
		if(validStates == null) validStates = new ArrayList<State>();
		
		State newState = new State();
		newState.stateName = stateName;
		newState.bitmap = BitmapFactory.decodeResource(caller.getResources(), r_bitmap);
		newState.state_delay_ms = state_delay_ms;
		newState.input_sensitive = input_sensitive;
		newState.time_sensitive = time_sensitive;
		
		validStates.add(newState);
		
		if(currentState == null) setState(0);		
	}
	
	/** Called by onInteraction only. Used to (try) to transition states. If enough time has passed and/or an interaction has occured 
	 * the state may change.
	 * @param has_interacted true if tryChangeState() was called as a response to a user interaction else false
	 * @return The previous state if state changed, otherwise (-1)
	 */
	private synchronized int tryChangeState(boolean has_interacted) {
		//If we haven't even added any states, return that state changed from 0 to 0
		//This is for "stateless" things like TrashCan
		if(validStates == null) return(0);
		//by default we do not change state; we first check to see if all conditions for changing state were met
		
		if(currentState.input_sensitive && !has_interacted) return(-1);
		
		long time_since_state_transition = System.currentTimeMillis()-time_of_state_transition;
		if(currentState.time_sensitive && (time_since_state_transition < currentState.state_delay_ms)) return(-1);
		
		//At this point we've determined that a state change can occur
		int next_state = (current_state_idx+1 < validStates.size()) ? current_state_idx+1 : 0;
		int old_state = current_state_idx;
		setState(next_state);
		return(old_state);
	}
	
	/** Change the state to something else.
	 * 
	 * @param new_state The index of the new state to set this GameItem's State to.
	 */
	private synchronized void setState(int new_state) {
		setLocked(); 
		
		currentState = validStates.get(new_state);
		current_state_idx = new_state;
		time_of_state_transition = System.currentTimeMillis();
		this.bitmap = currentState.bitmap;
		
		unLock();
	}
	
	public synchronized int getStateIdx() { 
		return current_state_idx;
	}
	
}
