import java.util.LinkedList;
import java.util.Queue;

public class Scheduler implements Runnable {

	private boolean inMotion; // whether the elevator is moving or not.
	
	private Queue<ElevatorEvent> requestQueue;

	public Scheduler() {
		requestQueue = new LinkedList<>();
	}

	/**
	 * To be called by the floor
	 * 
	 * @param event
	 */
	public synchronized void floorRequest(ElevatorEvent event) {
		if (inMotion) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		requestQueue.add(event);
		
	}

	/**
	 * To be called by the elevator.
	 * 
	 * @param elevatorNum the elevator identifier (for when we have > 1 elevator)
	 */
	public synchronized void elevatorMoveRequest(int elevatorNum) {
	
	}

	@Override
	public void run() {
		while(true) {
			if (!requestQueue.isEmpty()) {
				
			}
		}
		
	}
}
