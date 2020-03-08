# Elevator Control System and Simulator

#### Iteration 3: Implementing UDP/RPC

Purpose of Project: design and implement an elevator control system and simulator

Authors: Defa Hu, Zewen Chen, Henry Wilson, Mariam Almalki, Ruqaya Almalki

#### Contents of this ReadMe
- Iteration Description
- Breakdown of Responsibilities
- Explanation of Files and File Names
- Set-up Instructions
- Test Instructions 
- reflection of concurrency
- UML Class Diagram
- UML Sequence Diagram
- State Machine Diagrams


DESCRIPTION: 
  
          This project is simulating the elevator control system.
          we will have a file acting the user role to request the elevator.
          the Floor will get the request and send it to Scheduler. The Scheduler will decided which elevator to forward the request to.
          The Elevators will keep getting request from the Scheduler, and move to the correct floors.


### Breakdown of Responsibilities
**Zewen Chen**: Scheduler logic <br>
**Defa Hu** : Test Cases <br>
**Henry Wilson** : UMLs Sequence/Class Diagrams <br>
**Ruqaya Almalki**: RPC, scheduler logic, README <br>
**Mariam Almalki** : RPC, scheduler logic, Scheduler Test <br>

### Explanation of File Names
- **Floor**: reads info from a file, sends the requests to the Scheduler, waits for an ACK before sending another packet
- **FloorTest**: JUnit test for the above class.
- **Elevator**: Subscribes to a Scheduler. Receives requests from the Scheduler. Informs Scheduler of current postion/state.
- **ElevatorTest**: JUnit test for the above class
- **Scheduler**: Receives requests from Floor. Processes the request, deciding which elevator to send the request to. Stores info of all the request and subscribed elevators 
- **SchedulerTest**: JUnit test for the above class.

### Set-up Instructions
Run the Scheduler class, the Elevator class(es) , and then the Floor class.
Note: 
1.	this project uses JSON, you may need to import the jar into the build path. To do this right-click on the project name -> Build Path -> Configure Build Path -> Add External Jar, then select the "java-json" jar file in the project file. 
2.	When running on different machines, the ip addresses fed into the scheduler, the floor, and the elevator need to be changed in order to reflect the ip addresses of the machines used.

### Test Instructions 
Test cases are standard JUnit 5 Tests. Be sure to run each test individually to ensure that an address binding exception does not occur. 

### Reflection of Concurrency between iteration 2 and 3
In iteration 2 we used many boolean variables to ensure critical sections were encapsulated and only used 3 threads one for each entity. Whereas in iteration 3, we used various threads running concurrently, allowing the transfer of information (sending and receiving) to be more efficient. Increasing the amount of threads, and allocating each thread to implement a function allows the program to be running in such a way that it doesn't have to wait for a long time. For example, the scheduler could be registering new elevators and receiving data from the floor at the same time. Various threads could be sending/receiving information at the same time without conflict. However, because of this, we synchronized the HashMap containing the elevator info and the queue containing the passenger requests. This is because both of these data structures are accessed by different threads, therefore making them a critical section within the code that needs to have regulated access.

### UML Class Diagram
This can also be found as a png file in the project zip file. <br>
![UML](iteration3_class.png)

### UML Sequence Diagram
This can also be found as a png file in the project zip file. <br>
![SequenceDiagram](Iteration3_Sequence.png)

### State Machine Diagrams
These can also be found as a png file in the project zip file. <br>
![UML](SchedulerStateDiagram.png) 
<br>
![UML](ElevatorStateDiagram.png)


