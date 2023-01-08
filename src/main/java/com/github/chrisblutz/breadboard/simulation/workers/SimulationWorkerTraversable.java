package com.github.chrisblutz.breadboard.simulation.workers;

import java.util.List;

public interface SimulationWorkerTraversable {

    /**
     * Sets the signal state of the traversable element.
     *
     * @param active {@code true} for active (logic high, or 1),
     *               {@code false} for inactive (logic low, or 0)
     */
    void setSignalState(boolean active);
}
