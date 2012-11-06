package org.coffeecats.coffeetime.gameobjects;

import java.util.HashMap;

import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gamelogic.State;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.FastShoesUpgrade;
import org.coffeecats.coffeetime.gameobjects.upgradedefs.FasterShoesUpgrade;
import org.coffeecats.coffeetime.utility.CircularList;
import org.coffeecats.coffeetime.utility.DirectionBitmapMap;
import org.coffeecats.coffeetime.utility.Utility;

import org.coffeecats.coffeetime.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/** CoffeeGirl is the main actor in TacoTime. She is the character controlled by the player. CoffeeGirl has 
 * state managed by GameLogicThread and may interact with all of the GameItems. Her state is determined by 
 * what GameFoodItem she carries.
 * @author ivany
 *
 */
public class CoffeeGirl extends GameActor {
	private static final String activitynametag = "CoffeeGirl";
	
	/*The default moverate, in terms of the GameGrid vector length that may be traversed during each 100ms */  
	private static int DEFAULT_COFFEEGIRL_MOVERATE = 8;
	
	//Defines for states that CoffeeGirl can be in
	public static final int STATE_NORMAL = 0;
	public static final int STATE_CARRYING_COFFEE = 1;
	public static final int STATE_CARRYING_CUPCAKE = 2;
	public static final int STATE_CARRYING_BLENDEDDRINK = 3;
	public static final int STATE_CARRYING_PIESLICE = 4;
	public static final int STATE_CARRYING_SANDWICH = 5;
	public static final int STATE_CARRYING_ESPRESSO = 6;
	
	/** Default constructor for a coffeegirl object.
	 * 
	 * @param caller The calling Context so that bitmaps may be loaded and such. See GameActor (superclass) constructor
	 * for more details.
	 */
	public CoffeeGirl(Context caller) {
		super(caller, DEFAULT_COFFEEGIRL_MOVERATE);
		
		//Check if we have the "fast shoes" upgrade and change moverate if necessary
		if(GameInfo.hasUpgrade(FastShoesUpgrade.UPGRADE_NAME)) {
			Log.d(activitynametag, "CoffeeGirl detected that " + FastShoesUpgrade.UPGRADE_NAME + " has been bought.");
			this.move_rate = (int) (((double) this.move_rate) * 1.11);
		} else {
			Log.d(activitynametag, "CoffeeGirl did not detect that " + FastShoesUpgrade.UPGRADE_NAME + " has been bought.");
		}
		
		if(GameInfo.hasUpgrade(FasterShoesUpgrade.UPGRADE_NAME)) {
			this.move_rate = (int) (((double) this.move_rate) * 1.21);
		}
		
		//Add a state for each thing that CoffeeGirl may carry
		//Annoyingly a line must be added to MainGamePanel for each GameFoodItem that we add into
		//this game.
		this.addState("default", null);
		this.addState("carrying_coffee", null);
		this.addState("carrying_cupcake", null);
		this.addState("carrying_blended_drink", null);
		this.addState("carrying_pie_slice", null);
		this.addState("carrying_sandwich", null);
		this.addState("carrying_espresso", null);
		
		if(GameInfo.getCharacterGender().toLowerCase().equals("boy"))
			gameActorSprite = new CoffeeBoySprite(caller);
		else
			gameActorSprite = new CoffeeGirlSprite(caller);
	}
	
	/** This method is called by the InputThread when a user input (a tap) occurs somewhere on the screen. We convert
	 * the tap coordinates into GameGrid coordinates and then set that as this CoffeeGirl's target location for 
	 * motion
	 * 
	 * @param new_x The x co-ordinate, on the screen canvas, where the tap has occured.
	 * @param new_y The y co-ordinate, on the screen canvas, where the tap has occured.
	 */
	public void handleTap(int new_x, int new_y) {
		int new_x_gg = GameGrid.gameGridX(new_x);
		int new_y_gg = GameGrid.gameGridY(new_y);
		
		new_x_gg = GameGrid.constrainX(new_x_gg);
		new_y_gg = GameGrid.constrainY(new_y_gg);
		
		setLocked();
		target_x = new_x_gg;
		target_y = new_y_gg;
		unLock();
	}
	
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		if(isVisible()) {
			if(USING_NEW_SPRITES && gameActorSprite != null) {
				int vector_x = target_x - x;
				int vector_y = target_y - y;
				
				//Draw the appropriate "hands" onto the game sprite; these depend on whether the CoffeeGirl is holding
				//any GameFoodItems or not
				Bitmap handsBitmap = gameActorSprite.getHandsBitmap(vector_x, vector_y, itemHolding.equals("nothing")?0:1);
				this.draw(canvas, handsBitmap);
				
				//Figure out the direction heading and draw the currently-held item as appropriate into the game character area
				Bitmap foodItemBitmap = gameActorSprite.getHeldItemBitmap(itemHolding);
				int direction_heading = Utility.calculateHeadingDirection(vector_x, vector_y);
				
				switch(direction_heading) {
				
					case DirectionBitmapMap.DIRECTION_EAST:
						canvas.drawBitmap(foodItemBitmap, 
								GameGrid.canvasX(x) - (foodItemBitmap.getWidth() / 2) + 15, 
								GameGrid.canvasY(y) - (foodItemBitmap.getHeight() / 2), 
								null);
						break;
						
					case DirectionBitmapMap.DIRECTION_WEST:
						canvas.drawBitmap(foodItemBitmap, 
								GameGrid.canvasX(x) - (foodItemBitmap.getWidth() / 2) - 15, 
								GameGrid.canvasY(y) - (foodItemBitmap.getHeight() / 2), 
								null);
						break;
					
					case DirectionBitmapMap.DIRECTION_NORTH:
						canvas.drawBitmap(foodItemBitmap, 
								GameGrid.canvasX(x) - (foodItemBitmap.getWidth() / 2), 
								GameGrid.canvasY(y) - (foodItemBitmap.getHeight() / 2), 
								null); 
						break;
						
					default:
						canvas.drawBitmap(foodItemBitmap, 
								GameGrid.canvasX(x) - (foodItemBitmap.getWidth() / 2) + 15, 
								GameGrid.canvasY(y) - (foodItemBitmap.getHeight() / 2), 
								null); 
						break;
				}
			}
		}
	}
	
	
	
	/** For documentation see ViewObject interface */
	public String getName() {return "CoffeeGirl";}
	

	// Since CoffeeGirl's state is coupled to what items she is holding, we define these associations below
	private String itemHolding; //the item that CoffeeGirl holds
	private HashMap<String, State> itemToStateMap; //A map between GameFoodItems that CoffeeGirl may hold and
		//the relevant CoffeeGirl states
	
	/** Set an association between a particular GameFoodItem and a CoffeeGirl state. Called by the GLT.
	 * 
	 * @param item The GameFoodItem's name that we will associate state with
	 * @param state The index of the state to be associated with item.
	 */
	public void setItemHoldingToStateAssoc(String item, int state) {
		if(itemToStateMap == null) itemToStateMap = new HashMap<String, State>();
		itemToStateMap.put(item, validStates.get(state));
	}
	
	/** Set this CoffeeGirl to be holding the GameFoodItem named newItem. As a side effect CoffeeGirl's 
	 * state gets set to the state that is associated with this GameFoodItem (in itemToStateMap).
	 * @param newItem
	 */
	public synchronized void setItemHolding(String newItem) {
		itemHolding = newItem;
		setState(itemToStateMap.get(newItem).state_idx);
	}
	
	/** Return the name of the GameFoodItem that CoffeeGirl is holding 
	 * 
	 * @return String representing the GameFoodItem that CoffeeGirl currently holds.
	 */
	public synchronized String getItemHolding() {
		return(itemHolding);
	}	
}
