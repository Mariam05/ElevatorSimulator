package src.ElevatorSimulator;

public class Main {
	public static void main(String[] args) {
		
		Buffer b = new Buffer();
		Floor f = new Floor(b);
		Elevator e = new Elevator(b,1);
		
		Scheduler s = new Scheduler(f,e,b);
		
		Thread fThread = new Thread(f, "Floor");
		Thread eThread = new Thread(e, "Elevator");
		Thread sThread = new Thread(s, "Scheduler");
		Thread eThread2 = new Thread(e, "Elevator");
		
		fThread.start();
		eThread.start();
		eThread2.start();
		sThread.start();
		

	}
}
