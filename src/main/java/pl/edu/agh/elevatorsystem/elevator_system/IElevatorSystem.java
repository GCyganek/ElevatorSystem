package pl.edu.agh.elevatorsystem.elevator_system;

public interface IElevatorSystem {
    void pickup(int currentFloor, int direction, int destinationFloor);
    void update(int elevatorId, int currentFloor, int destinationFloor);
    void step();
    void status();
}
