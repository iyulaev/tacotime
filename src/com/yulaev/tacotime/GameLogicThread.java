package com.yulaev.tacotime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gamelogic.GameLevel;
import com.yulaev.tacotime.gamelogic.Interaction;
import com.yulaev.tacotime.gamelogic.leveldefs.GameLevel_1;
import com.yulaev.tacotime.gamelogic.leveldefs.GameLevel_2;
import com.yulaev.tacotime.gamelogic.leveldefs.GameLevel_3;
import com.yulaev.tacotime.gamelogic.leveldefs.GameLevel_4;
import com.yulaev.tacotime.gameobjects.CoffeeGirl;
import com.yulaev.tacotime.gameobjects.CustomerQueue;
import com.yulaev.tacotime.gameobjects.GameFoodItem;
import com.yulaev.tacotime.gameobjects.ViewObject;
import com.yulaev.tacotime.gameobjects.GameItem;
import com.yulaev.tacotime.gameobjects.objectdefs.Blender;
import com.yulaev.tacotime.gameobjects.objectdefs.CoffeeMachine;
import com.yulaev.tacotime.gameobjects.objectdefs.CounterTop;


/**
 * @author iyulaev
 * 
 * This thread handles all of the game logic. That is, whenever some two objects, usually the CoffeeGirl
 * and another GameItem, interact, this thread determines the result of the interaction. It also controls
 * the state of CoffeeGirl and increments points based on the result of in-game interactions.
 * 
 * GameLogicThread also features within it a state machine that corresponds to the current stage in the
 * game (i.e. are we playing? are we viewing a menu?). The state machine is advanced by "ticks" from the
 * TimerThread. This state machine is described in stateMachineClockTick().
 */
public class GameLogicThread extends Thread {
	
	private static final String activitynametag = "GameLogicThread";
	
	//Define types of messages accepted by ViewThread
	public static final int MESSAGE_INTERACTION_EVENT = 0;
	public static final int MESSAGE_TICK_PASSED = 1;
	public static final int MESSAGE_LOAD_GAME = 2;
	public static final int MESSAGE_NEXT_LEVEL = 4;
	public static final int MESSAGE_POSTLEVEL_DIALOG_OPEN = 5;
	public static final int MESSAGE_POSTLEVEL_DIALOG_CLOSED = 6;
	
	//Define types of messages accepted by OTHER threads/handlers
	public static final int MESSAGE_LEVEL_END = -1;
	public static final int MESSAGE_GAME_END = -2;

	//This message handler will receive messages, probably from the UI Thread, and
	//update the data objects and do other things that are related to handling
	//game-specific input
	public Handler handler;
	
	public CoffeeGirl coffeeGirl;
	public CustomerQueue customerQueue;
	public HashMap<String, GameItem> gameItems;
	public HashMap<String, GameFoodItem> foodItems;
	
	ViewThread viewThread;
	TimerThread timerThread;
	InputThread inputThread;
	GameLogicThread gameLogicThread;
	Context caller;
	
	//Used to time various things, like pre-level and post-level "announcements"
	int message_timer;
	
	/** Mostly just initializes the Handler that receives and acts on in-game interactions that occur */
	public GameLogicThread(ViewThread viewThread, TimerThread timerThread, InputThread inputThread, Context caller, boolean load_saved) {
		super();
		
		//Set pointers to all of the other threads
		//This is useful when we want to load a new level
		this.viewThread = viewThread;
		this.timerThread = timerThread;
		this.inputThread = inputThread;
		this.caller = caller;
		
		//"reset" GameInfo
		GameInfo.reset();
		
		gameItems = new HashMap<String, GameItem>();
		foodItems = new HashMap<String, GameFoodItem>();
		
		if(load_saved) GameInfo.loadSavedGame();
		
		//Creates a Handler that will be used to process Interaction and ClockTick messages, and advance the CoffeeGirl and
		//GameLogicThread state machines
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//Handle messages from viewThread that tell the GLT that an interaction between a GameActor and
				//a GameItem has occured
				if(msg.what == MESSAGE_INTERACTION_EVENT) {
					String interactee = (String)msg.obj;
					Log.d(activitynametag, "Got interaction event message! Actor interacted with " + interactee);
					
					//Attempt interaction and see if interactee changed state
					Interaction interactionResult = gameItems.get(interactee).onInteraction(coffeeGirl.getItemHolding());
					
					//If the interaction resulted in a state change, OR was successful (when interacting with CustomerQueue),
					//change coffeegirl state
					if(interactionResult.previous_state != -1 || interactionResult.was_success) {
						//coffeeGirl.setState(coffeeGirlNextState(coffee_girl_prev_state, interactee, interactee_state));
						coffeeGirlNextState(coffeeGirl.getState(), interactee, interactionResult);
					}
				}
				
				//Handle messages from timerThread that tell the GameLogicThread that a second has passed
				//This is the main state machine for the GameLogicThread! Which controls how the game works!
				//So this is the most important part of the game!!!
				else if(msg.what == MESSAGE_TICK_PASSED) {
					stateMachineClockTick();
				}
				
				//If we get a message to load a level, then load the saved game and transition to "pre-play" state
				//Note that "retry level" will call this also, since to retry level we just load the last game
				else if(msg.what == MESSAGE_LOAD_GAME) {
					GameInfo.loadSavedGame();
					GameInfo.setGameMode(GameInfo.MODE_MAINGAMEPANEL_PREPLAY);
					MessageRouter.sendPauseTimerMessage(false);
				}
				
				//If we are to advance to the next level, set the GameMode appropriately and unpause
				else if(msg.what == MESSAGE_NEXT_LEVEL) {
					GameInfo.saveCurrentGame();
					GameInfo.setGameMode(GameInfo.MODE_MAINGAMEPANEL_PREPLAY);
					MessageRouter.sendPauseTimerMessage(false);
				}
				
				//If the post-leveldialog has been closed advance to the (final) post-play state
				//whereupon we will exit the level and launch the between-level menu (or possibly just
				//display "game over"
				else if(msg.what == MESSAGE_POSTLEVEL_DIALOG_CLOSED) {
					GameInfo.setGameMode(GameInfo.MODE_MAINGAMEPANEL_POSTPLAY);
				}
			}
		};		
	}
	
	/** Used to provide a reference to this GameLogicThread instance. Mostly just used when we load a level, so that the 
	 * GameLevel class that we create can add GameItems and other such things to this GameLogicThread instance.
	 * @param gameLogicThread
	 */
	public void setSelf(GameLogicThread gameLogicThread) { this.gameLogicThread = gameLogicThread; }
	
	/** Since CoffeeGirl interacts with all other game items, describing the CoffeeGirl state machine is done on the global level
	 * rather than within CoffeeGirl itself. As a side effect GameInfo money and/or points may change depending on how
	 * CoffeeGirl's state has changed
	 * @param old_state The previous state of coffee girl
	 * @param interactedWith The ID of the GameItem CoffeeGirl interacted with
	 * @param interactee_state The state of the GameItem that CoffeeGirl interacted with
	 * @return What the next state should be, (if any change occurs)
	 */
	public void coffeeGirlNextState(int old_state, String interactedWith, Interaction interactionResult) {
		int interactee_state = interactionResult.previous_state;
		
		//CoffeeGirl's hands are empty, she interacts with a coffeemachine that is done -> she is now carrying coffee
		if(old_state == CoffeeGirl.STATE_NORMAL && 
				interactedWith.contains("CoffeeMachine") && 
				interactee_state == CoffeeMachine.STATE_DONE) coffeeGirl.setItemHolding("coffee");
		
		//CoffeeGirl's hands are empty, she interacts with a cupcake tray -> she is now carrying a cupcake
		if(old_state == CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("CupCakeTray")) coffeeGirl.setItemHolding("cupcake");
		
		//CoffeeGirl's hands are empty, she interacts with a pie slice tray tray -> she is now carrying a pie slice
		if(old_state == CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("PieTray")) coffeeGirl.setItemHolding("pieslice");
		
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
		
		//CoffeeGirl interacts with CustomerQueue - if the interaction is successful then CoffeeGirl loses the
		//item that she currently holds and gains some points in return
		if(old_state != CoffeeGirl.STATE_NORMAL && 
				interactedWith.equals("CustomerQueue") &&
				interactionResult.was_success) {
			GameInfo.setAndReturnPoints(interactionResult.point_result);
			GameInfo.setAndReturnMoney(interactionResult.money_result);
			coffeeGirl.setItemHolding("nothing");
		}
		
		//CounterTop interactions
		//If we hold nothing but the counter top has "something", we now hold that "something"
		//If we hold "something" and the counter top has nothing, counter top now holds that "something"
		if(interactedWith.contains("CounterTop")) {
			if(old_state == CoffeeGirl.STATE_NORMAL && interactee_state == CounterTop.STATE_HOLDING_COFFEE)
				coffeeGirl.setItemHolding("coffee");
			if(old_state == CoffeeGirl.STATE_NORMAL && interactee_state == CounterTop.STATE_HOLDING_CUPCAKE)
				coffeeGirl.setItemHolding("cupcake");
			if(old_state == CoffeeGirl.STATE_NORMAL && interactee_state == CounterTop.STATE_HOLDING_BLENDEDDRINK)
				coffeeGirl.setItemHolding("blended_drink");
			if(old_state != CoffeeGirl.STATE_NORMAL && interactee_state == CounterTop.STATE_IDLE)
				coffeeGirl.setItemHolding("nothing");
		}
		
		//Default case - don't change state!
	}

	/** Updates this GameLogicThread's state machine. Should be called every time a clock tick (nominally one
	 * real-time second) occurs
	 * 
	 * @sideeffect Mucks with GameInfo and MessageRouter to update game state and inform other Threads
	 * about the updates in the game state.
	 */
	public void stateMachineClockTick() {
		//IF we are viewing the main panel AND we are ready to play a level, this means the
		//game is ready for another level - load one!
		if(GameInfo.getGameMode() == GameInfo.MODE_MAINGAMEPANEL_PREPLAY) {
			//At the very beginning of the level, load the current level (increment previous level # by one)
			loadLevel(GameInfo.getLevel() + 1);
			
			//For three seconds tell the user that the evel is about to start
			message_timer = 3;
			MessageRouter.sendAnnouncementMessage("Level Start in " + message_timer, true);
			
			Log.v(activitynametag, "GLT is loading a new level!");
			GameInfo.setGameMode(GameInfo.MODE_MAINGAMEPANEL_PREPLAY_MESSAGE);
		}
		
		//If we are in the pre-play message, update the message we display (indicating to the user when we
		//are to start the level). Start the level when the count gets to zero.
		else if(GameInfo.getGameMode() == GameInfo.MODE_MAINGAMEPANEL_PREPLAY_MESSAGE) {
			if(message_timer > 0) {
				message_timer--;
				MessageRouter.sendAnnouncementMessage("Level Start in " + message_timer, true);
			}
			else {
				GameInfo.setGameMode(GameInfo.MODE_MAINGAMEPANEL_INPLAY);
				MessageRouter.sendAnnouncementMessage("", false); //remove the announcement message
				MessageRouter.sendPauseMessage(false); //unpauses ViewThread and InputThread
				Log.v(activitynametag, "GLT is starting a new level!");
			}
		}
		
		
		//If we are in play check to see if we should finish the level
		else if(GameInfo.getGameMode() == GameInfo.MODE_MAINGAMEPANEL_INPLAY) {
			GameInfo.decrementLevelTime();
			Log.v(activitynametag, GameInfo.getLevelTime() + " seconds remaining in this level!");
			GameInfo.setAndGetGameTimeMillis(TimerThread.TIMER_GRANULARIY);
			
			//If we've run out of time on this level, or customerQueue has run out, then kill the level
			if(GameInfo.getLevelTime() <= 0 || customerQueue.isFinished()) {
				Log.v(activitynametag, "GLT is finishing this level!");
				
				message_timer = 3;
				
				//Indicate that the level has been finished and, if we finished the last level, that the game is over
				if(GameInfo.getLevel() < MAX_GAME_LEVEL)
					MessageRouter.sendAnnouncementMessage("Level " + GameInfo.getLevel() + " Finished", true);
				else
					MessageRouter.sendAnnouncementMessage("Game Over", true);
				
				GameInfo.setGameMode(GameInfo.MODE_MAINGAMEPANEL_POSTPLAY_MESSAGE);
			}
		}
		
		//Display the level end message for a while and then display the level-end dialog, summarizing
		//the progress for this level
		else if(GameInfo.getGameMode() == GameInfo.MODE_MAINGAMEPANEL_POSTPLAY_MESSAGE) {
			if(message_timer > 0) {
				message_timer--;
			}
			//We use message_timer again to make sure we only display the post-level dialog once :)
			else if (message_timer == 0){
				//Calculate our end-level bonus and display the level end dialog
				GameLevel currLevel = getLevelInstance(GameInfo.getLevel());				
				GameInfo.setAndReturnMoney(currLevel.getBonusMoney(GameInfo.getLevelTime() > 0));
				GameInfo.setAndReturnPoints(currLevel.getBonusPoints(GameInfo.getLevelTime() > 0));
				
				//Send all of the accrued bonuses to the level-end dialog
				//See MessageRouter.sendPostLevelDialogOpenMessage() javadocs for an explanation of the arguments given
				MessageRouter.sendPostLevelDialogOpenMessage(GameInfo.points, GameInfo.money, 
						GameInfo.level_points-currLevel.getBonusPoints(GameInfo.getLevelTime() > 0), 
						GameInfo.level_money-currLevel.getBonusMoney(GameInfo.getLevelTime() > 0),
						currLevel.getBonusPoints(GameInfo.getLevelTime() > 0),
						currLevel.getBonusMoney(GameInfo.getLevelTime() > 0));
				
				message_timer--;
			}
		}
		
		//Finally, we either pause the game and send a level end message, going into a between-level menu
		//Or we send a game over message and end this game
		else if(GameInfo.getGameMode() == GameInfo.MODE_MAINGAMEPANEL_POSTPLAY) {
			//
			if(GameInfo.getLevel() < MAX_GAME_LEVEL) {
				MessageRouter.sendPauseMessage(true);
				MessageRouter.sendLevelEndMessage();
			}
			else {
				MessageRouter.sendGameOverMessage();
			}
		}
	}
	
	/** Sets the actor associated with this GameLogicThread
	 * 
	 * @param n_actor The CoffeeGirl Object that will be this game's Actor, i.e. the player-controlled character
	 */
	public synchronized void setActor(CoffeeGirl n_actor) {
		coffeeGirl = n_actor;
	}
	
	/** Set the CustomerQueue associated with this GameLogicThread. Called by a GameLevel constructor after the
	 * constructor has finished initlaizing the CustomerQueue so that this GLT can query the status of the 
	 * queue and advance it's state machine accordingly, i.e. finish the level when the queue is exhausted.
	 * @param n_customerQueue
	 */
	public synchronized void setCustomerQueue(CustomerQueue n_customerQueue) {
		customerQueue = n_customerQueue;
		this.addGameItem(customerQueue);
	}
	
	 /** Adds a GameItem to this TacoTime game. The GameItem will be put into the gameItems data structure 
	  * so that it's state can be updated when an interaction occurs. 
	  * @param n_gameItem The GameItem to put into the gameItems map.
	  */
	public synchronized void addGameItem(GameItem n_gameItem) {
		gameItems.put(n_gameItem.getName(), n_gameItem);
	}
	
	/** This method is typically called by MainGamePanel when adding new GameFoodItems into the game. Basically 
	 * we add a new type of food item and an associated CoffeeGirl state.
	 * @param foodItem The GameFoodItem to add to this game.
	 * @param associated_coffeegirl_state The associated state that CoffeeGirl will be put into when she 
	 * receives the FoodItem.
	 */
	public synchronized void addNewFoodItem(GameFoodItem foodItem, int associated_coffeegirl_state) {
		//If this is the first food item that we are adding then have CoffeeGirl be holding that food item
		//Better hope that the first food item is "nothing"!
		boolean doSetItemHolding = false;
		if(foodItems.isEmpty()) doSetItemHolding = true;
		
		//Add new food item to the list of food items for this level (foodItems)
		foodItems.put(foodItem.getName(), foodItem);
		//Set association between the new food item and an associated CoffeeGirl state
		coffeeGirl.setItemHoldingToStateAssoc(foodItem.getName(), associated_coffeegirl_state);
		
		//If CoffeeGirl's "held item" hasn't been set up yet then set it to the first foodItem that we add
		//better hope that the first one we add is "nothing"!
		if(doSetItemHolding) coffeeGirl.setItemHolding(foodItem.getName());
	}
	
	/** Return a List of the GameFoodItems that have been added to this GameLogicThread. Utility method that
	 * is a bit out of place but it is convenient since we have to explain the GameFoodItem -> CoffeeGirl State
	 * mapping to GTL anyway and so it implicitly gets a List of the GameFoodItems that are valid in this game.
	 * 
	 * @return A List of GameFoodItems valid for this game/level/whatever.
	 */
	public synchronized List<GameFoodItem> getFoodItems() {
		ArrayList<GameFoodItem> retval = new ArrayList<GameFoodItem>();
		Iterator<String> it = foodItems.keySet().iterator();
		while(it.hasNext()) retval.add(foodItems.get(it.next()));
		
		return(retval);
	}
	
	/**Clears all of the gameItems, foodItems, etc in preparation to load a new level.
	 * 
	 */
	public synchronized void reset() {
		coffeeGirl = null;
		gameItems = new HashMap<String, GameItem>();
		foodItems = new HashMap<String, GameFoodItem>();
	}
	
	@Override
	public void run() {
		
		;

	}
	
	public static final int MAX_GAME_LEVEL=4;
	/** Loads a new level; creates a GameLevel Object corresponding to the new level
	 * and kicks off (unpauses) all of the threads.
	 * @param levelNumber
	 */
	public void loadLevel(int levelNumber) {
		GameLevel newLevel = getLevelInstance(levelNumber);
		if(newLevel != null)
			newLevel.loadLevel(viewThread, gameLogicThread, inputThread, this.caller);
		
		//Set up the game state
		GameInfo.setLevel(levelNumber);
		
		//Clear the level info (how many points/dollars we've earned this level)
		GameInfo.levelReset();
		
		if(newLevel == null) 
			Log.d(activitynametag, "newLevel was unexpectedly null! levelNumber = " + levelNumber);
		else
			GameInfo.setLevelTime(newLevel.getLevelTime());
	}
	
	/** Return a new instance of a GameLevel sub-class corrsponding to level # levelNumber 
	 * 
	 * @param levelNumber The level number to return a GameLevel instance for
	 * @return GameLevel instance for level levelNumber
	 */
	public GameLevel getLevelInstance(int levelNumber) {
		GameLevel newLevel = null;
		
		if(levelNumber == 1) {
			//Launch level 1!
			newLevel = new GameLevel_1();
		}
		else if(levelNumber == 2) {
			//Launch level 2!
			newLevel = new GameLevel_2();
		}
		else if(levelNumber == 3) {
			newLevel = new GameLevel_3();
		}
		else if(levelNumber == 4) {
			newLevel = new GameLevel_4();
		}
		else {
			Log.e(activitynametag, "Invalid level reached!");
		}
		
		return(newLevel);
	}
	
}
