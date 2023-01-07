package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.components.PinTemplate;
import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;

public class BuiltinNode extends Node {

    public PinTemplate linkedPinTemplate;

    public BuiltinNode(PinTemplate linkedPinTemplate, SimulationWorkerTraversable[] nextElements) {
        super(nextElements);

        this.linkedPinTemplate = linkedPinTemplate;
    }

    public PinTemplate getLinkedPinTemplate() {
        return linkedPinTemplate;
    }
}
