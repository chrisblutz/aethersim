package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;
import com.github.chrisblutz.breadboard.simulation.workers.Worker;

public class BuiltinNode extends Node {

    public Pin linkedPin;
    // Keeps track of workers that can be reused by the builtin chip
    private Worker availableWorker = null;

    public BuiltinNode(Pin linkedPin) {
        this.linkedPin = linkedPin;
    }

    public Pin getLinkedPin() {
        return linkedPin;
    }

    public Worker getAvailableWorker() {
        return availableWorker;
    }

    public void setAvailableWorker(Worker availableWorker) {
        this.availableWorker = availableWorker;
    }
}
