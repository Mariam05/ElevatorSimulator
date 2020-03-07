//package ElevatorSimulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

enum ElevatorState {
	// enums for identifying elevator states
	IDLE, DOOR_OPEN, MOVING
}

public class Elevator {

	private DatagramPacket sendPacket, receivePacket, subscribePacket, ackPacket;
	private DatagramSocket sendSocket, receiveSocket, subscribeSocket, ackSocket;

	private int id; // to use in the future when there are multiple elevators
	private Scheduler scheduler;
	private int currFloor;
	private ControlDate c;
	private boolean dataIn;
	public ElevatorState state;
	private int updateStatusPort = 1026;

	private InetAddress schedulerAddress;

	private int subscriptionPort = 1035;
	private JSONObject subObj;

	public Elevator(int id, InetAddress schedulerAddress) {

		this.schedulerAddress = schedulerAddress;

		this.id = id;
		this.currFloor = 1;
		this.dataIn = false;
		state = ElevatorState.IDLE;

		subObj = new JSONObject();
		updateJSONObj();

		byte[] subArr = subObj.toString().getBytes();

		try {
			// server is bounded to port 69
			receiveSocket = new DatagramSocket(69);
			subscribeSocket = new DatagramSocket();
			sendSocket = new DatagramSocket();
			ackSocket = new DatagramSocket();

			subscribePacket = new DatagramPacket(subArr, subArr.length, schedulerAddress, subscriptionPort);
			subscribeSocket.send(subscribePacket);
			subscribeSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		receiveAndRespond();
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

	private void sendStateUpdate() {

		try {

			byte[] subArr = subObj.toString().getBytes();
			DatagramPacket updateStatePacket = new DatagramPacket(subArr, subArr.length, schedulerAddress,
					updateStatusPort);
			DatagramSocket updateStateSocket = new DatagramSocket();
			updateStateSocket.send(updateStatePacket);
			updateStateSocket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Move the elevator, and send current state to scheduler as it moves.
	 * 
	 * @param obj the JSONObject cmd received by the scheduler
	 */
	private void moveElevator(JSONObject obj) {
		try {
			int passengerFloor = obj.getInt("floor"); // passenger is at
			int dir = currFloor - passengerFloor; // closest floor to passenger
			if (dir < 0) { // moving up to passenger
				for (int i = currFloor; i <= passengerFloor; i++) {
					System.out.println("Elevator: moving to floor " + currFloor++);
					Thread.sleep(2000);
					updateJSONObj();
					sendStateUpdate();
					//receiveACK();
				}
				currFloor--;
				System.out.println("got to passenger...now moving to destination:");
				goToDestination(obj);

			} else if (dir == 0) {
				System.out.println("elevator at passenger floor: open doors");
				goToDestination(obj);
			} else {
				for (int i = currFloor; i >= passengerFloor; i--) {
					System.out.println("Elevator: moving to floor " + currFloor--);
					Thread.sleep(2000);
					updateJSONObj();
					sendStateUpdate();
				}
				currFloor++;
				System.out.println("got to passenger...now moving to destination:");
				goToDestination(obj);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void goToDestination(JSONObject obj) {
		int destinationFloor;
		try {
			destinationFloor = obj.getInt("destinationFloor");
			int goToDestination = currFloor - destinationFloor; // closest floor to passenger
			if (goToDestination < 0) { // moving up to destination floor
				for (int i = currFloor; i < destinationFloor; i++) {

					System.out.println("Elevator: moving to floor " + ++currFloor);
					Thread.sleep(2000);
					updateJSONObj();
					sendStateUpdate();
				}

			} else {
				for (int i = currFloor; i > destinationFloor; i--) {
					System.out.println("Elevator: moving to floor " + --currFloor);
					Thread.sleep(2000);
					updateJSONObj();
					sendStateUpdate();
				}

			}
		} catch (JSONException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // passenger is at

	}

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

				// execute command
				moveElevator(obj);

				// receiving the ack
//				
//				 byte replyData[] = new byte[100]; receivePacket = new
//				  DatagramPacket(replyData, replyData.length);
//				  System.out.println("Server: Waiting for ACK...\n");
//				  receiveSocket.receive(receivePacket); txt = new String(replyData, 0,
//				  receivePacket.getLength()); JSONObject ack = new JSONObject(txt);
//				  System.out.println("Server: ACK received");
//				  System.out.println("Contents (String): " + ack.toString());
//				  System.out.println("Contents (Bytes): " + receivePacket.getData() + "\n");

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
	 * sends an ack message to the one who sent the data to be forwarded (accepting
	 * the data/the reply)
	 * 
	 * @param port         to be sent to
	 * @param sendToSource who the ack should be sent to, the client or server
	 */
	private void receiveACK() {

		
		byte replyData[] = new byte[100];
		ackPacket = new DatagramPacket(replyData, replyData.length);
		System.out.println("Server: Waiting for ACK...\n");
		try {
			ackSocket.receive(ackPacket);
			String txt = new String(replyData, 0, ackPacket.getLength());
			JSONObject ack = new JSONObject(txt);
			System.out.println("elevator: ACK received from scheduler");
//			System.out.println("Contents (String): " + ack.toString());
//			System.out.println("Contents (Bytes): " + receivePacket.getData() + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	/**
	 * runs the Server forever, or until and exception occurs
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// run the program
		try {
			new Elevator(1, InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void receiveFloorInfo(ControlDate c) {
		this.c = c;
		this.dataIn = true;
		state = ElevatorState.MOVING;
		processState();

	}

	public synchronized void pressDoorOpenButton() {
		state = ElevatorState.DOOR_OPEN;
		processState();
	}

	public synchronized void pressDoorCloseButton() {
		state = ElevatorState.IDLE;
		processState();
	}

	public ControlDate getDate() {
		return this.c;
	}

	/**
	 * adding state to the elevator open button pressed
	 * initial<--------------------------------------------->doorOpen close button
	 * pressed || after 10 seconds ^ | | | | | | | | | data recieve press floor | |
	 * | | | | | | | moving-----------------------------------------------+ reach
	 * desFloor
	 */

	// method to process action to each states
	private void processState() {
		switch (state) {
		case IDLE:
			System.out.println("elevator: door closed and avliable");
			break;
		case DOOR_OPEN:
			System.out.println("elevator: door open");
			break;
		case MOVING:
			System.out.format("elevator: moving from floor %d to %d\n", c.getFloor(), c.getDestinationFloor());
			System.out.format("elevator: reach floor %d\n", c.getDestinationFloor());
			break;
		}
	}
}
