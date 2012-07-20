/** This is the main View (SurfaceView) of the TacoTime game engine. In addition to implementing 
 * the UI it also creates all of the game Threads, generates some initial objects, and launches everything.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import java.util.ArrayList;
import java.util.Iterator;

import com.yulaev.tacotime.gameobjects.CoffeeGirl;
import com.yulaev.tacotime.gameobjects.CoffeeMachine;
import com.yulaev.tacotime.gameobjects.GameItem;
import com.yulaev.tacotime.gameobjects.TrashCan;
import com.yulaev.tacotime.gameobjects.ViewObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
		CoffeeMachine coffeeMachine = new CoffeeMachine(this.getContext(), R.drawable.coffeemachine, 100, 50, GameItem.ORIENTATION_NORTH);
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
		
		TrashCan trashCan = new TrashCan(this.getContext(), R.drawable.trashcan, 200, 200, GameItem.ORIENTATION_EAST);
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
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
	}

	/** This onDraw() method will draw all of the ViewObjects in the game
	 * 
	 * @param canvas The Canvas to draw everything onto
	 * @param voAr An ArrayList of ViewObjects that will be drawn
	 */
	protected void onDraw(Canvas canvas, ArrayList<ViewObject> voAr) {
		canvas.drawColor(Color.BLACK);
		
		Iterator<ViewObject> it = voAr.iterator();
		while(it.hasNext()) {
			it.next().draw(canvas);
		}
	}

}
