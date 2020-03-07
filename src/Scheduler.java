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

/**
 * this class facilitates the communication between the Floor class and the
 * Elevator class
 * 
 * @version 15 Feb 2020
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
		SENDING, RECEIVING, IDLE
	}

	private HashMap<Integer, JSONObject> elevators;
	private InetAddress floorAddress;
	private DatagramSocket receiveSocket, ackSocket, updateElevatorSocket;
	private static int floorPort = 1000, serverPort = 69;

	private Queue<JSONObject> requestQueue;

	/**
	 * stores the state of the scheduler, it starts off idle
	 */
	private static States state = States.IDLE; // starts off as idle;

	/**
	 * object used to encapsulate the data being sent by the elevator/floor
	 */
	ControlDate c;

	/**
	 * Constructor initializing all the class variables
	 * 
	 * @param floor    the floor the elevator wants to send information to
	 * @param elevator the elevator the floor wants to send information to
	 * @param buffer   is used to receive information from a floor/elevator
	 */
	public Scheduler(InetAddress floorAddress) {

		requestQueue = new LinkedList<>();
		elevators = new HashMap<>();

		try {
			this.floorAddress = floorAddress;
			// the host port is 23, time out if waiting and no reply
			receiveSocket = new DatagramSocket(23);
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

	private void receiveFromFloor() {
		Runnable receive = new Runnable() {

			@Override
			public void run() {

				while (true) {
					// receiving the data
					byte[] data = new byte[100]; // array to hold received data in
					DatagramPacket receivePacket = new DatagramPacket(data, data.length); // receive packet
					String txt;
					try {
						// block till packet is received

						receiveSocket.receive(receivePacket);
					   // sendACK(1000, "floor", InetAddress.getLocalHost());
						txt = new String(data, 0, receivePacket.getLength());
						JSONObject obj2 = new JSONObject(txt);
						synchronized (requestQueue) {
							requestQueue.add(obj2);
						}
						// process the packet received
						System.out.println("Scheduler: Packet received from floor");
						System.out.println("Contents (String): " + txt);
						System.out.println("Contents (Bytes): " + receivePacket.getData() + "\n");

						sendACK(floorPort, "floor", floorAddress);
					} catch (IOException e) {
						System.out.println("socket timeout :/");
						// cleanup
						receiveSocket.close();
						// forwardSocket.close();
						// ackSocket.close();
						System.exit(1);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		};

		Thread rThread = new Thread(receive);
		rThread.start();
	}

	/**
	 * 
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

						getSubSocket.receive(newElevPacket);

						String elev = new String(arr, 0, newElevPacket.getLength());
						System.out.println("This elevator is subscribing to the scheduler: " + elev);
						JSONObject newElev = new JSONObject(elev);
						synchronized (elevators) {
							elevators.put(newElev.getInt("id"), newElev);
						}

						getSubSocket.close();

					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		};

		Thread sThread = new Thread(getSub);

		sThread.start();

	}

	private synchronized void updateElevatorStatus() {
		Runnable updateStatus = new Runnable() {

			@Override
			public void run() {
				while (true) {
					// receiving the data
					byte[] data = new byte[100]; // array to hold received data in
					DatagramPacket receivePacket = new DatagramPacket(data, data.length); // receive packet
					String txt;
					try {
						// block till packet is received
						updateElevatorSocket.receive(receivePacket);
						
						txt = new String(data, 0, receivePacket.getLength());
						JSONObject obj2 = new JSONObject(txt);
						System.out.println("receieved from elevator: " + obj2.toString());
						int elevId = obj2.getInt("id");
						
//						//send ack
//						String inetAdd = (String) elevators.get(elevId).get("InetAddress");
//						sendACK(1040, "elevator", InetAddress.getByName(inetAdd));
						
						synchronized (elevators) {
							elevators.put(elevId, obj2);
							notifyAll();
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

		Thread uThread = new Thread(updateStatus);
		uThread.start();

	}

	private synchronized void scheduleAndSendCmd() {

		// runnable so it can be ran as a thread
		Runnable sAndS = new Runnable() {
			@Override
			public void run() {
				DatagramPacket cmdPacket;
				DatagramSocket sendCmdSocket;

				while (true) {

					int elevToSchedule = 1;
					int minDistance = 1000;

					try {
						sendCmdSocket = new DatagramSocket();

						synchronized (requestQueue) {
							synchronized (elevators) {
								if (!requestQueue.isEmpty() && !elevators.isEmpty()) {

									JSONObject firstReq = requestQueue.remove();

									int currFloor = firstReq.getInt("floor");

									for (int elev : elevators.keySet()) {
										int distance = Math.abs(currFloor - (elevators.get(elev)).getInt("currFloor"));
										if (distance < minDistance) {
											elevToSchedule = elev;
											minDistance = distance;
										}
									}

									byte[] cmd = firstReq.toString().getBytes();
									String inetAdd = (String) elevators.get(elevToSchedule).get("InetAddress");

									cmdPacket = new DatagramPacket(cmd, cmd.length, InetAddress.getByName(inetAdd),
											serverPort);
									sendCmdSocket.send(cmdPacket);
									sendACK(1040, "elevator", InetAddress.getByName(inetAdd));
									
									//receive an ack here
								}
							}
						}
						sendCmdSocket.close();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		};

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
