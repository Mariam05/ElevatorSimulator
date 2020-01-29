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
	
	public ElevatorEvent(String time, int floor, boolean floorButton, int destinationFloor) {
		this.time = Time.valueOf(time);
		this.destinationFloor = destinationFloor;
		this.floor = floor;
		this.floorButton = floorButton;
	}
}
