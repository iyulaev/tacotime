package com.yulaev.tacotime;


import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


/**
 * @author iyulaev
 *
 * This thread will be used for sending out timed-interval Messages to other threads. Does nothing right now.
 */
public class TimerThread extends Thread {
	
	private static final String activitynametag = "TimerThread";
	
	private static final int TIMER_GRANULARIY = 1000;

	// flag to hold game state 
	private boolean running;
	public void setRunning(boolean running) {
		this.running = running;
	}

	public TimerThread() {
		super();
	}

	/** The run() methods does nothing every TIMER_GRANULARITY milliseconds.
	 */
	@Override
	public void run() {
		long tickCount = 0L;
		long lastTimerTick = 0L;
		
		while (running) {
			tickCount++;
			
			//Fire off timers if sufficient time has elapsed
			//no timers yet!
			if(System.currentTimeMillis() > lastTimerTick + TIMER_GRANULARIY)
			{
				MessageRouter.sendTickMessage();
				lastTimerTick = System.currentTimeMillis();
			}
			
			//So that we are off by no more than about 30% of a tick (roughly)
			try {Thread.sleep(TIMER_GRANULARIY/3);}
			catch (Exception e) {;}
		}
	}
	
}
