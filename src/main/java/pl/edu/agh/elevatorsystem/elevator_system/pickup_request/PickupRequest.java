package pl.edu.agh.elevatorsystem.elevator_system.pickup_request;

import pl.edu.agh.elevatorsystem.elevator.Direction;

import static pl.edu.agh.elevatorsystem.util.Constants.MAX_FLOORS;

public class PickupRequest {

    private final int currentFloor;
    private final Direction direction;
    private final int destinationFloor;
    private boolean inElevator;

    public PickupRequest(int currentFloor, Direction direction, int destinationFloor) {
        this(currentFloor, direction, destinationFloor, false);
    }
    public PickupRequest(int currentFloor, Direction direction, int destinationFloor, boolean inElevator) {
        this.currentFloor = currentFloor;
        this.direction = direction;
        this.destinationFloor = destinationFloor;
        this.inElevator = inElevator;
    }

    public boolean validateRequestFloors(int minFloor, int maxFloor) {
        return currentFloor >= minFloor && currentFloor <= maxFloor
                && destinationFloor > minFloor && destinationFloor <= maxFloor;
    }

    public boolean validateRequestDirection() {
        Direction validDirection = Direction.of(destinationFloor - currentFloor);
        return validDirection.equals(direction);
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public boolean isInElevator() {
        return inElevator;
    }

    public void setInElevator(boolean inElevator) {
        this.inElevator = inElevator;
    }
}
