package pl.edu.agh.elevatorsystem.elevator_system;

import pl.edu.agh.elevatorsystem.elevator.Elevator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.edu.agh.elevatorsystem.util.Constants.*;

public class MyElevatorSystem implements ElevatorSystem {

    int numberOfElevators;
    private final List<Elevator> elevators;

    public MyElevatorSystem(int numberOfElevators) {
        assignNumberOfElevators(numberOfElevators);
        elevators = createElevators();
    }

    private void assignNumberOfElevators(int numberOfElevators) {
        if (numberOfElevators < MIN_ELEVATORS) {
            numberOfElevators = MIN_ELEVATORS;
        } else if (numberOfElevators > MAX_ELEVATORS) {
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
        System.out.println("pickup " + currentFloor + " " + direction + " " + destinationFloor);
    }

    @Override
    public void step() {
        elevators.forEach(Elevator::makeStep);
    }

    @Override
    public void update(int elevatorId, int currentFloor, int destinationFloor) {
        elevators.get(elevatorId).updateStatus(currentFloor, destinationFloor);
    }

    @Override
    public void status() {
        System.out.println("ElevatorSystem status:");
        elevators.forEach(elevator -> System.out.println(elevator.getElevatorStatus()));
    }
}
