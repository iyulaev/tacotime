/** This Activity shows a list of the currently created Characters in the game, so that the
 * user can select a particular character to load and continue playing! */

package org.coffeecats.coffeetime;

import java.util.ArrayList;
import java.util.Set;

import org.coffeecats.coffeetime.gamelogic.GameDatabase;
import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gameobjects.GameUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.CoffeeMachineUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.CounterTopUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.FastShoesUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.FasterShoesUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.QuickBrewingUpgrade;

import org.coffeecats.coffeetime.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CharacterSelectListActivity extends ListActivity {
	//Used to self-reference this in Toasts, when calling from another namespace (like a Handler)
	CharacterSelectListActivity me; 
	String activitynametag = "CharacterSelectListActivity";
	
	IconicAdapter theListAdapter; //Used to populate this ListLayout with the contents of upgradesList
	ArrayList<String> characterNames;
	GameDatabase gameDB;
	
	static final int COLOR_GREYED_OUT = 0xFF555555;
	static final int COLOR_RED = 0xFFFF0000;
	
	@Override
	public void onCreate(Bundle bundle) {
		me=this;
		
		setContentView(R.layout.upgradelist);
		
		TextView title = (TextView) findViewById(R.id.title);
		TextView subtitle = (TextView) findViewById(R.id.subtitle);
		
		title.setText("Character Selection");
		subtitle.setVisibility(View.GONE);
		
		gameDB = new GameDatabase(me);
		gameDB.open();
		gameDB.loadDatabase();
		Set<String> characterNameSet = gameDB.databaseCache.keySet();
		gameDB.close();
		
		characterNames = new ArrayList<String>(characterNameSet.size());
		for(String name : characterNameSet) characterNames.add(name);
		
		theListAdapter = new IconicAdapter(this);
		setListAdapter(theListAdapter);
		
		super.onCreate(bundle);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		finish();
	}
	
	/** Called when the user clicks on a particular list item. Attempts to buy the upgrade 
	 * that the user has clicked on.
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) {
		if(position >= characterNames.size()) {
			Log.e (activitynametag, "onListItemClick() got invalid position");
			return;
		}
		String characterName = characterNames.get(position);
		GameInfo.characterName = characterName;
		
		Intent i = new Intent(v.getContext(), TacoTimeActivity.class);
		startActivityForResult(i,0);
	}
		
	/** IconicAdapter is used to populate our ListView with the available characters in the database.
	 * 
	 * See in-line comments below for more details
	 */
	class IconicAdapter extends ArrayAdapter<String> {
		CharacterSelectListActivity context;

		IconicAdapter(CharacterSelectListActivity characterSelectListActivity) {
			super(characterSelectListActivity, R.layout.characterlistitem, characterNames);
			this.context=characterSelectListActivity;
		}

		/** getView() is used to process each ListItem and fill the character description text */
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater=LayoutInflater.from(context);
			View row=inflater.inflate(R.layout.characterlistitem, null);
			
			//Fill in name for this character
			TextView name=(TextView)row.findViewById(R.id.character_name);
			name.setText(characterNames.get(position));
			
			//Fill in the last finished level for this character
			TextView level=(TextView)row.findViewById(R.id.character_level);
			//Add 1 because SavedCharacter.level reflects the last COMPLETED level by that character
			level.setText("Level " + Integer.toString(1 + gameDB.databaseCache.get(characterNames.get(position)).level)); 
			
			//Fill in the description for this SavedCharacter (corresponding to this row in the ListView)
			TextView description=(TextView)row.findViewById(R.id.character_description);
			description.setText(gameDB.databaseCache.get(characterNames.get(position)).toString());
			
			ImageView trashcan = (ImageView)row.findViewById(R.id.trashcan);
			trashcan.setOnClickListener(new MyClickListener(characterNames.get(position)));
			
			return(row);
		}
	}
	
	
	
	
	
	private class MyClickListener implements OnClickListener {
	    private String characterName;

	    public MyClickListener(String characterName) {
	       this.characterName = characterName;
	    }

	    public void onClick(View v) {
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(me);
	    	
			builder.setMessage("Are you sure you want to delete " + characterName + "?")
				.setCancelable(true)
				
				//Maps to "return to main menu"
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						deleteAssociatedCharacter();
					}	
			});
			
			AlertDialog alert = builder.create();
			alert.show();
	    }
	    
	    private void deleteAssociatedCharacter() {
	    	gameDB = new GameDatabase(me);
			gameDB.open();
			gameDB.loadDatabase();

			//Delete character
			gameDB.deleteCharacterFromDatabase(gameDB.databaseCache.get(characterName));
			theListAdapter.remove(characterName);
			
			gameDB.close();
			
			theListAdapter.notifyDataSetChanged();
			
			Log.i(activitynametag, "Deleting character " + characterName);
	    }

		public void onClick(DialogInterface arg0, int arg1) {
			onClick(null);
		}
	 }
}
