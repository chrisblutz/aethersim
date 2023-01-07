package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;

public class WireSegment implements SimulationWorkerTraversable {

    public boolean signalState;
    // This will either be a single wire segment or a single node
    private SimulationWorkerTraversable[] nextElements;

    public WireSegment(SimulationWorkerTraversable nextElement) {
        // Initialize in the logic low state
        this.signalState = false;
        // Create an array to wrap the next element, since the method that uses it needs an array returned
        this.nextElements = new SimulationWorkerTraversable[] {nextElement};
    }

    public WireSegment(boolean active, SimulationWorkerTraversable nextElement) {
        this.signalState = active;
        // Create an array to wrap the next element, since the method that uses it needs an array returned
        this.nextElements = new SimulationWorkerTraversable[] {nextElement};
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
