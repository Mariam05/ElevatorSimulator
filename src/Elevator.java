
public class Elevator implements Runnable {

	private int id; // to use in the future when there are multiple elevators
	private Scheduler scheduler;
	private int currFloor;
	private Buffer buffer;
	private ControlDate c;

	public Elevator(Buffer buffer) {
		this.buffer = buffer;
		this.currFloor = 0;
	}

	public void receiveFloorInfo(ControlDate c) {
		System.out.format("Elevator received floor request from scheduler: moving from floor %d to %d\n", c.getFloor(),
				c.getDestinationFloor());
		this.c = c;
	}

	@Override
	public void run() {
		while (true) {
			if (c != null)
				buffer.putElevatorData(c);
		}

	}
}
