package com.yulaev.tacotime.gamelogic;

import java.util.ArrayList;

import com.yulaev.tacotime.gameobjects.GameUpgrade;

import android.util.Log;

/**The GameInfo class keeps track of high-level global game data, such as how many points and how
 * much money the player has. Most methods are synchronized so that concurrency (sharing this
 * GameInfo object between multiple threads) should not be an issue.
 * @author ivany
 *
 */
public class GameInfo {
	public static final String activitynametag = "GameInfo";
	
	//The name of the "character" that the player has chosen. Should be used to load and save games to
	//the saved game database.
	public static String characterName = "null";
	
	//The amount of money and points that the player currently has
	public static int money;
	public static int points;
	
	//These variables keep track of what level we are on, how many seconds remain on the clock,
	//and whether the level is in play or not
	private static int level;
	private static int levelTime;
	
	//This string array represents the various upgrades that the user has bought 
	private static ArrayList<String> upgradesBought;
	
	//State information regarding what "view state" we are currently in
	public static final int MODE_MAINGAMEPANEL_PREPLAY = 0;
	public static final int MODE_MAINGAMEPANEL_PREPLAY_MESSAGE = 2;
	public static final int MODE_MAINGAMEPANEL_INPLAY = 3;
	public static final int MODE_MAINGAMEPANEL_POSTPLAY_MESSAGE = 4;
	public static final int MODE_MAINGAMEPANEL_POSTPLAY = 5;
	public static final int MODE_GAMEMENU_VIEW = 6;
	public static final int MODE_MAINMENU_VIEW = 7;
	private static int gameMode;
	
	
	/** Increment money and return the new value. Can be used to simply get the value of money
	 * if increment is set to zero.
	 * @param increment The amount to increment GameInfo.money by
	 * @return The new value of money
	 */
	public static synchronized int setAndReturnMoney(int increment) {
		money += increment;
		return money;
	}
	
	/** Increment points and return the new value. Can be used to simply get the value of points
	 * if increment is set to zero.
	 * @param increment The amount to increment GameInfo.points by
	 * @return The new value of points
	 */
	public static synchronized int setAndReturnPoints(int increment) {
		points += increment;
		return points;
	}
	
	/** Set the current level of the game; each level gives a different set of machines and a different 
	 * array of customers.
	 * 
	 * @param new_level The new level that we will be on.
	 * @return The new level that the game is set to.
	 */
	public static synchronized int setLevel(int new_level) {
		level = new_level;
		return(level);
	}
	
	/** Get the current level of this game.
	 * 
	 * @return The game level that this game is currently in.
	 */
	public static synchronized int getLevel() { return level; }
	
	/** Set the remaining time left to finish this level
	 * @param n_leveltime The numbre of clock ticks that shall be alloted to finish the current level.
	 * @return The (input) level time
	 */
	public static synchronized int setLevelTime(int n_leveltime) { 
		levelTime = n_leveltime; 
		return levelTime; 
	}
	
	/** Decrement the remaining time (in clock ticks) for this level
	 * @return The new value of levelTime, the remaining amount of clock ticks for this level.
	 */
	public static synchronized int decrementLevelTime() { return(--levelTime); }
	
	/** Return the remaining time for this level
	 * @return The remaining number of clock ticks for this level
	 */
	public static synchronized int getLevelTime() { return levelTime; }
	
	/** Set the mode (state) that this Game is currently in. Really this is what drives the GameLogicThread's
	 * core state machine.
	 * @param n_gamemode The mode (state) that this Game is in.
	 */
	public static synchronized void setGameMode(int n_gamemode) { gameMode = n_gamemode; }
	
	/** Get the mode (state) that this Game is in.
	 * 
	 * @return Current mode (state) of this Game.
	 */
	public static synchronized int getGameMode() { return gameMode; }
	
	
	
	
	/** Reset this GameInfo, effectively clearing global game state. Note, however, that characterName, 
	 * which is set by the very first "entry menu" for this game, does not get cleared. */
	public static synchronized void reset() {
		setGameMode(MODE_MAINGAMEPANEL_PREPLAY);
		setLevelTime(0);
		setLevel(0);
		
		upgradesBought = new ArrayList<String>();
		
		money = 0;
		points = 0;
	}
	
	/** Loads the saved game for this character. Not implemented yet. */
	public static synchronized void loadSavedGame() {
		Log.d(activitynametag, "GameInfo got loadSavedGame() call, but not implemented yet.");
	}
	
	/** Saves game state for this character. Not implemented yet. */
	public static synchronized void saveCurrentGame() {
		Log.d(activitynametag, "GameInfo got saveCurrentGame() call, but not implemented yet.");
	}
	
	/** Adds an upgrade that we've acquired to the list of bought upgrades (upgradesBought)
	 * 
	 * @param upgrade The GameUpgrade to add to the list of upgrades that we've bought
	 */
	public static synchronized void addUpgrade(GameUpgrade upgrade) {
		if(upgradesBought == null) upgradesBought = new ArrayList<String>();
		upgradesBought.add(upgrade.getName());
		
		Log.d(activitynametag, "Bought upgrade " + upgrade.getName());
	}
	
	/** Check if we've already bought the GameUpgrade upgrade
	 * 
	 * @param upgrade The upgrade to check if we've bought
	 * @return true if in this game upgrade has been bought/aquired, else false
	 */
	public static synchronized boolean hasUpgrade(GameUpgrade upgrade) {
		return(hasUpgrade(upgrade.getName()));
	}
	
	/** Return true if upgradeName exists in upgradesBought
	 * 
	 * @param upgradeName The upgrade we check the existence of in upgradesBought
	 * @return true if in this game we've aquired an upgrade with name upgradeName, else false
	 */
	public static synchronized boolean hasUpgrade(String upgradeName) {
		for(int count = 0 ; count < upgradesBought.size(); count++) {
			if(upgradesBought.get(count).equals(upgradeName)) return(true);
		}
		
		return(false);
	}
	
}
