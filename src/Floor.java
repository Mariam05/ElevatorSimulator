import java.io.BufferedReader;
import java.io.FileReader;

public class Floor implements Runnable{
	
	private Scheduler scheduler;
	
	public Floor(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void run() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("/data.txt"));
			String line = reader.readLine();
			while (line != null) {
				String[] splitted = line.split(" ");
				ElevatorEvent ee = new ElevatorEvent(splitted[0], splitted[1], splitted[2], splitted[3]);
				scheduler.floorRequest(ee);
				
				line = reader.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
