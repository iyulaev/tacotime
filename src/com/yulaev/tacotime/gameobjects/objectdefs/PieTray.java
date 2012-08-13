package com.yulaev.tacotime.gameobjects.objectdefs;

import com.yulaev.tacotime.gameobjects.GameItem;

import android.content.Context;

/** This class represents a tray of pie slices - basically an infinite sources of pie. Mmm pie. */

public class PieTray extends GameItem {
	public PieTray(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "PieTray", r_bitmap, x_pos, y_pos, orientation, 15, 15);
	}
}
