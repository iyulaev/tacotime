/** This is the main View (SurfaceView) of the TacoTime game engine. In addition to implementing 
 * the UI it also creates all of the game Threads, generates some initial objects, and launches everything.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import java.util.ArrayList;
import java.util.Iterator;

import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gameobjects.CoffeeGirl;
import com.yulaev.tacotime.gameobjects.CoffeeMachine;
import com.yulaev.tacotime.gameobjects.GameItem;
import com.yulaev.tacotime.gameobjects.TrashCan;
import com.yulaev.tacotime.gameobjects.ViewObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		timerThread.setRunning(true);
		viewThread.setRunning(true);
		
		//Create and add objects to viewThread containers
		CoffeeMachine coffeeMachine = new CoffeeMachine(this.getContext(), R.drawable.coffeemachine, 20, 20, GameItem.ORIENTATION_NORTH);
		//GameItem coffeeMachine = new GameItem(this.getContext(), "CoffeeMachine", R.drawable.coffeemachine, 100, 50, GameItem.ORIENTATION_NORTH);
		viewThread.addViewObject(coffeeMachine);
		viewThread.addGameItem(coffeeMachine);
		inputThread.addViewObject(coffeeMachine);
		gameLogicThread.addGameItem(coffeeMachine);
				
		CoffeeGirl coffeegirl = new CoffeeGirl(this.getContext(), holder.getSurfaceFrame());
		viewThread.addViewObject(coffeegirl);
		viewThread.setActor(coffeegirl);
		inputThread.addViewObject(coffeegirl);
		gameLogicThread.setActor(coffeegirl);
		
		TrashCan trashCan = new TrashCan(this.getContext(), R.drawable.trashcan, 100, 20, GameItem.ORIENTATION_EAST);
		viewThread.addViewObject(trashCan);
		viewThread.addGameItem(trashCan);
		inputThread.addViewObject(trashCan);
		gameLogicThread.addGameItem(trashCan);
		
		//Kick off all of the threads
		gameLogicThread.start();
		timerThread.start();
		viewThread.start();
		inputThread.start();
	}

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

	/** This onDraw() method will draw all of the ViewObjects in the game
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
		Log.v(activitynametag, "onDraw() drawing a grey rectangle around (" + GameGrid.maxCanvasX() + ", " + GameGrid.maxCanvasY() + ")");
		
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
