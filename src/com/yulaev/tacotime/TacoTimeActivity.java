/** This class implements the very top level of the TacoTime game engine.
 * Mostly all it does is create the MaingamePanel() and set up the window appearance.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import com.yulaev.tacotime.gamelogic.GameGrid;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class TacoTimeActivity extends Activity {
	
	private static final String activitynametag = "TacoTimeActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Setup game grid, by giving it window dimensions
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        GameGrid.setupGameGrid(dm.widthPixels, dm.heightPixels);
        
        
        // Change content view so that 
        setContentView(new MainGamePanel(this));
        Log.d(activitynametag, "View added");
    }
}