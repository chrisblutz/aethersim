package com.github.chrisblutz.breadboard.simulationproto;

import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;

public abstract class SimulatedDesign {

    public abstract LogicState getStateForPin(Pin pin);

    public abstract LogicState getStateForWire(Wire wire);

    public abstract SimulatedDesign getSimulatedChipDesign(Chip chip);
}
