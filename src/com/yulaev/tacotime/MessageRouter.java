/** This class routes messages to other classes; used as sort of a global message
 * creator and passer. It has no state, it is purely for announcing events and passing
 * information between threads.
 * 
 * This is the main interface between different threads of execution in TacoTime.
 */

package com.yulaev.tacotime;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MessageRouter {
	
	public static ViewThread viewThread;
	public static TimerThread timerThread;
	public static InputThread inputThread;
	public static GameLogicThread gameLogicThread;
	public static Handler ttaHandler;
	
	/** Called when a View Update should occur (not used) 
	 * 
	 */
	public synchronized static void sendViewUpdateMessage() {
		if(viewThread != null) {
			Message message = Message.obtain();
			message.what = ViewThread.MESSAGE_REFRESH_VIEW;
			viewThread.handler.sendMessage(message);
		}
	}
	
	/** Called when a user input tap occurs; sends the co-ordinates of the tap to the InputThread so that
	 * it can update game state accordingly.
	 * 
	 * @param x The x co-ordinate of the user tap
	 * @param y The y co-ordinate of the user tap
	 */
	public synchronized static void sendInputTapMessage(int x, int y) {
		if(inputThread != null) {
			Message message = Message.obtain();
			message.what = InputThread.MESSAGE_HANDLE_ONTAP;
			message.arg1 = x;
			message.arg2 = y;
			inputThread.handler.sendMessage(message);
		}
	}
	
	/** Called when a user input tap occurs; sends the co-ordinates of the tap to the InputThread so that
	 * it can update game state accordingly.
	 * 
	 * @param x The x co-ordinate of the user tap
	 * @param y The y co-ordinate of the user tap
	 */
	public synchronized static void sendInteractionEvent(String itemName) {
		if(gameLogicThread != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_INTERACTION_EVENT;
			message.obj = itemName;
			gameLogicThread.handler.sendMessage(message);
		}
	}
	
	/** This message informs the GameLogicThread that one game tick (one real-time second) has passed
	 * 
	 */
	public synchronized static void sendTickMessage() {
		if(gameLogicThread != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_TICK_PASSED;
			gameLogicThread.handler.sendMessage(message);
		}
	}
	
	/** This method is used to pause or unpause the game. When the game is paused, the Canvas that
	 * represents the game is still re-drawn but all motion of GameActors ceases, GameItems are no 
	 * longer updated, and GameLogicThread's state machine does not advance since the TimerThread
	 * stops sending out tick messages.
	 * 
	 * Called by GLT to un-pause the game when the pre-play count-down timer expires. Called by GTL
	 * to pause the game when the level ends.
	 * 
	 * @param paused Whether to pause the game or not.
	 */
	public synchronized static void sendPauseMessage(boolean paused) {
		if(inputThread != null) {
			Message message = Message.obtain();
			if(paused) message.what = InputThread.MESSAGE_SET_PAUSED;
			else message.what = InputThread.MESSAGE_SET_UNPAUSED;
			inputThread.handler.sendMessage(message);
		}
		
		if(viewThread != null) {
			Message message = Message.obtain();
			if(paused) message.what = ViewThread.MESSAGE_SET_PAUSED;
			else message.what = ViewThread.MESSAGE_SET_UNPAUSED;
			viewThread.handler.sendMessage(message);
		}
		
		if(timerThread != null) {
			Message message = Message.obtain();
			if(paused) message.what = TimerThread.MESSAGE_SET_PAUSED;
			else message.what = TimerThread.MESSAGE_SET_UNPAUSED;
			timerThread.handler.sendMessage(message);
		}
	}
	
	/** Send a message to inputThread and viewThread that the UI should be paused; prevents any
	 * ViewObjects from being updated and any input from affecting game state WHEN paused is true
	 * 
	 * TODO: unused
	 * 
	 * @param paused Whether to pause or un-pause view and input threads
	 */
	public synchronized static void sendPauseUIMessage(boolean paused) {
		if(inputThread != null) {
			Message message = Message.obtain();
			if(paused) message.what = InputThread.MESSAGE_SET_PAUSED;
			else message.what = InputThread.MESSAGE_SET_UNPAUSED;
			inputThread.handler.sendMessage(message);
		}
		
		if(viewThread != null) {
			Message message = Message.obtain();
			if(paused) message.what = ViewThread.MESSAGE_SET_PAUSED;
			else message.what = ViewThread.MESSAGE_SET_UNPAUSED;
			viewThread.handler.sendMessage(message);
		}
	}
	
	/** Pause the timer and prevent GameLogicThread from updating game state. Mostly used to un-pause the
	 * game after a Level gets loaded or a (saved) game gets loaded, since the game will have been puased by
	 * sendPauseMessage(); this allows the GLT state machine to advance but prevents the ViewThread from updating 
	 * which is intended since the pre-play count-down timer has not expired and therefore the players is 
	 * not allowed to start playing (yet)
	 * 
	 * @param paused Whether to pause the GLT or not 
	 */
	public synchronized static void sendPauseTimerMessage(boolean paused) {
		if(timerThread != null) {
			Message message = Message.obtain();
			if(paused) message.what = TimerThread.MESSAGE_SET_PAUSED;
			else message.what = TimerThread.MESSAGE_SET_UNPAUSED;
			timerThread.handler.sendMessage(message);
		}
	}
	
	/** TODO add comments */
	public synchronized static void sendSuspendViewThreadMessage(boolean suspended) {
		if(viewThread != null) {
			Message message = Message.obtain();
			if(suspended) message.what = ViewThread.MESSAGE_SET_SUSPENDED;
			else message.what = ViewThread.MESSAGE_SET_UNSUSPEND;
			viewThread.handler.sendMessage(message);
		}
	}
	
	/** TODO add comments */
	public synchronized static void sendSuspendTimerThreadMessage(boolean suspended) {
		if(timerThread != null) {
			Message message = Message.obtain();
			if(suspended) message.what = TimerThread.MESSAGE_SET_SUSPENDED;
			else message.what = TimerThread.MESSAGE_SET_UNSUSPEND;
			timerThread.handler.sendMessage(message);
		}
	}
	
	/** Sends  message to the ViewThread telling it to display an announcement. An announcement is displayed
	 * by overlaying some text on top of the game canvas display.
	 * 
	 * @param announcementText The text to display, overlayed on the screen, by viewThread.
	 * @param doDisplay If true, the announcementText will be displayed. If false, no text will be displayed
	 * and any presently displayed text gets cleared.
	 * 
	 */
	public synchronized static void sendAnnouncementMessage(String announcementText, boolean doDisplay) {
		if(viewThread != null) {
			Message message = Message.obtain();
			if(doDisplay) {
				message.what = ViewThread.MESSAGE_NEW_ANNOUNCEMENT;
				message.obj = announcementText;
			}
			else message.what = ViewThread.MESSAGE_STOP_ANNOUNCEMENT;
			viewThread.handler.sendMessage(message);
		}
	}
	
	/** Called when the back button gets pressed during game play. Tells the input thread that the back button 
	 * has been pressed and that the in-game dialog has been launched. Typically sent by TacoTimeMainGameActivity.
	 */
	public synchronized static void sendBackButtonDuringGameplayMessage() {
		if(inputThread != null) {
			Message message = Message.obtain();
			message.what = InputThread.MESSAGE_INGAME_DIALOG_LAUNCHED;
			inputThread.handler.sendMessage(message);
		}
	}
	
	/** Called when the in-game menu returns and finishes
	 * @param dialog_result the result of the in-game dialog call;
	 */
	public synchronized static void sendInGameDialogResult(int dialog_result) {
		if(inputThread != null) {
			Message message = Message.obtain();
			message.what = InputThread.MESSAGE_INGAME_DIALOG_FINISHED;
			message.arg1 = dialog_result;
			inputThread.handler.sendMessage(message);
		}
	}
	
	/** Called when we are asked to load the last saved game */
	public synchronized static void sendLoadGameMessage() {
		if(gameLogicThread != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_LOAD_GAME;
			gameLogicThread.handler.sendMessage(message);
			
			Log.v("MessageRouter", "Sent Load Game message");
		}
	}
	
	/** Called when we are asked to save the current game to our saved game db 
	 * TODO unused, remove at some point.
	 * */
	public synchronized static void sendSaveGameMessage() {
		if(gameLogicThread != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_SAVE_GAME;
			gameLogicThread.handler.sendMessage(message);
			
			Log.v("MessageRouter", "Sent Save Game message");
		}
	}
	
	/** Called when we are asked (typically by the betweenLevelMenuActivity) to save the game and
	 * continue onto the next level. */
	public synchronized static void sendNextLevelMessage() {
		if(gameLogicThread != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_NEXT_LEVEL;
			gameLogicThread.handler.sendMessage(message);
			
			Log.v("MessageRouter", "Sent next level message");
		}
	}
	
	/** Called when we are asked (typically by the betweenLevelMenuActivity) to save the game and
	 * continue onto the next level. */
	public synchronized static void sendLevelEndMessage() {
		if(ttaHandler != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_LEVEL_END;
			ttaHandler.sendMessage(message);
			
			Log.v("MessageRouter", "Sent level end message to the TacoTimeActivity handler");
		}
	}

	/** Called when the game has been finished (by the GameLogicThread) */
	public synchronized static void sendGameOverMessage() {
		if(ttaHandler != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_GAME_END;
			ttaHandler.sendMessage(message);
			
			Log.v("MessageRouter", "Sent game end message to the TacoTimeActivity handler");
		}
	}
	
	/** Called by GLT when the level is over. Tell the TacoTimeMainGameActivity to display the post-level dialog, 
	 * i.e. the dialog that will tell the user how many points / dollars they earned on the level, including 
	 * the level-dependent bonus
	 * 
	 * @param total_points The total number of points accrued through the end of this level
	 * @param total_money The total number of money accrued through the end of this level
	 * @param accrued_points The total points accrued during this level, NOT counting the bonus
	 * @param accrued_money The total money accrued during this level, NOT counting the bonus
	 * @param bonus_points The total bonus point count given for completing this level
	 * @param bonus_money The total bonus money pile given for completing this level
	 */
	public synchronized static void sendPostLevelDialogOpenMessage(int total_points, int total_money, int accrued_points, 
			int accrued_money, int bonus_points, int bonus_money) {
		if(ttaHandler != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_POSTLEVEL_DIALOG_OPEN;
			
			//Marshall all of the arguments into an ArrayList which we'll pass to the TacoTimeMainGameActivity
			ArrayList<Integer> pointArray = new ArrayList<Integer>();
			pointArray.add(new Integer(total_points));
			pointArray.add(new Integer(total_money));
			pointArray.add(new Integer(accrued_points));
			pointArray.add(new Integer(accrued_money));
			pointArray.add(new Integer(bonus_points));
			pointArray.add(new Integer(bonus_money));		
			message.obj = pointArray;
			
			ttaHandler.sendMessage(message);
			
			Log.v("MessageRouter", "Sent level end dialog display message to the TacoTimeActivity handler");
		}
	}
	
	/** Called by TacoTimeMainGameActivity to indicate to the GLT that the level-end dialog has been closed 
	 * and that the GLT should continue as planned.
	 */
	public synchronized static void sendPostLevelDialogClosedMessage() {
		if(gameLogicThread != null) {
			Message message = Message.obtain();
			message.what = GameLogicThread.MESSAGE_POSTLEVEL_DIALOG_CLOSED;
			gameLogicThread.handler.sendMessage(message);
			
			Log.v("MessageRouter", "Sent level end dialog closed message to the GLT handler");
		}
	}
}
