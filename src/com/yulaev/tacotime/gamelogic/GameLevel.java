/** This class implements the data that represents game setup for a particular level. 
 * level-global parameters will be set up by the constructor, the Threads will have their data
 * cleared, and new game elements will be added to complete the level setup.
 */

package com.yulaev.tacotime.gamelogic;

import android.content.Context;

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
	//bonus money/points for clearing the level within the alotted time
	protected int point_bonus, money_bonus;
	//If we don't clear it in time, what is the bonus then?
	protected float point_bonus_derating, money_bonus_derating;
	
	//how impatient are the customers
	//2.0 will cause their "patience" to tick down twice as fast
	protected float customer_impatience;
	//what is the maximum number of items a customer can order
	//should never be greater than 3
	protected int customer_max_order_size;

	protected int customer_dissatisfaction_penalty = 10;
	
	protected float proportion_customers_until_bonus = 0.8f;
	
	/** Set up this level; add all GameItems and such to the Threads, set up the Customers and such
	 * with the per-level parameters.
	 * @param vT ViewThread associated with this game session
	 * @param gLT GameLogicThread associated with this game session
	 * @param iT InputThread associated with this game session
	 */
	public void loadLevel(ViewThread vT, GameLogicThread gLT, InputThread iT, Context caller) {
		vT.reset();
		gLT.reset();
		iT.reset();
	}
	
	/** Return the number of clock ticks that should be alloted for completing this level.
	 * 
	 * @return The number of clock ticks that should be alloted for completing this level.
	 */
	public int getLevelTime() {
		return this.time_limit_sec;
	}
	
	/** Return the bonus point total for this level, given that the level was cleared in time (iff 
	 * cleared_level_in_time was true)
	 * @param cleared_level_in_time Was the level cleared in time?
	 * @return Level end bonus points
	 */
	public int getBonusPoints(int customers_served) { 
		return ((customers_served >= customersUntilBonus()) ? point_bonus : ((int) (point_bonus * point_bonus_derating) )); 
	}
	
	/** Return the bonus money total for this level, given that the level was cleared in time (iff 
	 * cleared_level_in_time was true)
	 * @param cleared_level_in_time Was the level cleared in time?
	 * @return Level end bonus money
	 */
	public int getBonusMoney(int customers_served) { 
		return ((customers_served >= customersUntilBonus()) ? money_bonus : ((int) (money_bonus * money_bonus_derating) )); 
	}
	
	public int getCustomerDissatisfactionPenalty(int customers_unsatisfied) {
		return (-1 * customers_unsatisfied * customer_dissatisfaction_penalty);
	}
	
	public int customersUntilBonus() {
		return ((int)(proportion_customers_until_bonus * customerQueue_length));
	}
}
