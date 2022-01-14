package pl.edu.agh.elevatorsystem.elevator_system;

import pl.edu.agh.elevatorsystem.elevator.Direction;
import pl.edu.agh.elevatorsystem.elevator.Elevator;
import pl.edu.agh.elevatorsystem.elevator.ElevatorStatus;
import pl.edu.agh.elevatorsystem.elevator_system.pickup_request.PickupRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.edu.agh.elevatorsystem.util.Constants.*;

/**
 * Implementation of the IElevatorSystem interface
 *
 * On pickup request:
 *
 * Find the closest elevator that is idle or is moving in the same direction as the request direction and the floor from
 * which the request was called is on the elevator's way. Otherwise save the request to the pendingRequests list and
 * check if there is any elevator that could handle the request after every elevator system simulation step.
 *
 */
public class MyElevatorSystem implements IElevatorSystem {

    private final int numberOfElevators;
    private final List<Elevator> elevators;
    private final List<PickupRequest> pendingPickups = new ArrayList<>();

    /**
     * Constructor checks the numberOfElevators provided and saves created elevators to the elevators list
     * @param numberOfElevators - number of elevators that the elevator system is going to have [minimum 1, maximum 16]
     */
    public MyElevatorSystem(int numberOfElevators) {
        this.numberOfElevators = assignNumberOfElevators(numberOfElevators);
        elevators = createElevators();
    }

    private int assignNumberOfElevators(int numberOfElevators) {
        if (numberOfElevators > MAX_ELEVATORS) {
            System.out.println("This system is for 16 elevators maximum. Creating 16 elevators...\n");
            numberOfElevators = MAX_ELEVATORS;
        } else if (numberOfElevators < MIN_ELEVATORS) {
            System.out.println("Minimum 1 elevator must be created for this system. Creating 1 elevator...\n");
            numberOfElevators = MIN_ELEVATORS;
        }

        return numberOfElevators;
    }

    private List<Elevator> createElevators() {
        return IntStream.range(0, numberOfElevators)
                .mapToObj(Elevator::new)
                .collect(Collectors.toList());
    }

    /**
     * Checks if received parameters can be used to create valid PickupRequest object and then checks if there is
     * any elevator that can handle the request using the assignElevatorToRequestIfPossible method. If there is not any
     * then the pickup request is added to the pendingRequests list.
     *
     * @param currentFloor      - floor from which the request for the elevator was called
     * @param direction         - direction in which the calling person would want to go (>0 - up, <0 - down)
     * @param destinationFloor  - floor that the person calling the elevator would want to be taken to
     */
    @Override
    public void pickup(int currentFloor, int direction, int destinationFloor) {
        if (!sameSign(destinationFloor - currentFloor, direction) || (destinationFloor == currentFloor)) {
            System.out.println("Invalid pickup request");
            return;
        }

        PickupRequest pickupRequest = new PickupRequest(currentFloor, Direction.of(direction), destinationFloor);
        if (!assignElevatorToRequestIfPossible(pickupRequest)) {
            pendingPickups.add(pickupRequest);
        }
    }

    /**
     * Uses findBestElevator method to get the best elevator for the provided pickupRequest. Received elevator is used
     * to handle the pickup request and then has direction and destination floor updated.
     *
     * @param pickupRequest - pickup request that needs to be handled
     * @return true if there was any elevator being able to handle the pickup request. Returns false otherwise.
     */
    private boolean assignElevatorToRequestIfPossible(PickupRequest pickupRequest) {
        Optional<Elevator> bestElevator = findBestElevator(pickupRequest);

        bestElevator.ifPresent(
                elevator -> {
                    elevator.handlePickupRequest(pickupRequest);
                    updateElevatorDirection(elevator);
                    updateElevatorDestinationFloor(elevator);
                }
        );

        return bestElevator.isPresent();
    }

    /**
     * Is used to obtain the best elevator for the given pickup request
     *
     * @param pickupRequest - pickup request that needs to be handled
     * @return Optional.empty if no elevator can handle the request for now. Otherwise returns the Optional.of(Elevator)
     * that contains the elevator that:
     * 1. has the shortest distance to the floor from which the request was called
     * 2. is idle / has direction matching the request direction
     * 3. has the floor from which the request was called on the way (does not need to change the direction to reach
     * that floor)
     */
    private Optional<Elevator> findBestElevator(PickupRequest pickupRequest) {
        Optional<Elevator> bestElevator = Optional.empty();
        int shortestDistance = 0;

        int pickupCurrentFloor = pickupRequest.getCurrentFloor();
        Direction pickupDirection = pickupRequest.getDirection();

        for (Elevator elevator : elevators) {
            if (canPickupRequest(elevator, pickupDirection, pickupCurrentFloor)) {
                int elevatorCurrentFloor = elevator.getElevatorStatus().getCurrentFloor();
                int currentDistance = Math.abs(elevatorCurrentFloor - pickupCurrentFloor);

                if (bestElevator.isEmpty()) {
                    bestElevator = Optional.of(elevator);
                    shortestDistance = Math.abs(elevatorCurrentFloor - pickupCurrentFloor);
                } else {
                    if (currentDistance < shortestDistance) {
                        shortestDistance = currentDistance;
                        bestElevator = Optional.of(elevator);
                    }
                }
            }
        }

        return bestElevator;
    }

    /**
     * Checks if elevator can handle the pickup request.
     *
     * Elevator can't handle the pickup request if:
     *      1. Is moving in opposite direction to request direction
     *      2. Floor from which the request was called is not on the way of the elevator
     *      3. Elevator is moving in one direction to pickup a person and will change its direction once
     *      that person enters the elevator
     *
     * @param elevator           - elevator to be checked
     * @param direction          - direction of the pickup request
     * @param pickupCurrentFloor - floor from which the pickup request was called
     * @return true if the elevator can handle the request. Otherwise returns false
     */
    private boolean canPickupRequest(Elevator elevator, Direction direction, int pickupCurrentFloor) {
        Direction elevatorDirection = elevator.getDirection();
        if (elevatorDirection.equals(Direction.IDLE)) return true;

        if (!elevator.getPickupRequests().isEmpty()) {
            PickupRequest pickupRequest = elevator.getPickupRequests().get(0);
            if (!pickupRequest.isInElevator()
                    && pickupRequest.getDirection().equals(elevator.getDirection().opposite())) {
                return false;
            }
        }

        boolean directionMatches = elevator.getDirection().equals(direction);
        return directionMatches
                && givenFloorIsOnTheElevatorsWay(elevator.getElevatorStatus(), pickupCurrentFloor, elevatorDirection);
    }

    /**
     * @param elevatorStatus    - elevatorStatus of the elevator being checked
     * @param floor             - floor to be checked if is on the elevator's way
     * @param elevatorDirection - elevator's current moving direction
     * @return true if the given floor is on the elevator's way. Otherwise returns false
     */
    private boolean givenFloorIsOnTheElevatorsWay(ElevatorStatus elevatorStatus, int floor, Direction elevatorDirection) {
        return Direction.of(floor - elevatorStatus.getCurrentFloor()).equals(elevatorDirection);
    }

    /**
     * Performs elevator system simulation step and then checks if any elevator after the simulation step can now
     * handle any of the pending requests
     */
    @Override
    public void step() {
        elevators.forEach(elevator -> {
            elevator.makeStep();
            updateElevatorDirection(elevator);
            updateElevatorDestinationFloor(elevator);
        });

        pendingPickups.removeIf(this::assignElevatorToRequestIfPossible);
    }

    /**
     * Updates the status of the chosen elevator
     * @param elevatorId        - elevatorId of the elevator that should be updated
     * @param currentFloor      - new currentFloor to be set for the chosen elevator
     * @param destinationFloor  - new destinationFloor to be set for the chosen elevator
     */
    @Override
    public void update(int elevatorId, int currentFloor, int destinationFloor) {
        if (elevatorId >= elevators.size()) {
            System.out.println("This elevator system has only " + elevators.size() + " elevators installed");
            return;
        }

        elevators.get(elevatorId).updateStatus(currentFloor, destinationFloor);
    }

    /**
     * Displays status of every elevator
     */
    @Override
    public void status() {
        System.out.println("ElevatorSystem status:");
        elevators.forEach(elevator -> System.out.println(elevator.getElevatorStatus()));
    }

    /**
     * Updates the elevator's moving direction
     *
     * If elevator's pickupRequests list is empty -> set moving direction of the elevator to IDLE
     *
     * Otherwise -> take the first request from the pickupRequests list and:
     *
     *      1. If the request is being handled (isInElevator == true) -> set the elevator's moving direction to request
     *      direction
     *
     *      2. If the request is not yet being handled (isInElevator == false) -> set the elevator's moving direction
     *      depending on the current elevator's floor and the floor that the request was called from
     *
     * @param elevator - elevator to be updated
     */
    private void updateElevatorDirection(Elevator elevator) {
        List<PickupRequest> pickupRequests = elevator.getPickupRequests();

        if (pickupRequests.isEmpty()) {
            elevator.setDirection(Direction.IDLE);
            return;
        }

        Direction elevatorDirection;
        ElevatorStatus elevatorStatus = elevator.getElevatorStatus();
        PickupRequest pickupRequest = pickupRequests.get(0);

        if (pickupRequest.isInElevator()) {
            elevatorDirection = pickupRequest.getDirection();
        } else {
            int pickupRequestCurrentFloor = pickupRequest.getCurrentFloor();
            int elevatorCurrentFloor = elevatorStatus.getCurrentFloor();

            if (pickupRequestCurrentFloor < elevatorCurrentFloor) {
                elevatorDirection = Direction.DOWN;
            } else {
                elevatorDirection = Direction.UP;
            }
        }

        elevator.setDirection(elevatorDirection);
    }

    /**
     * Updates the elevator's destination floor
     *
     * If pickupRequests elevator's list is empty -> set destination floor to Elevator.IDLE (-1)
     *
     * Otherwise:
     *
     * If elevator's moving up ->
     *
     *      1. If there are requests in the UP direction -> get max destinationFloor from all pickup requests that
     *      are being handled (isInElevator == true) and have direction == UP, get max currentFloor from all pickup requests
     *      that are not yet being handled (isInElevator == false) and have direction == UP. Then set the destinationFloor
     *      of the elevator to the maximum of these values.
     *
     *      2. If no requests in the UP direction -> set the destinationFloor of the elevator to the currentFloor
     *      of the pickup request in the pickupRequests list (must be only one and with direction == DOWN)
     *
     * If elevator's moving down ->
     *
     *      1. If there are requests in the DOWN direction -> get min destinationFloor from all pickup requests that
     *      are being handled (isInElevator == true) and have direction == DOWN, get min currentFloor from all pickup requests
     *      that are not yet being handled (isInElevator == false) and have direction == DOWN. Then set the destinationFloor
     *      of the elevator to the minimum of these values.
     *
     *      2. If no requests in the DOWN direction -> set the destinationFloor of the elevator to the currentFloor
     *      of the pickup request in the pickupRequests list (must be only one and with direction == UP)
     *
     * @param elevator - elevator to be updated
     */
    private void updateElevatorDestinationFloor(Elevator elevator) {
        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        ElevatorStatus elevatorStatus = elevator.getElevatorStatus();

        if (pickupRequests.isEmpty()) {
            elevatorStatus.setDestinationFloor(Elevator.IDLE);
            return;
        }

        Direction elevatorDirection = elevator.getDirection();

        if (elevatorDirection.equals(Direction.UP)) {
            if (noRequestsInGivenDirection(Direction.UP, pickupRequests)) {
                elevatorStatus.setDestinationFloor(
                        pickupRequests.get(0).getCurrentFloor()
                );
                return;
            }

            int destinationFloorCandidate1 = pickupRequests.stream()
                    .filter(pickupRequest -> pickupRequest.isInElevator()
                            && pickupRequest.getDirection().equals(Direction.UP))
                    .map(PickupRequest::getDestinationFloor)
                    .max(Integer::compare)
                    .orElse(-1);

            int destinationFloorCandidate2 = pickupRequests.stream()
                    .filter(pickupRequest -> !pickupRequest.isInElevator()
                            && pickupRequest.getDirection().equals(Direction.UP))
                    .map(PickupRequest::getCurrentFloor)
                    .max(Integer::compare)
                    .orElse(-1);

            int destinationFloor = Math.max(destinationFloorCandidate1, destinationFloorCandidate2);

            elevatorStatus.setDestinationFloor(destinationFloor);
        } else {
            if (noRequestsInGivenDirection(Direction.DOWN, pickupRequests)) {
                elevatorStatus.setDestinationFloor(
                        pickupRequests.get(0).getCurrentFloor()
                );
                return;
            }

            int destinationFloorCandidate1 = pickupRequests.stream()
                    .filter(pickupRequest -> pickupRequest.isInElevator()
                            && pickupRequest.getDirection().equals(Direction.DOWN))
                    .map(PickupRequest::getDestinationFloor)
                    .min(Integer::compare)
                    .orElse(MAX_FLOORS + 1);

            int destinationFloorCandidate2 = pickupRequests.stream()
                    .filter(pickupRequest -> !pickupRequest.isInElevator()
                            && pickupRequest.getDirection().equals(Direction.DOWN))
                    .map(PickupRequest::getCurrentFloor)
                    .min(Integer::compare)
                    .orElse(MAX_FLOORS + 1);

            int destinationFloor = Math.min(destinationFloorCandidate1, destinationFloorCandidate2);

            elevatorStatus.setDestinationFloor(destinationFloor);
        }
    }

    /**
     * @param direction - given direction
     * @return true if the given direction equals any of the pickup requests direction. Returns false otherwise
     */
    private boolean noRequestsInGivenDirection(Direction direction, List<PickupRequest> pickupRequests) {
        return pickupRequests
                .stream()
                .noneMatch(pickupRequest -> pickupRequest.getDirection().equals(direction));
    }

    private boolean sameSign(int x, int y) {
        return ((x < 0) == (y < 0));
    }

    // for testing only
    public Elevator getElevatorById(int elevatorId) {
        return elevators.get(elevatorId);
    }

    // for testing only
    public List<PickupRequest> getPendingPickups() {
        return pendingPickups;
    }
}
