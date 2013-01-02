package org.coffeecats.coffeetime.gamelogic;

import java.util.ArrayList;

import org.coffeecats.coffeetime.gameobjects.GameUpgrade;
import org.coffeecats.coffeetime.utility.ServerAnnouncement;


import android.content.Context;
import android.os.SystemClock;
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
	public static String characterName = "John Test";
	//The current character that we are playing
	private static SavedCharacter myCharacter;
	//The database used to load and save games
	private static GameDatabase gameDB = null;
	
	//The amount of money and points that the player currently has
	public static int money;
	public static int points;
	//The amount of money and points that the player has acquired on this level
	public static int level_money, level_points;
	
	//These variables keep track of what level we are on, how many game ticks remain on the clock,
	//and whether the level is in play or not
	public static final int MAX_GAME_LEVEL=24;
	private static int level;
	private static int levelTime;
	private static int customersLeftforLevel;
	private static int customersLeftforCleared;
	private static int customersLeftforBonus;
	
	//This string array represents the various upgrades that the user has bought 
	private static ArrayList<String> upgradesBought;
	
	//State information regarding what "view state" we are currently in
	//This defines the states that the GameLogicThread can be in
	public static final int MODE_MAINGAMEPANEL_PREPLAY = 0;
	public static final int MODE_MAINGAMEPANEL_PREPLAY_MESSAGE = 2;
	public static final int MODE_MAINGAMEPANEL_INPLAY = 3;
	public static final int MODE_MAINGAMEPANEL_POSTPLAY_MESSAGE = 4;
	public static final int MODE_MAINGAMEPANEL_POSTPLAY = 5;
	public static final int MODE_GAMEMENU_VIEW = 7;
	public static final int MODE_MAINMENU_VIEW = 8;
	private static int gameMode;
	
	//Store information about server-based announcements
	private static boolean new_server_announcement = false;
	private static ServerAnnouncement serverAnnouncement;
	
	/*game time, only gets incremented  by the TimerThread
	only gets incremented during active play; if the game is paused for some reason no incrementing
	will occur
	
	A note on GameInfo.currentTimeMillis() vs System.currentTimeMillis() - the former only updates when
	the game is running (TimerThread sending out ticks) AND the GLT is in the MODE_MAINGAMEPANEL_INPLAY
	state. The granularity of these upgrades is roughly once every second. Thus, 
	GameInfo.currentTimeMillis() is appropriate to use when, for example, we are using it to track time
	between state transitions for items that occur on the scale of several seconds. For doing things 
	like adjusting rate of motion for the time between frame updates (like for GameActor) is it probably 
	inappropriate since the GameInfo.game_time_millis variable will update too slow.
	
	*/
	private static long game_time_millis;
	
	
	/** Increment money and return the new value. Can be used to simply get the value of money
	 * if increment is set to zero.
	 * @param increment The amount to increment GameInfo.money by
	 * @return The new value of money
	 */
	public static synchronized int setAndReturnMoney(int increment) {
		money += increment;
		level_money += increment;
		return money;
	}
	
	/** Increment points and return the new value. Can be used to simply get the value of points
	 * if increment is set to zero.
	 * @param increment The amount to increment GameInfo.points by
	 * @return The new value of points
	 */
	public static synchronized int setAndReturnPoints(int increment) {
		points += increment;
		level_points += increment;
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
	
	/** Set the number of customers left in the current level */
	public static synchronized void setCustomersLeft(int customersLeftforLevel, int customersLeftforBonus, int customersLeftforCleared) {
		GameInfo.customersLeftforLevel = customersLeftforLevel;
		GameInfo.customersLeftforBonus = customersLeftforBonus;
		GameInfo.customersLeftforCleared = customersLeftforCleared;
	}
	
	public static synchronized int getCustomersLeftForLevel() { return customersLeftforLevel; }
	public static synchronized int getCustomersLeftForBonus() { return customersLeftforBonus; }
	public static synchronized int getCustomersLeftForCleared() { return customersLeftforCleared; }
	
	
	/** Reset this GameInfo, effectively clearing global game state. Note, however, that characterName, 
	 * which is set by the very first "entry menu" for this game, does not get cleared. */
	public static synchronized void reset() {
		setGameMode(MODE_MAINGAMEPANEL_PREPLAY);
		setLevelTime(0);
		setLevel(0);
		
		upgradesBought = new ArrayList<String>();
		
		money = 0;
		points = 0;
		
		levelReset();
		
		game_time_millis = SystemClock.uptimeMillis();
	}
	
	
	
	/** Clear level state only; clears level_money and level_points
	 * 
	 */
	public static synchronized void levelReset() {
		level_money = 0;
		level_points = 0;
		customersLeftforLevel = 0;
		customersLeftforBonus = 0;
		customersLeftforCleared = 0;
	}
	
	/** Initializes the database and either loads the SavedCharacter keyed by GameInfo.characterName 
	 * from the database or creates a new SavedCharacter.
	 * @param context The calling context, for gameDB creation.
	 */
	public static synchronized void initDB(Context context) {
		Log.d(activitynametag, "GameInfo got initDB() call!");
		
		if(gameDB == null) {
			gameDB = new GameDatabase(context);
		} else {
			gameDB.flushCache();
		}
		
		gameDB.open();
		gameDB.loadDatabase();
		
		myCharacter = gameDB.databaseCache.get(characterName);
		
		//Shouldn't ever happen now that CharacterSelectActivity ensures the character exists in the database
		if(myCharacter == null) {
			gameDB.saveCharacterToDatabase(new SavedCharacter(characterName, "boy"));
			myCharacter = gameDB.databaseCache.get(characterName);
			Log.e(activitynametag, "GameInfo entered invalid code (myCharacter == null");
		}
		
		gameDB.close();
	}
	
	/** Reset the character parameters. If GameInfo.characterName has a valid save in the database but
	 * we want to start a new game, we must reset the parameters for the character. Note that these parameters
	 * are only written back into the DB when saveCurrentGame() gets called (this is by design, since we don't
	 * want to save the game until the user completes the first level and hits "Save & Continue")
	 */
	public static synchronized void resetCharacter() {
		myCharacter.level = 0;
		myCharacter.money = 0;
		myCharacter.points = 0;
		myCharacter.upgrades = "";
	}
	
	/** Loads the saved game for this character. Since myCharacter should have already been loaded from the
	 * database in initDB() we simply re-load the data in myCharacter to the GameInfo data. */
	public static synchronized void loadSavedGame() {		
		Log.d(activitynametag, "GameInfo got loadSavedGame() call!");
				
		level = myCharacter.level;
		points = myCharacter.points;
		money = myCharacter.money;
		
		String [] upgradesLoaded = myCharacter.upgrades.split(",");
		upgradesBought = new ArrayList<String>();
		for(String s : upgradesLoaded) {
			if(s!=null && s.length()>1) upgradesBought.add(s);
		}
	}
	
	/** Saves game state for this character. Takes the GameInfo data, puts it into myCharacter, and saves
	 * the data into gameDB. */
	public static synchronized void saveCurrentGame() {
		Log.d(activitynametag, "GameInfo got saveCurrentGame() call!");
		
		//if(myCharacter == null) myCharacter = new SavedCharacter(characterName, "boy");
		myCharacter.level = level;
		myCharacter.points = points;
		myCharacter.money = money;
		String upgrades = new String("");
		for(int i = 0; i < upgradesBought.size(); i++) upgrades += upgradesBought.get(i)+",";
		myCharacter.upgrades = upgrades;
		
		gameDB.open();
		gameDB.saveCharacterToDatabase(myCharacter);
		gameDB.close();
	}
	
	/** return the gender of the current character */
	public static synchronized String getCharacterGender() {
		return(myCharacter.type);
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
	
	public static synchronized ArrayList<String> getUpgradesBoughtCopy() {
		ArrayList<String> returned = new ArrayList<String>(upgradesBought.size());
		for(String upgrade : upgradesBought) returned.add(upgrade);	
		return(returned);
	}
	
	
	
	/** Return the current total ACTIVE play time for this game, in milliseconds */
	public static synchronized long currentTimeMillis() {
		return setAndGetGameTimeMillis(0); 
	}
	
	/** Increment and return the current total ACTIVE play time for this game, in milliseconds */
	public static synchronized long setAndGetGameTimeMillis(long increment) {
		game_time_millis += increment;
		return(game_time_millis);
	}
	
	
	/** Query GameInfo to see if there is a new serverAnnoucement. If there is, return it
	 * and unset the new_server_announcement flag. Otherwise, return null.
	 * @return serverAnnoucement if the current announcement hasn't been read yet, else null
	 */
	public synchronized static ServerAnnouncement getAnnouncement() {
		if(new_server_announcement) {
			new_server_announcement = false;
			return serverAnnouncement;
		}
		else return null;
	}
	
	/** Set the new announcement */
	public synchronized static void setAnnouncement(ServerAnnouncement n_serverAnnouncement) {
		new_server_announcement = true;
		serverAnnouncement = n_serverAnnouncement;
	}
	
}
