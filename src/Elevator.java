/**
 * this class simulates the actions of the elevator
 * 
 * @author defa hu
 *
 */
public class Elevator implements Runnable {

	/**
	 * buffer object used to send information to elevator
	 */
	private Buffer buffer;

	/**
	 * object used to store information desired
	 */
	private ControlDate c;

	/**
	 * checks if there is data that needs to be sent
	 */
	private boolean dataIn;

	/**
	 * Constructor, initializes all instance variables
	 * 
	 * @param buffer object used to send data
	 */
	public Elevator(Buffer buffer) {
		this.buffer = buffer;
		this.dataIn = false;
	}

	/**
	 * method used to receive info from floor
	 * 
	 * @param c ControlDate object containing all required info
	 */
	public void receiveFloorInfo(ControlDate c) {
		System.out.format("Elevator received floor request from scheduler: moving from floor %d to %d\n", c.getFloor(),
				c.getDestinationFloor());

		this.c = c;
		this.dataIn = true;

	}

	/**
	 * method used to get controlDate object for test cases
	 * 
	 * @return
	 */
	public ControlDate getDate() {
		return this.c;
	}

	/**
	 * overrides run method in the Runnable interface 
	 * continuously poll, if there is data to be sent, send it
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (dataIn) {
				buffer.putElevatorData(c);
				dataIn = false;
			}

		}

	}
}
