package com.yulaev.tacotime.gamelogic;

import android.util.Log;

/** This class is used to translate from real, on-screen co-ordinates to the internal
 * game grid.
 * @author ivany
 *
 */

public class GameGrid {
	public static final int GAMEGRID_PADDING = 20;
	public static final int GAMEGRID_WIDTH = 100+(2*GAMEGRID_PADDING);
	public static final int GAMEGRID_HEIGHT = 120+(2*GAMEGRID_PADDING);
	
	
	private static int canvas_width, canvas_height;
	private static int canvas_anchor_x, canvas_anchor_y;
	//scaling factor when going from real canvas points to game grid points
	private static float scaling_factor; 
	
	public static void setupGameGrid(int n_canvas_width, int n_canvas_height) {
		float f_canvas_width = n_canvas_width;
		canvas_width = n_canvas_width;
		float f_canvas_height = n_canvas_height;
		canvas_height = n_canvas_height;
		
		//Set scaling factor so that the gamegrid's real size is less than the total screen size
		scaling_factor = (f_canvas_width/GAMEGRID_WIDTH < f_canvas_height/GAMEGRID_HEIGHT) ? 
				(f_canvas_width/GAMEGRID_WIDTH) : (f_canvas_height/GAMEGRID_HEIGHT);
		Log.v("balls", "Scaling factor choice was " + scaling_factor);
				
		//we assume that the phone aspect ratio, in portrait mode, is greater than
		//160/140 (probably a decent assumption)
		//Thus we display the game "top-aligned"
		canvas_anchor_x = canvas_width / 2;
		//canvas_anchor_x = (int) ((((float) GAMEGRID_WIDTH/2) * scaling_factor));
		canvas_anchor_y = (int) ((((float) GAMEGRID_HEIGHT/2) * scaling_factor));
	}
	
	public static int gameGridX(int canvas_x) {
		int retval = ( (int) (((float)canvas_x) / scaling_factor) );
		
		if(retval > GAMEGRID_WIDTH) return GAMEGRID_WIDTH;
		if(retval < 0) return(0);
		return(retval);
	}
	
	public static int gameGridY(int canvas_y) {
		int retval = ( (int) (((float)canvas_y) / scaling_factor) );
		
		if(retval > GAMEGRID_HEIGHT) return GAMEGRID_HEIGHT;
		if(retval < 0) return(0);
		return(retval);
	}
	
	public static int canvasX(int gamegrid_x) {
		return( (int) (((float)gamegrid_x) * scaling_factor) );
	}
	
	public static int canvasY(int gamegrid_y) {
		return( (int) (((float)gamegrid_y) * scaling_factor) );
	}
	
	public static int maxCanvasX() {
		return(canvas_anchor_x * 2);
	}
	
	public static int maxCanvasY() {
		return(canvas_anchor_y * 2);
	}
}
