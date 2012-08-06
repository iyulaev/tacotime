/** This class routes messages to other classes; used as sort of a global message
 * creator and passer.
 */

package com.yulaev.tacotime;

import android.os.Message;
import android.util.Log;

public class MessageRouter {
	
	public static ViewThread viewThread;
	public static TimerThread timerThread;
	public static InputThread inputThread;
	public static GameLogicThread gameLogicThread;
	
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
			
			Log.v("MessageRouter", "Sent Interaction Event message");
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
			
			//Log.v("MessageRouter", "Sent Tick Event message");
		}
	}
	
	/** This method is used to pause or unpause the game. When the game is paused, the Canvas that
	 * represents the game is still re-drawn but all motion of GameActors ceases, GameItems are no 
	 * longer updated, and GameLogicThread's state machine does not advance since the TimerThread
	 * stops sending out tick messages.
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
	
	/** Sends  message to the ViewThread telling it to display an announcement. An announcement is displayed
	 * by overlaying some text on top of the game canvas display.
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

}
