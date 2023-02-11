package com.github.chrisblutz.breadboard.designs.templates;

import com.github.chrisblutz.breadboard.designs.Chip;
import com.github.chrisblutz.breadboard.designs.Pin;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.simulation.SimulatedDesign;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ChipTemplate implements BreadboardSavable {

    private String id;
    private String name;

    private int width;
    private int height;

    private List<Pin> pins = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Pin> getPins() {
        return pins;
    }

    protected void setPins(List<Pin> pins) {
        this.pins = pins;
    }

    public void renderChipPackage(UIGraphics graphics, Chip chip, SimulatedDesign design) {
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_FOREGROUND));
        graphics.setFont(UITheme.getFont(ThemeKeys.Fonts.UI.TEXT_DEFAULT).derive(1f)); // TODO

        // Draw the chip text in the center of the chip
        graphics.drawStringCentered(getName(), (float) getWidth() / 2, (float) getHeight() / 2);
    }

    protected abstract Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer);

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        Map<String, Object> yamlMapping = new LinkedHashMap<>();

        // Put basic information
        yamlMapping.put("Id", id);
        yamlMapping.put("Name", name);

        // Get internals information and add it to mapping
        yamlMapping.put("Internals", dumpInternalsToYAML(writer));

        return yamlMapping;
    }
}
