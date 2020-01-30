import java.sql.Time;

/**
 * This class will be used to created event objects.
 * Each event object will have the following attributes associated with it:
 * Time, Floor or elevator number, button
 * 
 */
public class ElevatorEvent {

	private Time time;
	private int floor;
	private boolean floorButton; // true if up, false if down c
	private int destinationFloor;
	
	public ElevatorEvent(String time, String floor, String floorButton, String destinationFloor) {
		this.time = Time.valueOf(time);
		this.destinationFloor = Integer.parseInt(destinationFloor);
		this.floor = Integer.parseInt(floor);
		this.floorButton = floorButton.equalsIgnoreCase("up")  ? true : false;
	}
	
	public Time getTime() {
		return time;
	}
	
	public int getCurrFloor() {
		return floor;
	}
	
	public boolean getDirection() {
		return floorButton;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}
}
