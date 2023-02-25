package com.aethersim.simulation;

/**
 * A {@code LogicState} defines a simulated digital logic state for design elements.
 */
public enum LogicState {
    /**
     * Represents the "low" logic state (a binary 0)
     */
    LOW(true),
    /**
     * Represents the "high" logic state (a binary 1)
     */
    HIGH(true),
    /**
     * Represents an unconnected circuit component (e.g., floating, high-impedance, etc.)
     */
    UNCONNECTED(false),
    /**
     * Represents the state of a component driven by two conflicting signals (e.g., one high and one low)
     */
    CONFLICTED(false),
    /**
     * Represents that the state of a component is not known
     */
    UNKNOWN(false);

    private final boolean exclusive;

    /**
     * Creates a new {@code LogicState}.
     *
     * @param exclusive a logic state should be marked <em>exclusive</em> if the state defines a "driven" state (e.g,
     *                  low or high), where circuits should enter the {@link #CONFLICTED} state if two differing
     *                  exclusive states are applied to it
     */
    LogicState(boolean exclusive) {
        this.exclusive = exclusive;
    }

    /**
     * This methed returns whether this logic state is defined as <em>exclusive</em>.  Logic states are defined
     * as exclusive if the state defines a "driven" state (e.g, low or high), where circuits should enter the
     * {@link #CONFLICTED} state if two differing exclusive states are applied to it.
     * <p>
     * States such as {@link #UNCONNECTED} are not exclusive, as they can be overriden by signals driving the circuit
     * low or high.
     *
     * @return {@code true} if this state is exclusive, {@code false} otherwise
     */
    public boolean isExclusive() {
        return exclusive;
    }
}
