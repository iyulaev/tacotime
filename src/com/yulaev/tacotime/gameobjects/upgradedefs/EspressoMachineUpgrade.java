/** Describe the EspressoMachine upgrade. See EspressoMachine (GameItem) class for details on what GameItem
 * this upgrade introduces into the game. See GameUpgrade class for details on what the 
 * class variables mean. 
 */

package com.yulaev.tacotime.gameobjects.upgradedefs;

import com.yulaev.tacotime.gameobjects.GameUpgrade;

public class EspressoMachineUpgrade extends GameUpgrade {
	
	public static final String UPGRADE_NAME = "espressomachine";

	public EspressoMachineUpgrade() {
		this.upgradeCost = 300;
		this.upgradeDescription = "Add a espresso machine; adds another food item but it is worth points!";
		this.upgradeName = UPGRADE_NAME;
		this.upgradeLongName = "Espresso Machine";
		this.upgradeLevel = 5;
	}
}
