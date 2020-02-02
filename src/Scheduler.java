import java.util.LinkedList;
import java.util.Queue;

/**
 * this class facilitates the communication between the Floor class and the
 * Elevator class
 * 
 * @version 01 Feb 2020
 * @author Mariam Almalki, Ruqaya Almalki
 *
 */
public class Scheduler implements Runnable {
	ControlDate c;
	String source;

	/**
	 * the floor object that is receiving/sending information
	 */
	private Floor floor;

	/**
	 * the elevator object that is receiving/sending information
	 */
	private Elevator elev;

	/**
	 * buffer object used to help the scheduler receive information
	 */
	private Buffer buffer;

	/**
	 * Constructor initializing all the class variables
	 * 
	 * @param floor    the floor the elevator wants to send information to
	 * @param elevator the elevator the floor wants to send information to
	 * @param buffer   is used to receive information from a floor/elevator
	 */
	public Scheduler(Floor floor, Elevator elevator, Buffer buffer) {
		this.floor = floor;
		this.elev = elevator;
		this.buffer = buffer;
	}

	/**
	 * method used to send information from the floor to the elevator
	 * 
	 * @param c contains all the information the elevator needs from the floor
	 */
	private void sendRequestToElevator(ControlDate c) {
		System.out.println("Scheduler is sending info from floor to elevator");
		elev.receiveFloorInfo(c);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method used to send information from the elevator to the floor
	 * 
	 * @param c contains all the information the floor needs from the elevator
	 */
	private void sendDataToFloor(ControlDate c) {
		System.out.println("Scheduler is sending info from elevator to floor");
		floor.receiveDataFromElevator(c);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public ControlDate getDate() {
		return this.c;
	}
	public String getSource() {
		return this.source;
	}
	

	/**
	 * overrides the run method from the Runnable interface 
	 * continuously gets data from the buffer to send to the 
	 * corresponding floor/elevator
	 */
	@Override
	public void run() {
		while (true) {

			Object[] data = buffer.getData();

			this.source = (String) data[0];
			this.c = (ControlDate) data[1];
			if (source.equalsIgnoreCase("Floor")) {
				this.sendRequestToElevator(c);
			} else {
				this.sendDataToFloor(c);
			}

		}
	}
}
