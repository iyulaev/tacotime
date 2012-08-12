/** This activity implements a list of upgrades that the user may choose to buy */

package com.yulaev.tacotime;

import java.util.ArrayList;

import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gameobjects.GameUpgrade;
import com.yulaev.tacotime.gameobjects.upgradedefs.CoffeeMachineUpgrade;
import com.yulaev.tacotime.gameobjects.upgradedefs.CounterTopUpgrade;
import com.yulaev.tacotime.gameobjects.upgradedefs.FastShoesUpgrade;

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
	ArrayList<GameUpgrade> upgradesList;
	UpgradeMenuActivity me;
	IconicAdapter theListAdapter;
	
	@Override
	public void onCreate(Bundle bundle) {
		me=this;
		
		setContentView(R.layout.upgradelist);
		
		//Add ALL upgrades to this UpgradeMenuActivity
		upgradesList = new ArrayList<GameUpgrade>();
		upgradesList.add(new FastShoesUpgrade());
		upgradesList.add(new CoffeeMachineUpgrade());
		upgradesList.add(new CounterTopUpgrade());
		
		theListAdapter = new IconicAdapter(this);
		setListAdapter(theListAdapter);
		
		super.onCreate(bundle);
	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		
		if(tryBuyUpgrade(upgradesList.get(position))) {
			//grey out the title text for v, and append " (bought)" to it
			TextView name=(TextView)v.findViewById(R.id.upgrade_name);
			name.setTextColor(0xFF666666);
			
			//Update the cost colors for all (other) upgrades
			for(int i = 0; i<=parent.getCount()-1; i++) {
				try {
					View otherPosition = parent.getChildAt(i);
					TextView cost=(TextView)otherPosition.findViewById(R.id.upgrade_cost);
					if(Integer.parseInt(cost.getText().toString()) > GameInfo.money)
						cost.setTextColor(0xFFFF0000);
					
				} catch (NullPointerException npe) {
					// Iteration weirdness here! Android bug.
				}
			}
		}
		
	}
	
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
		
		else if(!GameInfo.hasUpgrade(upgrade)) {
			Toast t = Toast.makeText(me, "You've already bought " + upgrade.getUpgradeLongName() + "!", Toast.LENGTH_SHORT);
			t.show();
		}
		
		return(false);
	}
	
	private void buyUpgrade(GameUpgrade upgrade) {
		GameInfo.setAndReturnMoney(-1 * upgrade.getUpgradeCost());
		GameInfo.addUpgrade(upgrade);
	}
	
	class IconicAdapter extends ArrayAdapter<GameUpgrade> {
		UpgradeMenuActivity context;

		IconicAdapter(UpgradeMenuActivity context) {
			super(context, R.layout.upgradelistitem, upgradesList);
			this.context=context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater=LayoutInflater.from(context);
			View row=inflater.inflate(R.layout.upgradelistitem, null);
			
			TextView name=(TextView)row.findViewById(R.id.upgrade_name);
			name.setText(upgradesList.get(position).getUpgradeLongName());
			//Color upgrades Grey for the ones we have
			if(GameInfo.hasUpgrade(upgradesList.get(position)))
				name.setTextColor(0xFF666666);
			
			
			TextView description=(TextView)row.findViewById(R.id.upgrade_description);
			description.setText(upgradesList.get(position).getUpgradeDescription());
			
			
			TextView cost=(TextView)row.findViewById(R.id.upgrade_cost);
			cost.setText(Integer.toString(upgradesList.get(position).getUpgradeCost()));
			//Color costs RED for upgrades that we can't afford
			if(upgradesList.get(position).getUpgradeCost() > GameInfo.money)
				cost.setTextColor(0xFFFF0000);
			
			
			
			//Remove upgrades that we aren't allowed to buy yet
			if(upgradesList.get(position).getUpgradeLevel() > GameInfo.getLevel())
				row.setVisibility(View.GONE);
			
			return(row);
		}
	}
}
