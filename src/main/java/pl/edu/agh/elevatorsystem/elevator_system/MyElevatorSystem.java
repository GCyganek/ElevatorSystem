package pl.edu.agh.elevatorsystem.elevator_system;

import pl.edu.agh.elevatorsystem.elevator.Direction;
import pl.edu.agh.elevatorsystem.elevator.Elevator;
import pl.edu.agh.elevatorsystem.elevator_system.pickup_request.PickupRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.edu.agh.elevatorsystem.util.Constants.MAX_ELEVATORS;
import static pl.edu.agh.elevatorsystem.util.Constants.MIN_ELEVATORS;

public class MyElevatorSystem implements IElevatorSystem {

    int numberOfElevators;
    private final List<Elevator> elevators;
    private final List<PickupRequest> pendingPickups = new ArrayList<>();

    public MyElevatorSystem(int numberOfElevators) {
        assignNumberOfElevators(numberOfElevators);
        elevators = createElevators();
    }

    private void assignNumberOfElevators(int numberOfElevators) {
        if (numberOfElevators < MIN_ELEVATORS) {
            System.out.println("This system is for 16 elevators maximum. Creating 16 elevators...");
            numberOfElevators = MIN_ELEVATORS;
        } else if (numberOfElevators > MAX_ELEVATORS) {
            System.out.println("Minimum 1 elevator must be created for this system. Creating 1 elevator...");
            numberOfElevators = MAX_ELEVATORS;
        }

        this.numberOfElevators = numberOfElevators;
    }

    private List<Elevator> createElevators() {
        return IntStream.range(0, numberOfElevators)
                .mapToObj(Elevator::new)
                .collect(Collectors.toList());
    }

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

    private boolean assignElevatorToRequestIfPossible(PickupRequest pickupRequest) {
        Optional<Elevator> bestElevator = findBestElevator(pickupRequest);

        bestElevator.ifPresent(
                elevator -> {
                    elevator.handlePickupRequest(pickupRequest);
                    elevator.updateDirection();
                    elevator.updateDestinationFloor();
                }
        );

        return bestElevator.isPresent();
    }

    private Optional<Elevator> findBestElevator(PickupRequest pickupRequest) {
        Optional<Elevator> bestElevator = Optional.empty();
        int shortestDistance = 0;

        int pickupCurrentFloor = pickupRequest.getCurrentFloor();
        Direction pickupDirection = pickupRequest.getDirection();

        for (Elevator elevator : elevators) {
            if (elevator.isIdleOrHasMatchingDirection(pickupDirection)) {
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

    @Override
    public void step() {
        elevators.forEach(Elevator::makeStep);

        pendingPickups.removeIf(this::assignElevatorToRequestIfPossible);
    }

    @Override
    public void update(int elevatorId, int currentFloor, int destinationFloor) {
        if (elevatorId >= elevators.size()) {
            System.out.println("This elevator system has only " + elevators.size() + " elevators installed");
            return;
        }

        elevators.get(elevatorId).updateStatus(currentFloor, destinationFloor);
    }

    @Override
    public void status() {
        System.out.println("ElevatorSystem status:");
        elevators.forEach(elevator -> System.out.println(elevator.getElevatorStatus()));
    }

    private boolean sameSign(int x, int y) {
        return ((x < 0) == (y < 0));
    }
}
