
public class Buffer {

	private ControlDate request, data;
	private boolean requestIn;
	private boolean elevDataIn;
	private Object[] toScheduler;
	private boolean sendRequest;

	public Buffer() {
		toScheduler = new Object[2];
		requestIn = false;
		elevDataIn = false;
		sendRequest = false;
	}

	public synchronized void putFloorRequest(ControlDate request) {
		while (elevDataIn || sendRequest ) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.request = request;

		requestIn = true;
		sendRequest = true;

		notifyAll();
	}

	public synchronized void putElevatorData(ControlDate data) {
		while (requestIn || sendRequest) {
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
	
	public synchronized Object[] getData() {
		while (!sendRequest) { //!requestIn && !elevDataIn || 
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		sendRequest = false;
		
		if (requestIn) {
			toScheduler[0] = "Floor";
			toScheduler[1] = request;
			requestIn = false;
		}

		if (elevDataIn) {
			toScheduler[0] = "Elevator";
			toScheduler[1] = data;
			elevDataIn = false;
		}
		
		notifyAll();

		return toScheduler;
	}

}
