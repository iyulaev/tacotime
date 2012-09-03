/** Implements a circular list of some Object type, where we can iterate through the
 * list effectively forever, continually getting the "next" element.The underlying data 
 * type is an ArrayList. A size hint can be given to help size the underlying ArrayList.
 */

package com.yulaev.tacotime.utility;

import java.util.ArrayList;

public class CircularList <T> {
	ArrayList<T> objectList;
	int curr_index;
	
	public CircularList(int size_hint, T starting) {
		objectList = new ArrayList<T>(size_hint);
		curr_index = 0;
		
		add(starting);
	}
	
	public CircularList(int size_hint) {
		objectList = new ArrayList<T>(size_hint);
		curr_index = 0;
	}
	
	public CircularList() {
		objectList = new ArrayList<T>();
		curr_index = 0;
	}
	
	public void add(T newObject) {
		objectList.add(newObject);
	}
	
	/** Get the next object in the CircularList. As a consequence we increment curr_index.
	 * 
	 * @return The next object in the CircularList
	 */
	public T getNext() {
		if(objectList.size() <= 0) return(null);
		
		curr_index++;
		if(curr_index >= objectList.size()) curr_index=0;
		
		T returned = objectList.get(curr_index);		
		return(returned);
	}
	
	
	/** Get the currently indexed object from this CircularList, without advancing the list.
	 * 
	 * @return The current object in the CircularList
	 */
	public T getCurrent() {
		if(objectList.size() <= 0) return(null);
		return(objectList.get(curr_index));
	}
	
}
