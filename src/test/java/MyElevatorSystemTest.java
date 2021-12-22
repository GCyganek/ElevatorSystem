import org.junit.jupiter.api.Test;
import pl.edu.agh.elevatorsystem.elevator.Direction;
import pl.edu.agh.elevatorsystem.elevator.Elevator;
import pl.edu.agh.elevatorsystem.elevator_system.MyElevatorSystem;
import pl.edu.agh.elevatorsystem.elevator_system.pickup_request.PickupRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyElevatorSystemTest {

    private final MyElevatorSystem systemWith3Elevators = new MyElevatorSystem(3);
    private final MyElevatorSystem systemWith1Elevator = new MyElevatorSystem(1);

    private final PickupRequest pickupRequestFrom2To3 = new PickupRequest(2, Direction.UP, 3);
    private final PickupRequest pickupRequestFrom3To1 = new PickupRequest(3, Direction.DOWN, 1);
    private final PickupRequest pickupRequestFrom2To1 = new PickupRequest(2, Direction.DOWN, 1);
    private final PickupRequest pickupRequestFrom4To5 = new PickupRequest(4, Direction.UP, 5);

    private final PickupRequest pickupRequestFrom2To3InElevator =
            new PickupRequest(2, Direction.UP, 3, true);

    private final PickupRequest pickupRequestFrom3To1InElevator =
            new PickupRequest(3, Direction.DOWN, 1, true);

    private final PickupRequest pickupRequestFrom2To1InElevator =
            new PickupRequest(2, Direction.DOWN, 1, true);

    private final PickupRequest pickupRequestFrom1To5InElevator =
            new PickupRequest(1, Direction.UP, 5, true);

    private final PickupRequest pickupRequestFrom4To5InElevator =
            new PickupRequest(4, Direction.UP, 5, true);


    @Test
    public void updateElevatorsAndMakeStepTest() {
        Elevator elevator0 = systemWith3Elevators.getElevatorById(0);
        Elevator elevator1 = systemWith3Elevators.getElevatorById(1);
        Elevator elevator2 = systemWith3Elevators.getElevatorById(2);

        systemWith3Elevators.update(0, 2, 3);
        systemWith3Elevators.update(1, 10, 1);
        systemWith3Elevators.update(2, 0, 1);

        validateElevatorStatus(elevator0, 2, 3, Direction.UP);
        validateElevatorStatus(elevator1, 10, 1, Direction.DOWN);
        validateElevatorStatus(elevator2, 0, 1, Direction.UP);

        systemWith3Elevators.step();

        validateElevatorStatus(elevator0, 3, Elevator.IDLE, Direction.IDLE);
        validateElevatorStatus(elevator1, 9, 1, Direction.DOWN);
        validateElevatorStatus(elevator2, 1, Elevator.IDLE, Direction.IDLE);
    }

    @Test
    public void requestPickupWhenNoElevatorIsMovingInItsDirectionTest() {
        Elevator elevator0 = systemWith1Elevator.getElevatorById(0);

        systemWith1Elevator.update(0, 2, 3);
        systemWith1Elevator.pickup(1, 1, 2);

        assertEquals(1, systemWith1Elevator.getPendingPickups().size());
        assertFalse(elevator0.getPickupRequests().contains(new PickupRequest(1, Direction.UP, 2)));

        validateElevatorStatus(elevator0, 2, 3, Direction.UP);

        systemWith1Elevator.step();

        assertEquals(1, elevator0.getPickupRequests().size());
        assertTrue(systemWith1Elevator.getPendingPickups().isEmpty());
        validateElevatorStatus(elevator0, 3, 1, Direction.DOWN);

        for (int steps = 0; steps < 2; steps++) {
            systemWith1Elevator.step();
        }

        validateElevatorStatus(elevator0, 1, 2, Direction.UP);
    }

    @Test
    public void requestPickupWith2ElevatorsMovingInItsDirectionTest() {
        Elevator elevator0 = systemWith3Elevators.getElevatorById(0);
        Elevator elevator1 = systemWith3Elevators.getElevatorById(1);
        Elevator elevator2 = systemWith3Elevators.getElevatorById(2);

        systemWith3Elevators.update(0, 2, 3);
        systemWith3Elevators.update(1, 4, 8);
        systemWith3Elevators.update(2, 15, 9);

        validateElevatorStatus(elevator0, 2, 3, Direction.UP);
        validateElevatorStatus(elevator1, 4, 8, Direction.UP);
        validateElevatorStatus(elevator2, 15, 9, Direction.DOWN);

        systemWith3Elevators.pickup(9, 1, 10);

        PickupRequest pickupRequest = new PickupRequest(9, Direction.UP, 10);

        assertFalse(elevator0.getPickupRequests().contains(pickupRequest));
        assertTrue(elevator1.getPickupRequests().contains(pickupRequest));
        assertFalse(elevator2.getPickupRequests().contains(pickupRequest));
        assertFalse(systemWith3Elevators.getPendingPickups().contains(pickupRequest));

        validateElevatorStatus(elevator0, 2, 3, Direction.UP);
        validateElevatorStatus(elevator1, 4, 9, Direction.UP);
        validateElevatorStatus(elevator2, 15, 9, Direction.DOWN);
    }

    @Test
    public void pickupRequestFrom2ndTo3rdFloorTest() {
        Elevator elevator = systemWith1Elevator.getElevatorById(0);
        systemWith1Elevator.pickup(2, 1, 3);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(1, pickupRequests.size());

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequestFrom2To3, pickupRequest);
        assertFalse(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 0, 2, Direction.UP);

        for (int steps = 0; steps < 2; steps++) {
            systemWith1Elevator.step();
        }

        assertEquals(pickupRequestFrom2To3InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 2, 3, Direction.UP);

        systemWith1Elevator.step();

        validateElevatorStatus(elevator, 3, Elevator.IDLE, Direction.IDLE);

        assertTrue(elevator.getPickupRequests().isEmpty());
    }

    @Test
    public void pickupRequestFrom3rdTo1stFloorTest() {
        Elevator elevator = systemWith1Elevator.getElevatorById(0);
        systemWith1Elevator.pickup(3, -1, 1);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(1, pickupRequests.size());

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequestFrom3To1, pickupRequest);
        assertFalse(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 0, 3, Direction.UP);

        for (int steps = 0; steps < 3; steps++) {
            systemWith1Elevator.step();
        }

        assertEquals(pickupRequestFrom3To1InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 3, 1, Direction.DOWN);

        for (int steps = 0; steps < 2; steps++) {
            systemWith1Elevator.step();
        }

        validateElevatorStatus(elevator, 1, Elevator.IDLE, Direction.IDLE);

        assertTrue(elevator.getPickupRequests().isEmpty());
    }

    @Test
    public void pickupRequestsFrom2ndTo3rdFloorThenFrom2rdTo1stFloorTest() {
        Elevator elevator = systemWith1Elevator.getElevatorById(0);
        systemWith1Elevator.pickup(2, 1, 3);
        systemWith1Elevator.pickup(2, -1, 1);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(1, pickupRequests.size());

        validateElevatorStatus(elevator, 0, 2, Direction.UP);

        for (int steps = 0; steps < 2; steps++) {
            systemWith1Elevator.step();
        }

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequestFrom2To3InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 2, 3, Direction.UP);

        systemWith1Elevator.step();

        validateElevatorStatus(elevator, 3, 2, Direction.DOWN);

        pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequestFrom2To1, pickupRequest);
        assertFalse(pickupRequest.isInElevator());

        assertEquals(1, elevator.getPickupRequests().size());

        systemWith1Elevator.step();

        assertEquals(pickupRequestFrom2To1InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 2, 1, Direction.DOWN);

        systemWith1Elevator.step();

        validateElevatorStatus(elevator, 1, Elevator.IDLE, Direction.IDLE);

        assertTrue(elevator.getPickupRequests().isEmpty());

    }

    @Test
    public void pickupRequestsFrom2ndTo3rdFloorThenFrom3rdTo1stFloorTest() {
        Elevator elevator = systemWith1Elevator.getElevatorById(0);
        systemWith1Elevator.pickup(2, 1, 3);
        systemWith1Elevator.pickup(3, -1, 1);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(1, pickupRequests.size());

        validateElevatorStatus(elevator, 0, 2, Direction.UP);

        for (int steps = 0; steps < 2; steps++) {
            systemWith1Elevator.step();
        }

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequestFrom2To3InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 2, 3, Direction.UP);

        systemWith1Elevator.step();

        validateElevatorStatus(elevator, 3, 1, Direction.DOWN);

        pickupRequest = pickupRequests.get(0);
        assertEquals(1, elevator.getPickupRequests().size());
        assertEquals(pickupRequestFrom3To1InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        for (int steps = 0; steps < 2; steps++) {
            systemWith1Elevator.step();
        }

        validateElevatorStatus(elevator, 1, Elevator.IDLE, Direction.IDLE);

        assertTrue(elevator.getPickupRequests().isEmpty());
    }

    @Test
    public void pickupRequestsFrom1stTo5thFloorThenFrom2ndTo3rdFloorTest() {
        Elevator elevator = systemWith1Elevator.getElevatorById(0);
        systemWith1Elevator.pickup(1, 1, 5);
        systemWith1Elevator.pickup(2, 1, 3);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(2, pickupRequests.size());

        // elevator is empty and only knows that it will go up to the 1st floor (pickupRequestFrom1To5)
        // and then to the 2nd floor (pickupRequestFrom2To3)
        validateElevatorStatus(elevator, 0, 2, Direction.UP);

        systemWith1Elevator.step();

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequestFrom1To5InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        pickupRequest = pickupRequests.get(1);
        assertEquals(pickupRequestFrom2To3, pickupRequest);
        assertFalse(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 1, 5, Direction.UP);

        systemWith1Elevator.step();

        assertEquals(pickupRequestFrom2To3InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 2, 5, Direction.UP);

        systemWith1Elevator.step();

        validateElevatorStatus(elevator, 3, 5, Direction.UP);

        assertEquals(1, elevator.getPickupRequests().size());

        for (int steps = 0; steps < 2; steps++) {
            systemWith1Elevator.step();
        }

        validateElevatorStatus(elevator, 5, Elevator.IDLE, Direction.IDLE);

        assertTrue(elevator.getPickupRequests().isEmpty());
    }

    @Test
    public void pickupRequestsFrom3rdTo1stFloorThenFrom4thTo5thFloorTest() {
        Elevator elevator = systemWith1Elevator.getElevatorById(0);
        systemWith1Elevator.pickup(3, -1, 1);
        systemWith1Elevator.pickup(4, 1, 5);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertEquals(1, pickupRequests.size());

        PickupRequest pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequestFrom3To1, pickupRequest);
        assertFalse(pickupRequest.isInElevator());

        validateElevatorStatus(elevator, 0, 3, Direction.UP);

        for (int steps = 0; steps < 3; steps++) {
            systemWith1Elevator.step();
        }

        validateElevatorStatus(elevator, 3, 1, Direction.DOWN);

        assertEquals(pickupRequestFrom3To1InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        for (int steps = 0; steps < 2; steps++) {
            systemWith1Elevator.step();
        }

        validateElevatorStatus(elevator, 1, 4, Direction.UP);

        assertEquals(1, pickupRequests.size());

        pickupRequest = pickupRequests.get(0);
        assertEquals(pickupRequestFrom4To5, pickupRequest);
        assertFalse(pickupRequest.isInElevator());

        for (int steps = 0; steps < 3; steps++) {
            systemWith1Elevator.step();
        }

        validateElevatorStatus(elevator, 4, 5, Direction.UP);

        assertEquals(pickupRequestFrom4To5InElevator, pickupRequest);
        assertTrue(pickupRequest.isInElevator());

        systemWith1Elevator.step();

        validateElevatorStatus(elevator, 5, Elevator.IDLE, Direction.IDLE);

        assertTrue(elevator.getPickupRequests().isEmpty());
    }


    private void validateElevatorStatus(Elevator elevator, int currentFloor, int destinationFloor, Direction direction) {
        assertEquals(currentFloor, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(destinationFloor, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(direction, elevator.getDirection());
    }

}
