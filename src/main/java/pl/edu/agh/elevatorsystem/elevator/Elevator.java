package pl.edu.agh.elevatorsystem.elevator;

public class Elevator {

    public static final int IDLE = -1;

    private final ElevatorStatus elevatorStatus;
    private Direction direction = Direction.IDLE;

    public Elevator(int elevatorId) {
        this(elevatorId, 0);
    }

    public Elevator(int elevatorId, int currentFloor) {
        this.elevatorStatus = new ElevatorStatus(elevatorId, currentFloor, IDLE);
    }

    public void makeStep() { }

    public void updateStatus(int currentFloor, int destinationFloor) {
        elevatorStatus.setCurrentFloor(currentFloor);
        elevatorStatus.setDestinationFloor(destinationFloor);
        direction = evaluateCurrentDirection();
    }

    private Direction evaluateCurrentDirection() {
        return Direction.of(elevatorStatus.getDestinationFloor() - elevatorStatus.getCurrentFloor());
    }

    public ElevatorStatus getElevatorStatus() {
        return elevatorStatus;
    }

    public Direction getDirection() {
        return direction;
    }
}
