package org.coffeecats.coffeetime.gameobjects.objectdefs;

import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gameobjects.GameItem;

import com.yulaev.tacotime.R;

import android.content.Context;

/** This class describes the microwave used in the TacoTime game.
 * The microwave makes sandwiches
 * 
 * @author ivany
 *
 */

public class Microwave extends GameItem {
	
	//Define all of the state indices
	public static final int STATE_IDLE=0;
	public static final int STATE_BREWING=1;
	public static final int STATE_DONE=2;
	
	//Define state delays
	public static final int BAKE_TIME_MS = 4000;
	
	//keep track of how many coffee machines are instantiated (so we can name them appropriately)
	public static int instanceCount = 0;
	
	//Defines for default X and Y positions;
	public static int DEFAULT_XPOS = GameGrid.GAMEGRID_WIDTH - GameGrid.GAMEGRID_PADDING_RIGHT + 13; //113
	public static int DEFAULT_YPOS = GameGrid.GAMEGRID_PADDING_TOP + 5;//40;
	
	/** Constructor for CoffeeMachine mostly mimics a game items, except it sets the name by itself. Also it sets up
	 * all of the CoffeeMachine states and the associated bitmaps; the bitmap provided as an argument is just a "default" bitmap
	 * that probably never gets used.
	 * @param caller
	 * @param r_bitmap
	 * @param x_pos
	 * @param y_pos
	 * @param orientation
	 */
	public Microwave(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "Microwave" + (++instanceCount), r_bitmap, x_pos, y_pos, orientation, 15, 20);
		
		int bake_time = BAKE_TIME_MS;
		//if(GameInfo.hasUpgrade("quickbrewing")) brew_time -= 1000;

		//Add states that describe behavior of coffee machine
		//super.addState(String stateName, int state_delay_ms, int r_bitmap, boolean input_sensitive, boolean time_sensitive)
		this.addState("idle", 0, R.drawable.microwave_inactive, true, false);
		this.addState("baking", bake_time, R.drawable.microwave_active, false, true);
		this.addState("done", 10000, R.drawable.microwave_done, true, "nothing", true);
	}
}
