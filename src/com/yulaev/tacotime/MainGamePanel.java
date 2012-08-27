/** This is the main View (SurfaceView) of the TacoTime game engine. In addition to implementing 
 * the UI it also creates all of the game Threads, and launches everything. GameLogicThread (now)
 * deals with loading all of the levle data, setting up the GameItems, etc.
 * 
 * @author iyulaev
 */

package com.yulaev.tacotime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gamelogic.leveldefs.GameLevel_1;
import com.yulaev.tacotime.gameobjects.CoffeeGirl;
import com.yulaev.tacotime.gameobjects.CustomerQueue;
import com.yulaev.tacotime.gameobjects.GameFoodItem;
import com.yulaev.tacotime.gameobjects.GameItem;
import com.yulaev.tacotime.gameobjects.ViewObject;
import com.yulaev.tacotime.gameobjects.fooditemdefs.FoodItemBlendedDrink;
import com.yulaev.tacotime.gameobjects.fooditemdefs.FoodItemCoffee;
import com.yulaev.tacotime.gameobjects.fooditemdefs.FoodItemCupcake;
import com.yulaev.tacotime.gameobjects.fooditemdefs.FoodItemNothing;
import com.yulaev.tacotime.gameobjects.objectdefs.Blender;
import com.yulaev.tacotime.gameobjects.objectdefs.CoffeeMachine;
import com.yulaev.tacotime.gameobjects.objectdefs.CupCakeTray;
import com.yulaev.tacotime.gameobjects.objectdefs.TrashCan;

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
	
	//Make sure we don't double-make the threads
	boolean threads_launched;

	/** Constructor for MainGamePanel. Mostly this sets up and launches all of the game threads.
	 * 
	 * @param context The context that creates this MainGamePanel
	 * @param load_saved_game Whether to load a saved game or not
	 */
	public MainGamePanel(Context context, boolean load_saved_game) {
		super(context);
		
		Log.d(activitynametag, "MainGamePanel constructor called!");
		
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
		gameLogicThread = new GameLogicThread(viewThread, timerThread, inputThread, this.getContext(), load_saved_game);
		gameLogicThread.setSelf(gameLogicThread);
		MessageRouter.gameLogicThread = gameLogicThread;
		
		threads_launched = false;
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	/** Not sure why this is here right now but I suppose it is to handle changes like a change
	 * in orientation (which we should suppress anyway!). This is implemented here only because 
	 * we implement SurfaceHolder.Callback.
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/** In this method we perform pretty much all of the initialization and placement of in-game items.
	 * We create Objects for all of the in-game items, set up the game character (CoffeeGirl), add all
	 *  of the item's to the various thread's sensitivity lists, and kick off all of the threads once 
	 *  this is done.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(activitynametag, "MainGamePanel surfaceCreated() called!");
		
		//If we've not launched all of the Threads then do so
		if(!threads_launched) {
			// at this point the surface is created and
			// we can safely start the game loop
			
			//Kick off all of the threads
			viewThread.start();
			inputThread.start();
			gameLogicThread.start();
			timerThread.start();
			
			threads_launched = true;
		} 
		//Otherwise just un-suspend the timer and view threads (since they exist already...right?)
		else {
			MessageRouter.sendSuspendTimerThreadMessage(false);
			MessageRouter.sendSuspendViewThreadMessage(false);
		}
	}

	/** This method winds down all of the threads. */
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(activitynametag, "MainGamePanel surfaceDestroyed() called!");
		
		//Pause the threads that have event loops built in
		MessageRouter.sendSuspendTimerThreadMessage(true);
		MessageRouter.sendSuspendViewThreadMessage(true);
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
	Paint announcementPaint;
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
	}

	/** This onDraw() method will draw all of the ViewObjects in the game; it is the main onDraw() method
	 * that gets called by the ViewThread. It can also draw announcement messages that overlay the
	 * rest of the game.
	 * 
	 * @param canvas The Canvas to draw everything onto
	 * @param voAr An ArrayList of ViewObjects that will be drawn
	 * @param money The amount of money the player has so that we can draw it
	 * @param points The number of points the player has so that we can draw it
	 * @param boolean draw_announcement_message Whether or not to draw an announcement; it is a message that
	 * overlays everything else on the screen
	 * @param announcementMessage The announcement message to draw; will not be drawn (and may be null) if
	 * draw_announcement_message is set to false
	 */
	protected void onDraw(Canvas canvas, ArrayList<ViewObject> voAr, 
			int money, int points, 
			boolean draw_announcement_message, String announcementMessage) {
		if(gridPaint == null) {
			gridPaint = new Paint();
			gridPaint.setColor(Color.GRAY);
		}
		
		canvas.drawColor(Color.BLACK);
		
		//Draw in a background
		canvas.drawRect(0,0, GameGrid.maxCanvasX(), GameGrid.maxCanvasY(), gridPaint);
		
		//Draw ALL ViewObjects on the board
		Iterator<ViewObject> it = voAr.iterator();
		while(it.hasNext()) {
			it.next().draw(canvas);
		}
		
		//Set up all of the Paint objects if we haven't done this yet! (should only happen once per game)
		if(moneyPaint == null || pointsPaint == null || announcementPaint == null) {
			moneyPaint = new Paint();
			moneyPaint.setColor(Color.GREEN);
			moneyPaint.setTextSize(14);
			pointsPaint = new Paint();
			pointsPaint.setColor(Color.BLUE);
			pointsPaint.setTextSize(14);
			announcementPaint = new Paint();
			announcementPaint.setColor(Color.RED);
			announcementPaint.setTextSize(24);
			announcementPaint.setTextAlign(Paint.Align.CENTER);
		}
		
		//Draw money, points and display an announcement message IF there is an announcement
		canvas.drawText(Integer.toString(money), 14, canvas.getHeight()-40, moneyPaint);
		canvas.drawText(Integer.toString(points), 14, canvas.getHeight()-15, pointsPaint);
		if(draw_announcement_message) {
			canvas.drawText(announcementMessage, canvas.getWidth()/2, canvas.getHeight()/2, announcementPaint);
		}
		
	}

}
