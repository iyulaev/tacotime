/** This class implements the almost top-level of the TacoTime game engine, which actually
 * displays the game grid and other things. Typically we enter here from the main menu.
 * Mostly all it does is create the MaingamePanel() and set up the window appearance. Also it
 * creates and responds to the in-game dialog that gets launched when the users presses the 
 * back button (see onBackPressed()) and displays the end-of-level dialog (handler gets message 
 * GameLogicThread.MESSAGE_POSTLEVEL_DIALOG_OPEN)
 * 
 * @author iyulaev
 */
 
 //megan was here

package org.coffeecats.coffeetime;

import java.util.ArrayList;

import org.coffeecats.coffeetime.gamelogic.GameDatabase;
import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gamelogic.SavedCharacter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TacoTimeMainGameActivity extends Activity {
	
	private static final String activitynametag = "TacoTimeMainGameActivity";
	
	private TacoTimeMainGameActivity me;
	
	public static final int ASK_TUTORIAL_DIALOG = 0;
	public static final int LEVEL_END_DIALOG = 1;
	public static final int IN_GAME_DIALOG = 2;
	public static final int LEVEL_FAILED_DIALOG = 3;
	
	//The maingamepanel which will contain the canvas for the whole game
	MainGamePanel mgpView;
	//Dialog which gets launched
	Dialog dialog;
	String dialogTextStr;
	
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
		boolean watch_tutorial = false;
		
		Intent i = getIntent();
		load_saved_game = i.getBooleanExtra("LoadSavedGame", false);
		watch_tutorial = i.getBooleanExtra("WatchTutorial", false);
		
		// Setup game grid, by giving it window dimensions
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		GameGrid.setupGameGrid(dm.widthPixels, dm.heightPixels);
		
		//Create the MainGamePanel! MGP contains the actual canvas that will get drawn to
		//Also MGT loads and kicks off all of the logic/sound/view Threads
		mgpView = new MainGamePanel(this, load_saved_game, watch_tutorial);
		
		//Used only for allocating an intent to be launched from 
		final Context ttmgaContext = mgpView.getContext(); //this seems wrong...
		
		//Define a handler for handling messages from things like GLT, informing us that we need to launch an activity
		Handler handler = new Handler() {
			@SuppressWarnings("deprecation")
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
					dialogTextStr = dialogMessageBuilder.toString();
					
					showDialog(LEVEL_END_DIALOG);
				}
				else if(msg.what == GameLogicThread.MESSAGE_LEVEL_FAILED) {
					dialogTextStr=new String("Didn't clear enough customers! Must clear at least " + 
							msg.arg1 + ", you cleared " + msg.arg2 + ".");
					showDialog(LEVEL_FAILED_DIALOG);
				}
				else if(msg.what == GameLogicThread.MESSAGE_NEW_MACHINES_DIALOG) {
					ArrayList<ArrayList<Integer>> newMachines = (ArrayList<ArrayList<Integer>>) msg.obj;
					Log.d(activitynametag, "Displaying new machines dialog");
					displayNewMachinesDialog(newMachines);
				}
			}
		};
		
		MessageRouter.ttaHandler = handler; 
		
		// Change content view so that we can see the MainGamePanel!
		setContentView(mgpView);
	}
	
	
	/** Mostly just used to destroy the MainGamePanel, which itself calls destroy() on SoundThread,
	 * which stops all music and deallocates all MediaPlayer resources.
	 */
	@Override
	public void onDestroy () {
		mgpView.destroy();
		super.onDestroy();
	}
	
	/** When the Back button gets pressed during game play, we should NOT actually go back; rather, we should
	 * launch a dialog that asks the user whether they want to either
	 * 1. return to main menu
	 * 2. retry the level
	 * 3. continue
	 * The game will get paused when this happens. Note that all of the logic gets handled in the 
	 * InputThread, which then shoots some messages off to the other threads to co-ordinate activities.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		Log.d(activitynametag, "onBackPressed Called");
		
		//inform the InputThread that the in-game dialog has been launched
		MessageRouter.sendBackButtonDuringGameplayMessage(); 
		
		showDialog(IN_GAME_DIALOG);
	}
	
	
	
	/** Used to launch the end-of-level dialog and also the in-game dialog
	 * 
	 * Level-end dialog specified points, monye, etc earned during the level and as bonus
	 * In-game dialog asks whether user wants to continue, retry the level, or return to main menu
	 * Level failed dialog specifies that not enough customers were cleared when the level ended
	 * so the player will have to retry!
	 */
	protected Dialog onCreateDialog(int d) {
		
		//Dismiss any old dialogs, just for fun
		if(dialog != null) {
			dialog.dismiss();
		}
		
		switch (d) {
			//Handle the dialog that gets displayed at the very end of the level
			case LEVEL_END_DIALOG:				
				LevelEndDialog levelEndDialog = new LevelEndDialog(this);
				levelEndDialog.show();
				dialog = levelEndDialog;
				break;
				
			//Display the in-game dialog that pauses the game
			//and asks the user what they'd like to do
			case IN_GAME_DIALOG:	
				InGameDialog inGameDialog = new InGameDialog(this);
				inGameDialog.show();
				dialog = inGameDialog;
				break;

			//This dialog gets launched via a message passed from GameLogicThread
			//It tells the user that the level has been failed and they must retry it
			case LEVEL_FAILED_DIALOG:				
				LevelFailedDialog levelFailedDialog = new LevelFailedDialog(this);
				levelFailedDialog.show();
				dialog = levelFailedDialog;
				break;
		}
		
		return dialog;
	}
	
	/** Used to launch the "New Machines" dialog
	 * @param newMachines An array of arrays; each sub-array gives the bitmap resource values (ints)
	 * that should be displayed to show the relationships between machines and food items
	 */
	private void displayNewMachinesDialog(ArrayList<ArrayList<Integer>> newMachines) {
		MessageRouter.sendPauseGLTMessage(true); // pauses the game

		NewMachinesDialog newMachinesDialog = new NewMachinesDialog(this, newMachines);
		newMachinesDialog.show();
		
		dialog = newMachinesDialog;
	}
	
	
	/** This is the dialog that shows level-end summary when a level is cleared 
	 * successfully
	 * @author ivany
	 *
	 */
	class LevelEndDialog extends Dialog {

        protected LevelEndDialog(Context context) {
            super(context);
            
            setContentView(R.layout.okdialog);
        	setTitle("Level Complete!");
        	
        	TextView dialogText = (TextView) findViewById(R.id.dialogtext);
        	dialogText.setText(dialogTextStr);
        	
        	//OK button closes and ends the level
        	Button okButton = (Button) findViewById(R.id.ok);
        	okButton.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View v) {
        			MessageRouter.sendPostLevelDialogClosedMessage();
        			dismiss();
        		}
        	});
        	
        	//back button is the same as hitting OK
        	this.setOnCancelListener(new DialogInterface.OnCancelListener() {
        		public void onCancel(DialogInterface dialog) {
        			MessageRouter.sendPostLevelDialogClosedMessage();
        			dismiss();
        		}
        	});
        }
    }
	
	/** This is the dialog that is displayed when not enough customers are cleared, i.e.
	 * the level is failed. It prompts the user to retry the level.
	 * @author ivany
	 *
	 */
	class LevelFailedDialog extends Dialog {

        protected LevelFailedDialog(Context context) {
            super(context);
            
            setContentView(R.layout.okdialog);
        	setTitle("Level Failed");
        	
        	TextView dialogText = (TextView) findViewById(R.id.dialogtext);
        	dialogText.setText(dialogTextStr);
        	
        	//OK button closes and ends the level
        	Button okButton = (Button) findViewById(R.id.ok);
        	okButton.setText("Retry Level");
        	
        	okButton.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View v) {
        			dismiss();
        			MessageRouter.sendLoadGameMessage();
        		}
        	});
        	
        	//back button is the same as hitting OK
        	this.setOnCancelListener(new DialogInterface.OnCancelListener() {
        		public void onCancel(DialogInterface dialog) {
        			dismiss();
        			MessageRouter.sendLoadGameMessage();
        		}
        	});

            
        }

    }
	
	/** This is the dialog that is displayed when the back button is pressed during gameplay. 
	 * The dialog allows the user to retry the level, end the game (return to main menu) or
	 * continue.
	 * @author ivany
	 *
	 */
	class InGameDialog extends Dialog {

        protected InGameDialog(Context context) {
            super(context);
            
			setContentView(R.layout.ingamedialog);
			setTitle("Game Paused");
			
			Button mmButton = (Button) findViewById(R.id.mainmenubutton);
			Button retryButton = (Button) findViewById(R.id.retrylevelbutton);
			Button continueButton = (Button) findViewById(R.id.continuebutton);
			
			mmButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					MessageRouter.sendInGameDialogResult(InputThread.INGAMEDIALOGRESULT_MAIN_MENU);
					dismiss();
					//Destroy TTMGA/MGP and go back to the main menu activity
					finish();
					
				}
			});
			
			retryButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					MessageRouter.sendInGameDialogResult(InputThread.INGAMEDIALOGRESULT_RETRY_LEVEL);
					dismiss();
				}
			});
			
			continueButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					MessageRouter.sendInGameDialogResult(InputThread.INGAMEDIALOGRESULT_CONTINUE);
					dismiss();
				}
			});
			
			//back button is the same as hitting "Continue"
			setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					MessageRouter.sendInGameDialogResult(InputThread.INGAMEDIALOGRESULT_CONTINUE);
					dismiss();
				}
			});
        }
    }
	
	/** This is the dialog that is displayed when we begin a new level and the new level has some kind of machine(s)
	 * that require explanation
	 * @author ivany
	 *
	 */
	class NewMachinesDialog extends Dialog {
		/** Create a new NewMachinesDialog 
		 * 
		 * @param context The calling context
		 * @param newMachines An array of arrays; each sub-array gives the bitmap resource values (ints)
		 * that should be displayed to show the relationships between machines and food items
		 */
        protected NewMachinesDialog(Context context, ArrayList<ArrayList<Integer>> newMachines) {
            super(context);
            
			setContentView(R.layout.newmachinesdialog);
			setTitle("New Machines");
			
			Button okButton = (Button) findViewById(R.id.ok);
			LinearLayout newMachinesContainer = (LinearLayout) findViewById(R.id.new_machines_container);
			
			//Figure out what bitmaps we're supposed to put into the dialog and insert them
			LayoutInflater inflater=LayoutInflater.from(context);
			
			for(int i = 0; i < newMachines.size(); i++) {
				View newEntry = inflater.inflate(R.layout.newmachinesdialogentry, null);
				LinearLayout parent = (LinearLayout) newEntry.findViewById(R.id.newmachinesdialogentry_root);
				
				for(int j = 0; j < newMachines.get(i).size(); j++) {
					if(j!=0) {
						ImageView arrowImage = new ImageView(context);
						arrowImage.setImageResource(R.drawable.rarrow_blue);
						parent.addView(arrowImage);
					}
					
					ImageView machineEntry = new ImageView(context);
					machineEntry.setImageResource(newMachines.get(i).get(j));
					parent.addView(machineEntry);
				}
				
				newMachinesContainer.addView(newEntry);
			}
			
			//OK button closes dialog and tells the game to continue
			okButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					MessageRouter.sendPauseGLTMessage(false);
					dismiss();
				}
			});
			
			//back button is the same as hitting "OK"
			setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					MessageRouter.sendPauseGLTMessage(false);
					dismiss();
				}
			});
        }
    }
}