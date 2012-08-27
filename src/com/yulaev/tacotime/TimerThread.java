package com.yulaev.tacotime;


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
public class TimerThread extends Thread {
	
	private static final String activitynametag = "TimerThread";
	
	//Set how often Tick messages get sent
	public static final int TIMER_GRANULARIY = 1000;
	
	//Message passing (receiving)
	Handler handler;
	public static final int MESSAGE_SET_PAUSED = 0;
	public static final int MESSAGE_SET_UNPAUSED = 1;
	public static final int MESSAGE_SET_SUSPENDED = 2;
	public static final int MESSAGE_SET_UNSUSPEND = 3;

	// flag to hold game state 
	private boolean paused;
	private boolean suspended;
	
	long tickCount;
	long lastTimerTick;

	/** TimerThread constructor does nothing much except for initializing handler, which will handle messages 
	 * sent to this timer thread. Mostly these messages pause or suspend (or un-pause/un-suspend) the timer thread.
	 * When the thread is suspended, no call-backs occur and the thread basically stops cycling. When paused, callbacks
	 * still occur but no messages are sent out via MessageRouter.
	 */
	public TimerThread() {
		super();
		
		paused = false;
		suspended = false;

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MESSAGE_SET_PAUSED) setPaused(true);
				else if (msg.what == MESSAGE_SET_UNPAUSED) setPaused(false);
				else if (msg.what == MESSAGE_SET_SUSPENDED) setSuspended(true);
				else if (msg.what == MESSAGE_SET_UNSUSPEND) setSuspended(false);
				
			}
		};
	}
	
	/** Set whether this TimerThread is paused or now. When paused it does not send out timer tick
	 * messages.
	 * @param n_paused
	 */
	private void setPaused(boolean n_paused) {
		this.paused = n_paused;
	}
	
	/** Sets whether or not this TimerThread should be suspended. If we set suspended to false we 
	 * launch a notification to wake the TimerThread back up.
	 * @param n_suspended Whether this TimerThread should be suspended.
	 */
	private void setSuspended(boolean n_suspended) {		
		this.suspended = n_suspended;
		if(!this.suspended) callRefreshDelayed();
	}
	
	/** This methods sends out a timer tick and queues itself (via handler) to be called back after a fixed number 
	 * of milliseconds. It effectively implements the busy loop for TimerThread.
	 */
	private void callRefreshDelayed() { 
		handler.postDelayed(
			new Runnable() {
				public void run() {	
					if(!suspended) {
						if(SystemClock.uptimeMillis() > lastTimerTick + TIMER_GRANULARIY) {
							if(!paused) MessageRouter.sendTickMessage();
							lastTimerTick = SystemClock.uptimeMillis();
							tickCount++;
						}
						
						callRefreshDelayed();
					}
				}
			}, TIMER_GRANULARIY/3); //CallBack timer thread more often than necessary, just for fun.
					//Maybe it'll increase the accuracy of each tick? Not sure we care though.
	}

	/** The run() method doesn't do very much anymore; it really just un-suspends itself and then returns. The 
	 * delayed callback method (callRefreshDelayed()) implements the timer loop now. 
	 */
	@Override
	public void run() {
		tickCount = 0L;
		lastTimerTick = 0L;
		
		MessageRouter.sendSuspendTimerThreadMessage(false);
	}
	
}
