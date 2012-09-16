package com.yulaev.tacotime.gameobjects;

import java.util.ArrayList;

import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gamelogic.Interaction;
import com.yulaev.tacotime.gamelogic.State;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/** GameItems in general are non-player interactive items, like CoffeeMachine. They are ViewObjects so
 * they inhabit the GameGrid and are rendered onto the game canvas separately. They are also stateful 
 * although their state machines only allow them to advance from state n to (n+1) (or to wrap).
 * @author ivany
 *
 */
public class GameItem implements ViewObject {
	//Enum for the orientations
	public static final int ORIENTATION_NORTH = 0;
	public static final int ORIENTATION_SOUTH = 1;
	public static final int ORIENTATION_EAST = 2;
	public static final int ORIENTATION_WEST = 3;
	
	//Enum for event types
	public static final int EVENT_NULL = 0;
	public static final int EVENT_DEFAULT = 1;
	
	//Determine whether to draw the sensitivity area
	public static final boolean DRAW_SENSITIVITY_AREA = true;
	
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
	protected Bitmap bitmap;
	
	//Context where we were called from, only used to load bitmaps and other resources
	Context caller;
	
	//lock
	boolean locked;
	
	/** This constructor builds a new GameItem with a provided name and an int representing
	 * a bitmap resource.
	 * 
	 * @param caller The calling Context; mostly used for fetching Bitmaps in a totally uncool way.
	 * @param name The name of this GameItem
	 * @param r_bitmap The resource identifier of the bitmap representing this GameItem; note that this will 
	 * be replaced by whatever bitmaps are associated with the state that this GameItem is in, if this 
	 * GameItem is given more than zero states.
	 * @param x_pos The starting x position (on the GameGrid) of this GameItem
	 * @param y_pos The starting y position (on the GameGrid) of this GameItem
	 * @param orientation The orientation of the sensitivity area relative to where this GameItem is placed; valid 
	 * entries are GAMEITEM_ORIENTATION*
	 * @param gg_width The width of this item on the GameGrid. Also dictates the width of the GameGrid
	 * @param gg_height The height of this item on the GameGrid. Also dictates the height of the GameGrid
	 */
	public GameItem(Context caller, String name, int r_bitmap, int x_pos, int y_pos, int orientation, int gg_width, int gg_height) {
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
		
		width = gg_width;
		height = gg_height;
		
		Log.v(activitynametag, "Bitmap width = " + width + ", height = " + height);
		
		this.setOrientation(orientation);
		//Calculate sensitivity area (using orientation to determine where it is placed relative to the 
		//position of this GameItem)
		
		final float SENSITIVITY_EXPANSION_FACTOR_RIGHTSIDE = 0.75f;
		final float SENSITIVITY_EXPANSION_FACTOR_OTHERSIDE = 0.25f;
		
		sensitivity_xmin = x - width/2;
		sensitivity_ymin = y - height/2;
		sensitivity_xmax = x + width/2;
		sensitivity_ymax = y + height/2;
		if(orientation == ORIENTATION_SOUTH) { 
			sensitivity_ymin -= (SENSITIVITY_EXPANSION_FACTOR_RIGHTSIDE*height); 
			sensitivity_ymax += (SENSITIVITY_EXPANSION_FACTOR_OTHERSIDE*height); 
		}
		else if(orientation == ORIENTATION_NORTH) {
			sensitivity_ymax += (SENSITIVITY_EXPANSION_FACTOR_RIGHTSIDE*height);
			sensitivity_ymin -= (SENSITIVITY_EXPANSION_FACTOR_OTHERSIDE*height); 
		} 
		else if(orientation == ORIENTATION_EAST) {
			sensitivity_xmin -= (SENSITIVITY_EXPANSION_FACTOR_RIGHTSIDE*width);
			sensitivity_xmax += (SENSITIVITY_EXPANSION_FACTOR_OTHERSIDE*width);
		}
		else if(orientation == ORIENTATION_WEST) {
			sensitivity_xmax += (SENSITIVITY_EXPANSION_FACTOR_RIGHTSIDE*width);
			sensitivity_xmin -= (SENSITIVITY_EXPANSION_FACTOR_OTHERSIDE*width);
		}
		
		Log.d(activitynametag, "Item: " + name + ", sensitivity = (" + sensitivity_xmin + ", " + sensitivity_xmax + 
				", " + sensitivity_ymin + ", " + sensitivity_ymax + ")");
		
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
	
	/** Draws this GameItem to the Canvas canvas
	 * @param canvas The Canvas to draw this GameItem onto
	 */
	public void draw(Canvas canvas) {
		if(DRAW_SENSITIVITY_AREA) {
			int draw_max_x = GameGrid.canvasX(sensitivity_xmax);
			int draw_min_x = GameGrid.canvasX(sensitivity_xmin);
			int draw_min_y = GameGrid.canvasY(sensitivity_ymin);
			int draw_max_y = GameGrid.canvasY(sensitivity_ymax);
			
			Paint linePaint = new Paint();
			linePaint.setColor(Color.BLUE);
			linePaint.setStrokeWidth(3);
			
			canvas.drawLine(draw_min_x, draw_min_y, draw_min_x, draw_max_y, linePaint);
			canvas.drawLine(draw_min_x, draw_min_y, draw_max_x, draw_min_y, linePaint);
			canvas.drawLine(draw_max_x, draw_max_y, draw_max_x, draw_min_y, linePaint);
			canvas.drawLine(draw_max_x, draw_max_y, draw_min_x, draw_max_y, linePaint);
		}
		
		int draw_x = GameGrid.canvasX(x);
		int draw_y = GameGrid.canvasY(y);
		canvas.drawBitmap(bitmap, draw_x - (bitmap.getWidth() / 2), draw_y - (bitmap.getHeight() / 2), null);
	}
	
	/** called when a tap (user input) occurs somewhere on the canvas. Note that the coordinates provided as 
	 * arguments are canvas, not GameGrid, coordinates.
	 * 
	 * @param new_x_canvas the X coordinate of the user input tap
	 * @param new_y_canvas the Y coordinate of the user input tap
	 */
	public void handleTap(int new_x_canvas, int new_y_canvas) {	
		int new_x = GameGrid.gameGridX(new_x_canvas);
		int new_y = GameGrid.gameGridY(new_y_canvas);
		
		//setLocked();
		
		if(inSensitivityArea(new_x, new_y)) queueEvent();
		else clearEvents();
		//unLock();
	}
	
	/** Called when an interaction event is inserted into this GameItem's event queue. This generally occurs when the
	 * GameItem or its sensitivity area is tapped. When CoffeeGirl enters a GameItem's sensitivity area the 
	 * queue is checked for pending interactions; an interaction only occurs if a pending interaction is in the queue. */
	private void queueEvent() {
		interactionQueue[interactionQueueLength] = EVENT_DEFAULT;
		interactionQueueLength++;
		
		//Log.v(activitynametag+"."+this.getName(), "Queued interaction event.");
	}
	
	/** Clears the events in this GameItem's event queue; typically occurs when the user taps 
	 * somewhere else in the screen causing the Actor to go elsewhere. When there are no interactions queued the 
	 * CoffeeGirl cannot interact with a GameItem even if the sensitivity area is entered.
	 */
	private void clearEvents() {
		interactionQueueLength = 0;
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
			return(EVENT_DEFAULT);
		}
		else return EVENT_NULL;
	}
	
	/** These methods are used to lock and unlock the GameItem's internal variables, like position*/
	/*public synchronized boolean setLocked(){ while(locked); locked = true; return(locked); }	
	public synchronized void unLock() { locked = false; }*/
	
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
	 * Also it deals with returning the results of interactions (i.e. when the state has changed as a
	 * result of an interaction with a GameActor)
	 */
	
	
	/** Called when we determine that an interaction between the Actor and this GameItem 
	 * has occured (by GameLogicThread) 
	 * @param coffeeGirlHendItem The name of the GameFoodItem that CoffeeGirl currently holds
	 * @return The previous state IF we transitioned to a new state, else (-1). */
	public Interaction onInteraction(String coffeeGirlHeldItem) { return tryChangeState(true, coffeeGirlHeldItem); }
	
	//Valid states of this GameItem
	protected ArrayList<State<Bitmap>> validStates;
	//The index of the current state of this GameItem
	protected int current_state_idx;
	//The current State that this game item is in
	protected State <Bitmap> currentState;
	//The time that the current state was entered; mostly used to determine when the State can be transitioned of
	//of if the State is delay sensitive
	private long time_of_state_transition;
	
	/** Used when this GameItem is constructed, to add states to this GameItem 
	 * Assumption is that this is called during construction not from all of the various threads
	 * 
	 * @param stateName The name of the state
	 * @param state_delay_ms The delay until this state may be exited, ignored unless time_sensitive is set to true
	 * @param r_bitmap the resource identifier of the Bitmap representing the GameItem when it is in this State
	 * @param input_sensitive Whether interactions with CoffeeGirl must occur to exit this state
	 * @param requiredInput The required GameFoodItem that CoffeeGirl must be holding when she interacts with this state, 
	 * if any
	 * @param time_sensitive Whether this State requires that some amount of time elapse before we can exit it
	 * 
	 * */
	protected void addState(String stateName, int state_delay_ms, int r_bitmap, boolean input_sensitive, String requiredInput, boolean time_sensitive) {
		if(validStates == null) validStates = new ArrayList<State<Bitmap>>();
		
		State <Bitmap> newState = new State<Bitmap>();
		newState.stateName = stateName;
		newState.bitmap = BitmapFactory.decodeResource(caller.getResources(), r_bitmap);
		newState.state_delay_ms = state_delay_ms;
		newState.input_sensitive = input_sensitive;
		newState.time_sensitive = time_sensitive;
		newState.requiredInput = requiredInput;
		
		validStates.add(newState);
		
		if(currentState == null) setState(0);		
	}
	protected void addState(String stateName, int state_delay_ms, int r_bitmap, boolean input_sensitive, boolean time_sensitive) {
		addState(stateName, state_delay_ms, r_bitmap, input_sensitive, "null", time_sensitive);
	}
	
	/** Called by onInteraction only. Used to (try) to transition states. If enough time has passed and/or an interaction has occured 
	 * the state may change.
	 * @param has_interacted true if tryChangeState() was called as a response to a user interaction else false
	 * @param input A String representing the name of the current GameFoodItem that CoffeeGirl is holding (if any)
	 * @return The previous state if state changed, otherwise (-1)
	 */
	private synchronized Interaction tryChangeState(boolean has_interacted, String input) {
		//If we haven't even added any states, return that state changed from 0 to 0
		//This is for "stateless" things like TrashCan
		if(validStates == null) return(new Interaction(0));
		//by default we do not change state; we first check to see if all conditions for changing state were met
		
		long time_since_state_transition = GameInfo.currentTimeMillis()-time_of_state_transition;
		if(currentState.time_sensitive && (time_since_state_transition > currentState.state_delay_ms)) return(doChangeState());
		
		if(currentState.input_sensitive && has_interacted) {
			//If the current state requires an input item and the provided item is what is required
			//OR no input is required, the change state
			if((currentState.requiredInput.equals("null")) || (currentState.requiredInput.equals(input))) 
				return(doChangeState());
		}

		return(new Interaction(-1));		
	}
	private synchronized Interaction tryChangeState(boolean has_interacted) {
		return(tryChangeState(has_interacted, "null"));
	}
	
	/** Change the state of this GameItem to the next valid state and return the relevant
	 * Interaction object describing the state change.
	 */
	private synchronized Interaction doChangeState() {
		//At this point we've determined that a state change can occur
		int next_state = (current_state_idx+1 < validStates.size()) ? current_state_idx+1 : 0;
		int old_state = current_state_idx;
		setState(next_state);
		return(new Interaction(old_state));
	}
	
	/** Change the state to something else.
	 * 
	 * @param new_state The index of the new state to set this GameItem's State to.
	 */
	protected synchronized void setState(int new_state) {
		currentState = validStates.get(new_state);
		current_state_idx = new_state;
		time_of_state_transition = GameInfo.currentTimeMillis();
		this.bitmap = currentState.bitmap;
	}
}
