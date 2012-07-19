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

}
