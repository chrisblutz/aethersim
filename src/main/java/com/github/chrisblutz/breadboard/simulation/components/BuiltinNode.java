package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;

public class BuiltinNode extends Node {

    public Pin linkedPin;

    public BuiltinNode(Pin linkedPin) {
        this.linkedPin = linkedPin;
    }

    public Pin getLinkedPin() {
        return linkedPin;
    }
}
