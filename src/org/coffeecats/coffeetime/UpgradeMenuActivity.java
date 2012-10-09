/** This activity implements a list of upgrades that the user may choose to buy. It is invoked (via an Intent) 
 * by the BetweenLevelMenuActivity, since it is between levels that a user may buy upgrades. As a side-effect 
 * this activity makes changes to GameInfo.upgradesBought() and GameInfo.money, if the user chooses to buy 
 * various upgrades. */

package org.coffeecats.coffeetime;

import java.util.ArrayList;

import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gameobjects.GameUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.CoffeeMachineUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.CounterTopUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.EspressoMachineUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.FastShoesUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.FasterShoesUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.QuickBrewingUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.SoundSystemUpgrade;

import com.yulaev.tacotime.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UpgradeMenuActivity extends ListActivity {
	//Used to self-reference this in Toasts, when calling from another namespace (like a Handler)
	UpgradeMenuActivity me; 
	
	ArrayList<GameUpgrade> upgradesList; //holds a list of ALL GameUpgrades in this game
	IconicAdapter theListAdapter; //Used to populate this ListLayout with the contents of upgradesList
	
	static final int COLOR_GREYED_OUT = 0xFF555555;
	static final int COLOR_RED = 0xFFFF0000;
	
	@Override
	public void onCreate(Bundle bundle) {
		me=this;
		
		setContentView(R.layout.upgradelist);
		
		//Add ALL upgrades to this UpgradeMenuActivity
		upgradesList = new ArrayList<GameUpgrade>();
		upgradesList.add(new FastShoesUpgrade());
		upgradesList.add(new CoffeeMachineUpgrade());
		upgradesList.add(new CounterTopUpgrade());
		upgradesList.add(new FasterShoesUpgrade());
		upgradesList.add(new QuickBrewingUpgrade());
		upgradesList.add(new SoundSystemUpgrade());
		upgradesList.add(new EspressoMachineUpgrade());
		
		theListAdapter = new IconicAdapter(this);
		setListAdapter(theListAdapter);
		
		super.onCreate(bundle);
	}
	
	/** Called when the user clicks on a particular list item. Attempts to buy the upgrade 
	 * that the user has clicked on.
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) {
		
		if(tryBuyUpgrade(upgradesList.get(position))) {
			//grey out the title text for v, and append " (bought)" to it
			//TextView name=(TextView)v.findViewById(R.id.upgrade_name);
			//name.setTextColor(COLOR_GREYED_OUT);
			
			//Update the cost colors for all (other) upgrades and also see if the pre-requs
			//have changed at all
			/*for(int i = 0; i < parent.getCount(); i++) {
				try {
					View otherPosition = parent.getChildAt(i);
					updateListView(i, otherPosition);					
				} catch (NullPointerException npe) {
					// Iteration weirdness here! Android bug.
				}
			}*/
			
			theListAdapter.notifyDataSetInvalidated();
			theListAdapter.notifyDataSetChanged();
		}
		
	}
	
	/** Attempt to buy GameUpgrade upgrade. Return true if the upgrade is bought, otherwise return false. Side effects 
	 * (game state being updated in GameInfo) only occur if the upgrade is bought successfully.
	 * @param upgrade The GameUpgrade to purchase and add to GameInfo.upgradesBought.
	 * @return true if the upgrade is bought successfully, otherwise false (i.e. not enough money or already bought)
	 */
	private boolean tryBuyUpgrade(GameUpgrade upgrade) {
		if(GameInfo.money >= upgrade.getUpgradeCost() &&
				!GameInfo.hasUpgrade(upgrade)) {
			
			//Launch a dialog asking the user if they REALLY want to buy this upgrade (TODO)
			
			buyUpgrade(upgrade);
			return(true);
		}
		
		//If the user doesn't have enough money, inform them of this
		else if(GameInfo.money >= upgrade.getUpgradeCost()) {
			Toast t = Toast.makeText(me, "Sorry, not enough money to buy " + upgrade.getUpgradeLongName(), Toast.LENGTH_SHORT);
			t.show();
		}
		//If we've already bought the upgrade, inform the user and don't buy it
		else if(!GameInfo.hasUpgrade(upgrade)) {
			Toast t = Toast.makeText(me, "You've already bought " + upgrade.getUpgradeLongName() + "!", Toast.LENGTH_SHORT);
			t.show();
		}
		
		return(false);
	}
	
	/** Buy GameUpgrad upgrade. Changes GameInfo.money and adds the upgrade to the upgradesBought list in
	 * GameInfo
	 * @param upgrade The GameUpgrade to buy.
	 */
	private void buyUpgrade(GameUpgrade upgrade) {
		GameInfo.setAndReturnMoney(-1 * upgrade.getUpgradeCost());
		GameInfo.addUpgrade(upgrade);
	}
	
	/** IconicAdapter is used to populate our ListView with the available GameUpgrades. We also present the upgrade 
	 * cost and the description of the upgrade, and we color different text fields as appropriate to signify items 
	 * we can no longer buy or cannot afford. 
	 * 
	 * See in-line comments below for more details
	 */
	class IconicAdapter extends ArrayAdapter<GameUpgrade> {
		UpgradeMenuActivity context;

		IconicAdapter(UpgradeMenuActivity context) {
			super(context, R.layout.upgradelistitem, upgradesList);
			this.context=context;
		}

		/** getView() is used to process each ListItem and fill in/color text as necessary */
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater=LayoutInflater.from(context);
			View row=inflater.inflate(R.layout.upgradelistitem, null);
			
			//Fill in name for this ListItem
			TextView name=(TextView)row.findViewById(R.id.upgrade_name);
			name.setText(upgradesList.get(position).getUpgradeLongName());
			
			//Fill in description for this ListItem
			TextView description=(TextView)row.findViewById(R.id.upgrade_description);
			description.setText(upgradesList.get(position).getUpgradeDescription());
			
			//Fill in ListItem's cost
			TextView cost=(TextView)row.findViewById(R.id.upgrade_cost);
			cost.setText(Integer.toString(upgradesList.get(position).getUpgradeCost()));
			
			updateListView(position, row);
			return(row);
		}
	}
	
	public void updateListView(int position, View row) {
		if(row==null) return;		
		
		TextView name=(TextView)row.findViewById(R.id.upgrade_name);
		TextView cost=(TextView)row.findViewById(R.id.upgrade_cost);
		TextView description=(TextView)row.findViewById(R.id.upgrade_description);
		
		//Grey out things we already have
		if(GameInfo.hasUpgrade(upgradesList.get(position))) {
			cost.setTextColor(COLOR_GREYED_OUT);
			name.setTextColor(COLOR_GREYED_OUT);
			description.setText(upgradesList.get(position).getUpgradeDescription() + " (bought)");
		}
		//Color costs RED for upgrades that we can't afford
		else if(upgradesList.get(position).getUpgradeCost() > GameInfo.money) {
			cost.setTextColor(COLOR_RED); 
		}
		
		//Remove upgrades that we aren't allowed to buy yet
		if(upgradesList.get(position).getUpgradeLevel() > GameInfo.getLevel())
			row.setVisibility(View.INVISIBLE);
		
		//Remove upgrades for which prereqs aren't satisfied
		if(!upgradesList.get(position).prerequisitesSatisfied(GameInfo.getUpgradesBoughtCopy()))
			row.setVisibility(View.INVISIBLE);
		
		//Display upgrades where the requirements are satisfied
		if(upgradesList.get(position).prerequisitesSatisfied(GameInfo.getUpgradesBoughtCopy()) &&
				upgradesList.get(position).getUpgradeLevel() <= GameInfo.getLevel())
			row.setVisibility(View.VISIBLE);
	}
}
