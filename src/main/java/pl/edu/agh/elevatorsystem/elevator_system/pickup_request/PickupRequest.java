package pl.edu.agh.elevatorsystem.elevator_system.pickup_request;

import pl.edu.agh.elevatorsystem.elevator.Direction;

import java.util.Objects;

/**
 * Representation of the pickup request
 */
public class PickupRequest {

    private final int currentFloor;
    private final Direction direction;
    private final int destinationFloor;
    private boolean inElevator;

    public PickupRequest(int currentFloor, Direction direction, int destinationFloor) {
        this(currentFloor, direction, destinationFloor, false);
    }

    /**
     * @param currentFloor      - floor from which the request for the elevator was called
     * @param direction         - direction in which the calling person would want to go
     * @param destinationFloor  - floor that the person calling the elevator would want to be taken to
     * @param inElevator        - tells if the person requesting the elevator is being taken to the desired destination
     *                          or still waiting
     */
    public PickupRequest(int currentFloor, Direction direction, int destinationFloor, boolean inElevator) {
        this.currentFloor = currentFloor;
        this.direction = direction;
        this.destinationFloor = destinationFloor;
        this.inElevator = inElevator;
    }

    public boolean validateRequestFloors(int minFloor, int maxFloor) {
        return currentFloor >= minFloor && currentFloor <= maxFloor
                && destinationFloor >= minFloor && destinationFloor <= maxFloor;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PickupRequest that = (PickupRequest) o;
        return currentFloor == that.currentFloor && destinationFloor == that.destinationFloor
                && inElevator == that.inElevator && direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentFloor, direction, destinationFloor, inElevator);
    }
}
