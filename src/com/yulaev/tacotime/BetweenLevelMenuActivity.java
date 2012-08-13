/** This class implements the Activity that drives the between-level menu. This is the menu that 
 * is shown to the user between levels (upon completing a level) and allows the user to buy upgrades and
 * save & continue, or to retry the level.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import com.yulaev.tacotime.R;

import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class BetweenLevelMenuActivity extends Activity {
	
	private static final String activitynametag = "BetweenLevelMenuActivity";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		
		// Change content view so that we are using mainmenulayout now!
		setContentView(R.layout.betweenlevelmenu);
		
		//To retry a level we kill BetweenLevelMenuActivity after sending a message through MessageRouter
		//to load the last saved game (which takes us back to the beginning of the last level)
		Button retryLevel = (Button) findViewById(R.id.retry_level);
		retryLevel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MessageRouter.sendLoadGameMessage();
				finish();
			}
		});
		
		//To save & continue we send a message using MessageRouter to do this and kill BetweenLevelMenuActivity
		Button saveAndContinue = (Button) findViewById(R.id.save_and_continue);
		saveAndContinue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MessageRouter.sendNextLevelMessage();
				finish();
			}
		});
		
		//Launch UpgradeMenuActivity to display to the user a list of available upgrades
		Button viewBuyUpgrades = (Button) findViewById(R.id.view_buy_upgrades);
		viewBuyUpgrades.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//showUnimplementedError("View / Buy Upgrades");
				Intent i = new Intent(v.getContext(), UpgradeMenuActivity.class);
				startActivityForResult(i,0);
			}
		});
	}
	
	public void onBackPressed() {
		MessageRouter.sendNextLevelMessage();
		finish();
	}
	
	/** This method creates an AlertDialog, telling the user that something hasn't been implemented yet
	 * 
	 * @param whatsNotImplemented String describing the thing that hasn't been implemented.
	 */
	private void showUnimplementedError(String whatsNotImplemented) {
       
		Builder b = new AlertDialog.Builder(this)
        .setTitle("Not Implemented")
        .setIcon(R.drawable.fooditem_coffee )
        .setMessage("Sorry! " + whatsNotImplemented + " hasn't been implemented yet.")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                        //Put your code in here for a positive response
                }
        });
        
        b.show();
	}
}