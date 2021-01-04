
// postal worker class that implements Runnable
public class PostalWorker implements Runnable {

	private int workerNumber; // keeps track of which postal worker this is

	// constructor with worker number passed in
	public PostalWorker(int workerNum)
	{
		// store worker number
		workerNumber = workerNum;
	}

	// run method for Runnable interface
	public void run()
	{
		// print which postal worker was created
		System.out.println("Postal worker " + workerNumber + " created");

		// repeat while there are still customers left to serve
		while(true)
		{
			// try to see if there is a customer that is ready
			try {

				Project2.cust_ready.acquire();
			} catch (InterruptedException e) {
			}

			// try to dequeue customer after locking queue's mutex
			try {
				Project2.servingCustomerMutex.acquire();
			} catch (InterruptedException e) {
			}

			// storing dequeued customer and printing who the postal worker is serving
			Customer currentlyServing = Project2.servingCustomer.remove();
			System.out.println("Postal worker " + workerNumber + " serving customer " + currentlyServing.getCustNumber());

			// unlocking queue's mutex 
			Project2.servingCustomerMutex.release();

			// signal customer to print the task it's asking for
			currentlyServing.printTask(workerNumber);

			// int to keep track of service time for the task for this customer
			int sleepTime = 0;
			// int to get the currently serving customer's task
			int task = currentlyServing.getCustTask();

			if(task == 1) // 60 seconds means sleep 1 second
				sleepTime = 1000;
			else if(task == 2) // 90 seconds means sleep 1 and a half seconds
				sleepTime = 1500;
			else // 120 seconds means sleep 2 seconds
				sleepTime = 2000;

			// for buying stamps and mailing a letter no scales are needed, just sleep
			if(task!=3)
			{
				// sleep for the assigned task's amount of time
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {	
				}
			}

			// trying to access scales if mailing a package
			else
			{
				// locking mutex to protect scales
				try {
					Project2.scalesMutex.acquire();
				} catch (InterruptedException e) {
				}

				// trying to use scales and printing a message saying we're using scales
				try {
					Project2.scales_ready.acquire();
				} catch (InterruptedException e) {
				}

				// print that scales are in use
				System.out.println("Scales in use by postal worker " + workerNumber);

				// sleep for 2 seconds
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {	
				}

				// releasing scales and printing message saying we released scales
				Project2.scales_ready.release();
				System.out.println("Scales released by postal worker " + workerNumber);

				// unlocking scales mutex
				Project2.scalesMutex.release();
			}

			// printing which customer the postal worker finished serving
			System.out.println("Postal worker " + workerNumber + " finished serving customer " + currentlyServing.getCustNumber());
			Project2.worker_ready.release(); // signify that the current worker is ready for another customer
			Project2.customers_finished[currentlyServing.getCustNumber()].release(); // indicate that the current customer is finished
		}
	}
}
