/** This class implements the very top level of the TacoTime game engine.
 * It shows the Main Menu, that's about it.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gamelogic.Interaction;

import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.AlertDialog;
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
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Change content view so that we are using mainmenulayout now!
		setContentView(R.layout.mainmenulayout);
		
		Button newGame = (Button) findViewById(R.id.new_game);
		newGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), TacoTimeMainGameActivity.class);
				i.putExtra("LoadSavedGame", false);
				startActivityForResult(i,0);
			}
		});
		
		Button continueGame = (Button) findViewById(R.id.continue_game);
		continueGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showUnimplementedError("Continue game");
				/*Intent i = new Intent(v.getContext(), TacoTimeMainGameActivity.class);
				i.putExtra("LoadSavedGame", true);
				startActivityForResult(i,0);*/
			}
		});
	}
	
	/** Displays an AlertDialog dialog to the user, stating that whatsNotImplemented hasn't been implemented yet. 
	 * @param whatsNotImplemented String describing what it is that we haven't implemented.
	 * */
	private void showUnimplementedError(String whatsNotImplemented) {
       
		Builder b = new AlertDialog.Builder(this)
        .setTitle("Not Implemented")
        .setIcon(R.drawable.fooditem_coffee )
        .setMessage("Sorry! " + whatsNotImplemented + " hasn't been implemented yet.")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	; //onClick() does nothing, just kills the dialog.
                }
        });
        
        b.show();
	}
}