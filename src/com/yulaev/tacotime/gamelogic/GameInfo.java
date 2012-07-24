package com.yulaev.tacotime.gamelogic;

/**The GameInfo class keeps track of high-level global game data, such as how many points and how
 * much money the player has. Most methods are synchronized so that concurrency (sharing this
 * GameInfo object between multiple threads) should not be an issue.
 * @author ivany
 *
 */
public class GameInfo {
	public static int money;
	public static int points;
	public static int level;
	
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
}
