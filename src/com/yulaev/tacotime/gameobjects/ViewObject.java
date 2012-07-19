/** ViewObject implements the interface for all interactive/moving objects in the TacoTime game. 
 * This includes the player character, NPCs, and interactive items like the coffee machine.
 * All required methods of such objects are defined in this interface.
 */

package com.yulaev.tacotime.gameobjects;


import android.graphics.Canvas;

public interface ViewObject {
	/** Any ViewObject should implement onUpdate. When onUpdate() gets called the ViewObject
	 * shoud do things like calculate its new position, update it's sprite, etc
	 */
	public void onUpdate();
	
	/** Notify the ViewObject that a user input (screen tap) has occurred. 
	 * @param x The x-coordinate of the tap
	 * @param y The y-coordinate of the tap
	 * */
	public void handleTap(int x, int y); 
	
	/** All ViewObjects must implement draw(); draw() will simply draw the bitmap
	 * representing this ViewObject onto the provided Canvas
	 * @param canvas The Canvas onto which the ViewObject may be drawn.
	 */
	public void draw(Canvas canvas);
	
	/** Used to determine whether this ViewObject is an actor or not 
	 * return true if this character is a game character, otherwise false
	 */
	public boolean isActor();
	
	/** Used to determine whether this ViewObject is a game item (non-actor, interactive) or not 
	 * return true if this ViewObject is an interactive game item, otherwise false
	 */
	public boolean isItem();
	
	/** Used to get the name or ID of this ViewObject
	 * @return Name/ID of this ViewObject
	 */
	public String getName();
	
	/** Used to get the X and Y co-ordinates of this ViewObject
	 */
	public int getPositionX();
	public int getPositionY();
}
