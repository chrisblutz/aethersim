package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.components.builtins.BuiltinChipLogic;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.simulation.workers.Worker;

import java.util.*;
import java.util.concurrent.Callable;

public class BuiltinChip implements Callable<Worker[]> {

    private final BuiltinChipLogic chipLogic;
    private final BuiltinNode[] inputNodes;
    private final BuiltinNode[] outputNodes;

    private final Stack<Worker> availableWorkers = new Stack<>();

    public BuiltinChip(BuiltinChipLogic chipLogic, BuiltinNode[] inputNodes, BuiltinNode[] outputNodes) {
        this.chipLogic = chipLogic;
        this.inputNodes = inputNodes;
        this.outputNodes = outputNodes;
    }

    public BuiltinChipLogic getChipLogic() {
        return chipLogic;
    }

    public BuiltinNode[] getInputNodes() {
        return inputNodes;
    }

    public BuiltinNode[] getOutputNodes() {
        return outputNodes;
    }

    @Override
    public Worker[] call() throws Exception {
        // Build the map of pins to values
        Map<Pin, Boolean> inputValues = new HashMap<>();
        for (BuiltinNode node : inputNodes) {
            inputValues.put(node.getLinkedPin(), node.isActive());
            // If the node has an available worker, add it to the list
            if (node.getAvailableWorker() != null)
                availableWorkers.push(node.getAvailableWorker());
            // Clear any available workers
            node.setAvailableWorker(null);
        }

        // Run the builtin logic and retrieve the output values
        Map<Pin, Boolean> outputValues = chipLogic.doTick(inputValues);

        // For each output node, set its value and, if necessary, create a worker
        List<Worker> newWorkers = new ArrayList<>();
        for (BuiltinNode node : outputNodes) {
            boolean nodeState = outputValues.get(node.getLinkedPin());

            // If the state has changed, add a worker
            if (nodeState != node.isActive()) {
                Worker worker;
                // If we have an available worker, repurpose it.
                // Otherwise, create a new worker
                if (availableWorkers.size() > 0) {
                    worker = availableWorkers.pop();
                    worker.setActive(nodeState);
                    worker.setCurrentElement(node);
                } else {
                    worker = new Worker(nodeState, node);
                }
                newWorkers.add(worker);
            }
        }

        // Clear any remaining available workers
        availableWorkers.clear();

        // Return any continuing workers
        return newWorkers.toArray(new Worker[0]);
    }
}
