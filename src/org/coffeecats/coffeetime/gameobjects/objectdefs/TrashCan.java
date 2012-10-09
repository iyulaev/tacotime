package org.coffeecats.coffeetime.gameobjects.objectdefs;

import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gameobjects.GameItem;

import android.content.Context;

import com.yulaev.tacotime.R;

/** This is a TrashCan. It is a GameItem. It is used for disposing of things for a (small) point penalty. */

public class TrashCan extends GameItem {
	
	//Defines for default X and Y positions;
	public static int DEFAULT_XPOS = GameGrid.GAMEGRID_WIDTH - GameGrid.GAMEGRID_PADDING_RIGHT + 10; //110
	public static int DEFAULT_YPOS = GameGrid.GAMEGRID_PADDING_TOP + 65;//100;
	
	/** Constructor for TrashCan; TrashCan is stateless
	 */
	public TrashCan(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "TrashCan", r_bitmap, x_pos, y_pos, orientation, 15, 20);
	}
}
