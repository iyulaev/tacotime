/** This class implements the very top level of the TacoTime game engine.
 * It shows the Main Menu, that's about it.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.GameDatabase;
import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gamelogic.Interaction;
import com.yulaev.tacotime.utility.Analytics;

import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class TacoTimeActivity extends Activity {
	
	private static final String activitynametag = "TacoTimeActivity";
	private boolean saved_game_exists;
	
	Dialog dialog;
	private static final int ASK_TUTORIAL_DIALOG = 1;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Change content view so that we are using mainmenulayout now!
		setContentView(R.layout.mainmenulayout);
		
		saved_game_exists = false;
		
		//GameDatabase.test(this);
		//Load the database so that we can check whether or not we have a saved game for the
		//character name specified in GameInfo.characterName
		GameDatabase testDB = new GameDatabase(this);
		testDB.open();
		testDB.loadDatabase();
		testDB.printCache();
		if(testDB.databaseCache.containsKey(GameInfo.characterName)) saved_game_exists = true;
		testDB.close();
		
		//Setup analytics
		Analytics.beginSession(this);
		
		Button newGame = (Button) findViewById(R.id.new_game);
		newGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(ASK_TUTORIAL_DIALOG);
			}
		});
		
		Button continueGame = (Button) findViewById(R.id.continue_game);
		continueGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(saved_game_exists) {
					Intent i = new Intent(v.getContext(), TacoTimeMainGameActivity.class);
					i.putExtra("LoadSavedGame", true);
					i.putExtra("WatchTutorial", false);
					startActivityForResult(i,0);
				} else {
					showNoSavedGameError(GameInfo.characterName);
				}
			}
		});
		
		Log.v(activitynametag, "By the way, we are playing character " + GameInfo.characterName);
	}
	
	/** Called when game is exited basically */
	@Override
	public void onDestroy() {
		Analytics.endSession();
	}
	
	/** Displays an AlertDialog dialog to the user, indicating that no saved game exists
	 * @param characterName The name of the character for whom no saved game exists
	 * */
	private void showNoSavedGameError(String characterName) {
       
		Builder b = new AlertDialog.Builder(this)
        .setTitle("No Saved Game")
        .setIcon(R.drawable.fooditem_coffee )
        .setMessage("Sorry! No saved game exists for " + characterName + " yet.")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	; //onClick() does nothing, just kills the dialog.
                }
        });
        
        b.show();
	}
	
	
	/** Used to launch the dialog that asks the user, "would you like to see the tutorial?"
	 */
	protected Dialog onCreateDialog(int d) {
		
		dialog = new Dialog(this);
		
		switch (d) {
			case ASK_TUTORIAL_DIALOG:				
				dialog.setContentView(R.layout.asktutorialdialog);
				dialog.setTitle("Tutorial?");
				
				Button okButton = (Button) dialog.findViewById(R.id.ok);
				okButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Analytics.reportTutorialRun(true);
						
						Intent i = new Intent(v.getContext(), TacoTimeMainGameActivity.class);
						i.putExtra("LoadSavedGame", false);
						i.putExtra("WatchTutorial", true);
						startActivityForResult(i,0);
						
						dialog.dismiss();
					}
				});
				
				Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
				cancelButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Analytics.reportTutorialRun(false);
						
						Intent i = new Intent(v.getContext(), TacoTimeMainGameActivity.class);
						i.putExtra("LoadSavedGame", false);
						i.putExtra("WatchTutorial", false);
						startActivityForResult(i,0);
						
						dialog.dismiss();
					}
				});

				dialog.show();
				break;
		}
		
		
		
		return dialog;
	}
}