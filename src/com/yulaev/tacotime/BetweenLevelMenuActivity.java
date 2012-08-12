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
		
		Button retryLevel = (Button) findViewById(R.id.retry_level);
		retryLevel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MessageRouter.sendLoadGameMessage();
				finish();
			}
		});
		
		Button saveAndContinue = (Button) findViewById(R.id.save_and_continue);
		saveAndContinue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MessageRouter.sendNextLevelMessage();
				finish();
			}
		});
		
		Button viewBuyUpgrades = (Button) findViewById(R.id.view_buy_upgrades);
		viewBuyUpgrades.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//showUnimplementedError("View / Buy Upgrades");
				Intent i = new Intent(v.getContext(), UpgradeMenuActivity.class);
				startActivityForResult(i,0);
			}
		});
	}
	
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