package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;

import java.util.ArrayList;
import java.util.List;

public class NodeConnector implements SimulationWorkerTraversable {

    public boolean signalState = false;
    // This should be a single node
    private SimulationWorkerTraversable nextElement;

    public NodeConnector(SimulationWorkerTraversable nextElement) {
        this.nextElement = nextElement;
    }

    public boolean isActive() {
        return signalState;
    }

    @Override
    public void setSignalState(boolean active) {
        this.signalState = active;
    }

    public SimulationWorkerTraversable getNextTraversableElement() {
        return nextElement;
    }
}
