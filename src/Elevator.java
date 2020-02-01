
public class Elevator implements Runnable {

	private int id; // to use in the future when there are multiple elevators
	private Scheduler scheduler;
	private int currFloor;
	private Buffer buffer;
	private ControlDate c;
	private boolean dataIn;

	public Elevator(Buffer buffer) {
		this.buffer = buffer;
		this.currFloor = 0;
		this.dataIn = false;
	}

	public void receiveFloorInfo(ControlDate c) {
		System.out.format("Elevator received floor request from scheduler: moving from floor %d to %d\n", c.getFloor(),
				c.getDestinationFloor());

		this.c = c;
		this.dataIn = true;

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
}
