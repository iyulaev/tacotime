/** Describe the quick brewing upgrade. See GameUpgrade class for details on what the 
 * class variables mean. */

package com.yulaev.tacotime.gameobjects.upgradedefs;

import com.yulaev.tacotime.gameobjects.GameUpgrade;

public class QuickBrewingUpgrade extends GameUpgrade {
	
	public static final String UPGRADE_NAME = "quickbrewing";

	public QuickBrewingUpgrade() {
		this.upgradeCost = 200;
		this.upgradeDescription = "High Power heater for coffee machines! They now brew 1 second faster";
		this.upgradeName = UPGRADE_NAME;
		this.upgradeLongName = "High Power Brewing";
		this.upgradeLevel = 1;
		
		this.prerequisiteUpgrades = new String [1];
		prerequisiteUpgrades[0] = "secondcoffeemachine";
	}
}
