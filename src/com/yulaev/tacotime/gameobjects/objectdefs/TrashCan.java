package com.yulaev.tacotime.gameobjects.objectdefs;

import android.content.Context;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gameobjects.GameItem;

/** This is a TrashCan. It is a GameItem. It is used for disposing of things for a (small) point penalty. */

public class TrashCan extends GameItem {
	/** Constructor for TrashCan; TrashCan is stateless
	 */
	public TrashCan(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "TrashCan", r_bitmap, x_pos, y_pos, orientation, 10, 10);
	}
}
