package com.yulaev.tacotime.gameobjects.objectdefs;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gameobjects.GameItem;

import android.content.Context;

/** This class describes the Coffee Machine used in the TacoTime game
 * 
 * @author ivany
 *
 */

public class EspressoMachine extends GameItem {
	
	//Define all of the state indices
	public static final int STATE_IDLE=0;
	public static final int STATE_BREWING=1;
	public static final int STATE_DONE=2;
	
	//Define state delays
	public static final int BREW_TIME_MS = 2500;
	
	//keep track of how many coffee machines are instantiated (so we can name them appropriately)
	public static int instanceCount = 0;
	
	//Defines for default X and Y positions;
	public static int DEFAULT_XPOS = GameGrid.GAMEGRID_WIDTH - GameGrid.GAMEGRID_PADDING_RIGHT - 20;
	public static int DEFAULT_YPOS = GameGrid.GAMEGRID_PADDING_TOP - 10;//20;
	
	/** Constructor for CoffeeMachine mostly mimics a game items, except it sets the name by itself. Also it sets up
	 * all of the CoffeeMachine states and the associated bitmaps; the bitmap provided as an argument is just a "default" bitmap
	 * that probably never gets used.
	 * @param caller
	 * @param r_bitmap
	 * @param x_pos
	 * @param y_pos
	 * @param orientation
	 */
	public EspressoMachine(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "EspressoMachine" + (++instanceCount), r_bitmap, x_pos, y_pos, orientation, 20, 17);
		
		int brew_time = BREW_TIME_MS;
		
		//Add states that describe behavior of coffee machine
		//super.addState(String stateName, int state_delay_ms, int r_bitmap, boolean input_sensitive, boolean time_sensitive)
		this.addState("idle", 0, R.drawable.espresso_machine_inactive, true, false);
		this.addState("brewing", brew_time, R.drawable.espresso_machine_active, false, true);
		this.addState("done", 10000, R.drawable.espresso_machine_done, true, "nothing", true);
	}
}
