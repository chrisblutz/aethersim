package com.github.chrisblutz.breadboard.components.builtins;

import com.github.chrisblutz.breadboard.components.PinTemplate;

import java.util.HashMap;
import java.util.Map;

public class NotGateChipLogic implements BuiltinChipLogic {

    public static final PinTemplate OPERAND_PIN_TEMPLATE = new PinTemplate("Op", "Operand ");
    public static final PinTemplate RESULT_PIN_TEMPLATE = new PinTemplate("Result", "Result");

    private Boolean operandCurrent = null;
    private final Map<PinTemplate, Boolean> cachedOutputs = new HashMap<>();

    @Override
    public PinTemplate[] getInputPinTemplates() {
        return new PinTemplate[] {OPERAND_PIN_TEMPLATE};
    }

    @Override
    public PinTemplate[] getOutputPinTemplates() {
        return new PinTemplate[] {RESULT_PIN_TEMPLATE};
    }

    @Override
    public Map<PinTemplate, Boolean> doTick(Map<PinTemplate, Boolean> inputStates) {

        boolean operand = inputStates.get(OPERAND_PIN_TEMPLATE);

        // If the input hasn't changed, return early
        if (operandCurrent != null && operand == operandCurrent)
            return cachedOutputs;

        operandCurrent = operand;
        cachedOutputs.put(RESULT_PIN_TEMPLATE, !operand);

        // TODO Add gate delay
        return cachedOutputs;
    }
}
