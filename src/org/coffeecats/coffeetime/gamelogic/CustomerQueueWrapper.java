/** This class contains one or multiple CustomerQueues, and uses a bit of logic to present
 * a single interface to GLT for checking how many customers are left in the queues, etc.
 * 
 * It's a Composite pattern if you care about Design Patterns.
 */

package org.coffeecats.coffeetime.gamelogic;

import java.util.ArrayList;
import java.util.List;

import org.coffeecats.coffeetime.gameobjects.CustomerQueue;


public class CustomerQueueWrapper {
	final int EXPECTED_MAX_CUSTOMERQUEUES = 4;
	
	ArrayList<CustomerQueue> customerQueues;
	
	public CustomerQueueWrapper() {
		customerQueues = new ArrayList<CustomerQueue>(EXPECTED_MAX_CUSTOMERQUEUES);
	}
	
	/** Creates a new CustomerQueueWrapper containing a single (argument-provided) CustomerQueue
	 * 
	 * @param customerQueue The CustomerQueue to add to this wrapper
	 */
	public CustomerQueueWrapper(CustomerQueue customerQueue) {
		this();
		this.addCustomerQueue(customerQueue);
	}
	
	/** Creates a new CustomerQueueWrapper containing two (argument-provided) CustomerQueues
	 * 
	 * @param customerQueue1 The first CustomerQueue to add to this wrapper
	 * @param customerQueue2 The second CustomerQueue to add to this wrapper
	 */
	public CustomerQueueWrapper(CustomerQueue customerQueue1, CustomerQueue customerQueue2) {
		this(customerQueue1);
		this.addCustomerQueue(customerQueue2);
	}
	
	
	/** Adds another CustomerQueue to this CustomerQueueWrapper composite object
	 * 
	 * @param customerQueue The CustomerQueue to add to this wrapper
	 */
	public void addCustomerQueue(CustomerQueue customerQueue) {
		customerQueues.add(customerQueue);
	}
	
	/** Calculate the number of customers remaining in ALL of the CustomerQueues contained by
	 * this composite
	 */
	public int numberOfCustomersLeft() {
		int retval = 0;
		
		for(CustomerQueue cq : customerQueues) {
			retval += cq.numberOfCustomersLeft();
		}
		
		return(retval);
	}
	
	/** Calculate the number of customers served in ALL of the CustomerQueues contained by
	 * this composite
	 */
	public int numberOfCustomersServed() {
		int retval = 0;
		
		for(CustomerQueue cq : customerQueues) {
			retval += cq.numberOfCustomersServed();
		}
		
		return(retval);
	}
	
	/** Returns true iff all of the member queues have finished
	 */
	public boolean isFinished() {
		for(CustomerQueue cq : customerQueues) {
			if(!cq.isFinished()) return false;
		}
		
		return(true);
	}
	
	/** Calculate the number of customers ignored in ALL of the CustomerQueues contained by
	 * this composite
	 */
	public int numberOfCustomersIgnored() {
		int retval = 0;
		
		for(CustomerQueue cq : customerQueues) {
			retval += cq.numberOfCustomersIgnored();
		}
		
		return(retval);
	}
	
	/** Returns a List of the CustomerQueues contained by this CustomerQueueWrapper. Note that this
	 * List is the udnerlying data structure so whoever grabs it should definitely NOT modify the 
	 * List!
	 */
	public List<CustomerQueue> getContainedQueues() {
		return customerQueues;
	}

}
