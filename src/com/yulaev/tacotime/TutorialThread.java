package com.yulaev.tacotime;


import com.yulaev.tacotime.gamelogic.GameGrid;
import com.yulaev.tacotime.gamelogic.GameInfo;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;


/**
 * @author iyulaev
 *
 * This thread will be used for sending out timed-interval Messages to other threads. Mostly all of what
 * it does is send out a tick message via MessageRouter. This is the "clock" signal that will advance the
 * GameLogicThread state machine.
 */
public class TutorialThread extends Thread {
	
	private static final String activitynametag = "TutorialThread";
	
	//Set how often Tick messages get sent
	public static final int TIMER_GRANULARIY = 1000;
	
	
	int tickCount;
	long lastTimerTick;

	/** TimerThread constructor does nothing much except for initializing handler, which will handle messages 
	 * sent to this timer thread. Mostly these messages pause or suspend (or un-pause/un-suspend) the timer thread.
	 * When the thread is suspended, no call-backs occur and the thread basically stops cycling. When paused, callbacks
	 * still occur but no messages are sent out via MessageRouter.
	 */
	public TutorialThread() {
		super();
	}

	/** The run() method doesn't do very much anymore; it really just un-suspends itself and then returns. The 
	 * delayed callback method (callRefreshDelayed()) implements the timer loop now. 
	 */
	@Override
	public void run() {
		tickCount = -2;
		lastTimerTick = SystemClock.uptimeMillis();
		
		boolean running = true;
		
		while(running) {
			
			switch(tickCount){
				case 2: MessageRouter.sendAnnouncementMessage("", false); break;
				case 3: MessageRouter.sendAnnouncementMessage("You are running a cafe", true); break;
				case 6: MessageRouter.sendAnnouncementMessage("Customers will approach the counter", true); break;
				case 9: MessageRouter.sendAnnouncementMessage("Faster service brings more money & points", true); break;
				case 12: MessageRouter.sendAnnouncementMessage("Money can be used to buy upgrades", true); break;
				case 15: MessageRouter.sendAnnouncementMessage("Points reflect how you are loved", true); break;
				
				case 18: MessageRouter.sendAnnouncementMessage("The first customer is requesting a coffee", true); break;
				case 21: MessageRouter.sendAnnouncementMessage("To brew a coffee, tap on the coffee machine", true); break;
				case 22: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(16), GameGrid.canvasY(40)); break;
				case 25: MessageRouter.sendAnnouncementMessage("Wait several seconds once it is activated", true); break;
				case 28: MessageRouter.sendAnnouncementMessage("Tap again to get a coffee", true); break;
				case 29: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(16), GameGrid.canvasY(40)); break;
				case 31: MessageRouter.sendAnnouncementMessage("Now, bring the coffee to the customer", true); break;
				case 32: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(40), GameGrid.canvasY(GameGrid.GAMEGRID_HEIGHT-40)); break;
				//40, GameGrid.GAMEGRID_HEIGHT-40
				
				case 35: MessageRouter.sendAnnouncementMessage("There are other food items", true); break;
				case 38: MessageRouter.sendAnnouncementMessage("More will appear if your cafe is successful!", true); break;
				case 40: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(16), GameGrid.canvasY(40)); break;
				
				case 41: MessageRouter.sendAnnouncementMessage("If you make the wrong item, you can't sell it", true); break;
				case 42: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(16), GameGrid.canvasY(40)); break;
				case 44: MessageRouter.sendAnnouncementMessage("You'll have to throw it out", true); break;
				
				case 45: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(16), GameGrid.canvasY(40)); break;
				case 46: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(110), GameGrid.canvasY(100)); break;
				
				case 47: MessageRouter.sendAnnouncementMessage("Throwing out an item costs a few points", true); break;
				case 50: MessageRouter.sendAnnouncementMessage("But don't worry! Just try to ", true); break;
								
				case 51: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(113), GameGrid.canvasY(80)); break;
				
				case 52: MessageRouter.sendAnnouncementMessage("But don't worry! Just try to serve the right items", true); break;
				
				case 54: MessageRouter.sendAnnouncementMessage("", false); break;
				
				case 55: MessageRouter.sendSimulatedTapMessage(GameGrid.canvasX(40), GameGrid.canvasY(GameGrid.GAMEGRID_HEIGHT-40)); break;
				
				case 60: running = false; break;
			}
			
			tickCount++;
			
			try { Thread.sleep(TIMER_GRANULARIY); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
}
