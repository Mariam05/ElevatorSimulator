
public class Elevator implements Runnable{
	
	private int id;
	private Scheduler scheduler;

	public Elevator (Scheduler scheduler, int id) {
		this.scheduler = scheduler;
		this.id = id;
	}

	@Override
	public void run() {
		while(true) {
			scheduler.elevatorMoveRequest(id);
		}
		
	}
}
