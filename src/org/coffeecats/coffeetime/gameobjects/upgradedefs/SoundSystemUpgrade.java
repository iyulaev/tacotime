/** Describe the Sound System upgrade. See GameUpgrade class for details on what the 
 * class variables mean. */

package org.coffeecats.coffeetime.gameobjects.upgradedefs;

import org.coffeecats.coffeetime.gameobjects.GameUpgrade;

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
