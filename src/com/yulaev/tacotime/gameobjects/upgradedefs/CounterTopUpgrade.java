package com.yulaev.tacotime.gameobjects.upgradedefs;

import com.yulaev.tacotime.gameobjects.GameUpgrade;

public class CounterTopUpgrade extends GameUpgrade {
	
	public static final String UPGRADE_NAME = "countertop";

	public CounterTopUpgrade() {
		this.upgradeCost = 30;
		this.upgradeDescription = "Add a counter top that can store an item temporarily";
		this.upgradeName = UPGRADE_NAME;
		this.upgradeLongName = "Counter Top";
		this.upgradeLevel = 0;
	}
}
