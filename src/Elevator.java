
public class Elevator implements Runnable {

	private int id; // to use in the future when there are multiple elevators
	private Scheduler scheduler;
	private int currFloor;
	private Buffer buffer;
	private ControlDate c;
	private boolean dataIn;
	public ElevatorState state;

	public Elevator(Buffer buffer) {
		this.buffer = buffer;
		this.currFloor = 0;
		this.dataIn = false;
		state= ElevatorState.initial;	
	}

	public synchronized void receiveFloorInfo(ControlDate c) {
		this.c = c;
		this.dataIn = true;
		state= ElevatorState.moving;
		processState();

	}
	public synchronized void pressDoorOpenButton() {
		state = ElevatorState.doorOpen;
		processState();
	}
	public synchronized void pressDoorCloseButton() {
		state = ElevatorState.initial;
		processState();
	}
	public ControlDate getDate() {
		return this.c;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dataIn) {
				buffer.putElevatorData(c);
				dataIn = false;
			}

		}

	}
	
	
	/**
	 * adding state to the elevator
	 *                  open button pressed
	 * initial<--------------------------------------------->doorOpen
	 *           close button pressed || after 10 seconds       ^
	 *     |                                                    |
	 *     |                                                    |
	 *     |                                                    |
	 *     |                                                    |
	 *     |  data recive press floor                           |
	 *     |                                                    |
	 *     |                                                    |
	 *     |                                                    |
	 *     |                                                    |
	 *     moving-----------------------------------------------+  
	 *     					reach desFloor
	 */
	
	
	//method to process action to each states
	private void processState() {
			switch(state) {
			case initial:
				System.out.println("elevator: door closed and avliable");
				break;
			case doorOpen:
				System.out.println("elevator: door open");
				break;
			case moving:
				System.out.format("elevator: moving from floor %d to %d\n", c.getFloor(),c.getDestinationFloor());
				System.out.format("elevator: reach floor %d\n", c.getDestinationFloor());
				break;
		}
	}
}
