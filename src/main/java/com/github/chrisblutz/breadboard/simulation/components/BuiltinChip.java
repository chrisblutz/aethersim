package com.github.chrisblutz.breadboard.simulation.components;

import com.github.chrisblutz.breadboard.components.PinTemplate;
import com.github.chrisblutz.breadboard.components.builtins.BuiltinChipLogic;
import com.github.chrisblutz.breadboard.simulation.workers.Worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BuiltinChip implements Callable<Worker[]> {

    public BuiltinNode[] inputNodes;
    public BuiltinNode[] outputNodes;
    public BuiltinChipLogic logic;

    @Override
    public Worker[] call() throws Exception {

        // Build the map of pins to values
        Map<PinTemplate, Boolean> inputValues = new HashMap<>();
        for (BuiltinNode node : inputNodes)
            inputValues.put(node.getLinkedPinTemplate(), node.isActive());

        // Run the builtin logic and retrieve the output values
        Map<PinTemplate, Boolean> outputValues = logic.doTick(inputValues);

        // For each output node, set its value and, if necessary, create a worker
        List<Worker> newWorkers = new ArrayList<>();
        for (BuiltinNode node : outputNodes) {
            boolean nodeState = outputValues.get(node.getLinkedPinTemplate());

            // If the state has changed, add a worker
            if (nodeState != node.isActive())
                newWorkers.add(new Worker(nodeState, node));
        }

        // Return any continuing workers
        return newWorkers.toArray(new Worker[0]);
    }
}
