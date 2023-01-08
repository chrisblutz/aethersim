package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;

import java.util.ArrayList;
import java.util.List;

public class Node implements SimulationWorkerTraversable {

    public boolean signalState = false;
    // This will either be a single node or multiple wire segments
    private final List<SimulationWorkerTraversable> nextElements = new ArrayList<>();

    public boolean isActive() {
        return signalState;
    }

    @Override
    public void setSignalState(boolean active) {
        this.signalState = active;
    }

    //@Override
    public List<SimulationWorkerTraversable> getNextTraversableElements() {
        return nextElements;
    }

    public void addNextTraversableElement(SimulationWorkerTraversable nextElement) {
        nextElements.add(nextElement);
    }
}
