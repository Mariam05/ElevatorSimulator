import java.sql.Time;

public class ControlDate {
	
	private Time time;
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

	private int floor;
	private boolean floorButton; // true if up, false if down c
	private int destinationFloor;
	
	public ControlDate(Time time, int floor, boolean floorButton, int destinationFloor) {
		this.time=time;
		this.floor=floor;
		this.floorButton=floorButton;
		this.destinationFloor=destinationFloor;
	}
	

}
