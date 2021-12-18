package pl.edu.agh.elevatorsystem.elevator;

public class ElevatorStatus {

    private final int elevatorId;
    private int currentFloor;
    private int destinationFloor;

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

    @Override
    public String toString() {
        String destinationFloorString =
                (destinationFloor == Elevator.IDLE) ? "elevator is currently idle" : String.valueOf(destinationFloor);

        return "\tElevator ID: " + elevatorId + " ||| Current floor: " + currentFloor + " ||| Destination floor: " +
                destinationFloorString;
    }
}
