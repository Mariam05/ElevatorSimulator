package Tests;
import static org.junit.Assert.*;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import ElevatorSimulator.*;

public class ElevatorTest {
	
	private ControlDate date;
	private SimpleDateFormat sdf;
	private Elevator elevator;

	@Before
	public void setUp() throws Exception {
		System.out.println("GOing through");
		sdf = new SimpleDateFormat("hh:mm:ss.S");
		Buffer b = new Buffer();
		elevator = new Elevator(b);
		try {
			date = new ControlDate(new Time(sdf.parse("09:09:09.1").getTime()), 1, true, 5);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
		System.out.println("Floor: " + date.getFloor());
		elevator.receiveFloorInfo(date);
		assertTrue(elevator.state == Elevator.ElevatorState.moving);
	}

}

