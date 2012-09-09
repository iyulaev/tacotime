/** This Activity implements the very first screen that is shown when the game is launched. 
 * Basically, we ask the user whether they want to create a new character or load an existing 
 * character. If the former, we pop over a dialog allowing them to set up that new character. Otherwise,
 * we launch a ListActivity that displays the existing (created) characters.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.gamelogic.GameDatabase;
import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gamelogic.SavedCharacter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CharacterSelectActivity extends Activity {
	
	private static final String activitynametag = "CharacterSelectActivity";
	private CharacterSelectActivity me;
	
	//Used to create Character Creation Dialog
	private final int CREATE_CHARACTER_DIALOG = 22;
	Dialog dialog;
	String createdCharacterName;
	String createdCharacterType;
	EditText nameText;
	Spinner typeSpinner;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Change content view so that we are using mainmenulayout now!
		setContentView(R.layout.characterselectlayout);
		//Used by Dialogs and such to access this Activity's resources
		me = this;
				
		Button newGame = (Button) findViewById(R.id.existing_character);
		newGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), CharacterSelectListActivity.class);
				startActivityForResult(i,0);
			}
		});
		
		Button continueGame = (Button) findViewById(R.id.new_character);
		continueGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(CREATE_CHARACTER_DIALOG);
			}
		});
	}
	
	public void onPause() {
		super.onPause();
		finish();
	}
	
	/** Used to launch the character creation dialog and process the results of the dialog once the user
	 * hits OK. Touches GameInfo (if user hits OK) to create the new character.
	 */
	protected Dialog onCreateDialog(int d) {
		
		dialog = new Dialog(this);
		
		switch (d) {
			case CREATE_CHARACTER_DIALOG:				
				dialog.setContentView(R.layout.charactercreatedialog);
				dialog.setTitle("Create New Character");
				
				nameText = (EditText) dialog.findViewById(R.id.nametext);
				typeSpinner = (Spinner) dialog.findViewById(R.id.typeselect);
				
				//Some code to populate the spinner (from the Spinner description in Android API docs)
				//http://developer.android.com/guide/topics/ui/controls/spinner.html
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				        R.array.character_types, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				typeSpinner.setAdapter(adapter);
				
				Button okButton = (Button) dialog.findViewById(R.id.ok);
				okButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						createdCharacterName = nameText.getText().toString();
						
						String[] characterTypes = getResources().getStringArray(R.array.character_types);
						int character_type_index = typeSpinner.getSelectedItemPosition();
						createdCharacterType = characterTypes[character_type_index];
						
						//See if the character already exists in the database!
						GameDatabase gameDB = new GameDatabase(me);
						gameDB.open();
						gameDB.loadDatabase();
						//If character exists, inform the user (but otherwise do nothing)
						if(gameDB.databaseCache.containsKey(createdCharacterName)) {
							Toast t = Toast.makeText(me, "Sorry, character with name " + createdCharacterName +
									" already exists in database, please use a different name", Toast.LENGTH_SHORT);
							t.show();
						//Otherwise add the character into the database and close the dialog
						} else {
							GameInfo.characterName = createdCharacterName;
							gameDB.saveCharacterToDatabase(new SavedCharacter(createdCharacterName, createdCharacterType));
							removeDialog(CREATE_CHARACTER_DIALOG);
							
							Intent i = new Intent(me, TacoTimeActivity.class);
							startActivityForResult(i,0);
						}
						gameDB.close();
					}
				});
				
				Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
				cancelButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) { removeDialog(CREATE_CHARACTER_DIALOG); } 
				});

				dialog.show();
				break;
		}
		
		
		
		return dialog;
	}
}