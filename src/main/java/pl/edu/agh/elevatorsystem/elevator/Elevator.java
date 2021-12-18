package pl.edu.agh.elevatorsystem.elevator;

import pl.edu.agh.elevatorsystem.elevator_system.pickup_request.PickupRequest;

import java.util.ArrayList;
import java.util.List;

import static pl.edu.agh.elevatorsystem.util.Constants.MAX_FLOORS;

public class Elevator {

    public static final int IDLE = -1;

    private final ElevatorStatus elevatorStatus;
    private Direction direction = Direction.IDLE;
    private final List<PickupRequest> pickupRequests = new ArrayList<>();

    public Elevator(int elevatorId) {
        this(elevatorId, 0);
    }

    public Elevator(int elevatorId, int currentFloor) {
        this.elevatorStatus = new ElevatorStatus(elevatorId, currentFloor, IDLE);
    }

    public void makeStep() {
        if (direction.equals(Direction.IDLE)) return;
        elevatorStatus.movedOneFloor(direction);
        handleFulfilledPickupRequests();
        handlePendingPickupRequests();
        updateDirection();
        updateDestinationFloor();
    }

    private void handleFulfilledPickupRequests() {
        int currentElevatorFloor = elevatorStatus.getCurrentFloor();

        pickupRequests
                .removeIf(pickupRequest -> pickupRequest.getDestinationFloor() == currentElevatorFloor
                                            && pickupRequest.isInElevator());
    }

    private void handlePendingPickupRequests() {
        int currentElevatorFloor = elevatorStatus.getCurrentFloor();

        pickupRequests
                .forEach(pickupRequest -> {
                    if (!pickupRequest.isInElevator() && pickupRequest.getCurrentFloor() == currentElevatorFloor) {
                        pickupRequest.setInElevator(true);
                    }
                });
    }

    public void updateStatus(int currentFloor, int destinationFloor) {
        if (!pickupRequests.isEmpty()) {
            System.out.println("Can't update the elevator that is currently occupied!");
            return;
        }

        elevatorStatus.setCurrentFloor(currentFloor);
        elevatorStatus.setDestinationFloor(destinationFloor);
        direction = evaluateCurrentDirection();
        pickupRequests.add(new PickupRequest(currentFloor, direction, destinationFloor, true));
    }

    private Direction evaluateCurrentDirection() {
        return Direction.of(elevatorStatus.getDestinationFloor() - elevatorStatus.getCurrentFloor());
    }

    public boolean isIdleOrHasMatchingDirection(Direction direction) {
        return this.direction.equals(Direction.IDLE) || this.direction.equals(direction);
    }

    public void handlePickupRequest(PickupRequest pickupRequest) {
        pickupRequests.add(pickupRequest);

        if (pickupRequest.getCurrentFloor() == elevatorStatus.getCurrentFloor()) {
            pickupRequest.setInElevator(true);
        }

        if (pickupRequests.size() == 1) {
            updateDirection();
        }

        updateDestinationFloor();
    }

    public void updateDirection() {
        if (pickupRequests.isEmpty()) {
            direction = Direction.IDLE;
            return;
        }

        PickupRequest pickupRequest = pickupRequests.get(0);

        if (pickupRequest.isInElevator()) {
            direction = pickupRequest.getDirection();
        } else {
            int pickupRequestCurrentFloor = pickupRequest.getCurrentFloor();
            int elevatorCurrentFloor = elevatorStatus.getCurrentFloor();

            if (pickupRequestCurrentFloor < elevatorCurrentFloor) {
                direction = Direction.DOWN;
            } else {
                direction = Direction.UP;
            }
        }
    }

    public void updateDestinationFloor() {
        if (pickupRequests.isEmpty()) {
            elevatorStatus.setDestinationFloor(IDLE);
            return;
        }

        if (direction.equals(Direction.UP)) {
            int destinationFloorCandidate1 = pickupRequests.stream()
                    .filter(PickupRequest::isInElevator)
                    .map(PickupRequest::getDestinationFloor)
                    .max(Integer::compare)
                    .orElse(-1);

            int destinationFloorCandidate2 = pickupRequests.stream()
                    .filter(pickupRequest -> !pickupRequest.isInElevator())
                    .map(PickupRequest::getCurrentFloor)
                    .max(Integer::compare)
                    .orElse(-1);

            int destinationFloor = Math.max(destinationFloorCandidate1, destinationFloorCandidate2);

            elevatorStatus.setDestinationFloor(destinationFloor);
        } else {
            int destinationFloorCandidate1 = pickupRequests.stream()
                    .filter(PickupRequest::isInElevator)
                    .map(PickupRequest::getDestinationFloor)
                    .min(Integer::compare)
                    .orElse(MAX_FLOORS + 1);

            int destinationFloorCandidate2 = pickupRequests.stream()
                    .filter(pickupRequest -> !pickupRequest.isInElevator())
                    .map(PickupRequest::getCurrentFloor)
                    .min(Integer::compare)
                    .orElse(MAX_FLOORS + 1);

            int destinationFloor = Math.min(destinationFloorCandidate1, destinationFloorCandidate2);

            elevatorStatus.setDestinationFloor(destinationFloor);
        }
    }

    public ElevatorStatus getElevatorStatus() {
        return elevatorStatus;
    }

    public Direction getDirection() {
        return direction;
    }
}
