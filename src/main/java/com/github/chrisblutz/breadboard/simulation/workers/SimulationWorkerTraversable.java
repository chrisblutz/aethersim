package com.github.chrisblutz.breadboard.simulation.workers;

public interface SimulationWorkerTraversable {

    /**
     * Sets the signal state of the traversable element.
     *
     * @param active {@code true} for active (logic high, or 1),
     *               {@code false} for inactive (logic low, or 0)
     */
    void setSignalState(boolean active);

    /**
     * Gets the next traversable elements for this element.  For wire segments,
     * this may be another wire segment, a node, etc.  Multiple elements can
     * be returned here (e.g., a node that branches into multiple wires).
     *
     * @return An array of traversable elements that the worker should visit next
     */
    SimulationWorkerTraversable[] getNextTraversableElements();
}
