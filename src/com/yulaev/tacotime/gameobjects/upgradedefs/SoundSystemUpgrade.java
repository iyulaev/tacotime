/** Describe the Sound System upgrade. See GameUpgrade class for details on what the 
 * class variables mean. */

package com.yulaev.tacotime.gameobjects.upgradedefs;

import com.yulaev.tacotime.gameobjects.GameUpgrade;

public class SoundSystemUpgrade extends GameUpgrade {
	
	public static final String UPGRADE_NAME = "soundsystem";

	public SoundSystemUpgrade() {
		this.upgradeCost = 500;
		this.upgradeDescription = "Adds a sound system to keep customers entertained";
		this.upgradeName = UPGRADE_NAME;
		this.upgradeLongName = "Sound System";
		this.upgradeLevel = 2;
	}
}
