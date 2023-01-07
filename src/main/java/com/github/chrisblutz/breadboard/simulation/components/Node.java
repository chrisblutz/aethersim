package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;

public class Node implements SimulationWorkerTraversable {

    public boolean signalState;
    // This will either be a single node or multiple wire segments
    private SimulationWorkerTraversable[] nextElements;

    public Pin designerPin;

    public Node(SimulationWorkerTraversable[] nextElements) {
        // Initialize in the logic low state
        this.signalState = false;
        this.nextElements = nextElements;
    }

    public boolean isActive() {
        return signalState;
    }

    @Override
    public void setSignalState(boolean active) {
        this.signalState = active;
    }

    @Override
    public SimulationWorkerTraversable[] getNextTraversableElements() {
        return nextElements;
    }
}
