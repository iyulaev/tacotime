/** This class implements the almost top-level of the TacoTime game engine, which actually
 * displays the game grid and other things. Typically we enter here from the main menu.
 * Mostly all it does is create the MaingamePanel() and set up the window appearance.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import java.util.ArrayList;

import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gamelogic.GameInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class TacoTimeMainGameActivity extends Activity {
	
	private static final String activitynametag = "TacoTimeMainGameActivity";
	
	private TacoTimeMainGameActivity me;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(activitynametag, "TTMGA constructor called!");
		me = this;
		
		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Determine whether to load a saved game or not
		boolean load_saved_game = false;
		Intent i = getIntent();
		load_saved_game = i.getBooleanExtra("LoadSavedGame", false);
		
		// Setup game grid, by giving it window dimensions
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		GameGrid.setupGameGrid(dm.widthPixels, dm.heightPixels);
		
		MainGamePanel mgpView = new MainGamePanel(this, load_saved_game);
		
		//Used only for allocating an intent to be launched from 
		final Context ttmgaContext = mgpView.getContext(); //this seems wrong...
		
		//Define a handler for handling messages from things like GLT, informing us that we need to launch an activity
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//If we get this message from the GameLogicThread when we've just finished
				//a level and need to load the between-level menu
				if(msg.what == GameLogicThread.MESSAGE_LEVEL_END) {
					Intent i = new Intent(ttmgaContext, BetweenLevelMenuActivity.class);
					startActivityForResult(i,0);
				}
				//If we get a "game over" message then bounce the user back to the main menu
				else if(msg.what == GameLogicThread.MESSAGE_GAME_END) {
					finish();
				}
				//If we get a "display level end dialog" message then bounce the user back to the main menu
				else if(msg.what == GameLogicThread.MESSAGE_POSTLEVEL_DIALOG_OPEN) {
					//Un-marshall all point counts from the message object
					ArrayList<Integer> pointArray = (ArrayList<Integer>) msg.obj;
					int total_points = pointArray.get(0).intValue();
					int total_money = pointArray.get(1).intValue();
					int accrued_points = pointArray.get(2).intValue();
					int accrued_money = pointArray.get(3).intValue();
					int bonus_points = pointArray.get(4).intValue();
					int bonus_money = pointArray.get(5).intValue();
					
					StringBuilder dialogMessageBuilder = new StringBuilder();
					dialogMessageBuilder.append("Level over!\n");
					dialogMessageBuilder.append("Accrued Points: " + accrued_points + "\n");
					dialogMessageBuilder.append("Bonus Points: " + bonus_points + "\n");
					dialogMessageBuilder.append("Total Points: " + total_points + "\n\n");
					dialogMessageBuilder.append("Accrued Money: " + accrued_money + "\n");
					dialogMessageBuilder.append("Bonus Money: " + bonus_money + "\n");
					dialogMessageBuilder.append("Total Money: " + total_money);					
					
					//Put together the AlertDialog that will ask the user main menu/retry/continue
					AlertDialog.Builder builder = new AlertDialog.Builder(me);
					builder.setMessage(dialogMessageBuilder.toString())
						.setCancelable(true)
						
						//Maps to "return to main menu"
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								MessageRouter.sendPostLevelDialogClosedMessage();
							}
						});
					
					AlertDialog alert = builder.create();
					
					//use an OnDismissListener so that if the user hits back again, it is the same as hitting 'continue'
					alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							MessageRouter.sendPostLevelDialogClosedMessage();
						}
					});
					
					alert.show();
				}
			}
		};
		
		MessageRouter.ttaHandler = handler; 
		
		// Change content view so that we can see the MainGamePanel!
		setContentView(mgpView);
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
					
					//Destroy TTMGA/MGP and go back to the main menu activity
					finish();
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