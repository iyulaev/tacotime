package com.yulaev.tacotime;


import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gameobjects.CoffeeGirl;
import com.yulaev.tacotime.gameobjects.GameItem;
import com.yulaev.tacotime.gameobjects.ViewObject;


/**
 * @author iyulaev
 *
 * This Thread ... (TODO)
 */
public class ViewThread extends Thread {
	
	private static final String activitynametag = "ViewThread";
	
	//Define types of messages accepted by ViewThread
	public static final int MESSAGE_REFRESH_VIEW = 0;
	public static final int MESSAGE_SET_PAUSED = 1;
	public static final int MESSAGE_SET_UNPAUSED = 2;
	public static final int MESSAGE_NEW_ANNOUNCEMENT = 3;
	public static final int MESSAGE_STOP_ANNOUNCEMENT = 4;
	
	//Define view refresh period in ms
	public static final int VIEW_REFRESH_PERIOD = 20; 

	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private MainGamePanel gamePanel;
	//This message handler will receive messages, probably from the TimerThread, and
	//redraw the canvas and do other View-related things
	public static Handler handler;
	
	//Here we hold all of the objects that the ViewThread must update
	ArrayList<ViewObject> viewObjects;
	//Here we have the Actor, the player character
	CoffeeGirl actor;
	//Here we hold all of the GameItems that are rendered by this ViewThread
	ArrayList<GameItem> gameItems;
	
	//If running is false then we do the refreshView() command which sends an onUpdate() request
	//to all ViewObjects and also redraws the canvas
	private boolean running;
	//If paused is true then, during refreshView(), we do not call onUpdate() BUT we still re-draw the entire canvas
	private boolean paused;
	
	//Variables to hold the current announcement message (a message that will be printed in LARGE CHARACTERS
	//that overlay everything else on the canvas
	private boolean draw_announcement_message;
	private String announcementMessage;

	public ViewThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
		this.viewObjects = new ArrayList<ViewObject>();
		this.gameItems = new ArrayList<GameItem>();
		this.actor = null;
		
		running = false;
		paused = true;
		
		draw_announcement_message = false;
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MESSAGE_REFRESH_VIEW) {
					Log.d(activitynametag, "Got view refresh message!");
					//if(!isRefreshing) refreshView();
				}
				
				else if (msg.what == MESSAGE_SET_PAUSED) setPaused(true);
				else if (msg.what == MESSAGE_SET_UNPAUSED) setPaused(false);
				
				else if (msg.what == MESSAGE_NEW_ANNOUNCEMENT) {
					announcementMessage = (String) msg.obj;
					draw_announcement_message = true;
				}
				else if (msg.what == MESSAGE_STOP_ANNOUNCEMENT) {
					draw_announcement_message = false;
				}
			}
		};		
	}
	
	/** Determines whether the main ViewThread loop should run or not. 
	 * 
	 * @param n_running If true, the ViewThread loop will run. If not, the thread will fall out of the loop and finish.
	 */
	public void setRunning(boolean n_running) { running = n_running; }
	
	public void setPaused(boolean n_paused) { paused = n_paused; }

	/** Add a ViewObject to the viewThread viewObjects ArrayList; all ViewObjects are updated and rendered by the ViewThread
	 * 
	 * @param nVO New ViewObject to add to this ViewThread's VO list.
	 */
	public synchronized void addViewObject(ViewObject nVO) {
		viewObjects.add(nVO);
	}
	
	public synchronized void addGameItem(GameItem nGI) {
		gameItems.add(nGI);
		viewObjects.add(nGI);
	}
	
	public synchronized void setActor(CoffeeGirl nActor) {
		this.actor = nActor;
	}
	
	/** Clear gameItems, viewObjects, actor - typically one between game levels to  "reset" the game 
	 * 
	 */
	public synchronized void reset () {
		this.viewObjects = new ArrayList<ViewObject>();
		this.gameItems = new ArrayList<GameItem>();
		this.actor = null;
	}
	
	/** Call onUpdate() on all ViewObjects so that they can calculate their next position. Then re-draw the Canvas
	 */
	private void refreshView() {
		//If we are "paused" then no viewObjects should be updated; thier positions should remain static and
		//no Interactions are to occur
		if(!paused) {
			//Call onUpdate() on all ViewObjects
			for(int i = 0; i < viewObjects.size(); i++) viewObjects.get(i).onUpdate();
			/*Iterator<ViewObject> voIt= viewObjects.iterator();
			while(voIt.hasNext()) voIt.next().onUpdate();*/
			
			//Check for interactions between the Actor and any ViewObjects
			for(int i = 0; i < gameItems.size(); i++) {
				//IF the actor (CoffeeGirl) is within gameItems(i)'s sensitivity area
				//AND there is an event in gameItems(i)'s queue, send a message to the GameLogic thread
				if(gameItems.get(i).inSensitivityArea(actor)) {				
					int consumed_event = gameItems.get(i).consumeEvent();
					
					if(consumed_event != GameItem.EVENT_NULL) {
						MessageRouter.sendInteractionEvent(gameItems.get(i).getName());
						//Log.d(activitynametag, "Detected that Actor entered sensitivity area AND an event was queued!");
					}
					else {
						//Log.d(activitynametag, "Detected that Actor entered sensitivity area, but got event " + consumed_event);
					}
				}
			}
		}
		
		//FINALLY Redraw canvas
		Canvas canvas = null;
		try { 
			canvas = this.surfaceHolder.lockCanvas();
			this.gamePanel.onDraw(canvas, viewObjects, 
					GameInfo.setAndReturnMoney(0), GameInfo.setAndReturnPoints(0),
					draw_announcement_message, announcementMessage);

		} catch(Exception e) {;} 
		finally {
			if(canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
		}
		
		//Log.d(activitynametag, "refreshView() done!");
	}
	
	/** Set whether the announcement message should be displayed or not
	 * 
	 * @param newSetting
	 */
	public void setAnnouncementDisplay(boolean newSetting) {
		draw_announcement_message = newSetting;
	}
	public void setAnnouncement(String newAccouncement) {
		announcementMessage = newAccouncement;
	}
		
	
	
	/** The ViewThread run() method implements a loop which will attempt to redraw the Canvas
	 * no more often than every ViewThread.VIEW_REFRESH_PERIOD milliseconds.
	 */
	@Override
	public void run() {
		
		long lastViewUpdate = 0L;
		
		while(running) {
		
			if(System.currentTimeMillis() > lastViewUpdate + ViewThread.VIEW_REFRESH_PERIOD) {
				lastViewUpdate = System.currentTimeMillis();
				refreshView();
			}
			
			try {Thread.sleep(ViewThread.VIEW_REFRESH_PERIOD);}
			catch (Exception e) {;}
		
		}

	}
	
}
