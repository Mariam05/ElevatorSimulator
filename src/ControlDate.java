import java.sql.Time;

/**
 * This class will be used to created event objects.
 * Each event object will have the following attributes associated with it:
 * Time, Floor or elevator number, button
 * 
 */

public class elevatorEvent {
	
	private Time time;
        private int floor;
	private boolean floorButton; // true if up, false if down c
	private int destinationFloor;
	
	public elevatorEvent(Time time, int floor, boolean floorButton, int destinationFloor) {
		this.time=time;
		this.floor=floor;
		this.floorButton=floorButton;
		this.destinationFloor=destinationFloor;
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
	public boolean isFloorButton() {
		return floorButton;
	}

	/**
	 * @return the destinationFloor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}


	
}
