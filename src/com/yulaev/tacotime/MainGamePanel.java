/** This is the main View (SurfaceView) of the TacoTime game engine. In addition to implementing 
 * the UI it also creates all of the game Threads, generates some initial objects, and launches everything.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gameobjects.Blender;
import com.yulaev.tacotime.gameobjects.CoffeeGirl;
import com.yulaev.tacotime.gameobjects.CoffeeMachine;
import com.yulaev.tacotime.gameobjects.CupCakeTray;
import com.yulaev.tacotime.gameobjects.CustomerQueue;
import com.yulaev.tacotime.gameobjects.FoodItemBlendedDrink;
import com.yulaev.tacotime.gameobjects.FoodItemCoffee;
import com.yulaev.tacotime.gameobjects.FoodItemCupcake;
import com.yulaev.tacotime.gameobjects.FoodItemNothing;
import com.yulaev.tacotime.gameobjects.GameFoodItem;
import com.yulaev.tacotime.gameobjects.GameItem;
import com.yulaev.tacotime.gameobjects.TrashCan;
import com.yulaev.tacotime.gameobjects.ViewObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

	private static final String activitynametag = MainGamePanel.class.getSimpleName();
	
	//The below are the UI-related threads that are involved in handling input and rendering the game
	TimerThread timerThread;
	ViewThread viewThread;
	InputThread inputThread;
	GameLogicThread gameLogicThread;

	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		// create the timer loop thread, that fires off events to other threads
		timerThread = new TimerThread();
		MessageRouter.timerThread = timerThread;
		
		// create the view thread that will be responsible for updating the canvas
		viewThread = new ViewThread(getHolder(), this);
		MessageRouter.viewThread = viewThread;
		
		// create the input handler thread
		inputThread = new InputThread();
		MessageRouter.inputThread = inputThread;
		
		//create the Game Logic Thread
		gameLogicThread = new GameLogicThread();
		MessageRouter.gameLogicThread = gameLogicThread;
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/** In this method we perform pretty much all of the initialization and placement of in-game items.
	 * We create Objects for all of the in-game items, set up the game character (CoffeeGirl), add all
	 *  of the item's to the various thread's sensitivity lists, and kick off all of the threads once 
	 *  this is done.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		timerThread.setRunning(true);
		viewThread.setRunning(true);
		
		//Setup coffeegirl (actor)
		CoffeeGirl coffeegirl = new CoffeeGirl(this.getContext(), holder.getSurfaceFrame());
		viewThread.addViewObject(coffeegirl);
		viewThread.setActor(coffeegirl);
		inputThread.addViewObject(coffeegirl);
		gameLogicThread.setActor(coffeegirl);
		
		//Create and add objects to viewThread containers (UPDATE FOR NEW GAMEITEM)
		CoffeeMachine coffeeMachine = new CoffeeMachine(this.getContext(), R.drawable.coffeemachine, 16, 40, GameItem.ORIENTATION_WEST);
		//GameItem coffeeMachine = new GameItem(this.getContext(), "CoffeeMachine", R.drawable.coffeemachine, 100, 50, GameItem.ORIENTATION_NORTH);
		viewThread.addViewObject(coffeeMachine);
		viewThread.addGameItem(coffeeMachine);
		inputThread.addViewObject(coffeeMachine);
		gameLogicThread.addGameItem(coffeeMachine);		
		
		TrashCan trashCan = new TrashCan(this.getContext(), R.drawable.trashcan, 110, 40, GameItem.ORIENTATION_EAST);
		viewThread.addViewObject(trashCan);
		viewThread.addGameItem(trashCan);
		inputThread.addViewObject(trashCan);
		gameLogicThread.addGameItem(trashCan);
		
		CupCakeTray cupcakeTray = new CupCakeTray(this.getContext(), R.drawable.cupcake_tray, 113, 60, GameItem.ORIENTATION_EAST);
		viewThread.addViewObject(cupcakeTray);
		viewThread.addGameItem(cupcakeTray);
		inputThread.addViewObject(cupcakeTray);
		gameLogicThread.addGameItem(cupcakeTray);
		
		Blender blender = new Blender(this.getContext(), R.drawable.blender_idle, 16, 60, GameItem.ORIENTATION_WEST);
		viewThread.addViewObject(blender);
		viewThread.addGameItem(blender);
		inputThread.addViewObject(blender);
		gameLogicThread.addGameItem(blender);
		
		//Set up all Food Items (UPDATE FOR NEW FOODITEM)
		gameLogicThread.addNewFoodItem(new FoodItemNothing(this.getContext()), CoffeeGirl.STATE_NORMAL);
		gameLogicThread.addNewFoodItem(new FoodItemCoffee(this.getContext()), CoffeeGirl.STATE_CARRYING_COFFEE);
		gameLogicThread.addNewFoodItem(new FoodItemCupcake(this.getContext()), CoffeeGirl.STATE_CARRYING_CUPCAKE);
		gameLogicThread.addNewFoodItem(new FoodItemBlendedDrink(this.getContext()), CoffeeGirl.STATE_CARRYING_BLENDEDDRINK);
		
		//Magic numbers: 40 - x-position of Customers, (GameGrid.GAMEGRID_HEIGHT-45) - y-position of customers
		//1 - starting customer queue length, 
		CustomerQueue custQueue = new CustomerQueue(this.getContext(), 40, GameGrid.GAMEGRID_HEIGHT-45, GameItem.ORIENTATION_SOUTH, 
				5, gameLogicThread.getFoodItems());
		viewThread.addViewObject(custQueue);
		viewThread.addGameItem(custQueue);
		inputThread.addViewObject(custQueue);
		gameLogicThread.addGameItem(custQueue);
		
		
		//Kick off all of the threads
		gameLogicThread.start();
		timerThread.start();
		viewThread.start();
		inputThread.start();
	}

	/** This method winds down all of the threads. */
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(activitynametag, "Surface is being destroyed");
		
		timerThread.setRunning(false);
		viewThread.setRunning(false);
		
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				timerThread.join();
				viewThread.join();
				inputThread.join();
				gameLogicThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		
		Log.d(activitynametag, "Threads were shut down cleanly");
	}
	
	/** Responds to a touch event; mostly just sends the tap event to the InputThread via MessageRouter. 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.d(activitynametag, "Coords: x=" + event.getX() + ",y=" + event.getY());
			MessageRouter.sendInputTapMessage((int)event.getX(), (int)event.getY());
		}
		return super.onTouchEvent(event);
	}
	
	//Paint that we use in onDraw(); these are class variables so that we don't keep creating new ones
	Paint gridPaint;
	Paint moneyPaint;
	Paint pointsPaint;
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
	}

	/** This onDraw() method will draw all of the ViewObjects in the game; it is the main onDraw() method
	 * that gets called by the ViewThread.
	 * 
	 * @param canvas The Canvas to draw everything onto
	 * @param voAr An ArrayList of ViewObjects that will be drawn
	 * @param money The amount of money the player has so that we can draw it
	 * @param points The number of points the player has so that we can draw it
	 */
	protected void onDraw(Canvas canvas, ArrayList<ViewObject> voAr, int money, int points) {
		if(gridPaint == null) {
			gridPaint = new Paint();
			gridPaint.setColor(Color.GRAY);
		}
		
		canvas.drawColor(Color.BLACK);
		
		canvas.drawRect(0,0, GameGrid.maxCanvasX(), GameGrid.maxCanvasY(), gridPaint);
		
		Iterator<ViewObject> it = voAr.iterator();
		while(it.hasNext()) {
			it.next().draw(canvas);
		}
		
		if(moneyPaint == null || pointsPaint == null) {
			moneyPaint = new Paint();
			moneyPaint.setColor(Color.BLUE);
			moneyPaint.setTextSize(12);
			pointsPaint = new Paint();
			pointsPaint.setColor(Color.GREEN);
			pointsPaint.setTextSize(12);
		}
		
		canvas.drawText(Integer.toString(money), 10, canvas.getHeight()-30, moneyPaint);
		canvas.drawText(Integer.toString(points), 10, canvas.getHeight()-15, pointsPaint);
		
	}

}
