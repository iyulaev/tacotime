/** This class implements the Activity that drives the between-level menu. This is the menu that 
 * is shown to the user between levels (upon completing a level) and allows the user to buy upgrades and
 * save & continue, or to retry the level.
 * 
 * @author iyulaev (ivan@yulaev.com)
 */

package org.coffeecats.coffeetime;

import org.coffeecats.coffeetime.R;

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
	
	/** Called when the activity is first created. Just sets up button listeners and stuff.*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
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
	
	/** When back is pressed between levels, assume the user means "save and continue" */
	public void onBackPressed() {
		MessageRouter.sendNextLevelMessage();
		finish();
	}
}
