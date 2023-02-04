package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.simulationproto.SimulatedDesign;
import com.github.chrisblutz.breadboard.ui.toolkit.UIColor;
import com.github.chrisblutz.breadboard.ui.toolkit.UIFont;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;

import java.awt.*;
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

    protected void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    protected void setHeight(int height) {
        this.height = height;
    }

    public List<Pin> getPins() {
        return pins;
    }

    protected void setPins(List<Pin> pins) {
        this.pins = pins;
    }

    public void renderChipPackage(UIGraphics graphics, SimulatedDesign design, double scale, double offsetX, double offsetY) { // TODO Graphics api
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_FOREGROUND));
        graphics.setFont(UITheme.getFont(ThemeKeys.Fonts.UI.TEXT_DEFAULT)); // TODO

        // Draw the chip text in the center of the chip
        String chipText = getName();
        FontMetrics metrics = graphics.getInternalGraphics().getFontMetrics();
        int stringWidth = metrics.stringWidth(chipText);
        int stringHeightOffset = metrics.getAscent() - metrics.getDescent();
        graphics.drawString(chipText, (int) ((scale * getWidth() / 2) - ((double) stringWidth / 2) + offsetY), (int) ((scale * getHeight() / 2) + ((double) stringHeightOffset / 2) + offsetY));
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
