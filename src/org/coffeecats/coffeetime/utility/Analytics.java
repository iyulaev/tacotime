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

	public static void beginSession(Context caller) {
		String apiKey = (String) caller.getResources().getString(R.string.flurry_apik);
		Analytics.caller = caller; 
		FlurryAgent.onStartSession(caller, apiKey);
	}
	
	public static void endSession() {
		FlurryAgent.onEndSession(Analytics.caller);
	}
	
	public static void reportLevelFinished(int level, boolean gotBonus, float customer_satisfied_proportion) {
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("level", Integer.toString(level));
		parameters.put("gotbonus", Boolean.toString(gotBonus));
		parameters.put("proportion_customers_satisfied", Float.toString(customer_satisfied_proportion));
		FlurryAgent.logEvent(levelFinishedEventStr, parameters);
	}
	
	public static void reportTutorialRun(boolean was_run) {
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("run?", Boolean.toString(was_run));
		FlurryAgent.logEvent(tutorialRunStr, parameters);
	}
}
