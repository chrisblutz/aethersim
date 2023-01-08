package com.github.chrisblutz.breadboard.components.builtins;

import com.github.chrisblutz.breadboard.designs.components.Pin;

import java.util.HashMap;
import java.util.Map;

public class AndGateChipLogic implements BuiltinChipLogic {

    public static final Pin OPERAND_1_PIN = new Pin();
    public static final Pin OPERAND_2_PIN = new Pin();
    public static final Pin RESULT_PIN = new Pin();

    private Boolean operand1Current = null;
    private Boolean operand2Current = null;
    private final Map<Pin, Boolean> cachedOutputs = new HashMap<>();

    @Override
    public Pin[] getInputPins() {
        return new Pin[] {OPERAND_1_PIN, OPERAND_2_PIN};
    }

    @Override
    public Pin[] getOutputPins() {
        return new Pin[] {RESULT_PIN};
    }

    @Override
    public Map<Pin, Boolean> doTick(Map<Pin, Boolean> inputStates) {

        boolean operand1 = inputStates.get(OPERAND_1_PIN);
        boolean operand2 = inputStates.get(OPERAND_2_PIN);

        // If the inputs haven't changed, return early
        if (operand1Current != null && operand2Current != null && operand1 == operand1Current && operand2 == operand2Current)
            return cachedOutputs;

        operand1Current = operand1;
        operand2Current = operand2;
        cachedOutputs.put(RESULT_PIN, operand1 && operand2);

        // TODO Add gate delay
        return cachedOutputs;
    }
}
