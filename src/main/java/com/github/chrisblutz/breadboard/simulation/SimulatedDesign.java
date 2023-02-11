package com.github.chrisblutz.breadboard.simulation;

import com.github.chrisblutz.breadboard.designs.Chip;
import com.github.chrisblutz.breadboard.designs.Pin;
import com.github.chrisblutz.breadboard.designs.Wire;

public abstract class SimulatedDesign {

    private static class Blank extends SimulatedDesign {

        private Blank() {}

        @Override
        public LogicState getStateForPin(Pin pin) {
            return LogicState.UNCONNECTED;
        }

        @Override
        public LogicState getStateForWire(Wire wire) {
            return LogicState.UNCONNECTED;
        }

        @Override
        public SimulatedDesign getSimulatedChipDesign(Chip chip) {
            return none();
        }
    }

    private static Blank blankInstance;

    public abstract LogicState getStateForPin(Pin pin);

    public abstract LogicState getStateForWire(Wire wire);

    public abstract SimulatedDesign getSimulatedChipDesign(Chip chip);

    public static SimulatedDesign none() {
        if (blankInstance == null)
            blankInstance = new Blank();
        return blankInstance;
    }
}