package src.ElevatorSimulator;
import java.io.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class Floor implements Runnable {
	/**
	 * this class store read in event Time, floor or elevator number, and button
	 * into a list of ControlData structure
	 * 
	 * @author Zewen Chen
	 */
	File file;
	private Date date;
	private Time time;
	private int floor;
	private boolean floorButton; // true if up, false if down c
	private int destinationFloor;
	private ArrayList<ControlDate> datas;
	private Queue<ControlDate> requestQueue;
	private SimpleDateFormat sdf;
	private Buffer buffer;
	private boolean receivedData;

	/**
	 * Constructor used to initialize all instance variables
	 * 
	 * @param buffer object used to facilitate sending info to the elevator
	 */
	public Floor(Buffer buffer) {
		this.buffer = buffer;
		this.receivedData = true; // initialized to true so that it runs the first time
		this.file = new File("data.txt");
		this.datas = new ArrayList<ControlDate>();
		this.requestQueue = new LinkedList<>();
		sdf = new SimpleDateFormat("hh:mm:ss.S");
		getDataFromFile();

	}

	/**
	 * reads floor/elevator data from a file
	 */
	private void getDataFromFile() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				// System.out.println(str);
				String[] x = str.split(" ");
				for (int i = 0; i < x.length; i++) {
					if (i == 0) {
						// time
						date = sdf.parse(x[i]);
						time = new Time(date.getTime());
					}
					if (i == 1) {
						// floor
						floor = Integer.parseInt(x[i]);
					}
					if (i == 2) {
						// floorButton
						if (x[i].equals("Up")) {
							floorButton = true;
						}
						if (x[i].equals("Down")) {
							floorButton = false;
						}
					}
					if (i == 3) {
						// destinationFloor
						destinationFloor = Integer.parseInt(x[i]);
					}
				}

				datas.add(new ControlDate(time, floor, floorButton, destinationFloor));
				requestQueue.add(new ControlDate(time, floor, floorButton, destinationFloor));
			}

			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	/**
	 * used to receive the data sent by the elevator
	 * 
	 * @param c controlDate object containing all required info
	 */
	public void receiveDataFromElevator(ControlDate c) {
		System.out.format("Floor received elevator info reached floor %d\n", c.getDestinationFloor());
		this.receivedData = true;

	}

	/**
	 * method used to get variable stored in ControlDate object for testing purposes
	 * 
	 * @param i index of of the arrayList
	 * @return the object that we want to compare
	 */
	public ControlDate getData(int i) {
		return this.datas.get(i);
	}

	/**
	 * overrides the run method in the Runnable interface
	 */
	@Override
	public void run() {
		while (!requestQueue.isEmpty()) { //there are request

			if (receivedData) { //data from elevator is being received
				ControlDate x =requestQueue.remove();
				System.out.format("**************Floor seed a new message asking elevator move for %d to %d  **************\n",x.getFloor(), x.getDestinationFloor());
				buffer.putFloorRequest(x);
				receivedData = false;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
