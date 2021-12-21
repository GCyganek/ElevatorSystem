package pl.edu.agh.elevatorsystem.elevator;

import pl.edu.agh.elevatorsystem.elevator_system.pickup_request.PickupRequest;

import java.util.ArrayList;
import java.util.List;

import static pl.edu.agh.elevatorsystem.util.Constants.MAX_FLOORS;

/**
 * Representation of the elevator used in the elevator system
 * It is elevator system's responsibility to update elevator's status and direction after every step and pickup request
 */
public class Elevator {

    public static final int IDLE = -1;

    private final ElevatorStatus elevatorStatus;
    // elevator is idle after being created
    private Direction direction = Direction.IDLE;
    private final List<PickupRequest> pickupRequests = new ArrayList<>();

    public Elevator(int elevatorId) {
        this(elevatorId, 0);
    }

    /**
     * Assigns the new ElevatorStatus object to the elevatorStatus field with provided parameters and destination floor
     * set to IDLE (-1) as the elevator is not moving after being created (this.direction == Direction.IDLE)
     *
     * @param elevatorId   - elevatorId assigned to the created elevator
     * @param currentFloor - floor on which the elevator is now
     */
    public Elevator(int elevatorId, int currentFloor) {
        this.elevatorStatus = new ElevatorStatus(elevatorId, currentFloor, IDLE);
    }

    /**
     * Performs elevator step on elevator system simulation step
     *
     * If elevator is idle -> does nothing
     *
     * If elevator is moving -> moves one floor in the current direction, checks if there are any fulfilled pickup
     * requests (handleFulfilledPickupRequests method), checks if there are any requests on the current floor that the
     * elevator needs to handle (handlePendingPickupRequests method) and then updates direction and destination floor
     * of the elevator
     */
    public void makeStep() {
        if (direction.equals(Direction.IDLE)) return;
        elevatorStatus.movedOneFloor(direction);
        handleFulfilledPickupRequests();
        handlePendingPickupRequests();
    }

    /**
     * Removes pickup request from pickupRequests list if the person that called that request is in the elevator and
     * the destination floor of the request equals the current floor that the elevator is on
     */
    private void handleFulfilledPickupRequests() {
        int currentElevatorFloor = elevatorStatus.getCurrentFloor();

        pickupRequests
                .removeIf(pickupRequest -> pickupRequest.getDestinationFloor() == currentElevatorFloor
                                            && pickupRequest.isInElevator());
    }

    /**
     * Checks pickupRequests list and takes people that requested the pickup from the current floor
     * that the elevator is on
     */
    private void handlePendingPickupRequests() {
        int currentElevatorFloor = elevatorStatus.getCurrentFloor();

        pickupRequests
                .forEach(pickupRequest -> {
                    if (!pickupRequest.isInElevator() && pickupRequest.getCurrentFloor() == currentElevatorFloor) {
                        pickupRequest.setInElevator(true);
                    }
                });
    }

    /**
     * Validates update request and then:
     *
     * If the elevator is currently moving and handling pickup requests -> elevator status cannot be updated
     * Otherwise -> updates the status of this elevator using the provided parameters
     *
     * @param currentFloor      - floor that should be set to the currentFloor field of the elevatorStatus
     * @param destinationFloor  - floor that should be set to the destinationFloor field of the elevatorStatus
     */
    public void updateStatus(int currentFloor, int destinationFloor) {
        if (currentFloor >= MAX_FLOORS || destinationFloor >= MAX_FLOORS || currentFloor < 0 || destinationFloor < 0) {
            System.out.println("Invalid update request: currentFloor and destinationFloor must be in range [0, MAX_FLOOR - 1]");
            return;
        }

        if (!pickupRequests.isEmpty()) {
            System.out.println("Can't update the elevator that is currently occupied!");
            return;
        }

        elevatorStatus.setCurrentFloor(currentFloor);

        if (currentFloor != destinationFloor) {
            elevatorStatus.setDestinationFloor(destinationFloor);
            direction = evaluateCurrentDirection();
            pickupRequests.add(new PickupRequest(currentFloor, direction, destinationFloor, true));
        }
    }

    /**
     * @return current direction in which the elevator is moving
     */
    private Direction evaluateCurrentDirection() {
        if (elevatorStatus.getDestinationFloor() == IDLE) {
            return Direction.IDLE;
        }
        return Direction.of(elevatorStatus.getDestinationFloor() - elevatorStatus.getCurrentFloor());
    }

    /**
     * Validates pickup request, then adds it to the pickupRequests list and checks if request was called from the same
     * floor that the elevator is on -> if true, then take the person calling the request
     *
     * @param pickupRequest - pickup request to be handled by this elevator
     */
    public void handlePickupRequest(PickupRequest pickupRequest) {
        if (!pickupRequest.validateRequestFloors(0, MAX_FLOORS - 1) || !pickupRequest.validateRequestDirection()) {
            System.out.println("Invalid pickup request");
            return;
        }

        pickupRequests.add(pickupRequest);

        if (pickupRequest.getCurrentFloor() == elevatorStatus.getCurrentFloor()) {
            pickupRequest.setInElevator(true);
        }
    }

    public ElevatorStatus getElevatorStatus() {
        return elevatorStatus;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<PickupRequest> getPickupRequests() {
        return pickupRequests;
    }
}
