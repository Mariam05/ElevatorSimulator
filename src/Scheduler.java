/**
 * this class facilitates the communication between the Floor class and the
 * Elevator class
 * 
 * @version 01 Feb 2020
 * @author Mariam Almalki, Ruqaya Almalki
 *
 */

enum States {
	SENDING, RECEIVING, IDLE
}

//enum Events {
//	RECEIVING_ELEVATOR, RECEIVING_FLOOR, FLOOR_SENDING, ELEVATOR_SENDING
//}

public class Scheduler implements Runnable {

	private static States state = States.IDLE; // starts off as idle;
	Events e;

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

	/**
	 * gets the controlDate object for testing purposes
	 * 
	 * @return the controlDate object
	 */
	public ControlDate getDate() {
		return this.c;
	}

	/**
	 * gets the source of the message
	 * 
	 * @return Floor of Elevator depending on who is sending/receiving the message
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * overrides the run method from the Runnable interface continuously gets data
	 * from the buffer to send to the corresponding floor/elevator
	 */
	@Override
	public void run() {
		while (true) {
//			System.out.println("run method is going on");

//			System.out.println("event: " + buffer.getEvent());
//			System.out.println("state: " + Scheduler.state);

			Object[] data = buffer.getData(); // get the data

			switch (state) {// going through states of machine

			case IDLE: {
				// if(state == States.IDLE) {
				//System.out.println("in idle");
				if (buffer.getEvent() == Events.RECEIVING_FLOOR) { // floor is sending a request
					state = States.RECEIVING;
				} else if (buffer.getEvent() == Events.RECEIVING_ELEVATOR) { // elevator is sending response
					state = States.RECEIVING;
				} else if (buffer.getEvent() == Events.WAITING) {
					state = States.IDLE;
				}
				System.out.println("state: " + Scheduler.state);
				System.out.println("event: " + buffer.getEvent());
				//break;

			}
			case RECEIVING: {
				// if(state == States.RECEIVING) {
				//System.out.println("in receiving");
				if (buffer.getEvent() == Events.RECEIVING_FLOOR) { // scheduler needs to send
					state = States.SENDING;
					buffer.setEvent(Events.FLOOR_SENDING);
				} else if (buffer.getEvent() == Events.RECEIVING_ELEVATOR) {
					state = States.SENDING;
					buffer.setEvent(Events.ELEVATOR_SENDING);
				}
				System.out.println("state: " + Scheduler.state);
				System.out.println("event: " + buffer.getEvent());
				//break;
			}

			case SENDING: {
				// if(state == States.SENDING) {
				if (buffer.getEvent() == Events.FLOOR_SENDING) { // send data to floor
					this.sendRequestToElevator((ControlDate) data[1]);
					state = States.IDLE;
					buffer.setEvent(Events.WAITING);
				} else if (buffer.getEvent() == Events.ELEVATOR_SENDING) { // send request to elevator
					this.sendDataToFloor((ControlDate) data[1]);
					state = States.IDLE;
					buffer.setEvent(Events.WAITING);
				}
				System.out.println("state: " + Scheduler.state);
				System.out.println("event: " + buffer.getEvent());
				//break;
			}

			}// end switch

//			try { //wait till next cycle
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}

		}
	}
}
