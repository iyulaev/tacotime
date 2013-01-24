package org.coffeecats.coffeetime.gameobjects.objectdefs;

import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gameobjects.GameItem;

import org.coffeecats.coffeetime.MessageRouter;
import org.coffeecats.coffeetime.R;
import org.coffeecats.coffeetime.SoundThread;

import android.content.Context;
import android.util.Log;

/** This class describes the Coffee Machine used in the TacoTime game
 * 
 * @author ivany
 *
 */

public class CoffeeMachine extends GameItem {
	
	//Define all of the state indices
	public static final int STATE_IDLE=0;
	public static final int STATE_BREWING=1;
	public static final int STATE_DONE=2;
	
	//Define state delays
	public static final int BREW_TIME_MS = 2500;
	
	//Defines for default X and Y positions;
	public static int DEFAULT_XPOS = GameGrid.GAMEGRID_PADDING_LEFT - 14;
	public static int DEFAULT_YPOS = GameGrid.GAMEGRID_PADDING_TOP;
	public static int Y_DIST_TO_SECOND_MACHINE = 20;
	
	//keep track of how many coffee machines are instantiated (so we can name them appropriately)
	public static int instanceCount = 0;
	
	private int state_idx_brewing;
	private int state_idx_done;
	
	/** Constructor for CoffeeMachine mostly mimics a game items, except it sets the name by itself. Also it sets up
	 * all of the CoffeeMachine states and the associated bitmaps; the bitmap provided as an argument is just a "default" bitmap
	 * that probably never gets used.
	 * @param caller
	 * @param r_bitmap
	 * @param x_pos
	 * @param y_pos
	 * @param orientation
	 */
	public CoffeeMachine(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "CoffeeMachine" + (++instanceCount), r_bitmap, x_pos, y_pos, orientation, 15, 20);
		
		int brew_time = BREW_TIME_MS;
		if(GameInfo.hasUpgrade("quickbrewing")) brew_time -= 1000;
		
		//Add states that describe behavior of coffee machine
		//super.addState(String stateName, int state_delay_ms, int r_bitmap, boolean input_sensitive, boolean time_sensitive)
		this.addState("idle", 0, R.drawable.coffeemachine_idle, true, false);
		state_idx_brewing = this.addState("brewing", brew_time, R.drawable.coffeemachine, false, true);
		state_idx_done = this.addState("done", 10000, R.drawable.coffeemachine_done, true, "nothing", true);
	}
	
	protected void onChangeStatePlaySfx(int old_state, int new_state) {
		if(old_state == state_idx_brewing && new_state == state_idx_done)
			MessageRouter.sendPlayShortSfxMessage(SoundThread.SFX_GURGLE);
	}
}
