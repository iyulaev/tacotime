package com.yulaev.tacotime.gamelogic;

/** This class is used to return information about interactions between CoffeeGirl and the
 * GameItem, or between CoffeeGirl and the head of the CustomerQueue. It is 
 * really just used as a struct-like data container class. most of the class members are 
 * accessed directly and set by the interactee GameItem
 * @author ivany
 *
 */
public class Interaction {
	public int point_result;
	public int money_result;
	public boolean was_success;
	public int previous_state;
	
	/** Create a new Interaction. Default is an unsuccessful interaction. */
	public Interaction() {
		this.was_success = false;
	}
	
	/** Used by GameItem to return the previous state */
	public Interaction(int prev_state) {
		this.previous_state = prev_state;
	}
}
