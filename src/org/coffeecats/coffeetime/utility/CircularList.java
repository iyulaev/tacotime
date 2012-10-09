/** Implements a circular list of some Object type, where we can iterate through the
 * list effectively forever, continually getting the "next" element.The underlying data 
 * type is an ArrayList. A size hint can be given to help size the underlying ArrayList.
 */

package org.coffeecats.coffeetime.utility;

import java.util.ArrayList;
import java.util.List;

public class CircularList <T> {
	ArrayList<T> objectList;
	int curr_index;
	
	/** Create a new list, given a size "hint" and a single starting element to
	 * insert into the list.
	 * @param size_hint
	 * @param starting
	 */
	public CircularList(int size_hint, T starting) {
		objectList = new ArrayList<T>(size_hint);
		curr_index = 0;
		
		add(starting);
	}
	
	/** Create a new circular list, given a List of starting elements. */
	public CircularList(List<T> starting) {
		objectList = new ArrayList<T>(starting.size());
		curr_index = 0;
		
		for(int i = 0; i < starting.size(); i++)
			add(starting.get(i));
	}
	
	public CircularList(int size_hint) {
		objectList = new ArrayList<T>(size_hint);
		curr_index = 0;
	}
	
	public CircularList() {
		objectList = new ArrayList<T>();
		curr_index = 0;
	}
	
	/** Add a new object to the end of the CircularList */
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
	
	/** Gets the first item in this CircularList, and "rewind" the list
	 * 
	 * @return The first object that was inserted into this CircularList
	 */
	public T getFirst() {
		if(objectList.size() <= 0) return(null);
		
		curr_index = 0;
		return(objectList.get(curr_index));
	}
	
}
