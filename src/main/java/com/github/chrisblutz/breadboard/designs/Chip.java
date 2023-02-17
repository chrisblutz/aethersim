package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.templates.ChipTemplate;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Rectangle;

import java.util.Map;

public class Chip implements BreadboardSavable {

    private Vertex location;

    private ChipTemplate chipTemplate;
    private Rectangle boundingBox;

    public Vertex getLocation() {
        return location;
    }

    public void setLocation(Vertex location) {
        this.location = location;
        if (chipTemplate != null)
            this.boundingBox = new Rectangle(getLocation().getX(), getLocation().getY(), chipTemplate.getWidth(), chipTemplate.getHeight());
    }

    public ChipTemplate getChipTemplate() {
        return chipTemplate;
    }

    public void setChipTemplate(ChipTemplate chipTemplate) {
        this.chipTemplate = chipTemplate;
        if (location != null)
            this.boundingBox = new Rectangle(location.getX(), location.getY(), chipTemplate.getWidth(), chipTemplate.getHeight());
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
