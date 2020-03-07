import java.sql.Time;

public class Main {
	public static void main(String[] args) {
		
//		Buffer b = new Buffer();
//		Floor f = new Floor(b);
//		Elevator e = new Elevator(b);
//		
//		Scheduler s = new Scheduler(f,e,b);
//		
//		Thread fThread = new Thread(f, "Floor");
//		Thread eThread = new Thread(e, "Elevator");
//		Thread sThread = new Thread(s, "Scheduler");
//		
//		fThread.start();
//		eThread.start();
//		sThread.start();
		
		ControlDate c = new ControlDate(new Time(1,2,3), 4, true, 5);
		ControlDate d = new ControlDate(new Time(1,2,3), 5, true, 5);
		
		System.out.println("are they equal: " + c.equals(d));
		

	}
}
