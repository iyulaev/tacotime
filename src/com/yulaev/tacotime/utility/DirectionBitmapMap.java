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
	public DirectionBitmapMap(boolean n_direction_sensitive, int default_direction) {
		this(n_direction_sensitive);
		this.default_direction = default_direction;
		
	}
	public DirectionBitmapMap(boolean n_direction_sensitive) {
		this.direction_sensitive = n_direction_sensitive;
		
		if(n_direction_sensitive) {
			directionIndexedLists = new ArrayList<CircularList<Bitmap>>(DIRECTION_SENSITIVE_SIZE);
			for(int i = 0; i < DIRECTION_SENSITIVE_SIZE; i++) directionIndexedLists.add(null);
		}
		else {
			directionIndexedLists = new ArrayList<CircularList<Bitmap>>(DIRECTION_INSENSITIVE_SIZE);
			for(int i = 0; i < DIRECTION_INSENSITIVE_SIZE; i++) directionIndexedLists.add(null);
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
		for(int i = 0; i < DIRECTION_INSENSITIVE_SIZE; i++) directionIndexedLists.add(null);
				
		default_direction = DIRECTION_NULL;		
		
		directionIndexedLists.set(DIRECTION_NULL, startingList);
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
		for(int i = 0; i < DIRECTION_SENSITIVE_SIZE; i++) directionIndexedLists.add(null);
				
		default_direction = DIRECTION_NULL;
		
		directionIndexedLists.set(direction, startingList);
	}
	
	/** Set the CircularList for a particular direction to a newly-provided CircularList.
	 * 
	 * @param direction The direction that newList is to be associated with.
	 * @param newList The new CircularList for the given direction
	 */
	public void setDirectionList(int direction, CircularList<Bitmap> newList) {
		if(direction_sensitive) {
			directionIndexedLists.set(direction,newList);
		} else {
			directionIndexedLists.set(DIRECTION_NULL,newList);
		}
	}
	
	/** Get the CircularList for a particular direction heading. Returns the list for DIRECTION_NULL if
	 * this DirectionBitmapMap is not direction-sensitive.
	 * @param direction Direction to get the CircularList for.
	 * @return The appropriate circular list; returns the list for DIRECTION_NULL if this isn't direction_sensitive.
	 */
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
		
		return getDirectionList(Utility.calculateHeadingDirection(vector_x, vector_y));
	}
}
