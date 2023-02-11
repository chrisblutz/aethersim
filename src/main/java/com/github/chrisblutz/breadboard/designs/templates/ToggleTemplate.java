package com.github.chrisblutz.breadboard.designs.templates;

import com.github.chrisblutz.breadboard.designs.Chip;
import com.github.chrisblutz.breadboard.designs.Pin;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.simulation.LogicState;
import com.github.chrisblutz.breadboard.simulation.SimulatedDesign;
import com.github.chrisblutz.breadboard.ui.render.designs.DesignEditorUtils;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.UIStroke;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToggleTemplate extends SimulatedTemplate<ToggleState> {

    public static final Pin OUTPUT = new Pin();

    private static ToggleTemplate template;

    private final Map<Chip, ToggleState> chipStates = new HashMap<>();

    private ToggleTemplate() {}

    public Pin getOutput() {
        return OUTPUT;
    }

    @Override
    public void initialize(Chip chip) {
        chipStates.put(chip, new ToggleState(LogicState.LOW));
    }

    public LogicState getDrivenState(Chip chip) {
        return getState(chip).getDrivenState();
    }

    public void setDrivenState(Chip chip, LogicState state) {
        getState(chip).setDrivenState(state);
    }

    @Override
    public void dispose(Chip chip) {
        chipStates.remove(chip);
    }

    @Override
    public ToggleState getState(Chip chip) {
        return chipStates.get(chip);
    }

    @Override
    public void simulate(ToggleState state) { /* do nothing */ }

    @Override
    public void renderChipPackage(UIGraphics graphics, Chip chip, SimulatedDesign design) {
        // Get the state for the chip, so we can determine the expected states
        ToggleState chipState = getState(chip);
        LogicState drivenState = chipState.getDrivenState();

        graphics.setColor(DesignEditorUtils.getColorForLogicState(drivenState));
        graphics.setStroke(UIStroke.solid(0.2f, UIStroke.Cap.BUTT, UIStroke.Join.ROUND));
        graphics.drawLine(2, 2, 4, 2);

        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_BACKGROUND).darker());
        graphics.fillEllipse(0.75, 0.75, 2.5, 2.5);

        graphics.setColor(DesignEditorUtils.getColorForLogicState(drivenState));
        graphics.fillEllipse(1, 1, 2, 2);
    }

    @Override
    protected Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }

    public static ToggleTemplate getTemplate() {
        // If the template doesn't exist, create it
        if (template == null) {
            // Configure pin if necessary
            if (OUTPUT.getId() == null) {
                OUTPUT.setId("toggle_output");
                OUTPUT.setName("Output");
                OUTPUT.setChipX(4);
                OUTPUT.setChipY(2);
            }

            // Configure template
            template = new ToggleTemplate();
            template.setId("toggle");
            template.setName("Toggle");
            template.setWidth(4);
            template.setHeight(4);
            template.setPins(List.of(OUTPUT));
            template.getOutputPins().add(OUTPUT);
        }

        return template;
    }
}
