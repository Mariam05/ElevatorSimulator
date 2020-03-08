
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class Floor {
	/**
	 * this class store read in event Time, floor or elevator number, and button
	 * into a list of ControlData structure
	 * 
	 * @author Zewen Chen
	 */

	/* from a3 client */
	DatagramSocket sendSocket, receiveSocket;
	private DatagramPacket sendPacket, receivePacket;
	private static int hostPort = 23;
	private int i = 0;

	private InetAddress schedulerAddress;
	File file;
	private Date date;
	private Time time;
	private int floor;
	private boolean floorButton; // true if up, false if down c
	private int destinationFloor;
	private ArrayList<ControlDate> datas;
	private static Queue<ControlDate> requestQueue;
	private SimpleDateFormat sdf;

	/**
	 * Constructor used to initialize all instance variables
	 * 
	 * @param buffer object used to facilitate sending info to the elevator
	 */
	public Floor(InetAddress addr) {

		try {
			schedulerAddress = addr;
			this.file = new File("data.txt");
			this.datas = new ArrayList<ControlDate>();
			this.requestQueue = new LinkedList<>();
			sdf = new SimpleDateFormat("hh:mm:ss.S");
			getDataFromFile();
			sendSocket = new DatagramSocket();
			// bind receiving socket to port 1000
			receiveSocket = new DatagramSocket(1000);
			// timeout if not receiving anything
			// receiveSocket.setSoTimeout(5000);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
		// initiate the sending/receiving of data
		sendAndReceive();
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

	public void sendAndReceive() {
		byte msg[] = null;
		String msgString = "";

		/* continue till no more inputs in file */
		while (true) {
			if (!requestQueue.isEmpty()) {
				ControlDate c = requestQueue.remove();
				msgString = c.toString();
				msg = c.getByteArray();
			} else {
				System.exit(0);
			}

			try {
				// sending packet to host
				sendPacket = new DatagramPacket(msg, msg.length, schedulerAddress, hostPort);
				System.out.println("Floor: request count " + i++);
				System.out.println("Contents (String): " + msgString);
				System.out.println("Contents (Bytes): " + sendPacket.getData());
				sendSocket.send(sendPacket);

				System.out.println("Floor: Packet sent.\n");

				// TODO: Make the var random or make a sped up clock
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// get a reply from the host, any reply
				byte data[] = new byte[100];
				receivePacket = new DatagramPacket(data, data.length);

				// Block until a packet is received
				System.out.println("Floor: Waiting for ACK from host... ");
				// process received packet
				receiveSocket.receive(receivePacket);
				System.out.println("Floor: Reply received from host");
				System.out.println("Contents [String]: " + data.toString());
				System.out.println("Contents [Bytes]: " + receivePacket.getData() + "\n");

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("\nsocket timedout :/");
				// cleanup
				receiveSocket.close();
				sendSocket.close();
				System.exit(1);
			}

		}
	}

	public static void main(String[] args) {
		try {
			new Floor(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
