package pl.edu.agh.elevatorsystem.elevator_system;

public interface IElevatorSystem {
    /**
     * Handles the pickup request
     * @param currentFloor      - floor from which the request for the elevator was called
     * @param direction         - direction in which the calling person would want to go (>0 - up, <0 - down)
     * @param destinationFloor  - floor that the person calling the elevator would want to be taken to
     */
    void pickup(int currentFloor, int direction, int destinationFloor);

    /**
     * Updates the state of the given elevator
     * @param elevatorId        - elevatorId of the elevator that should be updated
     * @param currentFloor      - new currentFloor to be set for the chosen elevator
     * @param destinationFloor  - new destinationFloor to be set for the chosen elevator
     */
    void update(int elevatorId, int currentFloor, int destinationFloor);

    /**
     * Performs the elevator system simulation step
     */
    void step();

    /**
     * Displays current state of the elevator system (elevators list and their status)
     */
    void status();
}
