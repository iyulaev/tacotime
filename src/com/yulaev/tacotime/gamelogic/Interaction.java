package com.yulaev.tacotime.gamelogic;

/** This class is used to return information about interactions between CoffeeGirl and the
 * CustomerQueue, or rather between CoffeeGirl and the head of the CustomerQueue
 * @author ivany
 *
 */
public class Interaction {
	public int point_result;
	public int money_result;
	public boolean was_success;
	
	public Interaction() {
		this.was_success = false;
	}
}
