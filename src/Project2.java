// import semaphore, linked lists, and queues since we use those
import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

public class Project2 {

	// max capacity of post office is 10
	public static Semaphore max_capacity = new Semaphore(10, true);
	// keeps track of ready postal workers
	public static Semaphore worker_ready = new Semaphore(3, true);
	// keeps track of whether the scales are ready
	public static Semaphore scales_ready = new Semaphore(1, true);
	// keeps track of whether the customers are ready or not
	public static Semaphore cust_ready = new Semaphore(0, true);
	// keeps track of the 50 customers
	public static Semaphore customers_finished[] = new Semaphore[50];
	private int count; // keeps track of which customer we're on
	
	// queue to keep track of which customers are being served
	public static Queue<Customer> servingCustomer = new LinkedList<>();
	// mutex to protect enqueue and dequeue
	public static Semaphore servingCustomerMutex = new Semaphore(1, true);
	// mutex to protect scales
	public static Semaphore scalesMutex = new Semaphore(1, true);
	
	public static void main(String args[])
	{
		// make 50 customers and 50 threads for those customers
		Customer customers[] = new Customer[50];
		Thread customerThreads[] = new Thread[50];
		
		// initialize the customers array with customers and the customerThreads array with the corresponding customer and initialize the customers_finished semaphore array for each customer
		for(int i = 0; i<50; i++)
		{
			customers_finished[i] = new Semaphore(0, true);
			customers[i] = new Customer(i);
			customerThreads[i] = new Thread(customers[i]);
		}
		
		// make 3 postal workers and threads for those workers
		PostalWorker worker0 = new PostalWorker(0);
		PostalWorker worker1 = new PostalWorker(1);
		PostalWorker worker2 = new PostalWorker(2);
		
		Thread worker0Thread = new Thread(worker0);
		Thread worker1Thread = new Thread(worker1);
		Thread worker2Thread = new Thread(worker2);

		// print the opening message
		System.out.println("Simulating Post Office with 50 customers and 3 postal workers\n");
		
		// starting worker threads
		worker0Thread.start();
		worker1Thread.start();
		worker2Thread.start();

		// starting customer threads
		for(int i = 0; i < 50; i++)
		{
			customerThreads[i].start();
		}
		
		// trying to join customer threads
		for(int i = 0; i < 50; i++)
		{
			try {
				customerThreads[i].join();
			} catch (InterruptedException e) {
			}
			
			// print after a customer thread is joined
			System.out.println("Joined customer " + i);
		}
		
		// exit once complete so postal workers don't continue running
		System.exit(0);
		
	}
}



