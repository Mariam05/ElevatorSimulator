package ElevatorSimulator;

/**
 * This class represents a clock that is used to keep track of the time elapsed
 * since it's instantiation.
 * 
 * Time is represented as one integer for the number of seconds passed.
 * @author Misho
 *
 */
public class Clock {

	/**
	 * Number of seconds passed since clock started
	 */
	private static int count = 0;

	

	/**
	 * Thread to increment the clock every second.
	 */
	public static void startClock() {
		Runnable incrementClock = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000); // wait one second
						count++; // increment
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		};

		Thread t = new Thread(incrementClock);
		t.start();
	}
	
	/**
	 * 
	 * @return int representing the number of seconds passed since clock started
	 */
	public static int getCurrTime() {
		return count;
	}
	
	/**
	 * Takes in a timer value for how long to count towards
	 * @param time the time in seconds to time
	 * @throws Exception when the timer goes off
	 */
	public static void startTimer(int time) throws Exception {
		for (int i = 0; i < time; i++) {
			Thread.sleep(1000);
		}
		throw new Exception("Timer went off");
	}
}
