package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.components.builtins.BuiltinChipLogic;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.simulation.workers.Worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BuiltinChip implements Callable<Worker[]> {

    private final BuiltinChipLogic chipLogic;
    private final BuiltinNode[] inputNodes;
    private final BuiltinNode[] outputNodes;

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
        for (BuiltinNode node : inputNodes)
            inputValues.put(node.getLinkedPin(), node.isActive());

        // Run the builtin logic and retrieve the output values
        Map<Pin, Boolean> outputValues = chipLogic.doTick(inputValues);

        // For each output node, set its value and, if necessary, create a worker
        List<Worker> newWorkers = new ArrayList<>();
        for (BuiltinNode node : outputNodes) {
            boolean nodeState = outputValues.get(node.getLinkedPin());

            // If the state has changed, add a worker
            if (nodeState != node.isActive())
                newWorkers.add(new Worker(nodeState, node));
        }

        // Return any continuing workers
        return newWorkers.toArray(new Worker[0]);
    }
}
