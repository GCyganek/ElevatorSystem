package pl.edu.agh.elevatorsystem.elevator;

/**
 * Represents the status of the elevator
 */
public class ElevatorStatus {

    private final int elevatorId;
    private int currentFloor;
    private int destinationFloor;

    /**
     * @param elevatorId        - elevatorId assigned to the elevator
     * @param currentFloor      - floor that the elevator is now on
     * @param destinationFloor  - floor that the elevator is moving to (-1 == the elevator is not moving)
     */
    public ElevatorStatus(int elevatorId, int currentFloor, int destinationFloor) {
        this.elevatorId = elevatorId;
        this.currentFloor = currentFloor;
        this.destinationFloor = destinationFloor;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    /**
     * Updates currentFloor field after elevator move
     * @param direction - direction in which the elevator moved
     */
    public void movedOneFloor(Direction direction) {
        currentFloor += direction.intValue();
    }

    /**
     * @return status of the elevator as a String
     */
    @Override
    public String toString() {
        String destinationFloorString =
                (destinationFloor == Elevator.IDLE) ? "elevator is currently idle" : String.valueOf(destinationFloor);

        return "\tElevator ID: " + elevatorId + " ||| Current floor: " + currentFloor + " ||| Destination floor: " +
                destinationFloorString;
    }
}
