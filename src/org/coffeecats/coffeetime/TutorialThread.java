package org.coffeecats.coffeetime;


import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.gameobjects.CustomerQueue;
import org.coffeecats.coffeetime.gameobjects.objectdefs.CoffeeMachine;
import org.coffeecats.coffeetime.gameobjects.objectdefs.CupCakeTray;
import org.coffeecats.coffeetime.gameobjects.objectdefs.TrashCan;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;


/**
 * @author iyulaev
 *
 * This thread executes a "tutorial script". Basically it simulates user input and displays announcements
 * according to some time delays. By these means it attempts to teach the user the principles of 
 * Coffee Time gameplay.
 */

public class TutorialThread extends Thread {
	
	private static final String activitynametag = "TutorialThread";
	
	//Set how often Tick messages get sent
	public static final int TIMER_GRANULARIY = 1000;
	
	private static boolean running;
	
	
	int tickCount;
	long lastTimerTick;

	/** TimerThread constructor does nothing much except for initializing handler, which will handle messages 
	 * sent to this timer thread. Mostly these messages pause or suspend (or un-pause/un-suspend) the timer thread.
	 * When the thread is suspended, no call-backs occur and the thread basically stops cycling. When paused, callbacks
	 * still occur but no messages are sent out via MessageRouter.
	 */
	public TutorialThread() {
		super();
		tickCount = -2;
		running = true;
	}
	
	/** Causes this thread to discontinue running (and drop out of the run() method) */
	public synchronized void finish() {
		running = false; 
	}

	/** The run() method doesn't do very much anymore; it really just un-suspends itself and then returns. The 
	 * delayed callback method (callRefreshDelayed()) implements the timer loop now. 
	 */
	@Override
	public void run() {
		lastTimerTick = SystemClock.uptimeMillis();
		
		while(running) {

			switch(tickCount){
				case 2: MessageRouter.sendAnnouncementMessage("", false); break;
				case 3: MessageRouter.sendAnnouncementMessage("You are running a cafe", true, true); break;
				case 6: MessageRouter.sendAnnouncementMessage("Customers will approach the counter", true, true); break;
				case 9: MessageRouter.sendAnnouncementMessage("Faster service brings more money & points", true, true); break;
				case 12: MessageRouter.sendAnnouncementMessage("Money can be used to buy upgrades", true, true); break;
				case 15: MessageRouter.sendAnnouncementMessage("Points reflect how much you are loved", true, true); break;
				
				case 18: MessageRouter.sendAnnouncementMessage("The first customer is requesting a coffee", true, true); break;
				case 21: MessageRouter.sendAnnouncementMessage("To brew a coffee, tap on the coffee machine", true, true); break;
				case 22: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(CoffeeMachine.DEFAULT_XPOS), GameGrid.canvasY(CoffeeMachine.DEFAULT_YPOS)); break;
				case 25: MessageRouter.sendAnnouncementMessage("Wait several seconds once it is activated", true, true); break;
				case 28: MessageRouter.sendAnnouncementMessage("Tap again to get a coffee", true, true); break;
				case 29: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(CoffeeMachine.DEFAULT_XPOS), GameGrid.canvasY(CoffeeMachine.DEFAULT_YPOS)); break;
				case 31: MessageRouter.sendAnnouncementMessage("Now, bring the coffee to the customer", true, true); break;
				case 32: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(CustomerQueue.X_POS), GameGrid.canvasY(CustomerQueue.Y_POS_FROM_GG_TOP)); break;
				//40, GameGrid.GAMEGRID_HEIGHT-40
				
				case 35: MessageRouter.sendAnnouncementMessage("There are other food items", true, true); break;
				case 38: MessageRouter.sendAnnouncementMessage("More will appear if your cafe is successful!", true, true); break;
				case 40: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(CoffeeMachine.DEFAULT_XPOS), GameGrid.canvasY(CoffeeMachine.DEFAULT_YPOS)); break;
				
				case 41: MessageRouter.sendAnnouncementMessage("If you make the wrong item, you can't sell it", true, true); break;
				case 42: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(CoffeeMachine.DEFAULT_XPOS), GameGrid.canvasY(CoffeeMachine.DEFAULT_YPOS)); break;
				case 44: MessageRouter.sendAnnouncementMessage("You'll have to throw it out", true, true); break;
				
				case 45: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(CoffeeMachine.DEFAULT_XPOS), GameGrid.canvasY(CoffeeMachine.DEFAULT_YPOS)); break;
				case 46: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(TrashCan.DEFAULT_XPOS), GameGrid.canvasY(TrashCan.DEFAULT_YPOS)); break;
				
				case 47: MessageRouter.sendAnnouncementMessage("Throwing out an item costs a few points", true, true); break;
				case 50: MessageRouter.sendAnnouncementMessage("But don't worry!", true, true); break;
								
				case 51: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(CupCakeTray.DEFAULT_XPOS), GameGrid.canvasY(CupCakeTray.DEFAULT_YPOS)); break;
				
				case 52: MessageRouter.sendAnnouncementMessage("Just try to serve the right items", true, true); break;
				
				case 54: MessageRouter.sendAnnouncementMessage("", false); break;
				
				case 55: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(CustomerQueue.X_POS), GameGrid.canvasY(CustomerQueue.Y_POS_FROM_GG_TOP)); break;
				
				case 60: running = false; break;
			}
			
			tickCount++;
			
			try { Thread.sleep(TIMER_GRANULARIY); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
}
