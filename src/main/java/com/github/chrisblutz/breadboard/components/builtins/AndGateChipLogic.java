package com.github.chrisblutz.breadboard.components.builtins;

import com.github.chrisblutz.breadboard.components.PinTemplate;

import java.util.HashMap;
import java.util.Map;

public class AndGateChipLogic implements BuiltinChipLogic {

    public static final PinTemplate OPERAND_1_PIN_TEMPLATE = new PinTemplate("Op1", "Operand 1");
    public static final PinTemplate OPERAND_2_PIN_TEMPLATE = new PinTemplate("Op2", "Operand 2");
    public static final PinTemplate RESULT_PIN_TEMPLATE = new PinTemplate("Result", "Result");

    private Boolean operand1Current = null;
    private Boolean operand2Current = null;
    private final Map<PinTemplate, Boolean> cachedOutputs = new HashMap<>();

    @Override
    public PinTemplate[] getInputPinTemplates() {
        return new PinTemplate[] {OPERAND_1_PIN_TEMPLATE, OPERAND_2_PIN_TEMPLATE};
    }

    @Override
    public PinTemplate[] getOutputPinTemplates() {
        return new PinTemplate[] {RESULT_PIN_TEMPLATE};
    }

    @Override
    public Map<PinTemplate, Boolean> doTick(Map<PinTemplate, Boolean> inputStates) {

        boolean operand1 = inputStates.get(OPERAND_1_PIN_TEMPLATE);
        boolean operand2 = inputStates.get(OPERAND_2_PIN_TEMPLATE);

        // If the inputs haven't changed, return early
        if (operand1Current != null && operand2Current != null && operand1 == operand1Current && operand2 == operand2Current)
            return cachedOutputs;

        operand1Current = operand1;
        operand2Current = operand2;
        cachedOutputs.put(RESULT_PIN_TEMPLATE, operand1 && operand2);

        // TODO Add gate delay
        return cachedOutputs;
    }
}
