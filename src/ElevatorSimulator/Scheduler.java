package ElevatorSimulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import ElevatorSimulator.Elevator.ElevatorState;

/**
 * this class contains the logic engine of the process. it receives the
 * requests, processes it, and sends a command to an elevator to fulfill the
 * request
 * 
 * @version 07 March 2020
 * @author Mariam Almalki, Ruqaya Almalki
 *
 */
public class Scheduler {

	/**
	 * states of the scheduler
	 * 
	 * @author Ruqaya Almalki
	 *
	 */
	public enum States {
		UPDATING
	}
	
	public JSONObject currReq;

	/**
	 * contains a list of all the subscribed elevators and their contents
	 */
	private HashMap<Integer, JSONObject> elevators;
	/**
	 * IP address of the floor
	 */
	private InetAddress floorAddress;
	/**
	 * sockets used to send and receive different types of information
	 */
	private DatagramSocket floorSocket, ackSocket, updateElevatorSocket;
	/**
	 * ports used to communicate with the different entities in the system
	 */
	private static int floorPort = 1000, serverPort = 69, elevatorACKport = 1040;

	/**
	 * queue full of all the requests coming in from the floor
	 */
	private Queue<JSONObject> requestQueue;

	/**
	 * stores the state of the scheduler, it starts off idle
	 */
	private static States state = States.UPDATING; // starts off as idle;

	/**
	 * object used to encapsulate the data being sent by the elevator/floor
	 */
	ControlDate c;

	/**
	 * Constructor initializing all the class variables
	 * 
	 * @param floorAddress is the ip address of the floor
	 */
	public Scheduler(InetAddress floorAddress) {

		requestQueue = new LinkedList<>();
		elevators = new HashMap<>();

		try {
			requestQueue = new LinkedList<>();
			elevators = new HashMap<>();
			this.floorAddress = floorAddress;
			// the host port is 23, time out if waiting and no reply
			floorSocket = new DatagramSocket(23);
			updateElevatorSocket = new DatagramSocket(1026);
			ackSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		addSubscribers();
		receiveFromFloor();
		scheduleAndSendCmd();
		updateElevatorStatus();
	}
	
	
	public int getNumRequests() {
		return requestQueue.size();
	}

	/**
	 * receives the info from the floor and stores it in a queue, sends the floor an
	 * ACK. its a thread so it can constantly be doing this while other stuff go on.
	 */
	private void receiveFromFloor() {
		Runnable receive = new Runnable() {

			@Override
			public void run() {

				while (true) {
					try {
						// receiving the data
						byte[] data = new byte[100]; // array to hold received data in
						DatagramPacket receivePacket = new DatagramPacket(data, data.length); // receive packet
						String txt;
						// block till packet is received
						floorSocket.receive(receivePacket);

						// process the received packet
						txt = new String(data, 0, receivePacket.getLength());
						JSONObject obj2 = new JSONObject(txt);
						currReq = obj2;
						synchronized (requestQueue) {
							requestQueue.add(obj2);
						}
						System.out.println("Queue " + requestQueue.size());
						System.out.println("Scheduler: Packet received from floor");
						System.out.println("Contents (String): " + txt);
						System.out.println("Contents (Bytes): " + receivePacket.getData() + "\n");

						// send an ACK
						sendACK(floorPort, "floor", floorAddress);

					} catch (IOException e) {
						System.out.println("socket timeout :/");
						// cleanup
						floorSocket.close();
						System.exit(1);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			}

		};
		// create thread and start it
		Thread rThread = new Thread(receive);
		rThread.start();
	}

	/**
	 * the elevators needs to subscribe to the Scheduler in order to be recognized
	 * by the system
	 */
	private void addSubscribers() {

		Runnable getSub = new Runnable() {
			byte[] arr = new byte[100];
			DatagramPacket newElevPacket;
			DatagramSocket getSubSocket;

			@Override
			public void run() {
				while (true) {
					newElevPacket = new DatagramPacket(arr, arr.length);

					try {
						getSubSocket = new DatagramSocket(1035);
						// receive subscription packet from elevator
						getSubSocket.receive(newElevPacket);
						String elev = new String(arr, 0, newElevPacket.getLength());
						System.out.println("This elevator is subscribing to the scheduler: " + elev);
						// extract info in JSON format
						JSONObject newElev = new JSONObject(elev);
						synchronized (elevators) {
							// add it to the Hashmap containing all elevator info
							elevators.put(newElev.getInt("id"), newElev);
						}
						getSubSocket.close();

					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
		};
		// start the threads
		Thread sThread = new Thread(getSub);
		sThread.start();

	}
	/**
	 * constantly updates the elevator states/positions
	 */
	private synchronized void updateElevatorStatus() {
		Runnable updateStatus = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						// receiving the data
						byte[] data = new byte[100]; // array to hold received data in
						DatagramPacket receivePacket = new DatagramPacket(data, data.length); // receive packet
						String txt;
						// block till packet is received
						updateElevatorSocket.receive(receivePacket);

						txt = new String(data, 0, receivePacket.getLength());
						JSONObject obj2 = new JSONObject(txt);
						System.out.println("receieved from elevator: " + obj2.toString());
						int elevId = obj2.getInt("id");

						synchronized (elevators) {
							elevators.put(elevId, obj2);
						}

						// process the packet received
						System.out.println("Scheduler: Status Update received");
						System.out.println("Contents (String): " + txt);
						System.out.println("Contents (Bytes): " + receivePacket.getData() + "\n");

					} catch (IOException e) {
						System.out.println("socket timeout :/");
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		};

		// start the threads
		Thread uThread = new Thread(updateStatus);
		uThread.start();

	}

	/**
	 * 
	 * Change so that:
	 * only send request if state of elevator is the same or if state of elevator is idle. 
	 * contains logic that decides which elevator should process the request given
	 * their current states. It then sends the request to that elevator
	 */
	private synchronized void scheduleAndSendCmd() {

		// runnable so it can be ran as a thread
		Runnable sAndS = new Runnable() {
			@Override
			public void run() {
				DatagramPacket cmdPacket;
				DatagramSocket sendCmdSocket;

				while (true) {

					int elevToSchedule = 1; // elevator id to give the request to
					int minDistance = 1000; // shortest distance to the passenger

					try {
						sendCmdSocket = new DatagramSocket();

						synchronized (requestQueue) {
							synchronized (elevators) {
								if (!requestQueue.isEmpty() && !elevators.isEmpty()) {
									// get the request
									JSONObject firstReq = requestQueue.remove();
									int currFloor = firstReq.getInt("floor");
									int destFloor = firstReq.getInt("destinationFloor");
									ElevatorState direction;
									if((currFloor - destFloor) < 0 ) {//State == UP
										direction = ElevatorState.UP;
									}else {
										direction = ElevatorState.DOWN;
									}
									
									// iterate through to get the elevator that has the min distance
									for (int elev : elevators.keySet()) {
										int distance = Math.abs(currFloor - (elevators.get(elev)).getInt("currFloor"));
									    boolean checkState = ((elevators.get(elev)).get("State") == direction); 
										if (distance < minDistance && checkState) {
											elevToSchedule = elev;
											minDistance = distance;
										}
									}

									// send the request to the selected elevator
									byte[] cmd = firstReq.toString().getBytes();
									String inetAdd = (String) elevators.get(elevToSchedule).get("InetAddress");
									cmdPacket = new DatagramPacket(cmd, cmd.length, InetAddress.getByName(inetAdd),
											serverPort);
									sendCmdSocket.send(cmdPacket);
									sendACK(elevatorACKport, "elevator", InetAddress.getByName(inetAdd));

									// receive an ack here
								}
							}
						}
						sendCmdSocket.close();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		};
		// start the threads
		Thread thread = new Thread(sAndS);
		thread.start();

	}
	/**
	 * sends an ack message to the one who sent the data to be forwarded (accepting
	 * the data/the reply)
	 * 
	 * @param port         to be sent to
	 * @param sendToSource who the ack should be sent to, the client or server
	 */
	private void sendACK(int port, String sendToSource, InetAddress sourceAddress) {
		try {
			JSONObject ack = new JSONObject();
			ack.put("message", "ACK");
			byte[] data = ack.toString().getBytes();
			DatagramPacket ackPacket = new DatagramPacket(data, data.length, sourceAddress, port);
			System.out.println("Scheduler: Sending ACK to " + sendToSource + "...");
			System.out.println("Contents(String) " + ack.toString());
			ackSocket.send(ackPacket);
			System.out.println("Scheduler: ACK sent\n");
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @return the number of elevators that the scheduler must manage
	 */
	public int getNumElevators() {
		return elevators.size();
	}
	
	public JSONObject getElevatorInfo(int elevID){
		return elevators.get(elevID);
	}

	/**
	 * runs the Host program continuously
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// run the program
		try {
			new Scheduler(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
