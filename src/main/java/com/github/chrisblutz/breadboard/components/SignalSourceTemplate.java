package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.simulationproto.LogicState;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SignalSourceTemplate extends ChipTemplate {

    public static final Pin OUTPUT = new Pin();

    private static SignalSourceTemplate drivenHighTemplate;
    private static SignalSourceTemplate drivenLowTemplate;
    private static SignalSourceTemplate pulledHighTemplate;
    private static SignalSourceTemplate pulledLowTemplate;

    private final LogicState drivenState, pulledState;

    private SignalSourceTemplate(LogicState drivenState, LogicState pulledState) {
        this.drivenState = drivenState;
        this.pulledState = pulledState;
    }

    public LogicState getDrivenState() {
        return drivenState;
    }

    public LogicState getPulledState() {
        return pulledState;
    }

    public Pin getOutput() {
        return OUTPUT;
    }

    @Override
    protected Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }

    public static SignalSourceTemplate getDrivenHighTemplate() {
        // If the template doesn't exist, create it
        if (drivenHighTemplate == null) {
            // Configure pin if necessary
            if (OUTPUT.getId() == null) {
                OUTPUT.setId("source_output");
                OUTPUT.setName("Output");
                OUTPUT.setChipX(4);
                OUTPUT.setChipY(1);
            }

            // Configure template
            drivenHighTemplate = new SignalSourceTemplate(LogicState.HIGH, LogicState.UNKNOWN);
            drivenHighTemplate.setId("driven_high");
            drivenHighTemplate.setName("1");
            drivenHighTemplate.setWidth(4);
            drivenHighTemplate.setHeight(2);
            drivenHighTemplate.setPins(List.of(OUTPUT));
        }

        return drivenHighTemplate;
    }

    public static SignalSourceTemplate getDrivenLowTemplate() {
        // If the template doesn't exist, create it
        if (drivenLowTemplate == null) {
            // Configure pin if necessary
            if (OUTPUT.getId() == null) {
                OUTPUT.setId("source_output");
                OUTPUT.setName("Output");
                OUTPUT.setChipX(4);
                OUTPUT.setChipY(1);
            }

            // Configure template
            drivenLowTemplate = new SignalSourceTemplate(LogicState.LOW, LogicState.UNKNOWN);
            drivenLowTemplate.setId("driven_low");
            drivenLowTemplate.setName("0");
            drivenLowTemplate.setWidth(4);
            drivenLowTemplate.setHeight(2);
            drivenLowTemplate.setPins(List.of(OUTPUT));
        }

        return drivenLowTemplate;
    }

    public static SignalSourceTemplate getPulledHighTemplate() {
        // If the template doesn't exist, create it
        if (pulledHighTemplate == null) {
            // Configure pin if necessary
            if (OUTPUT.getId() == null) {
                OUTPUT.setId("source_output");
                OUTPUT.setName("Output");
                OUTPUT.setChipX(4);
                OUTPUT.setChipY(1);
            }

            // Configure template
            pulledHighTemplate = new SignalSourceTemplate(LogicState.UNKNOWN, LogicState.HIGH);
            pulledHighTemplate.setId("pulled_high");
            pulledHighTemplate.setName("(1)");
            pulledHighTemplate.setWidth(4);
            pulledHighTemplate.setHeight(2);
            pulledHighTemplate.setPins(List.of(OUTPUT));
        }

        return pulledHighTemplate;
    }

    public static SignalSourceTemplate getPulledLowTemplate() {
        // If the template doesn't exist, create it
        if (pulledLowTemplate == null) {
            // Configure pin if necessary
            if (OUTPUT.getId() == null) {
                OUTPUT.setId("source_output");
                OUTPUT.setName("Output");
                OUTPUT.setChipX(4);
                OUTPUT.setChipY(1);
            }

            // Configure template
            pulledLowTemplate = new SignalSourceTemplate(LogicState.UNKNOWN, LogicState.LOW);
            pulledLowTemplate.setId("pulled_low");
            pulledLowTemplate.setName("(0)");
            pulledLowTemplate.setWidth(4);
            pulledLowTemplate.setHeight(2);
            pulledLowTemplate.setPins(List.of(OUTPUT));
        }

        return pulledLowTemplate;
    }
}
