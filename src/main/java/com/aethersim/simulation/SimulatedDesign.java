package com.aethersim.simulation;

import com.aethersim.designs.Design;
import com.aethersim.designs.Chip;
import com.aethersim.designs.Pin;
import com.aethersim.designs.wires.WireNode;
import com.aethersim.designs.wires.WireSegment;

/**
 * A {@code SimulatedDesign} is the component of the simulation system responsible for mapping simulated
 * {@link LogicState}s back to the appropriate design elements in a {@link Design}.  Each simulation module
 * will implement this functionality differently, but this class exists to ensure a consistent API for retrieving
 * design element logic states.
 */
public abstract class SimulatedDesign {

    // This singleton design returns "unconnected" for all design elements, and is useful as a default design
    private static class Blank extends SimulatedDesign {

        private Blank() {}

        @Override
        public LogicState getStateForPin(Pin pin) {
            return LogicState.UNCONNECTED;
        }

        @Override
        public LogicState getStateForWireNode(WireNode wireNode) {
            return LogicState.UNCONNECTED;
        }

        @Override
        public LogicState getStateForWireSegment(WireSegment wireSegment) {
            return LogicState.UNCONNECTED;
        }

        @Override
        public SimulatedDesign getSimulatedChipDesign(Chip chip) {
            return none();
        }
    }

    // This holds the singleton instance (allocated when used) for the Blank class above
    private static Blank blankInstance;

    /**
     * This method retrieves the current simulated {@link LogicState} for the specified {@link Pin}.  It must never
     * return {@code null}.  For unknown values, return either {@link LogicState#UNCONNECTED} or
     * {@link LogicState#UNKNOWN}.
     *
     * @param pin the {@link Pin} to retrieve state for
     * @return The current simulated {@link LogicState} for the {@link Pin}
     */
    public abstract LogicState getStateForPin(Pin pin);

    /**
     * This method retrieves the current simulated {@link LogicState} for the specified {@link WireNode}.  It must
     * never return {@code null}.  For unknown values, return either {@link LogicState#UNCONNECTED} or
     * {@link LogicState#UNKNOWN}.
     *
     * @param wireNode the {@link WireNode} to retrieve state for
     * @return The current simulated {@link LogicState} for the {@link WireNode}
     */
    public abstract LogicState getStateForWireNode(WireNode wireNode);

    /**
     * This method retrieves the current simulated {@link LogicState} for the specified {@link WireSegment}.  It must
     * never return {@code null}.  For unknown values, return either {@link LogicState#UNCONNECTED} or
     * {@link LogicState#UNKNOWN}.
     *
     * @param wireSegment the {@link WireSegment} to retrieve state for
     * @return The current simulated {@link LogicState} for the {@link WireSegment}
     */
    public abstract LogicState getStateForWireSegment(WireSegment wireSegment);

    /**
     * This method retrieves the {@code SimulatedDesign} for the specified chip.  This is used by AetherSim to
     * determine states for child chips (e.g., the transistors inside a logic gate).  This method must never return
     * {@code null}.  If no design is available, return {@link #none()} to provide a default simulated design.
     *
     * @param chip the {@link Chip} to retrieve the simulated design for
     * @return The current {@code SimulatedDesign} for the {@link Chip}
     */
    public abstract SimulatedDesign getSimulatedChipDesign(Chip chip);

    /**
     * This method returns the "default" simulated design that returns {@link LogicState#UNCONNECTED} for all
     * design elements.  It is useful when providing default simulated designs where no other design exists, but
     * should be avoided if another simulated design is available.
     *
     * @return A simulated design that returns {@link LogicState#UNCONNECTED} for all design elements.
     */
    public static SimulatedDesign none() {
        if (blankInstance == null)
            blankInstance = new Blank();
        return blankInstance;
    }
}
