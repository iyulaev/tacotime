package com.yulaev.tacotime.gameobjects;

import android.content.Context;

import com.yulaev.tacotime.R;

public class TrashCan extends GameItem {
	/** Constructor for TrashCan; TrashCan is stateless
	 */
	public TrashCan(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "TrashCan", r_bitmap, x_pos, y_pos, orientation);
	}
}
