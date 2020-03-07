//package ElevatorSimulator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import org.json.*;

public class ControlDate {

	private Time time;
	/**
	 * This class will be used to created event objects. Each event object will have
	 * the following attributes associated with it: Time, Floor or elevator number,
	 * button
	 * 
	 */

	private int floor;
	private boolean floorButton; // true if up, false if down c
	private int destinationFloor;

	public ControlDate(Time time, int floor, boolean floorButton, int destinationFloor) {
		this.time=time;
		this.floor=floor;
		this.floorButton=floorButton;
		this.destinationFloor=destinationFloor;
	}
	
	/**
	 * converts to a string using JSON format for easy extraction
	 */
	public String toString() {
		JSONObject controlDate = new JSONObject();
		try {
			controlDate.put("time", time);
			controlDate.put("floor", floor);
			controlDate.put("floorButton", floorButton);
			controlDate.put("destinationFloor", destinationFloor);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return controlDate.toString(); 
	}
	
	/**
	 * 
	 * @return bytes of the JSON object
	 */
	public byte[] getByteArray() {
		return this.toString().getBytes();
	}

	/**
	 * @return the time
	 */
	public Time getTime() {
		return time;
	}

	/**
	 * @return the floor
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * @return the floorButton
	 */
	public boolean getFloorButton() {
		return floorButton;
	}

	/**
	 * @return the destinationFloor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	/**
	 * checks if ControlDate objects are equal
	 */
	public boolean equals(Object o) {
		if (o == this) { 
            return true; 
        } 
        if (!(o instanceof ControlDate)) { 
            return false; 
        } 
           
        ControlDate c = (ControlDate) o; 
          
        // Compare the data members and return accordingly  
        return (this.getTime()).equals(c.getTime())
                && Integer.compare(this.floor, c.floor) == 0
                && this.floorButton == c.floorButton
                && Integer.compare(this.destinationFloor, c.destinationFloor) == 0; 
	}
	
}