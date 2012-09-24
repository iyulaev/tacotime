/** This class contains one or multiple CustomerQueues, and uses a bit of logic to present
 * a single interface to GLT for checking how many customers are left in the queues, etc.
 * 
 * It's a Composite pattern if you care about Design Patterns.
 */

package com.yulaev.tacotime.gamelogic;

import java.util.ArrayList;
import java.util.List;

import com.yulaev.tacotime.gameobjects.CustomerQueue;

public class CustomerQueueWrapper {
	final int EXPECTED_MAX_CUSTOMERQUEUES = 4;
	
	ArrayList<CustomerQueue> customerQueues;
	
	public CustomerQueueWrapper() {
		customerQueues = new ArrayList<CustomerQueue>(EXPECTED_MAX_CUSTOMERQUEUES);
	}
	
	public CustomerQueueWrapper(CustomerQueue customerQueue) {
		this();
		this.addCustomerQueue(customerQueue);
	}
	
	public CustomerQueueWrapper(CustomerQueue customerQueue1, CustomerQueue customerQueue2) {
		this(customerQueue1);
		this.addCustomerQueue(customerQueue2);
	}
	
	
	public void addCustomerQueue(CustomerQueue customerQueue) {
		customerQueues.add(customerQueue);
	}
	
	//numberOfCustomersLeft
	public int numberOfCustomersLeft() {
		int retval = 0;
		
		for(CustomerQueue cq : customerQueues) {
			retval += cq.numberOfCustomersLeft();
		}
		
		return(retval);
	}
	
	//numberOfCustomersServed
	public int numberOfCustomersServed() {
		int retval = 0;
		
		for(CustomerQueue cq : customerQueues) {
			retval += cq.numberOfCustomersServed();
		}
		
		return(retval);
	}
	
	//isFinished
	public boolean isFinished() {
		for(CustomerQueue cq : customerQueues) {
			if(!cq.isFinished()) return false;
		}
		
		return(true);
	}
	
	//numberOfCustomersIgnored
	public int numberOfCustomersIgnored() {
		int retval = 0;
		
		for(CustomerQueue cq : customerQueues) {
			retval += cq.numberOfCustomersIgnored();
		}
		
		return(retval);
	}
	
	public List<CustomerQueue> getContainedQueues() {
		return customerQueues;
	}

}
