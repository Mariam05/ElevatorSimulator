package Tests;

import ElevatorSimulator.*;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class SchedulerTest {
	
	private static InetAddress floorAddress;

	private static JSONObject subObj;

	/**
	 * Test to ensure that an elevator can subscribe to the scheduler
	 */
	@Test
	public void test() {
		try {
			floorAddress = InetAddress.getLocalHost();
			
			/* Test subscription of an elevator */
			Scheduler s = new Scheduler(floorAddress);
			//Thread.sleep(1000);
			Elevator e = new Elevator(1, InetAddress.getLocalHost());
			Thread.sleep(1000); // give it time to register 
			System.out.println(s.getNumElevators());
			assertEquals(s.getNumElevators(), 1 );
			
			subObj = new JSONObject();
			subObj.put("id", 1);
			subObj.put("InetAddress", InetAddress.getLocalHost().getHostName());
			subObj.put("currFloor", 1);
			subObj.put("State", Elevator.ElevatorState.IDLE );
			subObj.put("destinationFloor", 2);
			
			/* Test to ensure that elevator states change */
			JSONObject elevInitState = s.getElevatorInfo(1);
			System.out.println("INitial: " + elevInitState);
			e.goToDestination(subObj);
			Thread.sleep(1000);
			JSONObject elevCurrState = s.getElevatorInfo(1);
			System.out.println("Current: " + elevCurrState);
			
			assertFalse((s.getElevatorInfo(1).toString()).equals(elevInitState.toString()));
			
			/* Test that scheduler receives data from floor */
			//ControlDate c = new ControlDate()
//			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.S");
//			Date date = sdf.parse("09:09:09.1");
//			Time time = new Time(date.getTime());
//			//Floor f = new Floor(InetAddress.getLocalHost());
//			System.out.println("REQUESTS FROM FLOOR: " + s.getNumRequests());
//			Thread.sleep(500);
//			assertTrue(s.getNumRequests() > 0);
//			
//			ControlDate c = new ControlDate(time, 5, false, 2);
			
			
			
		} catch (UnknownHostException | InterruptedException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
