package org.coffeecats.coffeetime.gamelogic.leveldefs;

import java.util.ArrayList;

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
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemEspresso;
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemNothing;
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemPieSlice;
import org.coffeecats.coffeetime.gameobjects.fooditemdefs.FoodItemSandwich;
import org.coffeecats.coffeetime.gameobjects.objectdefs.Blender;
import org.coffeecats.coffeetime.gameobjects.objectdefs.CoffeeMachine;
import org.coffeecats.coffeetime.gameobjects.objectdefs.CounterTop;
import org.coffeecats.coffeetime.gameobjects.objectdefs.CupCakeTray;
import org.coffeecats.coffeetime.gameobjects.objectdefs.EspressoMachine;
import org.coffeecats.coffeetime.gameobjects.objectdefs.Microwave;
import org.coffeecats.coffeetime.gameobjects.objectdefs.PieTray;
import org.coffeecats.coffeetime.gameobjects.objectdefs.SoundSystem;
import org.coffeecats.coffeetime.gameobjects.objectdefs.TrashCan;

import android.content.Context;

import org.coffeecats.coffeetime.R;

/** Describes level #1 for the Coffee Time game! */

public class GameLevel_1 extends GameLevel {
	
	private static boolean TESTING_MODE = true; 
	
	public GameLevel_1() {
		this.level_number = 1;
		this.customerQueue_length = 6;
		this.point_mult = 1.0f;
		this.money_mult = 1.0f;
		this.customer_impatience = 0.6f;
		this.time_limit_sec = 45;
		this.customer_max_order_size = 1;
		
		this.point_bonus = 20;
		this.money_bonus = 10;
		this.point_bonus_derating = 0.5f;
		this.money_bonus_derating = 0.5f;
		
		//New machines? Coffee machine, cupcakes, 
		newMachines = new ArrayList<ArrayList<Integer>>();
		
		newMachines.add(new ArrayList<Integer>());
		newMachines.get(0).add(R.drawable.coffeemachine_idle);
		newMachines.get(0).add(R.drawable.fooditem_coffee);
		
		newMachines.add(new ArrayList<Integer>());
		newMachines.get(1).add(R.drawable.cupcake_tray);
		newMachines.get(1).add(R.drawable.fooditem_cupcake);
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
		
		if(GameInfo.hasUpgrade("secondcoffeemachine") || TESTING_MODE) {
			coffeeMachine = new CoffeeMachine(caller, R.drawable.coffeemachine, 
					CoffeeMachine.DEFAULT_XPOS, CoffeeMachine.DEFAULT_YPOS+CoffeeMachine.Y_DIST_TO_SECOND_MACHINE, GameItem.ORIENTATION_WEST);
			viewThread.addGameItem(coffeeMachine);
			inputThread.addViewObject(coffeeMachine);
			gameLogicThread.addGameItem(coffeeMachine);
		}
		
		if(GameInfo.hasUpgrade("secondblender") || TESTING_MODE) {
			Blender blender = new Blender(caller, R.drawable.blender_idle, 
					Blender.DEFAULT_XPOS, Blender.DEFAULT_YPOS+Blender.Y_DIST_TO_SECOND_BLENDER, GameItem.ORIENTATION_WEST);
			viewThread.addGameItem(blender);
			inputThread.addViewObject(blender);
			gameLogicThread.addGameItem(blender);
		}
		
		if(GameInfo.hasUpgrade("countertop") || TESTING_MODE) {
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
		
		if(TESTING_MODE) {
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
			
			Microwave microwave = new Microwave(caller, R.drawable.microwave_inactive, 
					Microwave.DEFAULT_XPOS, Microwave.DEFAULT_YPOS, GameItem.ORIENTATION_EAST);
			//viewThread.addViewObject(blender);
			viewThread.addGameItem(microwave);
			inputThread.addViewObject(microwave);
			gameLogicThread.addGameItem(microwave);
			
			if(true) {
				EspressoMachine espressomachine = new EspressoMachine(caller, R.drawable.espresso_machine_inactive, 
						EspressoMachine.DEFAULT_XPOS, EspressoMachine.DEFAULT_YPOS, GameItem.ORIENTATION_SOUTH);
				//viewThread.addViewObject(blender);
				viewThread.addGameItem(espressomachine);
				inputThread.addViewObject(espressomachine);
				gameLogicThread.addGameItem(espressomachine);
			}
			
			if(true) {
				SoundSystem soundsystem = new SoundSystem(caller);
				//viewThread.addViewObject(blender);
				viewThread.addGameItem(soundsystem);
				inputThread.addViewObject(soundsystem);
				gameLogicThread.addGameItem(soundsystem);
				
				this.customer_impatience *= 0.9;
			}
		}
		
		//Set up all Food Items (UPDATE FOR NEW FOODITEM)
		gameLogicThread.addNewFoodItem(new FoodItemNothing(caller), CoffeeGirl.STATE_NORMAL);
		gameLogicThread.addNewFoodItem(new FoodItemCoffee(caller), CoffeeGirl.STATE_CARRYING_COFFEE);
		gameLogicThread.addNewFoodItem(new FoodItemCupcake(caller), CoffeeGirl.STATE_CARRYING_CUPCAKE);
		
		if(TESTING_MODE) {
			gameLogicThread.addNewFoodItem(new FoodItemBlendedDrink(caller), CoffeeGirl.STATE_CARRYING_BLENDEDDRINK);
			gameLogicThread.addNewFoodItem(new FoodItemPieSlice(caller), CoffeeGirl.STATE_CARRYING_PIESLICE);
			gameLogicThread.addNewFoodItem(new FoodItemSandwich(caller), CoffeeGirl.STATE_CARRYING_SANDWICH);
			
			if(true)
				gameLogicThread.addNewFoodItem(new FoodItemEspresso(caller), CoffeeGirl.STATE_CARRYING_ESPRESSO);
		}
		
		//Magic numbers: 40 - x-position of Customers, (GameGrid.GAMEGRID_HEIGHT-45) - y-position of customers
		CustomerQueue custQueue1 = new CustomerQueue(caller, 
				CustomerQueue.X_POS, 
				CustomerQueue.Y_POS_FROM_GG_TOP,
				GameItem.ORIENTATION_NORTH, 
				customerQueue_length, point_mult, money_mult, 
				customer_impatience, customer_max_order_size, 
				gameLogicThread.getFoodItems(), 
				1); //1 -> Magic number, the CustomerQueue ID # (for multiple queue instances)
		viewThread.addGameItem(custQueue1);
		inputThread.addViewObject(custQueue1);
		
		if(TESTING_MODE) {
			CustomerQueue custQueue2 = new CustomerQueue(caller, 
				CustomerQueue.X_POS + CustomerQueue.DISTANCE_TO_QUEUE_TWO, 
				CustomerQueue.Y_POS_FROM_GG_TOP, 
				GameItem.ORIENTATION_NORTH, 
				customerQueue_length, point_mult, money_mult, 
				customer_impatience, customer_max_order_size, 
				gameLogicThread.getFoodItems(), 2);
			viewThread.addGameItem(custQueue2);
			inputThread.addViewObject(custQueue2);
			
			gameLogicThread.setCustomerQueue(new CustomerQueueWrapper(custQueue1, custQueue2));
		} else
			gameLogicThread.setCustomerQueue(new CustomerQueueWrapper(custQueue1));
	}
}
