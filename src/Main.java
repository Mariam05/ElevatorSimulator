
public class Main {
	public static void main(String[] args) {
		Scheduler s = new Scheduler();
		Thread f = new Thread(new Floor(s));
		Thread sThread = new Thread(s);
		Thread e = new Thread(new Elevator(s));
		
		sThread.start();
		f.start();
		e.start();
		

	}
}
