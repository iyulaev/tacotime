package com.yulaev.tacotime.gameobjects;

import android.content.Context;

/** This class represents a tray of cupcakes - basically an infinite sources of pastries. Yum! */

public class CupCakeTray extends GameItem {
	public CupCakeTray(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "CupCakeTray", r_bitmap, x_pos, y_pos, orientation, 15, 15);
	}
}
