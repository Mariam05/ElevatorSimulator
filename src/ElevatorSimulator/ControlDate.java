package src.ElevatorSimulator;
import java.sql.Time;

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
	 * @param floor the floor to set
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	}

	/**
	 * @param floorButton the floorButton to set
	 */
	public void setFloorButton(boolean floorButton) {
		this.floorButton = floorButton;
	}

	/**
	 * @param destinationFloor the destinationFloor to set
	 */
	public void setDestinationFloor(int destinationFloor) {
		this.destinationFloor = destinationFloor;
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