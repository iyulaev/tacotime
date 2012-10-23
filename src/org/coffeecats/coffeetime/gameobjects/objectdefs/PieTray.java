package org.coffeecats.coffeetime.gameobjects.objectdefs;

import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gameobjects.GameItem;

import android.content.Context;

/** This class represents a tray of pie slices - basically an infinite sources of pie. Mmm pie. */

public class PieTray extends GameItem {
	
	//Defines for default X and Y positions;
	public static int DEFAULT_XPOS = GameGrid.GAMEGRID_WIDTH - GameGrid.GAMEGRID_PADDING_RIGHT + 13; //113;
	public static int DEFAULT_YPOS = GameGrid.GAMEGRID_PADDING_TOP + 20; //60
	
	public PieTray(Context caller, int r_bitmap, int x_pos, int y_pos, int orientation) {
		super(caller, "PieTray", r_bitmap, x_pos, y_pos, orientation, 15, 20);
	}
}
