Elevator System
====================================

This repository contains Java implementation of the elevator system that can handle multiple pickup 
requests from different floors. Elevator system can be tested using CLI simulation with commands
explained below.

Running and testing the project
------------------------------------

In project directory launch gradle and use:

- `gradle build` to build the project
  

- `gradle run` to run the application or `gradle run --console=plain` (drops execution status)
  

- `gradle cleanTask task` to run tests 


Simulation commands
------------------------------------

- `help` - shows commands list
  

- `pickup (currentFloor) (direction) (destinationFloor)` - sends request for the elevator to the elevator system
  

- `update (elevatorId) (currentFloor) (destinationFloor)` - immediately updates status of the elevator with the chosen elevatorId 
  

- `step` -> performs one step of the simulation
              
  
- `status` -> shows list of elevators with their status
           
     
- `quit` -> ends the simulation


Elevator system implementation
------------------------------------

- Elevator system implemented in MyElevatorSystem class for the pickup request:

    - finds the closest elevator that is being idle or is moving in the same direction as the person that requested 
      the pickup is willing to. Also, that elevator needs to have the floor from which the request was called on its way
      
    - if there is no such elevator, the request is queued up in the *pendingRequests* list. After each simulation
    step there is a check if any of the queued requests can be now handled