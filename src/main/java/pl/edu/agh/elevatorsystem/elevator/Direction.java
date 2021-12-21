package pl.edu.agh.elevatorsystem.elevator;

/**
 * Represents the direction of the elevator
 */
public enum Direction {
    IDLE,
    UP,
    DOWN;

    public static Direction of(int value) {
        if (value == 0) return IDLE;
        else if (value > 0) return UP;
        else return DOWN;
    }

    public int intValue() {
        if (this.equals(IDLE)) return 0;
        else if (this.equals(UP)) return 1;
        else return -1;
    }

    public Direction opposite() {
        if (this.equals(UP)) return DOWN;
        else if (this.equals(DOWN)) return UP;
        else return IDLE;
    }
}
