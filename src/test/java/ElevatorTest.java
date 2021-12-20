import org.junit.jupiter.api.Test;
import pl.edu.agh.elevatorsystem.elevator.Direction;
import pl.edu.agh.elevatorsystem.elevator.Elevator;
import pl.edu.agh.elevatorsystem.elevator_system.pickup_request.PickupRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ElevatorTest {

    // elevator on the ground floor with elevatorId = 0
    private final Elevator elevator = new Elevator(0);

    private final PickupRequest pickupRequestFrom2To3 = new PickupRequest(2, Direction.UP, 3);

    private final PickupRequest pickupRequestFrom3To1 = new PickupRequest(3, Direction.DOWN, 1);

    private final PickupRequest pickupRequestFrom2To1 = new PickupRequest(2, Direction.DOWN, 1);

    private final PickupRequest pickupRequestFrom1To5 = new PickupRequest(1, Direction.UP, 5);


    @Test
    public void makeStepOnIdleElevatorWithoutRequestsTest() {
        elevator.makeStep();

        assertEquals(0, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(Elevator.IDLE, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());
    }

    @Test
    public void updateCurrentFloorTo5AndDestinationFloorTo1Test() {
        elevator.updateStatus(5, 1);
        assertEquals(5, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(1, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.DOWN, elevator.getDirection());

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(1, pickupRequests.size());

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertTrue(pickupRequest.isInElevator());
        assertEquals(5, pickupRequest.getCurrentFloor());
        assertEquals(1, pickupRequest.getDestinationFloor());

        for (int steps = 0; steps < 4; steps++) {
            elevator.makeStep();
        }

        pickupRequests = elevator.getPickupRequests();
        assertTrue(pickupRequests.isEmpty());

        assertEquals(1, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(Elevator.IDLE, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());
    }

    @Test
    public void updateWithTheSameCurrentAndDestinationFloorTest() {
        assertEquals(0, elevator.getElevatorStatus().getCurrentFloor());
        elevator.updateStatus(5, 5);
        assertEquals(5, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(Elevator.IDLE, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertTrue(pickupRequests.isEmpty());
    }

    @Test
    public void pickupRequestFrom2ndTo3rdFloorTest() {
        elevator.handlePickupRequest(pickupRequestFrom2To3);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(1, pickupRequests.size());

        assertEquals(2, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        for (int steps = 0; steps < 2; steps++) {
            elevator.makeStep();
        }

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequest, pickupRequestFrom2To3);
        assertTrue(pickupRequest.isInElevator());

        assertEquals(2, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(3, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        elevator.makeStep();

        assertEquals(3, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(Elevator.IDLE, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());

        assertTrue(elevator.getPickupRequests().isEmpty());
    }

    @Test
    public void pickupRequestFrom3rdTo1stFloorTest() {
        elevator.handlePickupRequest(pickupRequestFrom3To1);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(1, pickupRequests.size());

        assertEquals(3, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        for (int steps = 0; steps < 3; steps++) {
            elevator.makeStep();
        }

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequest, pickupRequestFrom3To1);
        assertTrue(pickupRequest.isInElevator());

        assertEquals(3, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(1, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.DOWN, elevator.getDirection());

        for (int steps = 0; steps < 2; steps++) {
            elevator.makeStep();
        }

        assertEquals(1, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(Elevator.IDLE, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());

        assertTrue(elevator.getPickupRequests().isEmpty());
    }

    @Test
    public void pickupRequestsFrom2ndTo3rdFloorThenFrom2rdTo1stFloorTest() {
        elevator.handlePickupRequest(pickupRequestFrom2To3);
        elevator.handlePickupRequest(pickupRequestFrom2To1);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(2, pickupRequests.size());

        assertEquals(2, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        for (int steps = 0; steps < 2; steps++) {
            elevator.makeStep();
        }

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequest, pickupRequestFrom2To3);
        assertTrue(pickupRequest.isInElevator());

        pickupRequest = pickupRequests.get(1);
        assertEquals(pickupRequest, pickupRequestFrom2To1);
        assertTrue(pickupRequest.isInElevator());

        assertEquals(2, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(3, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        elevator.makeStep();

        assertEquals(3, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(1, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.DOWN, elevator.getDirection());

        assertEquals(1, elevator.getPickupRequests().size());

        for (int steps = 0; steps < 2; steps++) {
            elevator.makeStep();
        }

        assertEquals(1, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(Elevator.IDLE, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());

        assertTrue(elevator.getPickupRequests().isEmpty());

    }

    @Test
    public void pickupRequestsFrom2ndTo3rdFloorThenFrom3rdTo1stFloorTest() {
        elevator.handlePickupRequest(pickupRequestFrom2To3);
        elevator.handlePickupRequest(pickupRequestFrom3To1);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(2, pickupRequests.size());

        assertEquals(2, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        for (int steps = 0; steps < 2; steps++) {
            elevator.makeStep();
        }

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequest, pickupRequestFrom2To3);
        assertTrue(pickupRequest.isInElevator());

        pickupRequest = pickupRequests.get(1);
        assertEquals(pickupRequest, pickupRequestFrom3To1);
        assertFalse(pickupRequest.isInElevator());

        assertEquals(2, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(3, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        elevator.makeStep();

        assertEquals(3, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(1, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.DOWN, elevator.getDirection());

        assertEquals(1, elevator.getPickupRequests().size());

        for (int steps = 0; steps < 2; steps++) {
            elevator.makeStep();
        }

        assertEquals(1, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(Elevator.IDLE, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());

        assertTrue(elevator.getPickupRequests().isEmpty());
    }

    @Test
    public void pickupRequestsFrom1stTo5thFloorThenFrom2ndTo3rdFloorTest() {
        elevator.handlePickupRequest(pickupRequestFrom1To5);
        elevator.handlePickupRequest(pickupRequestFrom2To3);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(2, pickupRequests.size());

        // elevator is empty and only knows that it will go up to the 1st floor (pickupRequestFrom1To5)
        // and then to the 2nd floor (pickupRequestFrom2To3)
        assertEquals(2, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        elevator.makeStep();

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequest, pickupRequestFrom1To5);
        assertTrue(pickupRequest.isInElevator());

        pickupRequest = pickupRequests.get(1);
        assertEquals(pickupRequest, pickupRequestFrom2To3);
        assertFalse(pickupRequest.isInElevator());

        assertEquals(1, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(5, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        elevator.makeStep();

        assertEquals(pickupRequest, pickupRequestFrom2To3);
        assertTrue(pickupRequest.isInElevator());

        assertEquals(2, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(5, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        elevator.makeStep();

        assertEquals(3, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(5, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.UP, elevator.getDirection());

        assertEquals(1, elevator.getPickupRequests().size());

        for (int steps = 0; steps < 2; steps++) {
            elevator.makeStep();
        }

        assertEquals(5, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(Elevator.IDLE, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());

        assertTrue(elevator.getPickupRequests().isEmpty());
    }
}
