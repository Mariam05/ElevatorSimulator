
public class Buffer {

	private Scheduler scheduler;
	private ControlDate request, data;
	private boolean requestIn;
	private Object[] toScheduler;

	public Buffer() {
		toScheduler = new Object[2];
		requestIn = false;
	}

	public synchronized void putFloorRequest(ControlDate request) {
		while (requestIn) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.request = request;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestIn = true;
		notifyAll();
	}

	public synchronized void putElevatorData(ControlDate data) {
		while (!requestIn) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.data = data;
		
		requestIn = false;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		notifyAll();
		
	}
	
	public synchronized void getData() {
		
	}

}
