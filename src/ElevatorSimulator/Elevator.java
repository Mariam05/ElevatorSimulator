package ElevatorSimulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;



public class Elevator {

	/**
	 * ENUM for elevator states
	 * 
	 * @author Mariam Almalki, Ruqaya Almalki, Zewen Chen
	 *
	 */
	public enum ElevatorState {
		IDLE, DOOR_OPEN, DOOR_CLOSED, UP, DOWN
	}
	/*
	 * Sockets and packets used to send and receive to/from the scheduler
	 */
	private DatagramPacket receivePacket, subscribePacket, ackPacket;
	private DatagramSocket sendSocket, receiveSocket, subscribeSocket, ackSocket;
	/*
	 * the scheduler's address
	 */
	private InetAddress schedulerAddress;
	/*
	 * the elevator state
	 */
	public ElevatorState state;
	/*
	 * used to as a key by the scheduler to keep track of the number of elevators
	 */
	private int id;
	/*
	 * the current floor the elevator is at
	 */
	private int currFloor;

	/**
	 * port used to update the status of the elevator with the scheduler
	 */
	private int updateStatusPort = 1026;

	/*
	 * the ACK port used to communicate with the scheduler
	 */
	private int ackPort = 1040;
	/*
	 * port used when an elevator wants to subscribe to a scheduler
	 */
	private int subscriptionPort = 1035;
	/*
	 * object containing all the elevator information
	 */
	private JSONObject subObj;

	/**
	 * Constructor used to initialize all instance variables
	 * 
	 * @param id               the elevator identifier used by the scheduler as a
	 *                         key
	 * @param schedulerAddress the ip address of the scheduler
	 */
	public Elevator(int id, InetAddress schedulerAddress) {

		this.schedulerAddress = schedulerAddress;
		this.id = id;
		this.currFloor = 1;
		state = ElevatorState.IDLE;

		// create json and store all the instance variable states
		subObj = new JSONObject();
		updateJSONObj();

		byte[] subArr = subObj.toString().getBytes();

		try {
			// server is bounded to port 69
			receiveSocket = new DatagramSocket(69);
			subscribeSocket = new DatagramSocket();
			sendSocket = new DatagramSocket();
			ackSocket = new DatagramSocket(ackPort);

			// subscribe to the scheduler, so it knows of its existance
			subscribePacket = new DatagramPacket(subArr, subArr.length, schedulerAddress, subscriptionPort);
			subscribeSocket.send(subscribePacket);
			subscribeSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		receiveAndRespond();
	}

	/**
	 * Set the current floor to the desired floor. 
	 * This method is used for testing purposes only. 
	 * @param floor the floor to set the elevator to 
	 */
	public void setCurrFloor(int floor) {
		this.currFloor = floor;
	}
	/**
	 * Info on the elevator in JSON format
	 */
	private void updateJSONObj() {
		try {
			subObj.put("id", id);
			subObj.put("InetAddress", InetAddress.getLocalHost().getHostName());
			subObj.put("currFloor", currFloor);
			subObj.put("State", state);
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sends the scheduler the state of the elevator (the JSON with all the info)
	 */
	private void sendStateUpdate() {
		try {
			byte[] subArr = subObj.toString().getBytes();
			DatagramPacket updateStatePacket = new DatagramPacket(subArr, subArr.length, schedulerAddress,
					updateStatusPort);
			DatagramSocket updateStateSocket = new DatagramSocket();
			updateStateSocket.send(updateStatePacket);
			updateStateSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Move the elevator to the floor of the passenger, and send current state to
	 * scheduler as it moves.
	 * 
	 * @param obj the JSONObject command received by the scheduler
	 */
	public void moveElevator(JSONObject obj) {
		try {
			int passengerFloor = obj.getInt("floor"); // floor passenger is at
			int dir = currFloor - passengerFloor; // closest floor to passenger
			if (dir < 0) { // moving up to passenger
				for (int i = currFloor; i <= passengerFloor; i++) {
					System.out.println("Elevator: moving to floor " + currFloor++);
					Thread.sleep(2000);
					updateJSONObj();
					sendStateUpdate();
					// receiveACK();
					state = ElevatorState.UP;
				}
				currFloor--;
				state = ElevatorState.DOOR_OPEN;
				System.out.println("got to passenger...now moving to destination:");
				state = ElevatorState.DOOR_CLOSED;
				goToDestination(obj);

			} else if (dir == 0) { // already there
				System.out.println("elevator at passenger floor: open doors");
				state = ElevatorState.DOOR_OPEN;
				state = ElevatorState.DOOR_CLOSED;
				goToDestination(obj);
			} else { // moving down
				for (int i = currFloor; i >= passengerFloor; i--) {
					System.out.println("Elevator: moving to floor " + currFloor--);
					Thread.sleep(2000);
					updateJSONObj();
					sendStateUpdate();
					// receiveACK();
					state = ElevatorState.DOWN;
				}
				currFloor++;
				state = ElevatorState.DOOR_OPEN;
				System.out.println("got to passenger...now moving to destination:");
				state = ElevatorState.DOOR_CLOSED;
				goToDestination(obj);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * brings the passenger to their destination floor
	 * 
	 * @param obj JSON obj containing the request info
	 */
	public void goToDestination(JSONObject obj) {
		int destinationFloor;
		try {
			destinationFloor = obj.getInt("destinationFloor"); // destination of passenger
			int goToDestination = currFloor - destinationFloor; // closest floor to passenger
			if (goToDestination < 0) { // moving up to destination floor
				for (int i = currFloor; i < destinationFloor; i++) {

					System.out.println("Elevator: moving to floor " + ++currFloor);
					Thread.sleep(2000);
					updateJSONObj();
					sendStateUpdate();
					// receiveACK();
					state = ElevatorState.UP;
				}
				state = ElevatorState.DOOR_OPEN;
				state = ElevatorState.DOOR_CLOSED;
				state = ElevatorState.IDLE;

			} else {
				for (int i = currFloor; i > destinationFloor; i--) { // moving down to destination
					System.out.println("Elevator: moving to floor " + --currFloor);
					Thread.sleep(2000);
					updateJSONObj();
					sendStateUpdate();
					// receiveACK();
					state = ElevatorState.DOWN;
				}
				state = ElevatorState.DOOR_OPEN;
				state = ElevatorState.DOOR_CLOSED;
				state = ElevatorState.IDLE;
			}
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * receive a request, send an ACK, process the request
	 */
	private void receiveAndRespond() {

		String txt;
		while (true) {
			try {

				byte data[] = new byte[100];
				// receive the request
				receivePacket = new DatagramPacket(data, data.length);
				// block until packet is received
				System.out.println("Elevator: Currently idle. Waiting for request \n ");
				receiveSocket.receive(receivePacket);

				// extracts the info in JSON format
				txt = new String(data, 0, receivePacket.getLength());
				JSONObject obj = new JSONObject(txt);

				// process the data received
				System.out.println("Server: Request received: ");
				System.out.println("Contents (String): " + obj.toString());
				System.out.println("Contents (Bytes): " + receivePacket.getData() + "\n");

				// send an ack
				JSONObject ack = new JSONObject();
				ack.put("message", "ACK");
				byte[] data1 = ack.toString().getBytes();
				DatagramPacket ackPacket = new DatagramPacket(data1, data1.length, schedulerAddress, ackPort);
				System.out.println("Elevator: sending ack to scheduler...");
				System.out.println("Contents(String) " + ack.toString());
				ackSocket.send(ackPacket);
				System.out.println("Elevator: ACK sent\n");

				// execute command
				moveElevator(obj);

			} catch (Exception e1) {
				e1.printStackTrace();
				// cleanup
				sendSocket.close();
				receiveSocket.close();
				System.exit(1);
			}
		}
	}

	/**
	 * method used to receive an ACK sent by the scheduler
	 */
	private void receiveACK() {
		byte replyData[] = new byte[100];
		ackPacket = new DatagramPacket(replyData, replyData.length);
		try {
			ackSocket.receive(ackPacket);
			String txt = new String(replyData, 0, ackPacket.getLength());
			JSONObject ack = new JSONObject(txt);
			System.out.println("elevator: ACK received from scheduler");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * creates an instance of the elevator
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// for multiple elevators change the id
			// InetAddress addr = InetAddress.getByName("cb5107-22");
			(new Elevator(1, InetAddress.getLocalHost())).receiveAndRespond();;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public int getCurrentFloor() {
		// TODO Auto-generated method stub
		return currFloor;
	}
}
