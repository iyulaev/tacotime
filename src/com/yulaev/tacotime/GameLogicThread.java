package com.yulaev.tacotime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yulaev.tacotime.gameobjects.CoffeeGirl;
import com.yulaev.tacotime.gameobjects.CoffeeMachine;
import com.yulaev.tacotime.gameobjects.ViewObject;
import com.yulaev.tacotime.gameobjects.GameItem;


/**
 * @author iyulaev
 * 
 * This thread handles user input. It receives messages, mostly from MessageRouter, and handles 
 * them appropriately. For example, on user taps, we call handleTap() on all ViewObjects that have
 * been put into this GameLogicThread's viewObjects array.
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

	public GameLogicThread() {
		super();
		gameItems = new HashMap<String, GameItem>();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MESSAGE_INTERACTION_EVENT) {
					String interactee = (String)msg.obj;
					Log.d(activitynametag, "Got interaction event message! Actor interacted with " + interactee);
					
					//Attempt interaction and see if interactee changed state
					int interactee_state = gameItems.get(interactee).onInteraction();
					
					//If the interaction resulted in a state change, change coffeegirl state
					if(interactee_state != -1) {
						int coffee_girl_prev_state = coffeeGirl.getState();
						coffeeGirl.setState(coffeeGirlNextState(coffee_girl_prev_state, interactee, interactee_state));
					}
				}
			}
		};		
	}
	
	public void setActor(CoffeeGirl n_actor) {
		coffeeGirl = n_actor;
	}
	
	public void addGameItem(GameItem n_gameItem) {
		gameItems.put(n_gameItem.getName(), n_gameItem);
	}
	
	/** Since CoffeeGirl interacts with all other game items, describing the CoffeeGirl state machine is done on the global level
	 * rather than within CoffeeGirl itself.
	 * @param old_state The previous state of coffee girl
	 * @param interactedWith The ID of the GameItem CoffeeGirl interacted with
	 * @param interactee_state The state of the GameItem that CoffeeGirl interacted with
	 * @return What the next state should be, (if any change occurs)
	 */
	public int coffeeGirlNextState(int old_state, String interactedWith, int interactee_state) {
		//CoffeeGirl's hands are empty, she interacts with a coffeemachine that is done -> she is now carrying coffee
		if(old_state == CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("CoffeeMachine") && 
				interactee_state == CoffeeMachine.STATE_DONE) return(CoffeeGirl.STATE_CARRYING_COFFEE);
		
		//CoffeeGirl's hands are NOT empty, she interacts with trashcan -> hands now empty
		if(old_state != CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("TrashCan")) return(CoffeeGirl.STATE_NORMAL);
		
		//Default case - don't change state!
		return(old_state);
	}
	
	@Override
	public void run() {
		
		;

	}
	
}
