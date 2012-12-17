/** Describe the (2nd) CoffeeMachine upgrade. See GameUpgrade class for details on what the 
 * class variables mean. */

package org.coffeecats.coffeetime.gameobjects.upgradedefs;

import org.coffeecats.coffeetime.gameobjects.GameUpgrade;

public class BlenderUpgrade extends GameUpgrade {
	
	public static final String UPGRADE_NAME = "secondblender";

	public BlenderUpgrade() {
		this.upgradeCost = 250;
		this.upgradeDescription = "Adds a second blender to the cafe";
		this.upgradeName = UPGRADE_NAME;
		this.upgradeLongName = "2nd Blender";
		this.upgradeLevel = 3;
	}
}
