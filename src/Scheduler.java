import java.util.LinkedList;
import java.util.Queue;

public class Scheduler implements Runnable {
	
	private Floor floor;
	private Elevator elev;
	private boolean requestPending;
	
	private ControlDate event;

	public Scheduler() {
		requestPending = false;
	}
	
	public synchronized void putRequest(ControlDate ee) {
		while(requestPending) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.event = ee;
		System.out.println("Sending a request from floor " + ee.getFloor());
		try {
			Thread.sleep(1000); //give the request some time 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestPending = true;
		notifyAll();
		
		
	}
	
	public synchronized void getRequest() {
		while(!requestPending) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Moving elevator to floor " + event.getDestinationFloor());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		requestPending = false;
		
		notifyAll();
	}

	@Override
	public void run() {
			
	}
}
