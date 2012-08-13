package com.yulaev.tacotime;


import com.yulaev.tacotime.gamelogic.GameInfo;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
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

	// flag to hold game state 
	private boolean running;
	private boolean paused;
	private boolean suspended;

	public TimerThread() {
		super();
		
		paused = false;
		running = false;
		suspended = false;

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MESSAGE_SET_PAUSED) setPaused(true);
				else if (msg.what == MESSAGE_SET_UNPAUSED) setPaused(false);
				
			}
		};
	}
	
	private synchronized void setPaused(boolean n_paused) {
		this.paused = n_paused;
	}
	
	/** Sets whether or not this TimerThread should be suspended. If we set suspended to false we 
	 * launch a notification to wake the TimerThread back up.
	 * @param n_suspended Whether this TimerThread should be suspended.
	 */
	public synchronized void setSuspended(boolean n_suspended) {		
		if(!n_suspended && this.suspended) {
			this.suspended = n_suspended;
			notifyAll();
		}
		else this.suspended = n_suspended;
	}
	
	private synchronized void checkSuspended() {
		while(this.suspended) {
			try { wait(); }
			catch (InterruptedException e) {}
		}
	}

	/** The run() methods launched a tick message every TIMER_GRANULARITY milliseconds. It uses Thread.sleep() between
	 * ticks to suspend execution and it uses checkSuspend() with wait()/notifyAll() to suspend this thread between 
	 * game levels.
	 */
	@Override
	public void run() {
		long tickCount = 0L;
		long lastTimerTick = 0L;
		
		while (running) {
			tickCount++;
			
			//If we are suspended then this will block until we are no longer
			checkSuspended();
			
			//Fire off timers if sufficient time has elapsed
			//no timers yet!
			if(System.currentTimeMillis() > lastTimerTick + TIMER_GRANULARIY)
			{
				if(!paused)	{
					
					MessageRouter.sendTickMessage();
					
				}
				
				lastTimerTick = System.currentTimeMillis();
			}
			
			//So that we are off by no more than about 30% of a tick (roughly)
			try {Thread.sleep(TIMER_GRANULARIY/3);}
			catch (Exception e) {;}
		}
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
}
