package org.coffeecats.coffeetime.gamelogic.leveldefs;

import org.coffeecats.coffeetime.GameLogicThread;
import org.coffeecats.coffeetime.InputThread;
import org.coffeecats.coffeetime.ViewThread;
import org.coffeecats.coffeetime.gamelogic.CustomerQueueWrapper;
import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gamelogic.GameLevel;
import org.coffeecats.coffeetime.gameobjects.CoffeeGirl;
import org.coffeecats.coffeetime.gameobjects.CustomerQueue;
import org.coffeecats.coffeetime.gameobjects.GameItem;
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemBlendedDrink;
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemCoffee;
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemCupcake;
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemNothing;
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemPieSlice;
import org.coffeecats.coffeetime.gameobjects.objectdefs.Blender;
import org.coffeecats.coffeetime.gameobjects.objectdefs.CoffeeMachine;
import org.coffeecats.coffeetime.gameobjects.objectdefs.CounterTop;
import org.coffeecats.coffeetime.gameobjects.objectdefs.CupCakeTray;
import org.coffeecats.coffeetime.gameobjects.objectdefs.PieTray;
import org.coffeecats.coffeetime.gameobjects.objectdefs.SoundSystem;
import org.coffeecats.coffeetime.gameobjects.objectdefs.TrashCan;

import android.content.Context;

import org.coffeecats.coffeetime.R;

/** Describes level #3 for the Coffee Time game! */

public class GameLevel_4 extends GameLevel {
	public GameLevel_4() {
		this.level_number = 4;
		this.customerQueue_length = 30;
		this.point_mult = 1.5f;
		this.money_mult = 1.5f;
		this.customer_impatience = 0.6f * customer_impatience_modifier_for_2_lines;
		this.time_limit_sec = 3 * 60 - 10;
		this.customer_max_order_size = 3;
		
		this.point_bonus = 60;
		this.money_bonus = 25;
		this.point_bonus_derating = 0.25f;
		this.money_bonus_derating = 0.25f;
	}
	
	/** Set up this level; add all GameItems and such to the Threads, set up the Customers and such
	 * with the per-level parameters.
	 * @param vT ViewThread associated with this game session
	 * @param gLT GameLogicThread associated with this game session
	 * @param iT InputThread associated with this game session
	 * @param caller The calling Context for loading resources and such
	 */
	public void loadLevel(ViewThread viewThread, GameLogicThread gameLogicThread, InputThread inputThread, Context caller) {
		super.loadLevel(viewThread, gameLogicThread, inputThread, caller);
		
		//Setup coffeegirl (actor)
		CoffeeGirl coffeegirl = new CoffeeGirl(caller);
		viewThread.addViewObject(coffeegirl);
		viewThread.setActor(coffeegirl);
		inputThread.addViewObject(coffeegirl);
		gameLogicThread.setActor(coffeegirl);
		
		//Create and add objects to viewThread containers (UPDATE FOR NEW GAMEITEM)
		CoffeeMachine coffeeMachine = new CoffeeMachine(caller, R.drawable.coffeemachine, 
				CoffeeMachine.DEFAULT_XPOS, CoffeeMachine.DEFAULT_YPOS, GameItem.ORIENTATION_WEST);
		//GameItem coffeeMachine = new GameItem(caller, "CoffeeMachine", R.drawable.coffeemachine, 100, 50, GameItem.ORIENTATION_NORTH);
		//viewThread.addViewObject(coffeeMachine);
		viewThread.addGameItem(coffeeMachine);
		inputThread.addViewObject(coffeeMachine);
		gameLogicThread.addGameItem(coffeeMachine);	
		
		if(GameInfo.hasUpgrade("secondcoffeemachine")) {
			coffeeMachine = new CoffeeMachine(caller, R.drawable.coffeemachine, 
					CoffeeMachine.DEFAULT_XPOS, CoffeeMachine.DEFAULT_YPOS+CoffeeMachine.Y_DIST_TO_SECOND_MACHINE, GameItem.ORIENTATION_WEST);
			viewThread.addGameItem(coffeeMachine);
			inputThread.addViewObject(coffeeMachine);
			gameLogicThread.addGameItem(coffeeMachine);
		}
		
		if(GameInfo.hasUpgrade("countertop")) {
			CounterTop counterTop = new CounterTop(caller, R.drawable.countertop_grey, 
					CounterTop.DEFAULT_XPOS, CounterTop.DEFAULT_YPOS, GameItem.ORIENTATION_SOUTH);
			viewThread.addGameItem(counterTop);
			inputThread.addViewObject(counterTop);
			gameLogicThread.addGameItem(counterTop);
		}
		
		TrashCan trashCan = new TrashCan(caller, R.drawable.trashcan, 
				TrashCan.DEFAULT_XPOS, TrashCan.DEFAULT_YPOS, GameItem.ORIENTATION_EAST);
		//viewThread.addViewObject(trashCan);
		viewThread.addGameItem(trashCan);
		inputThread.addViewObject(trashCan);
		gameLogicThread.addGameItem(trashCan);
		
		CupCakeTray cupcakeTray = new CupCakeTray(caller, R.drawable.cupcake_tray, 
				CupCakeTray.DEFAULT_XPOS, CupCakeTray.DEFAULT_YPOS, GameItem.ORIENTATION_EAST);
		//viewThread.addViewObject(cupcakeTray);
		viewThread.addGameItem(cupcakeTray);
		inputThread.addViewObject(cupcakeTray);
		gameLogicThread.addGameItem(cupcakeTray);
		
		PieTray pieTray = new PieTray(caller, R.drawable.cake_tray, 
				PieTray.DEFAULT_XPOS, PieTray.DEFAULT_YPOS, GameItem.ORIENTATION_EAST);
		//viewThread.addViewObject(cupcakeTray);
		viewThread.addGameItem(pieTray);
		inputThread.addViewObject(pieTray);
		gameLogicThread.addGameItem(pieTray);
		
		Blender blender = new Blender(caller, R.drawable.blender_idle, 
				Blender.DEFAULT_XPOS, Blender.DEFAULT_YPOS, GameItem.ORIENTATION_WEST);
		//viewThread.addViewObject(blender);
		viewThread.addGameItem(blender);
		inputThread.addViewObject(blender);
		gameLogicThread.addGameItem(blender);
		
		if(GameInfo.hasUpgrade("secondblender")) {
			blender = new Blender(caller, R.drawable.blender_idle, 
					Blender.DEFAULT_XPOS, Blender.DEFAULT_YPOS+Blender.Y_DIST_TO_SECOND_BLENDER, GameItem.ORIENTATION_WEST);
			viewThread.addGameItem(blender);
			inputThread.addViewObject(blender);
			gameLogicThread.addGameItem(blender);
		}
		
		if(GameInfo.hasUpgrade("soundsystem")) {
			SoundSystem soundsystem = new SoundSystem(caller);
			//viewThread.addViewObject(blender);
			viewThread.addGameItem(soundsystem);
			inputThread.addViewObject(soundsystem);
			gameLogicThread.addGameItem(soundsystem);
			
			this.customer_impatience *= 0.9;
		}
		
		//Set up all Food Items (UPDATE FOR NEW FOODITEM)
		gameLogicThread.addNewFoodItem(new FoodItemNothing(caller), CoffeeGirl.STATE_NORMAL);
		gameLogicThread.addNewFoodItem(new FoodItemCoffee(caller), CoffeeGirl.STATE_CARRYING_COFFEE);
		gameLogicThread.addNewFoodItem(new FoodItemCupcake(caller), CoffeeGirl.STATE_CARRYING_CUPCAKE);
		gameLogicThread.addNewFoodItem(new FoodItemBlendedDrink(caller), CoffeeGirl.STATE_CARRYING_BLENDEDDRINK);
		gameLogicThread.addNewFoodItem(new FoodItemPieSlice(caller), CoffeeGirl.STATE_CARRYING_PIESLICE);
		
		CustomerQueue custQueue = new CustomerQueue(caller, CustomerQueue.X_POS, 
				CustomerQueue.Y_POS_FROM_GG_TOP, 
				GameItem.ORIENTATION_NORTH,  
				customerQueue_length/2, point_mult, money_mult, 
				customer_impatience, customer_max_order_size, 
				gameLogicThread.getFoodItems());
		
		CustomerQueue custQueue2 = new CustomerQueue(caller, 
				CustomerQueue.X_POS + CustomerQueue.DISTANCE_TO_QUEUE_TWO, 
				CustomerQueue.Y_POS_FROM_GG_TOP, 
				GameItem.ORIENTATION_NORTH, 
				customerQueue_length/2, point_mult, money_mult, 
				customer_impatience, customer_max_order_size, 
				gameLogicThread.getFoodItems(), 2);
			viewThread.addGameItem(custQueue2);
			inputThread.addViewObject(custQueue2);
		
		viewThread.addGameItem(custQueue2);
		inputThread.addViewObject(custQueue2);
		viewThread.addGameItem(custQueue);
		inputThread.addViewObject(custQueue);
		
		gameLogicThread.setCustomerQueue(new CustomerQueueWrapper(custQueue, custQueue2));
	}
}
