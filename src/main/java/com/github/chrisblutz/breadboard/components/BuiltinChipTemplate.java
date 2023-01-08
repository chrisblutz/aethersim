package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.components.builtins.AndGateChipLogic;
import com.github.chrisblutz.breadboard.components.builtins.BuiltinChipLogic;
import com.github.chrisblutz.breadboard.components.builtins.NotGateChipLogic;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;

public class BuiltinChipTemplate extends ChipTemplate {

    private static BuiltinChipTemplate andGateTemplate;
    private static BuiltinChipTemplate notGateTemplate;

    public Callable<BuiltinChipLogic> chipLogicBuilder;

    public Callable<BuiltinChipLogic> getChipLogicBuilder() {
        return chipLogicBuilder;
    }

    @Override
    protected Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {
    }

    public static BuiltinChipTemplate getAndGateTemplate() {
        // If the template doesn't exist, create it
        if (andGateTemplate == null) {
            // Initialize the pins for the template
            AndGateChipLogic.OPERAND_1_PIN.chipX = 0;
            AndGateChipLogic.OPERAND_1_PIN.chipY = 1;
            AndGateChipLogic.OPERAND_1_PIN.input = true;
            AndGateChipLogic.OPERAND_2_PIN.chipX = 0;
            AndGateChipLogic.OPERAND_2_PIN.chipY = 3;
            AndGateChipLogic.OPERAND_2_PIN.input = true;
            AndGateChipLogic.RESULT_PIN.chipX = 6;
            AndGateChipLogic.RESULT_PIN.chipY = 2;
            AndGateChipLogic.RESULT_PIN.input = false;

            // Create the template
            andGateTemplate = new BuiltinChipTemplate();
            andGateTemplate.id = "and";
            andGateTemplate.name = "AND";
            andGateTemplate.width = 6;
            andGateTemplate.height = 4;
            andGateTemplate.chipLogicBuilder = AndGateChipLogic::new;
            andGateTemplate.getPins().addAll(Arrays.asList(
                    AndGateChipLogic.OPERAND_1_PIN,
                    AndGateChipLogic.OPERAND_2_PIN,
                    AndGateChipLogic.RESULT_PIN));
        }

        return andGateTemplate;
    }

    public static BuiltinChipTemplate getNotGateTemplate() {
        // If the template doesn't exist, create it
        if (notGateTemplate == null) {
            // Initialize the pins for the template
            NotGateChipLogic.OPERAND_PIN.chipX = 0;
            NotGateChipLogic.OPERAND_PIN.chipY = 1;
            NotGateChipLogic.OPERAND_PIN.input = true;
            NotGateChipLogic.RESULT_PIN.chipX = 6;
            NotGateChipLogic.RESULT_PIN.chipY = 1;
            NotGateChipLogic.RESULT_PIN.input = false;

            // Create the template
            notGateTemplate = new BuiltinChipTemplate();
            notGateTemplate.id = "not";
            notGateTemplate.name = "NOT";
            notGateTemplate.width = 6;
            notGateTemplate.height = 2;
            notGateTemplate.chipLogicBuilder = NotGateChipLogic::new;
            notGateTemplate.getPins().addAll(Arrays.asList(
                    NotGateChipLogic.OPERAND_PIN,
                    NotGateChipLogic.RESULT_PIN));
        }

        return notGateTemplate;
    }
}
