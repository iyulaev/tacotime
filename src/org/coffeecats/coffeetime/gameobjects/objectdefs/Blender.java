package org.coffeecats.coffeetime.gameobjects.objectdefs;

import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gameobjects.GameItem;

import org.coffeecats.coffeetime.R;

import android.content.Context;

/** This class describes the Blender GameItem used in the TacoTime game
 * 
 * @author ivany
 *
 */

public class Blender extends GameItem {
	
	//Define all of the state indices
	public static final int STATE_IDLE=0;
	public static final int STATE_BLENDING=1;
	public static final int STATE_DONE=2;
	
	//Defines for default X and Y positions;
	public static int DEFAULT_XPOS = GameGrid.GAMEGRID_PADDING_LEFT - 14;
	public static int DEFAULT_YPOS = GameGrid.GAMEGRID_PADDING_TOP + 45;
	
	/** Constructor for CoffeeMachine mostly mimics a game items, except it sets the name by itself. Also it sets up
	 * all of the CoffeeMachine states and the associated bitmaps; the bitmap provided as an argument is just a "default" bitmap
	 * that probably never gets used.
	 * @param caller
	 * @param r_bitmap
	 * @param x_pos
	 * @param y_pos
	 * @param orientation
	 */
	public Blender(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "Blender", r_bitmap, x_pos, y_pos, orientation, 15, 20);
		
		//Add states that describe behavior of coffee machine
		//super.addState(String stateName, int state_delay_ms, int r_bitmap, boolean input_sensitive, boolean time_sensitive)
		this.addState("idle", 0, R.drawable.blender_idle, true, "coffee", false);
		this.addState("blending", 1000, R.drawable.blender, false, true);
		this.addState("done", 7500, R.drawable.blender_done, true, "nothing", true);
	}
}
