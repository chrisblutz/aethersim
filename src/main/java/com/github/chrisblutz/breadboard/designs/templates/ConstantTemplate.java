package com.github.chrisblutz.breadboard.designs.templates;

import com.github.chrisblutz.breadboard.designs.Chip;
import com.github.chrisblutz.breadboard.designs.Pin;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.simulation.LogicState;
import com.github.chrisblutz.breadboard.simulation.SimulatedDesign;
import com.github.chrisblutz.breadboard.ui.render.designs.DesignEditorUtils;
import com.github.chrisblutz.breadboard.ui.toolkit.UIColor;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.UIStroke;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.display.helpers.GraphicsContext;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantTemplate extends SimulatedTemplate<ConstantState> {

    public static final Pin OUTPUT = new Pin();

    private static ConstantTemplate drivenHighTemplate;
    private static ConstantTemplate drivenLowTemplate;
    private static ConstantTemplate pulledHighTemplate;
    private static ConstantTemplate pulledLowTemplate;

    private final Map<Chip, ConstantState> chipStates = new HashMap<>();

    private final LogicState drivenState, pulledState;

    private ConstantTemplate(LogicState drivenState, LogicState pulledState) {
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
    public void initialize(Chip chip) {
        chipStates.put(chip, new ConstantState(drivenState, pulledState));
    }

    @Override
    public void dispose(Chip chip) {
        chipStates.remove(chip);
    }

    @Override
    public ConstantState getState(Chip chip) {
        return chipStates.get(chip);
    }

    @Override
    public void simulate(ConstantState state) { /* do nothing */ }

    @Override
    public void renderChipPackage(UIGraphics graphics, Chip chip, SimulatedDesign design) {
        // Get the state for the chip, so we can determine the expected states
        ConstantState chipState = getState(chip);
        LogicState drivenState = chipState.getDrivenState();
        LogicState pulledState = chipState.getPulledState();
        LogicState stateAtOutput = design.getStateForPin(OUTPUT);

        // If the driven state is not unknown, render the straight line connecting the circle to output with
        // a triangle indicating a "driven" signal
        // Otherwise, render the "resistor" lines to indicate a "pulled" signal
        if (drivenState != LogicState.UNKNOWN) {
            graphics.setColor(DesignEditorUtils.getColorForLogicState(drivenState));
            graphics.setStroke(UIStroke.solid(0.2f, UIStroke.Cap.BUTT, UIStroke.Join.ROUND));
            graphics.drawLine(1, 1, 2.25, 1);
            graphics.drawLine(3, 1, 4, 1);
            graphics.drawPolygon(new double[] {2.25, 2.25, 3}, new double[] {0.5, 1.5, 1}, 3);
        } else {
            graphics.setColor(DesignEditorUtils.getColorForLogicState(pulledState));
            graphics.setStroke(UIStroke.solid(0.2f));
            graphics.drawLine(1, 1, 2.75, 1);

            graphics.setColor(DesignEditorUtils.getColorForLogicState(stateAtOutput));
            graphics.drawLine(2.75, 1, 4, 1);

            graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_BACKGROUND).brighter());
            graphics.fillRoundRect(2, 0.6, 1.25, 0.8, 0.25, 0.25);

            // Set input color
            graphics.setColor(DesignEditorUtils.getColorForLogicState(
                    pulledState == stateAtOutput ? pulledState : LogicState.UNCONNECTED
            ));
            graphics.setStroke(UIStroke.solid(0.1f, UIStroke.Cap.ROUND, UIStroke.Join.ROUND));
            graphics.drawPolyline(new double[] {2.125, 2.325, 2.525, 2.725, 2.925, 3.125}, new double[] {0.7, 1.3, 0.7, 1.3, 0.7, 1.3}, 6);
        }

        // Draw the circle for the state
        graphics.setColor(DesignEditorUtils.getColorForLogicState(
                drivenState != LogicState.UNKNOWN ? drivenState : pulledState
        ));
        graphics.fillEllipse(0.25, 0.25, 1.5, 1.5);
    }

    @Override
    protected Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }

    public static ConstantTemplate getDrivenHighTemplate() {
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
            drivenHighTemplate = new ConstantTemplate(LogicState.HIGH, LogicState.UNKNOWN);
            drivenHighTemplate.setId("driven_high");
            drivenHighTemplate.setName("1");
            drivenHighTemplate.setWidth(4);
            drivenHighTemplate.setHeight(2);
            drivenHighTemplate.setPins(List.of(OUTPUT));
            drivenHighTemplate.getOutputPins().add(OUTPUT);
        }

        return drivenHighTemplate;
    }

    public static ConstantTemplate getDrivenLowTemplate() {
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
            drivenLowTemplate = new ConstantTemplate(LogicState.LOW, LogicState.UNKNOWN);
            drivenLowTemplate.setId("driven_low");
            drivenLowTemplate.setName("0");
            drivenLowTemplate.setWidth(4);
            drivenLowTemplate.setHeight(2);
            drivenLowTemplate.setPins(List.of(OUTPUT));
            drivenLowTemplate.getOutputPins().add(OUTPUT);
        }

        return drivenLowTemplate;
    }

    public static ConstantTemplate getPulledHighTemplate() {
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
            pulledHighTemplate = new ConstantTemplate(LogicState.UNKNOWN, LogicState.HIGH);
            pulledHighTemplate.setId("pulled_high");
            pulledHighTemplate.setName("(1)");
            pulledHighTemplate.setWidth(4);
            pulledHighTemplate.setHeight(2);
            pulledHighTemplate.setPins(List.of(OUTPUT));
            pulledHighTemplate.getOutputPins().add(OUTPUT);
        }

        return pulledHighTemplate;
    }

    public static ConstantTemplate getPulledLowTemplate() {
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
            pulledLowTemplate = new ConstantTemplate(LogicState.UNKNOWN, LogicState.LOW);
            pulledLowTemplate.setId("pulled_low");
            pulledLowTemplate.setName("(0)");
            pulledLowTemplate.setWidth(4);
            pulledLowTemplate.setHeight(2);
            pulledLowTemplate.setPins(List.of(OUTPUT));
            pulledLowTemplate.getOutputPins().add(OUTPUT);
        }

        return pulledLowTemplate;
    }
}
