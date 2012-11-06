/** This class is a utility class for handling analytics-related calls. It gets called from various 
 * portions of the CoffeeTime game, in order to report (anonymous!) data describing game usage and
 * gameplay statistics (to help us calibrate level difficulty, for example).
 */

package org.coffeecats.coffeetime.utility;

import java.util.TreeMap;

import android.content.Context;

import com.flurry.android.FlurryAgent;
import org.coffeecats.coffeetime.R;

public class Analytics {
	//Define event types
	private static final String levelFinishedEventStr = "LevelReached";
	private static final String tutorialRunStr = "TutorialRun";
	
	private static Context caller;

	/** Initialize a new flurry analytics session */
	public static void beginSession(Context caller) {
		String apiKey = (String) caller.getResources().getString(R.string.flurry_apik);
		Analytics.caller = caller; 
		FlurryAgent.onStartSession(caller, apiKey);
	}
	
	/* Ends this flurry analytics session */
	public static void endSession() {
		FlurryAgent.onEndSession(Analytics.caller);
	}
	
	/** Report that a level has been completed
	 * 
	 * @param level The level that was cleared
	 * @param all_customers_served Whether all of the customers in the queue have been served
	 * @param gotBonus Whether the bonus was obtained
	 * @param customer_satisfied_proportion The proportions of customers that were served (versus the total size of the queue)
	 */
	public static void reportLevelFinished(int level, boolean  all_customers_served, boolean gotBonus, float customer_satisfied_proportion) {
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("level", Integer.toString(level));
		parameters.put("servedallcust", Boolean.toString(all_customers_served));
		parameters.put("gotbonus", Boolean.toString(gotBonus));
		parameters.put("proportion_customers_satisfied", Float.toString(customer_satisfied_proportion));
		FlurryAgent.logEvent(levelFinishedEventStr, parameters);
	}
	
	/** Report that the level was NOT cleared and the user was asked to restart
	 * 
	 * @param level The level was wasn't cleared
	 * @param customer_served The number of customers that the player managed to serve
	 * @param required_to_clear The number of customers the player HAS to server in order to clear the level
	 * @param customer_satisfied_proportion The proportion of customers that was served
	 */
	public static void reportLevelFailed(int level, int customer_served, int required_to_clear, float customer_satisfied_proportion) {
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("level", Integer.toString(level));
		parameters.put("customersserved", Integer.toString(customer_served));
		parameters.put("customerstoclear", Integer.toString(required_to_clear));
		parameters.put("proportion_customers_satisfied", Float.toString(customer_satisfied_proportion));
		FlurryAgent.logEvent(levelFinishedEventStr, parameters);
	}
	
	/** Reports whether the tutorial was run
	 * 
	 * @param was_run Whether the user ran the tutorial or not 
	 */
	public static void reportTutorialRun(boolean was_run) {
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("run?", Boolean.toString(was_run));
		FlurryAgent.logEvent(tutorialRunStr, parameters);
	}
}
