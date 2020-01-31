
public class Elevator implements Runnable{
	
	private int id; //elevator number
	private Scheduler scheduler;
	private int currFloor;

	public Elevator (Scheduler scheduler) {
		this.scheduler = scheduler;
		this.currFloor = 0;
	}

	@Override
	public void run() {
		while(true) {
			scheduler.getRequest();
		}
		
	}
}
