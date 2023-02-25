package com.aethersim.designs.templates;

import com.aethersim.designs.Chip;
import com.aethersim.designs.Pin;
import com.aethersim.designs.Point;
import com.aethersim.projects.Scope;
import com.aethersim.simulation.LogicState;
import com.aethersim.simulation.SimulatedDesign;
import com.aethersim.ui.render.designs.DesignEditorUtils;
import com.aethersim.ui.toolkit.UIGraphics;
import com.aethersim.ui.toolkit.UIStroke;
import com.aethersim.ui.toolkit.UITheme;
import com.aethersim.ui.toolkit.display.theming.ThemeKeys;
import com.aethersim.utils.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public static void registerAll() {
        // Configure pin
        OUTPUT.setId("toggle_output");
        OUTPUT.setName("Output");
        OUTPUT.setChipLocation(new Point(4, 2));
        OUTPUT.setChipOrientation(Direction.RIGHT);

        // Configure template
        ToggleTemplate template = new ToggleTemplate();
        template.setId("toggle");
        template.setName("Toggle");
        template.setWidth(4);
        template.setHeight(4);
        template.setPins(Set.of(OUTPUT));
        template.getOutputPins().add(OUTPUT);

        // Register
        ChipTemplate.register(template, Scope.NATIVE);
    }
}
