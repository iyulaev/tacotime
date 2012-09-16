/** Describe the "fast shoes" upgrade. See GameUpgrade class for details on what the 
 * class variables mean.  */

package com.yulaev.tacotime.gameobjects.upgradedefs;

import com.yulaev.tacotime.gameobjects.GameUpgrade;

public class FasterShoesUpgrade extends GameUpgrade {
	
	public static final String UPGRADE_NAME = "fastershoes";

	public FasterShoesUpgrade() {
		this.upgradeCost = 425;
		this.upgradeDescription = "Increase walking speed another 20%";
		this.upgradeName = UPGRADE_NAME;
		this.upgradeLongName = "Faster Shoes";
		this.upgradeLevel = 1;
		
		this.prerequisiteUpgrades = new String [1];
		prerequisiteUpgrades[0] = "fastshoes";
	}
}
