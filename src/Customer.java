
// customer class that implements Runnable
public class Customer implements Runnable {

	private int task; // keeps track of the customer's task where 1 is buying stamps, 2 is mailing a letter, and 3 is mailing a package
	private int customerNumber; // keeps track of what the customer's number is
	
	// constructor that is passed the customer's number
	public Customer(int custNum) 
	{
		// stores the randomly assigned task where 1 is buying stamps, 2 is mailing a letter, and 3 is mailing a package
		task = (int)(Math.random()*3 )+ 1; // randomly generates a number from 1 to 3
		customerNumber = custNum; // keeps track of which customer this is
	}
	
	// function to get the customer's number
	public int getCustNumber()
	{
		return customerNumber;
	}
	
	// function to get the customer's task
	public int getCustTask()
	{
		return task;
	}
	
	// function for customer thread to print the task that it's asking to do
	public void printTask(int workerNumber)
	{
		// string representation of the task the customer is asking to do
		String taskString = "";
		if(task == 1)
		{
			taskString = "buy stamps";
		}
		else if(task == 2)
		{
			taskString = "mail a letter";
		}
		else
		{
			taskString = "mail a package";
		}
		
		// print what task the customer is asking to do
		System.out.println("Customer " + customerNumber + " asks postal worker " + workerNumber + " to " + taskString);
	}
	
	// run method for Runnable interface
	public void run()
	{
		// print which customer was created
		System.out.println("Customer " + customerNumber + " created");
		
		// try to enter the post office and print when succesful
		try {
			Project2.max_capacity.acquire();
		} catch (InterruptedException e) {
		}
		System.out.println("Customer " + customerNumber + " enters post office");
		
		// try to be attended by a postal worker
		try {
			Project2.worker_ready.acquire();
		} catch (InterruptedException e) {
		}
		
		// add customer to the serving customer queue by locking mutex
		try {
			Project2.servingCustomerMutex.acquire();
		} catch (InterruptedException e) {
		}
		
		// adding this customer to the serving customer queue
		Project2.servingCustomer.add(this);
		// signal that this customer is ready to be served
		Project2.cust_ready.release();

		// unlock serving customer mutex
		Project2.servingCustomerMutex.release();
				
		// keep track of whether the customer is finished to move on
		try {
			Project2.customers_finished[customerNumber].acquire();
		} catch (InterruptedException e)
		{	
		}
		
		// print that customer leaves post office
		System.out.println("Customer " + customerNumber + " leaves post office");
		// signify that another customer can enter the post office now
		Project2.max_capacity.release();
	}
}
