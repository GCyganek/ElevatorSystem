package pl.edu.agh.elevatorsystem;

import pl.edu.agh.elevatorsystem.elevator_system.ElevatorSystem;
import pl.edu.agh.elevatorsystem.elevator_system.MyElevatorSystem;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElevatorSystemApp {

    public static Pattern PICKUP_PATTERN = Pattern.compile("(pickup)\\s+([1-9]+[0-9]*)\\s+([-]?[1-9]+[0-9]*)\\s+([1-9]+[0-9]*)");
    public static Pattern UPDATE_PATTERN = Pattern.compile("(update)\\s+([1][0-6]|[0-9])\\s+([1-9]+[0-9]*)\\s+([1-9]+[0-9]*)");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ElevatorSystem elevatorSystem = new MyElevatorSystem(5);

        boolean keepLooping = true;
        String line, firstWord;

        while (keepLooping) {
            line = scanner.nextLine();
            firstWord = line.toLowerCase().split(" ")[0];

            switch (firstWord) {
                case "pickup" -> pickupElevatorIfInputValid(line, elevatorSystem);
                case "update" -> updateElevatorIfInputValid(line, elevatorSystem);
                case "step" -> elevatorSystem.step();
                case "status" -> elevatorSystem.status();
                case "quit" -> keepLooping = false;
                default -> System.out.println("Invalid input");
            }
        }
    }

    public static void pickupElevatorIfInputValid(String inputLine, ElevatorSystem elevatorSystem) {
        Matcher matcher = PICKUP_PATTERN.matcher(inputLine);

        if (matcher.find()) {
            int currentFloor = Integer.parseInt(matcher.group(2));
            int direction = Integer.parseInt(matcher.group(3));
            int destinationFloor = Integer.parseInt(matcher.group(4));

            if (!sameSign(destinationFloor - currentFloor, direction) || (destinationFloor == currentFloor)) {
                System.out.println("Invalid input");
                return;
            }

            elevatorSystem.pickup(currentFloor, direction, destinationFloor);
        } else {
            System.out.println("Invalid input");
        }
    }

    private static boolean sameSign(int x, int y) {
        return ((x < 0) == (y < 0));
    }

    public static void updateElevatorIfInputValid(String inputLine, ElevatorSystem elevatorSystem) {
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
