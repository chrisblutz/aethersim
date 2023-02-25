package com.aethersim.utils;

public enum Direction {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

    private final int xDiff, yDiff;

    Direction(int xDiff, int yDiff) {
        this.xDiff = xDiff;
        this.yDiff = yDiff;
    }

    public int getXDiff() {
        return xDiff;
    }

    public int getYDiff() {
        return yDiff;
    }

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
