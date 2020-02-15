package Tests;
import java.text.ParseException;

import org.junit.Test;

import ElevatorSimulator.*;

public class SchedulerTest {

	@Test
	public void test() throws ParseException, InterruptedException {
		// initiate modules and start threads
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
		
		// give elevator enough time to get a date to begin with
		while (e.getDate() == null) {
			;
		}
		
		// since there is a significant delay between floor obtaining a date and
		// elevator obtaining the date, temp will hold elevator's last date
		// and hold off the assertion until elevator's date has updated
		ControlDate temp;
		for (int i = 0; i < 10; i++) {
			temp = e.getDate();
			assert(f.getData(i).equals(e.getDate()));
			while (temp == e.getDate() && i != 9) { // i == 9 is the exit condition
					Thread.sleep(250);
			}
		}
	}
}
