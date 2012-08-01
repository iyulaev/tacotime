/** This class implements the data that represents game setup for a particular level. 
 * level-global parameters will be set up by the constructor, the Threads will have their data
 * cleared, and new game elements will be added to complete the level setup.
 */

package com.yulaev.tacotime.gamelogic;

import com.yulaev.tacotime.GameLogicThread;
import com.yulaev.tacotime.InputThread;
import com.yulaev.tacotime.ViewThread;

public class GameLevel {
	//What level number is this
	protected int level_number;
	
	//How long is the customer queue for this level
	protected int customerQueue_length;
	//what is the time limit for the level (in seconds)
	protected int time_limit_sec;
	
	//multipliers for money and points for this level
	protected float point_mult,money_mult;
	//bonus money/points for clearing 
	protected int point_bonus, money_bonus;
	
	//how impatient are the customers
	//2.0 will cause their "patience" to tick down twice as fast
	protected float customer_impatience;
	
	//upgrades available (not yet implemented)
	protected String [] upgradesAvailable;
	
	/** Set up this level; add all GameItems and such to the Threads, set up the Customers and such
	 * with the per-level parameters.
	 * @param vT ViewThread associated with this game session
	 * @param gLT GameLogicThread associated with this game session
	 * @param iT InputThread associated with this game session
	 */
	public void loadLevel(ViewThread vT, GameLogicThread gLT, InputThread iT) {
		vT.reset();
		gLT.reset();
		iT.reset();
	}

}
