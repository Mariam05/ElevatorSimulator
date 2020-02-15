package ElevatorSimulator;

public class Main {
	public static void main(String[] args) {
		
		Buffer b = new Buffer();
		Floor f = new Floor(b);
		Elevator e = new Elevator(b);
		
		Scheduler s = new Scheduler(f,e,b);
		
		Thread fThread = new Thread(f, "Floor");
		Thread eThread = new Thread(e, "Elevator");
		Thread sThread = new Thread(s, "Scheduler");
		
		fThread.start();
		eThread.start();
		sThread.start();
		

	}
}
