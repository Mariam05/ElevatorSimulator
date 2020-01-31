import java.util.LinkedList;
import java.util.Queue;

public class Scheduler implements Runnable {

	private Floor floor;
	private Elevator elev;
	private boolean requestPending;
	private Buffer buffer;

	private ControlDate event;

	public Scheduler(Floor floor, Elevator elevator, Buffer buffer) {
		requestPending = false;
		this.floor = floor;
		this.elev = elevator;
		this.buffer = buffer;
	}

	private void sendRequestToElevator(ControlDate c) {
		System.out.println("Scheduler is sending info from floor to elevator");
		elev.receiveFloorInfo(c);
	}

	private void sendDataToFloor(ControlDate c) {
		System.out.println("Scheduler is sending info from elevator to floor");
		floor.receiveDataFromElevator(c);
	}

	@Override
	public void run() {
		while (true) {
			Object[] data = buffer.getData();
			String source = (String)data[0];
			ControlDate c = (ControlDate)data[1];
			if (source.equalsIgnoreCase("Floor")) {
				this.sendRequestToElevator(c);
			} else {
				this.sendDataToFloor(c);
			}
		}
	}
}
