package pl.edu.agh.elevatorsystem;

import pl.edu.agh.elevatorsystem.elevator_system.IElevatorSystem;
import pl.edu.agh.elevatorsystem.elevator_system.MyElevatorSystem;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the simulation of the elevator system with I/O operations on the CLI
 */
public class ElevatorSystemApp {

    public static final Pattern PICKUP_PATTERN =
            Pattern.compile("(pickup)\\s+([0]|[1-9]+[0-9]*)\\s+([-]?[1-9]+[0-9]*)\\s+([0]|[1-9]+[0-9]*)");

    public static final Pattern UPDATE_PATTERN =
            Pattern.compile("(update)\\s+([1][0-6]|[0-9])\\s+([0]|[1-9]+[0-9]*)\\s+([0]|[1-9]+[0-9]*)");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Elevator system simulation");
        System.out.println("Enter number of elevators [1 - 16]: ");

        int numberOfElevators = scanner.nextInt();
        IElevatorSystem IElevatorSystem = new MyElevatorSystem(numberOfElevators);

        scanner.nextLine();

        printHelp();

        boolean keepLooping = true;
        String line, firstWord;

        while (keepLooping) {
            line = scanner.nextLine();
            firstWord = line.toLowerCase().split(" ")[0];

            switch (firstWord) {
                case "help" -> printHelp();
                case "pickup" -> pickupElevatorIfInputValid(line, IElevatorSystem);
                case "update" -> updateElevatorIfInputValid(line, IElevatorSystem);
                case "step" -> IElevatorSystem.step();
                case "status" -> IElevatorSystem.status();
                case "quit" -> keepLooping = false;
                default -> System.out.println("Invalid input");
            }
        }
    }

    public static void printHelp() {
        String help = """
                help -> shows commands list
                pickup [currentFloor (0 - 255)] [direction ( > 0 => up; < 0 => down)] [destinationFloor (0 - 255)] ->
                    sends request for the elevator to the elevator system
                update [elevatorId (0 - 15)] [currentFloor (0 - 255)] [destinationFloor (0 - 255)] ->
                    immediately updates status of elevator with chosen elevatorId
                step -> performs one step of the simulation
                status -> shows list of elevators with their status
                quit -> ends the simulation
                """;

        System.out.println(help);
    }

    /**
     * Validates the pickup request and performs it if it's correct
     * @param inputLine      - input line from the user where the first word is "pickup"
     * @param elevatorSystem - elevatorSystem to handle the pickup request if the pickup request is correct
     */
    public static void pickupElevatorIfInputValid(String inputLine, IElevatorSystem elevatorSystem) {
        Matcher matcher = PICKUP_PATTERN.matcher(inputLine);

        if (matcher.find()) {
            int currentFloor = Integer.parseInt(matcher.group(2));
            int direction = Integer.parseInt(matcher.group(3));
            int destinationFloor = Integer.parseInt(matcher.group(4));

            elevatorSystem.pickup(currentFloor, direction, destinationFloor);
        } else {
            System.out.println("Invalid input");
        }
    }

    /**
     * Validates the update request and performs it if it's correct
     * @param inputLine       - input line from the user where the first word is "update"
     * @param elevatorSystem  - elevatorSystem to be updated if the update request is correct
     */
    public static void updateElevatorIfInputValid(String inputLine, IElevatorSystem elevatorSystem) {
        Matcher matcher = UPDATE_PATTERN.matcher(inputLine);

        if (matcher.find()) {
            int elevatorId = Integer.parseInt(matcher.group(2));
            int currentFloor = Integer.parseInt(matcher.group(3));
            int destinationFloor = Integer.parseInt(matcher.group(4));

            elevatorSystem.update(elevatorId, currentFloor, destinationFloor);
        } else {
            System.out.println("Invalid input");
        }
    }
}
