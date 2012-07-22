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
}
