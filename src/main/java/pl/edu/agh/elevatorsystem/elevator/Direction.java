package pl.edu.agh.elevatorsystem.elevator;

public enum Direction {
    IDLE, UP, DOWN;

    public static Direction of(int value) {
        if (value == 0) return IDLE;
        else if (value > 0) return UP;
        else return DOWN;
    }
}
