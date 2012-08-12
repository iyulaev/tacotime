/**GameUpgrade represents an upgrade item that the user can buy or otherwise acquire while the
 * game is being played. Upgrades have various effects; some can alter characteristics of the
 * user player (for example, making CoffeeGirl walk/run faster), while some may add game items;
 * for example, the CounterTop upgrade adds a new GameItem, a countertop that the user can use as
 * temporary storage of items.
 * 
 * Generally the effects of various upgrades are handled in other classes; CoffeeGirl, for example,
 * will check the upgradesBought list in GameItem and change walking speed if the FastShoesUpgrade 
 * has been aquired. Thus, the GameUpgrade class doesn't really do much as a class by itself.
 */

package com.yulaev.tacotime.gameobjects;

public class GameUpgrade {
	//The String that gets used to represent this upgrade; alphanumeric only
	protected String upgradeName;
	
	//The String that is displayed to the user as the name of this upgrade
	protected String upgradeLongName;
	//The String that describes this upgrade; displayed to the user in the screen where this 
	//upgrade may be bought
	protected String upgradeDescription;
	//The cost (in money) of this upgrade
	protected int upgradeCost;
	//The minimum level for this upgrade (upgrade can be bought after upgradeLevel is complete)
	protected int upgradeLevel;
	
	public String getName() {
		return upgradeName;
	}
	
	//Accessor methods for GameUpgrade fields
	public String getUpgradeLongName() { return upgradeLongName; }
	public String getUpgradeDescription() { return upgradeDescription; }
	public int getUpgradeCost() { return upgradeCost; }
	public int getUpgradeLevel() { return upgradeLevel; }
}
