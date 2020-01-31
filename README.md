# Elevator Control System and Simulator

#### Iteration 1 - Establish Connection Between the Three Subsystems

Purpose of Project: design and implement an elevator control system and simulator

Authors: Defa Hu, Zewen Chen, Henrry Wilson, Mariam Almalki, Ruqaya Malki

##### Contents of this ReadMe
- Iteration Description
- Breakdown of Responsibilities
- Explanation of Files and File Names
- Set-up Instructions
- Test Instructions 
- UML Class Diagram

DESCRIPTION: 
  
          This project is simulating the elevator control system.
          we will have a file acting the user role to request the elevator.
          the Floor will get the request and send it to Scheduler, and Elevator
          keep getting request from Scheduler, then Elevator knows what floor to go.


#### Breakdown of Responsibilities
**Zewen Chen**: Floor class <br>
**Finn Hu** : Elevator class <br>
**Henry Wilson** : Test case <br>
**Ruqaya Almalki**: Scheduler & Buffer <br>
**Mariam Almalki** : Scheduler & Buffer <br>

#### Explanation of File Names
- **Floor**: 
- **Elevator**:
- **Buffer**: The Elevators and Floor are constantly polling the buffer to see if they can send their requests / data. It controls who has access to sending the request / data (only one at a time)
- **Scheduler**: The Scheduler is responsible for constantly polling the buffer to see if there is data for it to send. If there is, it sends it to the appropriate class. 
- **Main**: This class is used to instantiate and start the threads and buffer. 

#### Set-up Instructions
Simply run the Main() class. Each class will output a print statement on the console describing the action it is taking. 

#### Test Instructions 
- [[explanation of how the test case works goes here]]

#### UML Class Diagram
This can also be found as a png file in the project zip file. <br>
![UML](name_of_uml_img.png)

