/**
 * this class helps to facilitate the communication between the Floor class and
 * the Elevator class, handles sending requests
 * 
 * @version 01 Feb 2020
 * @author Mariam Almalki, Ruqaya Almalki
 *
 */

enum Events {
		RECEIVING_ELEVATOR, RECEIVING_FLOOR, FLOOR_SENDING, ELEVATOR_SENDING, WAITING
}

public class Buffer {
	private static Events event = Events.WAITING; //initially just waiting to receive/send something

	/**
	 * request contains the information the floor wants to send; data contains the
	 * information the elevator wants to send
	 */
	private ControlDate request, data;

	/**
	 * requestIn determines if there is a request to be processed or not
	 */
	private boolean requestIn;

	/**
	 * elevDataIn determines if the elevator has data they are sending
	 */
	private boolean elevDataIn;

	/**
	 * array containing information needed to be forwarded to scheduler
	 */
	private Object[] toScheduler;

	/**
	 * determines if request is sent
	 */
	private boolean sendRequest;

	/**
	 * Constructor initializes all class variables
	 */
	public Buffer() {
		toScheduler = new Object[2];
		requestIn = false;
		elevDataIn = false;
		sendRequest = false;
		event = Events.WAITING;
	}
	
	public Events getEvent() {
		return Buffer.event;
	}
	
	public void setEvent(Events e) {
		event = e; 
	}

	/**
	 * method used to send information from the floor to the scheduler
	 * 
	 * @param request contains information about the desired event
	 */
	public synchronized void putFloorRequest(ControlDate request) {
		while (elevDataIn || sendRequest) { //request is being processed
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		event = Events.RECEIVING_FLOOR;
		this.request = request;

		requestIn = true;
		sendRequest = true;

		notifyAll();
	}

	/**
	 * method used to send information from the elevator to the scheduler
	 * 
	 * @param data contains the information about the event
	 */
	public synchronized void putElevatorData(ControlDate data) {
		while (requestIn || sendRequest) { //request is being proccessed
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.data = data;

		elevDataIn = true;
		sendRequest = true;

		notifyAll();

	}

	/**
	 * method used by the scheduler to receive data the Floor/Elevator sent
	 * 
	 * @return an object containing who sent the data and the corresponding data
	 */
	public synchronized Object[] getData() {
		while (!sendRequest) { // !requestIn && !elevDataIn ||
			try { 
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		sendRequest = false;

		if (requestIn) { //floor is sending the request
			toScheduler[0] = "Floor";
			toScheduler[1] = request;
			requestIn = false;
			event = Events.RECEIVING_FLOOR;
		}

		if (elevDataIn) { //elevator is sending the request
			toScheduler[0] = "Elevator";
			toScheduler[1] = data;
			elevDataIn = false;
			event = Events.RECEIVING_ELEVATOR;
		}

		notifyAll();

		return toScheduler;
	}

}
