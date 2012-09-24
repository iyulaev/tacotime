/** DirectionBitmapMap is a data structure that contains multiple CircularLists, indexed by
 * heading directions. The purpose of the class is to serve as a container for sprite bitmaps; 
 * for a given GameActor heading in a particular direction, we have a CircularList which represents the
 * different frames contained in the animation for the GameActor.
 */

package com.yulaev.tacotime.utility;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class DirectionBitmapMap {
	public static final int DIRECTION_NULL = 0;
	public static final int DIRECTION_NORTH = 1;
	public static final int DIRECTION_SOUTH = 2;
	public static final int DIRECTION_EAST = 3;
	public static final int DIRECTION_WEST = 4;
	public static final int DIRECTION_SENSITIVE_SIZE = 5;
	public static final int DIRECTION_INSENSITIVE_SIZE = 1;
	
	ArrayList<CircularList<Bitmap>> directionIndexedLists; //ArrayList of the different CircularLists, one for each direction
	boolean direction_sensitive; //Whether or not we return a different CircularList for different directions
	int default_direction; //the direction we return when the vector is zero
	
	/** Create a new DirectionBitmapMap, with given direction (in)sensitivity.
	 * 
	 * @param n_direction_sensitive Whether this DirectionBitmapMap will be sensitive to direction.
	 */
	public DirectionBitmapMap(boolean n_direction_sensitive) {
		this.direction_sensitive = n_direction_sensitive;
		
		if(n_direction_sensitive) {
			directionIndexedLists = new ArrayList<CircularList<Bitmap>>(DIRECTION_SENSITIVE_SIZE);
		}
		else {
			directionIndexedLists = new ArrayList<CircularList<Bitmap>>(DIRECTION_INSENSITIVE_SIZE);
		}
		
		default_direction = DIRECTION_NULL;
	}
	
	/** Create a new DirectionBitmapMap, with given starting CircularList. Assumes that this DBM
	 * will be insensitive to direction.
	 * 
	 * @param startingList CircularList for initial (and only) direction.
	 */
	public DirectionBitmapMap(CircularList<Bitmap> startingList) {
		this.direction_sensitive = false;
		directionIndexedLists = new ArrayList<CircularList<Bitmap>>(DIRECTION_INSENSITIVE_SIZE);
				
		default_direction = DIRECTION_NULL;		
		
		directionIndexedLists.add(DIRECTION_NULL, startingList);
	}
	
	/** Create a new DirectionBitmapMap, with given starting CircularList. Assumes that this DBM
	 * will be sensitive to direction.
	 * 
	 * @param direction The direction that startingList will be associated with
	 * @param startingList CircularList for initial (and only) direction.
	 */
	public DirectionBitmapMap(int direction, CircularList<Bitmap> startingList) {
		this.direction_sensitive = true;
		directionIndexedLists = new ArrayList<CircularList<Bitmap>>(DIRECTION_SENSITIVE_SIZE);
				
		default_direction = DIRECTION_NULL;
		
		directionIndexedLists.add(direction, startingList);
	}
	
	/** Set the CircularList for a particular direction to a newly-provided CircularList.
	 * 
	 * @param direction The direction that newList is to be associated with.
	 * @param newList The new CircularList for the given direction
	 */
	public void setDirectionList(int direction, CircularList<Bitmap> newList) {
		if(direction_sensitive) {
			directionIndexedLists.add(direction,newList);
		} else {
			directionIndexedLists.add(DIRECTION_NULL,newList);
		}
	}
	
	public CircularList<Bitmap> getDirectionList(int direction) {
		if(direction_sensitive) {
			return directionIndexedLists.get(direction);
		} else {
			return directionIndexedLists.get(DIRECTION_NULL);
		}
	}
	
	/** Computes direction based on provided x and y vector of travel, then return the 
	 * relevant CircularList. We basically look at the provided (x,y) vector and determine 
	 * whether it points mostly north, south, east, or west. We short-circuit this and return
	 * the CircularList for null direction if this DBM is direction-insensitive. */ 
	public CircularList<Bitmap> getDirectionList(int vector_x, int vector_y) {
		//Not direction-sensitive? return the first entry in directionIndexedLists
		if(!direction_sensitive) 
			return directionIndexedLists.get(DIRECTION_NULL);
		
		//No direction of travel? Return the default direction
		if(vector_x == 0 && vector_y == 0) 
			return getDirectionList(default_direction);
		
		//x vector greater than y vector? east-west direction
		if(Math.abs(vector_x) > Math.abs(vector_y)) {
			if(vector_x > 0) return getDirectionList(DIRECTION_EAST);
			else return getDirectionList(DIRECTION_WEST);
		}
		else {
			if(vector_y > 0) return getDirectionList(DIRECTION_NORTH);
			else return getDirectionList(DIRECTION_SOUTH);
		}
	}
}
