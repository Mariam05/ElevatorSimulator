package src.ElevatorSimulator;
/**
 * This class represents an elevator. 
 * @author defa hu
 * @author Zewen Chen
 *
 *
 * @version iteration 3
 * @author zewen chen
 */
public class Elevator implements Runnable {

	private int id; // to use in the future when there are multiple elevators
	private int currFloor, desFloor;
	private Buffer buffer;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	private ControlDate c;
	private boolean dataIn;
	private boolean moveUp;
	public ElevatorState state;
	
	public enum ElevatorState {
		//enums for identifying elevator states
	initial,doorOpen, moving, 
	}

	/**
	 * Create an elevator and associate it with a buffer
	 * @param buffer
	 */
	public Elevator(Buffer buffer,int id) {
		this.buffer = buffer;
		this.id = id;
		this.dataIn = false;	
	}

	/**
	 * Get information (i.e. a request from the floor)
	 * @param c
	 * @throws Exception 
	 */
	public synchronized void receiveFloorInfo(ControlDate c) {
		this.c = c;
		this.dataIn = true;
		currFloor=c.getFloor();
		desFloor=c.getDestinationFloor();
		moveUp = c.getFloorButton();
		state= ElevatorState.moving;
		processState();

	}
	
	/**
	 * Get the date
	 * @return
	 */
	public ControlDate getDate() {
		return this.c;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dataIn) {
				buffer.putElevatorData(c);
				dataIn = false;
			}

		}

	}
	/**
	 * Print out the state
	 * @throws Exception 
	 */
	private void processState() {
		System.out.println("elevator: current at "+ currFloor);	
			if(moveUp) {
				currFloor++;
			}else {
				currFloor--;
			}
			c.setFloor(currFloor);
							
			//try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.format("elevator: moved to %d\n", c.getFloor());
		
	}
}
