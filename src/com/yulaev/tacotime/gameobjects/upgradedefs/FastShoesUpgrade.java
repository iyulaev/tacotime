/** Describe the "fast shoes" upgrade. See GameUpgrade class for details on what the 
 * class variables mean.  */

package com.yulaev.tacotime.gameobjects.upgradedefs;

import com.yulaev.tacotime.gameobjects.GameUpgrade;

public class FastShoesUpgrade extends GameUpgrade {
	
	public static final String UPGRADE_NAME = "fastshoes";

	public FastShoesUpgrade() {
		this.upgradeCost = 150;
		this.upgradeDescription = "Increase walking speed 20%";
		this.upgradeName = UPGRADE_NAME;
		this.upgradeLongName = "Fast Shoes";
		this.upgradeLevel = 0;
	}
}
