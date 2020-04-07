package ElevatorSimulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This floor represents an elevator. An elevator has states. An elevator can
 * have 2 different faults: - A timing floor fault (this is fatal, ends the
 * systems) - A door jam fault (this fault is transient, we will recover) The
 * floor timing error is hard coded in line 6 of the data file.
 *
 */

public class Elevator {

	/**
	 * ENUM for elevator states
	 * 
	 * @author Mariam Almalki, Ruqaya Almalki, Zewen Chen
	 *
	 */
	public enum ElevatorState {
		IDLE, DOOR_OPEN, DOOR_CLOSED, UP, DOWN, FIXING_DOORS
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
	 * Keeps track of the current value of the timer
	 */
	private int timer;

	/**
	 * Value to initialize timer to
	 */
	private static int timer_time = 6;

	/*
	 * boolean to indicate fault
	 */
	private boolean fault = false, tFault = false;

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
		timer = timer_time;

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

			// subscribe to the scheduler, so it knows of its existence
			subscribePacket = new DatagramPacket(subArr, subArr.length, schedulerAddress, subscriptionPort);
			subscribeSocket.send(subscribePacket);
			subscribeSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		receiveAndRespond();
	}

	/**
	 * Set the current floor to the desired floor. This method is used for testing
	 * purposes only.
	 * 
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
	 * This method randomly determines whether a door jam occurs. If yes, then we
	 * give the fixer guy time to fix it.
	 */
	private void checkDoorFault() {
		Random r = new Random();

		int val = r.nextInt(10); // generate a number between 0 and 9 (inclusive)

		if (val >= 6) {
			state = ElevatorState.FIXING_DOORS;
			System.out.println("Door is jamed. Please stand by while fixing ....");
			try {
				Thread.sleep(3000); // give it time to fix.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Fixed!");
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
			timer = timer_time;
			int passengerFloor = obj.getInt("floor"); // floor passenger is at
			int dir = currFloor - passengerFloor; // closest floor to passenger
			if (dir < 0) { // moving up to passenger
				for (int i = currFloor; i <= passengerFloor; i++) {
					System.out.println("Elevator: moving to floor " + currFloor++);
					updateTimer();
					Thread.sleep(2000);
					updateJSONObj();
					state = ElevatorState.UP;
					sendStateUpdate();
					// receiveACK();

				}
				currFloor--;
				state = ElevatorState.DOOR_OPEN;
				System.out.println("got to passenger(s) who made the request");
				checkDoorFault();
				state = ElevatorState.DOOR_CLOSED;
				goToDestination(obj);

			} else if (dir == 0) { // already there
				System.out.println("elevator at passenger floor: open doors");
				state = ElevatorState.DOOR_OPEN;
				checkDoorFault();
				state = ElevatorState.DOOR_CLOSED;
				goToDestination(obj);
			} else { // moving down
				for (int i = currFloor; i >= passengerFloor; i--) {
					System.out.println("Elevator: moving to floor " + currFloor--);
					Thread.sleep(2000);
					updateTimer();
					updateJSONObj();
					state = ElevatorState.DOWN;
					sendStateUpdate();
					// receiveACK();
				}
				currFloor++;
				state = ElevatorState.DOOR_OPEN;
				System.out.println("got to passenger(s) who made the request");
				checkDoorFault();
				state = ElevatorState.DOOR_CLOSED;
				goToDestination(obj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * If the timer reaches 0, it means that it the elevator took way too long to
	 * reach the floor and there is an error
	 * 
	 * @throws Exception if the timer has reached 0
	 */
	private void updateTimer() throws Exception {
		timer--;
		if (timer == 0) {
			tFault = true;
			throw new Exception("Fatal floor timing error.. exiting");
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
			timer = timer_time;
			destinationFloor = obj.getInt("destinationFloor"); // destination of passenger
			int goToDestination = currFloor - destinationFloor; // closest floor to passenger
			if (goToDestination < 0) { // moving up to destination floor
				for (int i = currFloor; i < destinationFloor; i++) {

					System.out.println("Elevator: moving to floor " + ++currFloor);
					updateTimer();
					Thread.sleep(2000);

					updateJSONObj();
					state = ElevatorState.UP;
					sendStateUpdate();
					// receiveACK();

				}
				state = ElevatorState.DOOR_OPEN;
				checkDoorFault();
				state = ElevatorState.DOOR_CLOSED;
				state = ElevatorState.IDLE;

			} else {
				for (int i = currFloor; i > destinationFloor; i--) { // moving down to destination
					System.out.println("Elevator: moving to floor " + --currFloor);
					updateTimer();
					Thread.sleep(2000);

					updateJSONObj();
					state = ElevatorState.DOWN;
					sendStateUpdate();
					// receiveACK();

				}
				state = ElevatorState.DOOR_OPEN;
				checkDoorFault();
				state = ElevatorState.DOOR_CLOSED;
				state = ElevatorState.IDLE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
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
			(new Elevator(1, InetAddress.getLocalHost())).receiveAndRespond();
			;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public int getCurrentFloor() {
		return currFloor;
	}

	/**
	 * This method is for testing purposes only... Removes the randomized aspect of
	 * the door fault
	 * 
	 * @param x
	 */
	public void checkDoorFaultTest(int x) {

		if (x >= 6) {
			state = ElevatorState.FIXING_DOORS;
			System.out.println("Door is jamed. Please stand by while fixing ....");
			fault = true;

			try {
				Thread.sleep(3000); // give it time to fix.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Fixed!");
			return;
		}

		fault = false;
	}

	/**
	 * brings the passenger to their destination floor.
	 * This is a method to be used for testing only...
	 * Removes unnecessary calls that interact with other components of the system 
	 * 
	 * @param obj JSON obj containing the request info
	 */
	public void goToDestinationTest(JSONObject obj) {
		int destinationFloor;
		try {
			timer = timer_time;
			destinationFloor = obj.getInt("destinationFloor"); // destination of passenger
			int goToDestination = currFloor - destinationFloor; // closest floor to passenger
			if (goToDestination < 0) { // moving up to destination floor
				for (int i = currFloor; i < destinationFloor; i++) {
					System.out.println("Elevator: moving to floor " + ++currFloor);
					updateTimer();
					Thread.sleep(500);
				}
			} else {
				for (int i = currFloor; i > destinationFloor; i--) { // moving down to destination
					System.out.println("Elevator: moving to floor " + --currFloor);
					updateTimer();
					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/***
	 * getter and setter for fault flag
	 */
	public void setFaultFlag(boolean x) {
		this.fault = x;
	}

	public boolean getFaultFlag() {
		return this.fault;
	}

	/**
	 * Get the flag for the timing fault
	 * 
	 * @return the vale of the timing fault flage.
	 */
	public boolean getTFaultFlag() {
		return tFault;
	}

}
