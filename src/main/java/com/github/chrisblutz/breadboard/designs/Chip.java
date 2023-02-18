package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.templates.ChipTemplate;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Rectangle;

import java.util.Map;

public class Chip extends DesignElement implements BreadboardSavable {

    private Point location;

    private ChipTemplate chipTemplate;
    private Rectangle boundingBox;

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
        if (chipTemplate != null)
            this.boundingBox = new Rectangle(getLocation().getX(), getLocation().getY(), chipTemplate.getWidth(), chipTemplate.getHeight());
        // Attach the transform to the point
        location.setTransform(getTransform());
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

    @Override
    protected void onTransformUpdated() {

    }

    @Override
    protected void onTransformAccepted() {
        // Update the location of the chip to accept the transformed values
        location = location.withTransform();
    }

    @Override
    public boolean contains(Point point) {
        return false;
    }
}
