package com.yulaev.tacotime;


import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yulaev.tacotime.gameobjects.ViewObject;


/**
 * @author iyulaev
 * 
 * This thread handles user input. It receives messages, mostly from MessageRouter, and handles 
 * them appropriately. For example, on user taps, we call handleTap() on all ViewObjects that have
 * been put into this InputThread's viewObjects array.
 */
public class InputThread extends Thread {
	
	private static final String activitynametag = "InputThread";
	
	//Define types of messages accepted by ViewThread
	public static final int MESSAGE_HANDLE_ONTAP = 0;

	//This message handler will receive messages, probably from the UI Thread, and
	//update the data objects and do other things that are related to handling
	//game-specific input
	public static Handler handler;
	
	//Here we hold all of the objects that the ViewThread must update
	ArrayList<ViewObject> viewObjects;

	public InputThread() {
		super();
		this.viewObjects = new ArrayList<ViewObject>();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MESSAGE_HANDLE_ONTAP) {
					Log.d(activitynametag, "Got input message!");
					handleTap(msg.arg1, msg.arg2);
				}
			}
		};		
	}

	/** Add a new ViewObject to this InputThread's ViewObject data structure. All VOs added
	 * to this InputThread will have user input passed to them via handleTap() and other
	 * such methods.
	 * @param nVO The new ViewObject to add to this InputThread's ViewObject sensitivity list.
	 */
	
	public void addViewObject(ViewObject nVO) {
		viewObjects.add(nVO);
	}
	
	/** This method is called when a HANDLE_ONTAP message is received by this InputThread.
	 * 
	 * @param x The x co-ordinate of the user tap
	 * @param y The y co-ordinate of the user tap
	 */
	public void handleTap(int x, int y) {
		Iterator<ViewObject> it = viewObjects.iterator();
		while(it.hasNext()) it.next().handleTap(x,y);
	}
	
	@Override
	public void run() {
		
		;

	}
	
}
