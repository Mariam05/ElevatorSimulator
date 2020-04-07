package Tests;

import ElevatorSimulator.*;
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
 * @author Defa Hu, Zewen Chen, Mariam Almalki
 */

public class ElevatorTest {

	 private static ControlDate date;
	 private static Elevator elevator;
	 private static JSONObject subObj;
	 
	 /**
	  * Set up an elevator with a request. 
	  * @throws Exception
	  */
	@Before
	public void setUpBeforeClass() throws Exception {
		System.out.println("GOing through");
		elevator = new Elevator(1,InetAddress.getLocalHost());
//		subObj = new JSONObject();
//		subObj.put("id", 1);
//		subObj.put("InetAddress", InetAddress.getLocalHost().getHostName());
//		subObj.put("currFloor", 1);
//		subObj.put("State", Elevator.ElevatorState.IDLE );
//		subObj.put("destinationFloor", 5);	
	}

	
	/**
	 * This test ensures that the elevator can move to a requested destination. 
	 */
	@Test
	public void testgoToDestination() {
		subObj = new JSONObject();
		try { 
			subObj.put("id", 1);
			subObj.put("InetAddress", InetAddress.getLocalHost().getHostName());
			subObj.put("currFloor", 1);
			subObj.put("State", Elevator.ElevatorState.IDLE );
			subObj.put("destinationFloor", 3);	// give it a value that will result in a fault 
		} catch (Exception e ) {
			// do nothing
		}
		elevator.goToDestination(subObj);
		assertTrue(3 == elevator.getCurrentFloor());
	}
	
	/**
	 * This test checks the logic for having a door jam. 
	 */
	@Test
	public void testDoorJamFault() {
		elevator.setFaultFlag(false); // make sure the flag is false
		elevator.checkDoorFaultTest(7); // pass in a number that should set it to true
		assertTrue(elevator.getFaultFlag()); // assert that it is in fact true. 
	}
	
	/**
	 * This test checks the logic of having a floor timing fault.
	 */
	@Test
	public void testFloorTimingFault() {
		subObj = new JSONObject();
		try { 
			subObj.put("id", 1);
			subObj.put("InetAddress", InetAddress.getLocalHost().getHostName());
			subObj.put("currFloor", 1);
			subObj.put("State", Elevator.ElevatorState.IDLE );
			subObj.put("destinationFloor", 7);	// give it a value that will result in a fault 
		} catch (Exception e ) {
			// expecting an exception, no worries; do nothing
		}
		elevator.goToDestinationTest(subObj);
		assertTrue(elevator.getTFaultFlag());
		
	}


}