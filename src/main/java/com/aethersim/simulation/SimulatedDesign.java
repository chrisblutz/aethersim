package com.aethersim.simulation;

import com.aethersim.designs.Chip;
import com.aethersim.designs.Pin;
import com.aethersim.designs.wires.WireNode;
import com.aethersim.designs.wires.WireSegment;

public abstract class SimulatedDesign {

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

    private static Blank blankInstance;

    public abstract LogicState getStateForPin(Pin pin);

    public abstract LogicState getStateForWireNode(WireNode wireNode);

    public abstract LogicState getStateForWireSegment(WireSegment wireSegment);

    public abstract SimulatedDesign getSimulatedChipDesign(Chip chip);

    public static SimulatedDesign none() {
        if (blankInstance == null)
            blankInstance = new Blank();
        return blankInstance;
    }
}
