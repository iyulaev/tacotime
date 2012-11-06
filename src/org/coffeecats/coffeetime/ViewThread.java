package org.coffeecats.coffeetime;


import java.util.ArrayList;

import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gameobjects.CoffeeGirl;
import org.coffeecats.coffeetime.gameobjects.GameItem;
import org.coffeecats.coffeetime.gameobjects.ViewObject;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;



/**
 * @author iyulaev
 *
 * This Thread is responsible for all of the in-game 2D rendering work, as well as tracking the
 * GameGrid-based position of all of the game elements and observing when they interact. Every
 * VIEW_REFRESH_PERIOD the ViewThread will call the onUpdate() method for every GameItem, and
 * then will draw the GameItem to the Canvas.
 */

public class ViewThread extends Thread {
	
	private static final String activitynametag = "ViewThread";
	
	//Define types of messages accepted by ViewThread
	public static final int MESSAGE_REFRESH_VIEW = 0;
	public static final int MESSAGE_SET_PAUSED = 1;
	public static final int MESSAGE_SET_UNPAUSED = 2;
	public static final int MESSAGE_NEW_ANNOUNCEMENT = 3;
	public static final int MESSAGE_STOP_ANNOUNCEMENT = 4;
	public static final int MESSAGE_SET_SUSPENDED = 5;
	public static final int MESSAGE_SET_UNSUSPEND = 6;
	
	//Define view refresh period in ms (define the bounds also for adjustable frame rate!)
	public static final int MIN_VIEW_REFRESH_PERIOD = 20;
	public static final int VIEW_REFRESH_PERIOD = 50;
	public static final int MAX_VIEW_REFRESH_PERIOD = 250;
	public static final int VIEW_REFRESH_PERIOD_MAX_INCR = 50;
	public static final int VIEW_SUSPEND_PERIOD = 1000/3;
	//Current calculated refresh time. We'll trim this value so that we don't end up over-loading
	//the phone's graphics subsystem (it causes the phone to flip the fuck out for some reason)
	private static int current_view_refresh_period;

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
	
	//If paused is true then, during refreshView(), we do not call onUpdate() BUT we still re-draw the entire canvas
	//Thus no interactions may occur and nothing moves but rendering still occurs
	private boolean paused;
	//If suspended is true then the event loop is stopped and we wait to be un-suspended. THis should be a relatively
	//low-power and low computational intensity state
	private boolean suspended;
	
	//Variables to hold the current announcement message (a message that will be printed in LARGE CHARACTERS
	//that overlay everything else on the canvas
	private boolean draw_announcement_message;
	private String announcementMessage;
	private boolean usingItalicAnnouncement;

	/** Constructor for ViewThread. Most of the work is in setting up the Message Handler, which is responsible 
	 * for receiving messages from the GameLogicThread.
	 * @param surfaceHolder The SurfaceHolder for the canvas that we will be drawing to
	 * @param gamePanel The MainGamePanel that we will be doing drawing duty for.
	 */
	public ViewThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
		
		paused = true;
		suspended = false;
		
		draw_announcement_message = false;
		usingItalicAnnouncement = false;
		
		handler = new Handler() {
			@SuppressLint({ "HandlerLeak" })
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MESSAGE_REFRESH_VIEW) {
					Log.d(activitynametag, "Got view refresh message!");
					//if(!isRefreshing) refreshView();
				}
				
				else if (msg.what == MESSAGE_SET_PAUSED) setPaused(true);
				else if (msg.what == MESSAGE_SET_UNPAUSED) setPaused(false);
				
				//handle announcement messages entered in by the GameLogicThread
				else if (msg.what == MESSAGE_NEW_ANNOUNCEMENT) {
					announcementMessage = (String) msg.obj;
					setItalicAnnoucement(msg.arg1!=0);
					draw_announcement_message = true;
				}
				else if (msg.what == MESSAGE_STOP_ANNOUNCEMENT) {
					draw_announcement_message = false;
				}
				
				else if(msg.what == MESSAGE_SET_SUSPENDED) {
					setSuspended(true);
				}
				else if(msg.what == MESSAGE_SET_UNSUSPEND) {
					setSuspended(false);
				} 
			}
		};
		
		this.reset();
	}

	/** Add a ViewObject to the viewThread viewObjects ArrayList; all ViewObjects are updated and rendered by the ViewThread
	 * @param nVO New ViewObject to add to this ViewThread's VO list.
	 */
	public synchronized void addViewObject(ViewObject nVO) {
		viewObjects.add(nVO);
	}
	
	/** Add a Gameitem to this viewThread's gameItems Arraylist; all of the GameItems are tracked for potential interactions. As
	 * as side effect all gameItems are addviewObject()'d also.
	 * @param nGI The new GameItem to add to this threads gameItems array.
	 */
	public synchronized void addGameItem(GameItem nGI) {
		gameItems.add(nGI);
		viewObjects.add(nGI);
	}
	
	/** Sets the main player Actor for this ViewThread 
	 * @param nActor the Actor that we will specify for this ViewThread.
	 * */
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
		current_view_refresh_period = VIEW_REFRESH_PERIOD;
	}
	
	/** Call onUpdate() on all ViewObjects so that they can calculate their next position. Then re-draw the Canvas.
	 * Also check for Interactions between the Actor and GameItems and send InteractionEvents through MessageRouter
	 * to the GameLogicThread if an interaction occurs.
	 */
	private void refreshView() {
		long start_refresh_time = SystemClock.uptimeMillis();
		
		//If we are "paused" then no viewObjects should be updated; thier positions should remain static and
		//no Interactions are to occur
		if(!paused) {
			//Call onUpdate() on all ViewObjects
			for(int i = 0; i < viewObjects.size(); i++) viewObjects.get(i).onUpdate();
			
			//Check for interactions between the Actor and any ViewObjects
			for(int i = 0; i < gameItems.size(); i++) {
				//IF the actor (CoffeeGirl) is within gameItems(i)'s sensitivity area
				//AND there is an event in gameItems(i)'s queue, send a message to the GameLogic thread
				if(gameItems.get(i).inSensitivityArea(actor)) {				
					int consumed_event = gameItems.get(i).consumeEvent();
					//Notify the GLT (via MessageRouter) if an Interaction generates some non-null event
					if(consumed_event != GameItem.EVENT_NULL) 
						MessageRouter.sendInteractionEvent(gameItems.get(i).getName());
				}
			}
		}
		
		//FINALLY Redraw canvas
		Canvas canvas = null;
		try { 
			canvas = this.surfaceHolder.lockCanvas();
			this.gamePanel.onDraw(canvas, viewObjects, 
					GameInfo.setAndReturnMoney(0), GameInfo.setAndReturnPoints(0),
					draw_announcement_message, announcementMessage,
					GameInfo.getLevelTime(),
					GameInfo.getCustomersLeftForLevel(),
					GameInfo.getCustomersLeftForBonus(),
					GameInfo.getCustomersLeftForCleared(),
					drawItalicAnnoucement());

		} catch(Exception e) {;} 
		finally {
			if(canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
		}
		
		//Update the refresh period based on how long this frame took to render
		updateViewRefreshPeriod(SystemClock.uptimeMillis() - start_refresh_time);
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
	
	/** Set whether Annoucement messages should be drawn using a smaller, italicised font or not. This is typically 
	 * only used by the TutorialThread when displaying more text in annoucements than usual.
	 * @param usingItalicAnnouncement
	 * @return
	 */
	private synchronized void setItalicAnnoucement(boolean usingItalicAnnouncement) { 
		this.usingItalicAnnouncement = usingItalicAnnouncement; 
	}
	private synchronized boolean drawItalicAnnoucement() { return usingItalicAnnouncement; }
	
	/** Allows the ViewThread to be paused. When the ViewThread is paused stuff still gets drawn to the canvas but
	 * no GameItems get updated and no interactions are allowed to take place.
	 * @param n_paused
	 */
	public void setPaused(boolean n_paused) { paused = n_paused; }
	
	/** Sets whether or not this ViewThread should be suspended. If we set suspended to false we 
	 * launch a notification to wake the ViewThread back up.
	 * @param n_suspended Whether this ViewThread should be suspended.
	 */
	private void setSuspended(boolean n_suspended) {		
		this.suspended = n_suspended;
		if(!suspended) callRefreshDelayed();
	}
	
	/** Used to dynamically change the view refresh period. Basically, we adjust by (at most)
	 * VIEW_REFRESH_PERIOD_MAX_INCR. If the last refresh took longer than the default timer then we increase
	 * the period to the next refresh. We never allow the period to be less than MIN_VIEW_REFRESH_PERIOD nor
	 * greater than MAX_VIEW_REFRESH_PERIOD.
	 * 
	 * @param last_update_time
	 */
	private void updateViewRefreshPeriod(long last_update_time) {
		long period_change = (last_update_time - current_view_refresh_period)/2;
		
		if(period_change > VIEW_REFRESH_PERIOD_MAX_INCR) period_change = VIEW_REFRESH_PERIOD_MAX_INCR;
		if(period_change < -1 * VIEW_REFRESH_PERIOD_MAX_INCR) period_change = -1 * VIEW_REFRESH_PERIOD_MAX_INCR;
		
		int period_change_int = (int) period_change;
		
		if(current_view_refresh_period + period_change_int > MAX_VIEW_REFRESH_PERIOD) 
			current_view_refresh_period = MAX_VIEW_REFRESH_PERIOD;
		else if(current_view_refresh_period + period_change_int < MIN_VIEW_REFRESH_PERIOD) 
			current_view_refresh_period = MIN_VIEW_REFRESH_PERIOD;
		else current_view_refresh_period = current_view_refresh_period + period_change_int;
	}
	
	/** Get the current view refresh period. */
	private int getViewRefreshPeriod() {
		return current_view_refresh_period;
	}
	
	/**This is the main ViewThread loop. Basically we call refreshView() (if we are not suspended) and then, once again,
	 * if we are not suspended, we post a callback to handler to call callRefreshDelayed() again in ViewThread.
	 * VIEW_REFRESH_PERIOD ms. Every time that refreshView() is called the canvas gets redrawn and position-related
	 * things (like interactions) get evaluated.
	 */
	private void callRefreshDelayed() { 
		handler.postDelayed(
			new Runnable() {
				public void run() {
					
					if(!suspended) {
						refreshView();
						callRefreshDelayed();
					}
				}
			}, getViewRefreshPeriod());
	}

	
	/** The ViewThread run() method doesn't really do anything anymore except for un-suspend this ViewThread.
	 */
	@Override
	public void run() {		
		MessageRouter.sendSuspendViewThreadMessage(false);
	}
	
}
