package com.aethersim.simulation;

public enum LogicState {
    LOW(true),
    HIGH(true),
    UNCONNECTED(false),
    CONFLICTED(false),
    UNKNOWN(false);

    private final boolean exclusive;

    LogicState(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isExclusive() {
        return exclusive;
    }
}
