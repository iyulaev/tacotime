package com.yulaev.tacotime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gameobjects.Blender;
import com.yulaev.tacotime.gameobjects.CoffeeGirl;
import com.yulaev.tacotime.gameobjects.CoffeeMachine;
import com.yulaev.tacotime.gameobjects.GameFoodItem;
import com.yulaev.tacotime.gameobjects.ViewObject;
import com.yulaev.tacotime.gameobjects.GameItem;


/**
 * @author iyulaev
 * 
 * This thread handles all of the game logic. That is, whenever some two objects, usually the CoffeeGirl
 * and another GameItem, interact, this thread determines the result of the interaction. It also controls
 * the state of CoffeeGirl and increments points based on the result of in-game interactions.
 */
public class GameLogicThread extends Thread {
	
	private static final String activitynametag = "GameLogicThread";
	
	//Define types of messages accepted by ViewThread
	public static final int MESSAGE_INTERACTION_EVENT = 0;

	//This message handler will receive messages, probably from the UI Thread, and
	//update the data objects and do other things that are related to handling
	//game-specific input
	public static Handler handler;
	
	public CoffeeGirl coffeeGirl;
	public HashMap<String, GameItem> gameItems;
	public HashMap<String, GameFoodItem> foodItems;
	
	/** Mostly just initializes the Handler that receives and acts on in-game interactions that occur */
	public GameLogicThread() {
		super();
		
		//"initialize" GameInfo
		GameInfo.money = 0;
		GameInfo.points = 0;
		
		gameItems = new HashMap<String, GameItem>();
		foodItems = new HashMap<String, GameFoodItem>();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MESSAGE_INTERACTION_EVENT) {
					String interactee = (String)msg.obj;
					Log.d(activitynametag, "Got interaction event message! Actor interacted with " + interactee);
					
					//Attempt interaction and see if interactee changed state
					int interactee_state = gameItems.get(interactee).onInteraction(coffeeGirl.getItemHolding());
					
					//If the interaction resulted in a state change, change coffeegirl state
					if(interactee_state != -1) {
						//coffeeGirl.setState(coffeeGirlNextState(coffee_girl_prev_state, interactee, interactee_state));
						coffeeGirlNextState(coffeeGirl.getState(), interactee, interactee_state);
					}
				}
			}
		};		
	}
	
	/** Sets the actor associated with this GameLogicThread
	 * 
	 * @param n_actor The CoffeeGirl Object that will be this game's Actor, i.e. the player-controlled character
	 */
	public void setActor(CoffeeGirl n_actor) {
		coffeeGirl = n_actor;
	}
	
	 /** Adds a GameItem to this TacoTime game. The GameItem will be put into the gameItems data structure 
	  * so that it's state can be updated when an interaction occurs. 
	  * @param n_gameItem The GameItem to put into the gameItems map.
	  */
	public void addGameItem(GameItem n_gameItem) {
		gameItems.put(n_gameItem.getName(), n_gameItem);
	}
	
	/** This method is typically called by MainGamePanel when adding new GameFoodItems into the game. Basically 
	 * we add a new type of food item and an associated CoffeeGirl state.
	 * @param foodItem The GameFoodItem to add to this game.
	 * @param associated_coffeegirl_state The associated state that CoffeeGirl will be put into when she 
	 * receives the FoodItem.
	 */
	public void addNewFoodItem(GameFoodItem foodItem, int associated_coffeegirl_state) {
		boolean doSetItemHolding = false;
		if(foodItems.isEmpty()) doSetItemHolding = true;
		
		foodItems.put(foodItem.getName(), foodItem);
		coffeeGirl.setItemHoldingToStateAssoc(foodItem.getName(), associated_coffeegirl_state);
		
		//If CoffeeGirl's "held item" hasn't been set up yet then set it to the first foodItem that we add
		//better hope that the first one we add is "nothing"!
		if(doSetItemHolding) coffeeGirl.setItemHolding(foodItem.getName());
	}
	
	/** Since CoffeeGirl interacts with all other game items, describing the CoffeeGirl state machine is done on the global level
	 * rather than within CoffeeGirl itself. As a side effect GameInfo money and/or points may change depending on how
	 * CoffeeGirl's state has changed
	 * @param old_state The previous state of coffee girl
	 * @param interactedWith The ID of the GameItem CoffeeGirl interacted with
	 * @param interactee_state The state of the GameItem that CoffeeGirl interacted with
	 * @return What the next state should be, (if any change occurs)
	 */
	public void coffeeGirlNextState(int old_state, String interactedWith, int interactee_state) {
		//CoffeeGirl's hands are empty, she interacts with a coffeemachine that is done -> she is now carrying coffee
		if(old_state == CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("CoffeeMachine") && 
				interactee_state == CoffeeMachine.STATE_DONE) coffeeGirl.setItemHolding("coffee");
		
		//CoffeeGirl's hands are empty, she interacts with a cupcake tray -> she is now carrying a cupcake
		if(old_state == CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("CupCakeTray")) coffeeGirl.setItemHolding("cupcake");
		
		//CoffeeGirl has a coffee, she interacts with blender -> she now has nothing
		if(old_state == CoffeeGirl.STATE_CARRYING_COFFEE && 
				interactedWith.equals("Blender")) coffeeGirl.setItemHolding("nothing");
		//CoffeeGirl's hands are empty, she interacts with a blender that is done -> she is now carrying blended drink
		if(old_state == CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("Blender") && 
				interactee_state == Blender.STATE_DONE) coffeeGirl.setItemHolding("blended_drink");
		
		//CoffeeGirl's hands are NOT empty, she interacts with trashcan -> hands now empty, increment money and/or points
		if(old_state != CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("TrashCan")) {
			GameInfo.setAndReturnPoints(foodItems.get(coffeeGirl.getItemHolding()).pointsOnInteraction(interactedWith, 0));
			GameInfo.setAndReturnMoney(foodItems.get(coffeeGirl.getItemHolding()).moneyOnInteraction(interactedWith, 0)); 
			coffeeGirl.setItemHolding("nothing");
		}
		
		//Default case - don't change state!
		//return(old_state);
	}
	
	@Override
	public void run() {
		
		;

	}
	
}
