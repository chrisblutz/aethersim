package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.templates.ChipTemplate;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.Map;

public class Chip implements BreadboardSavable {

    private Vertex location;

    private ChipTemplate chipTemplate;

    public Vertex getLocation() {
        return location;
    }

    public void setLocation(Vertex location) {
        this.location = location;
    }

    public ChipTemplate getChipTemplate() {
        return chipTemplate;
    }

    public void setChipTemplate(ChipTemplate chipTemplate) {
        this.chipTemplate = chipTemplate;
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
