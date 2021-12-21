import org.junit.jupiter.api.Test;
import pl.edu.agh.elevatorsystem.elevator.Direction;
import pl.edu.agh.elevatorsystem.elevator.Elevator;
import pl.edu.agh.elevatorsystem.elevator_system.pickup_request.PickupRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ElevatorTest {

    // elevator on the ground floor with elevatorId = 0
    private final Elevator elevator = new Elevator(0);

    @Test
    public void makeStepOnIdleElevatorWithoutRequestsTest() {
        elevator.makeStep();

        validateElevatorStatus(elevator, 0, Elevator.IDLE, Direction.IDLE);
    }

    @Test
    public void updateCurrentFloorTo5AndDestinationFloorTo1Test() {
        elevator.updateStatus(5, 1);

        validateElevatorStatus(elevator, 5, 1, Direction.DOWN);

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
    }

    @Test
    public void updateWithTheSameCurrentAndDestinationFloorTest() {
        assertEquals(0, elevator.getElevatorStatus().getCurrentFloor());
        elevator.updateStatus(5, 5);

        validateElevatorStatus(elevator, 5, Elevator.IDLE, Direction.IDLE);

        List<PickupRequest> pickupRequests = elevator.getPickupRequests();
        assertTrue(pickupRequests.isEmpty());
    }

        @Test
    public void updateWhileElevatorIsNotEmptyTest() {
        elevator.updateStatus(5, 1);

        validateElevatorStatus(elevator, 5, 1, Direction.DOWN);

        elevator.updateStatus(10, 3);

        // nothing should change, update should be rejected
        validateElevatorStatus(elevator, 5, 1, Direction.DOWN);
        assertEquals(1, elevator.getPickupRequests().size());
        assertTrue(elevator.getPickupRequests().contains(new PickupRequest(5, Direction.DOWN, 1, true)));
    }

    private void validateElevatorStatus(Elevator elevator, int currentFloor, int destinationFloor, Direction direction) {
        assertEquals(currentFloor, elevator.getElevatorStatus().getCurrentFloor());
        assertEquals(destinationFloor, elevator.getElevatorStatus().getDestinationFloor());
        assertEquals(direction, elevator.getDirection());
    }
}
