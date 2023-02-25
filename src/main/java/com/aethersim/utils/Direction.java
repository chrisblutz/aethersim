package com.aethersim.utils;

/**
 * A {@code Direction} represents a direction in coordinate space, defined by the X and Y offsets required to travel
 * in that direction from an origin point.
 */
public enum Direction {
    /** Represents the "up" direction (negative Y) */
    UP(0, -1),
    /** Represents the "down" direction (positive Y) */
    DOWN(0, 1),
    /** Represents the "left" direction (negative X) */
    LEFT(-1, 0),
    /** Represents the "right" direction (positive X) */
    RIGHT(1, 0);

    private final int xDiff, yDiff;

    /**
     * Defines a new direction, including the offsets required to get there from an origin point.  For example, moving
     * "left" in coordinate space means going subtracting one in the X direction, so the offsets for "left" would
     * be defined as {@code xDiff = -1} and {@code yDiff = 0}.
     *
     * @param xDiff
     * @param yDiff
     */
    Direction(int xDiff, int yDiff) {
        this.xDiff = xDiff;
        this.yDiff = yDiff;
    }

    /**
     * This method returns the X offset required to move one unit in this direction.
     *
     * @return The X offset for this {@code Direction}
     */
    public int getXDiff() {
        return xDiff;
    }

    /**
     * This method returns the Y offset required to move one unit in this direction.
     *
     * @return The Y offset for this {@code Direction}
     */
    public int getYDiff() {
        return yDiff;
    }

    /**
     * This method gets the opposite direction to the current one.  This results in the direction with opposite offsets,
     * so the opposite of "left" is "right", the opposite of "up" is "down", and so on.
     *
     * @return The opposite {@code Direction} to the current one
     */
    public Direction getOpposing() {
        switch (this) {
            case UP -> {
                return DOWN;
            }
            case DOWN -> {
                return UP;
            }
            case LEFT -> {
                return RIGHT;
            }
            default -> {
                return LEFT;
            }
        }
    }
}
