package com.github.chrisblutz.breadboard.components.builtins;

import com.github.chrisblutz.breadboard.designs.components.Pin;

import java.util.HashMap;
import java.util.Map;

public class NotGateChipLogic implements BuiltinChipLogic {

    public static final Pin OPERAND_PIN = new Pin();
    public static final Pin RESULT_PIN = new Pin();

    private Boolean operandCurrent = null;
    private final Map<Pin, Boolean> cachedOutputs = new HashMap<>();

    @Override
    public Pin[] getInputPins() {
        return new Pin[] {OPERAND_PIN};
    }

    @Override
    public Pin[] getOutputPins() {
        return new Pin[] {RESULT_PIN};
    }

    @Override
    public Map<Pin, Boolean> doTick(Map<Pin, Boolean> inputStates) {

        boolean operand = inputStates.get(OPERAND_PIN);

        // If the input hasn't changed, return early
        if (operandCurrent != null && operand == operandCurrent)
            return cachedOutputs;

        operandCurrent = operand;
        cachedOutputs.put(RESULT_PIN, !operand);

        // TODO Add gate delay
        return cachedOutputs;
    }
}
