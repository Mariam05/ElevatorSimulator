package Tests;

import static org.junit.Assert.assertTrue;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ElevatorSimulator.*;

/**
 * This is the test class for the Scheduler
 * 
 * Current version tests the elevator state when waiting for data to be received. 
 * The tests have letters in front of them so that they will run in order. 
 * 
 * @version 15 Feb 2020
 * 
 * @author Henry Wilson
 * @author Mariam Almalki
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SchedulerTest {

	private ControlDate controlDate;
	private SimpleDateFormat sdf;
	private Scheduler s;
	private Buffer b;
	private Floor f;
	private Elevator e;

	@Before
	public void setUp() throws Exception {

		// initiate modules and start threads
		b = new Buffer();
		f = new Floor(b);
		e = new Elevator(b);
		s = new Scheduler(f, e, b);

		sdf = new SimpleDateFormat("hh:mm:ss.S");
		try {
			controlDate = new ControlDate(new Time(sdf.parse("09:09:09.1").getTime()), 1, true, 5);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check that the scheduler is waiting for a response after sending
	 * a request to the elevator
	 */
	@Test
	public void aTestSendRequestToElevator() {
		s = new Scheduler(f, e, b);
		s.sendRequestToElevator(controlDate);
		assertTrue(s.getState() == Scheduler.States.IDLE);
	}
	
	/**
	 * Check that the scheduler is waiting for a request after sending 
	 * a response to the floor. 
	 */
	@Test
	public void bTestSendDataToFloor() {
		s.sendDataToFloor(controlDate);
		assertTrue(s.getState() == Scheduler.States.IDLE);
	}
	
	/**
	 * Test the data transfer between classes 
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	@Test
	public void testDataTransfer() throws ParseException, InterruptedException {
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
			assert (f.getData(i).equals(e.getDate()));
			while (temp == e.getDate() && i != 9) { // i == 9 is the exit condition
				Thread.sleep(250);
			}
		}
		
	}
}
