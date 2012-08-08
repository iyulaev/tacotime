package com.yulaev.tacotime.gamelogic;

/**The GameInfo class keeps track of high-level global game data, such as how many points and how
 * much money the player has. Most methods are synchronized so that concurrency (sharing this
 * GameInfo object between multiple threads) should not be an issue.
 * @author ivany
 *
 */
public class GameInfo {
	//The amount of money and points that the player currently has
	public static int money;
	public static int points;
	
	//These variables keep track of what level we are on, how many seconds remain on the clock,
	//and whether the level is in play or not
	private static int level;
	private static int levelTime;

	
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
	public static int setLevel(int new_level) {
		level = new_level;
		return(level);
	}
	
	/** Get the current level of this game.
	 * 
	 * @return The game level that this game is currently in.
	 */
	public static int getLevel() { return level; }
	
	/** Set the remaining time left to finish this level
	 * @param n_leveltime The numbre of clock ticks that shall be alloted to finish the current level.
	 * @return The (input) level time
	 */
	public static int setLevelTime(int n_leveltime) { 
		levelTime = n_leveltime; 
		return levelTime; 
	}
	
	/** Decrement the remaining time (in clock ticks) for this level
	 * @return The new value of levelTime, the remaining amount of clock ticks for this level.
	 */
	public static int decrementLevelTime() { return(--levelTime); }
	
	/** Return the remaining time for this level
	 * @return The remaining number of clock ticks for this level
	 */
	public static int getLevelTime() { return levelTime; }
	
	/** Set the mode (state) that this Game is currently in. Really this is what drives the GameLogicThread's
	 * core state machine.
	 * @param n_gamemode The mode (state) that this Game is in.
	 */
	public static void setGameMode(int n_gamemode) { gameMode = n_gamemode; }
	
	/** Get the mode (state) that this Game is in.
	 * 
	 * @return Current mode (state) of this Game.
	 */
	public static int getGameMode() { return gameMode; }
	
}
