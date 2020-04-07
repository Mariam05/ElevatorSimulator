package Tests;

import ElevatorSimulator.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONObject;
import org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/*
* Elevator Test cases
 * 
 * @version 07 Mar 2020
 * @author Defa Hu
 */

public class ElevatorTest {

	 private static ControlDate date;
	 private static Elevator elevator;
	 private static JSONObject subObj;
	 
	@Before
	public void setUpBeforeClass() throws Exception {
		System.out.println("GOing through");
		elevator = new Elevator(1,InetAddress.getLocalHost());
		subObj = new JSONObject();
		subObj.put("id", 1);
		subObj.put("InetAddress", InetAddress.getLocalHost().getHostName());
		subObj.put("currFloor", 1);
		subObj.put("State", Elevator.ElevatorState.IDLE );
		subObj.put("destinationFloor", 5);
		elevator.setFaultFlag(true);
		elevator.checkDoorFaultTest(9);
	}

	
	@Test
	public void testgoToDestination() {
		elevator.goToDestination(subObj);
		assertTrue(5 == elevator.getCurrentFloor());
		
	}
	@Test
	public void chcekFaulte() {
		assertFalse(elevator.getFaultFlag());
		
	}
}