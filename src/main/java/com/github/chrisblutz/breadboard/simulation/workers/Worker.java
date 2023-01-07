package com.github.chrisblutz.breadboard.simulation.workers;

import com.github.chrisblutz.breadboard.simulation.components.Node;
import com.github.chrisblutz.breadboard.simulation.components.WireSegment;

import java.util.concurrent.Callable;

public class Worker implements Callable<Worker[]>, Cloneable {

    private boolean signalActive;
    private SimulationWorkerTraversable currentElement;

    public Worker(boolean signalActive, SimulationWorkerTraversable initialElement) {
        this.signalActive = signalActive;
        this.currentElement = initialElement;
    }

    @Override
    public Worker[] call() {
        // Set the state of the current element
        currentElement.setSignalState(signalActive);

        // Determine if we have subsequent elements
        SimulationWorkerTraversable[] subsequentElements = currentElement.getNextTraversableElements();

        // If we have no further elements to traverse, return no continuing workers
        if (subsequentElements.length == 0)
            return new Worker[0];

        // If we have only one subsequent element and it is a node, set all node states up to the next wire segment(s),
        // then set the subsequent elements again so the following control blocks correctly set the continuing workers
        if (subsequentElements.length == 1 && subsequentElements[0] instanceof Node) {
            // Pull out the first element
            SimulationWorkerTraversable currentNode = subsequentElements[0];
            // Set node states for every node until we encounter a non-node next element
            while (true) {
                currentNode.setSignalState(signalActive);
                // Find next elements
                SimulationWorkerTraversable[] nodeSubsequentElements = currentNode.getNextTraversableElements();

                // Since we've hit the end of our traversable elements, return no continuing workers
                if (nodeSubsequentElements == null || nodeSubsequentElements.length == 0)
                    return new Worker[0];

                if (nodeSubsequentElements.length == 1 && nodeSubsequentElements[0] instanceof Node) {
                    // If we have encountered a node, set the current node and continue the loop
                    currentNode = nodeSubsequentElements[0];
                } else {
                    // Otherwise, set the outer subsequent elements and break out of the loop
                    subsequentElements = nodeSubsequentElements;
                    break;
                }
            }
        }

        // If we have only one subsequent element, determine if it is a wire segment.  If so, set the current
        // element and return this worker as continuing
        if (subsequentElements.length == 1 && subsequentElements[0] instanceof WireSegment) {
            this.currentElement = subsequentElements[0];
            return new Worker[]{this};
        }

        // If we have multiple subsequent elements, clone this worker for all but the first segment and return all
        // cloned workers as continuing
        if (subsequentElements.length > 1) {
            Worker[] continuingWorkers = new Worker[subsequentElements.length];

            // Configure this worker as the first continuing worker
            this.currentElement = subsequentElements[0];
            continuingWorkers[0] = this;

            // Configure all other workers as clones of this one
            for (int i = 1; i < subsequentElements.length; i++) {
                continuingWorkers[i] = (Worker) this.clone();
                continuingWorkers[i].currentElement = subsequentElements[i];
            }

            // Return our list of continuing workers
            return continuingWorkers;
        }

        // If we get here, we have no more traversable elements, so return no continuing workers
        return new Worker[0];
    }

    @Override
    protected Object clone() {
        return new Worker(this.signalActive, this.currentElement);
    }
}
