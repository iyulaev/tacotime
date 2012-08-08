/** This class implements the almost top-level of the TacoTime game engine, which actually
 * displays the game grid and other things. Typically we enter here from the main menu.
 * Mostly all it does is create the MaingamePanel() and set up the window appearance.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import com.yulaev.tacotime.gamelogic.GameGrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class TacoTimeMainGameActivity extends Activity {
	
	private static final String activitynametag = "TacoTimeMainGameActivity";
	
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
		
		// Change content view so that we can see the MainGamePanel!
		setContentView(new MainGamePanel(this));
	}
	
	/** When the Back button gets pressed during game play, we should NOT actually go back; rather, we should
	 * launch a dialog that asks the user whether they want to either
	 * 1. return to main menu
	 * 2. retry the level
	 * 3. continue
	 * The game will get paused when this happens. Note that all of the logic gets handled in the 
	 * InputThread, which then shoots some messages off to the other threads to co-ordinate activities.
	 */
	@Override
	public void onBackPressed() {
		Log.d(activitynametag, "onBackPressed Called");
		
		//inform the InputThread that the in-game dialog has been launched
		MessageRouter.sendBackButtonDuringGameplayMessage(); 
		
		//Put together the AlertDialog that will ask the user main menu/retry/continue
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Game Paused. What would you like to do?")
			.setCancelable(true)
			
			//prototype for setButton():
			//void setButton(CharSequence text, DialogInterface.OnClickListener listener)
			
			//Maps to "return to main menu"
			//TODO: should this method directly destroy MainGamePanel and then destroy this Activity?
			.setPositiveButton("Main Menu", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MessageRouter.sendInGameDialogResult(InputThread.INGAMEDIALOGRESULT_MAIN_MENU);
				}
			})
			//Maps to "retry level"
			.setNeutralButton("Retry Level", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MessageRouter.sendInGameDialogResult( InputThread.INGAMEDIALOGRESULT_RETRY_LEVEL);
				}
			})
			//Maps to "continue"
			.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MessageRouter.sendInGameDialogResult(InputThread.INGAMEDIALOGRESULT_CONTINUE);
				}
			});
		
		AlertDialog alert = builder.create();
		
		//use an OnDismissListener so that if the user hits back again, it is the same as hitting 'continue'
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				MessageRouter.sendInGameDialogResult(InputThread.INGAMEDIALOGRESULT_CONTINUE);
			}
		});
		
		alert.show();
		
	}

}