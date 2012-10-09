package org.coffeecats.coffeetime.gameobjects.objectdefs;

import org.coffeecats.coffeetime.gamelogic.GameGrid;
import org.coffeecats.coffeetime.gameobjects.GameItem;
import org.coffeecats.coffeetime.utility.CircularList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.SystemClock;

import org.coffeecats.coffeetime.R;

/** This is a TrashCan. It is a GameItem. It is used for disposing of things for a (small) point penalty. */

public class SoundSystem extends GameItem {
	private CircularList<Bitmap> leftBitMapList;
	private CircularList<Bitmap> rightBitMapList;
	private long timeSinceUpdate;
	
	private static final int FRAME_RATE_MS = 500;
	private static final int OFFSET_FROM_EDGES = 7;
	
	
	/** Constructor for TrashCan; TrashCan is stateless
	 */
	public SoundSystem(Context caller) {
		super(caller, "SoundSystem", R.drawable.speaker_left_f0, 1, 1, GameItem.ORIENTATION_NORTH, 1, 1);
		
		leftBitMapList = new CircularList<Bitmap>(2);
		rightBitMapList = new CircularList<Bitmap>(2);
		
		leftBitMapList.add(BitmapFactory.decodeResource(caller.getResources(), R.drawable.speaker_left_f0));
		leftBitMapList.add(BitmapFactory.decodeResource(caller.getResources(), R.drawable.speaker_left_f1));
		
		rightBitMapList.add(BitmapFactory.decodeResource(caller.getResources(), R.drawable.speaker_right_f0));
		rightBitMapList.add(BitmapFactory.decodeResource(caller.getResources(), R.drawable.speaker_right_f1));
		
		timeSinceUpdate = -1;
	}
	
	/** Draws this GameItem to the Canvas canvas. We override the superclass method because SoundSystem will 
	 * actually draw two bitmaps, one on the left and one on the right bottom corners of the screen
	 * @param canvas The Canvas to draw this GameItem onto
	 */
	@Override
	public void draw(Canvas canvas) {		
		int draw_x_l = GameGrid.canvasX(OFFSET_FROM_EDGES);
		int draw_y_l = GameGrid.canvasY(GameGrid.GAMEGRID_HEIGHT - OFFSET_FROM_EDGES);
		int draw_x_r = GameGrid.canvasX(GameGrid.GAMEGRID_WIDTH - OFFSET_FROM_EDGES);
		int draw_y_r = GameGrid.canvasY(GameGrid.GAMEGRID_HEIGHT - OFFSET_FROM_EDGES);
		
		if(SystemClock.uptimeMillis() > FRAME_RATE_MS + timeSinceUpdate) {
			canvas.drawBitmap(leftBitMapList.getNext(), draw_x_l - (bitmap.getWidth() / 2), draw_y_l - (bitmap.getHeight() / 2), null);
			canvas.drawBitmap(rightBitMapList.getNext(), draw_x_r - (bitmap.getWidth() / 2), draw_y_r - (bitmap.getHeight() / 2), null);
			timeSinceUpdate = SystemClock.uptimeMillis();
		} else {
			canvas.drawBitmap(leftBitMapList.getCurrent(), draw_x_l - (bitmap.getWidth() / 2), draw_y_l - (bitmap.getHeight() / 2), null);
			canvas.drawBitmap(rightBitMapList.getCurrent(), draw_x_r - (bitmap.getWidth() / 2), draw_y_r - (bitmap.getHeight() / 2), null);
		}
	}
}
