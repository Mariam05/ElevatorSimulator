
public class Buffer {

	private ControlDate request, data;
	private boolean requestIn;
	private boolean elevDataIn;
	private Object[] toScheduler;
	private boolean requestSent;

	public Buffer() {
		toScheduler = new Object[2];
		requestIn = false;
		elevDataIn = false;
		requestSent = false;
	}

	public synchronized void putFloorRequest(ControlDate request) {
		while (requestIn && elevDataIn) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Request in");
		this.request = request;

		requestIn = true;
		requestSent = false;

		notifyAll();
	}

	public synchronized void putElevatorData(ControlDate data) {
		System.out.println("Got here");
		while (!requestIn || elevDataIn) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Data In");
		this.data = data;

		elevDataIn = true;

		notifyAll();

	}

	public synchronized Object[] getData() {
		while (!requestIn && !elevDataIn) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestIn) {
			toScheduler[0] = "Floor";
			toScheduler[1] = request;
		}

		if (elevDataIn) {
			toScheduler[0] = "Elevator";
			toScheduler[1] = data;
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		requestSent = true;
		requestIn = false;
		elevDataIn = false;
		
		notifyAll();

		return toScheduler;
	}

}
