package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.List;
import java.util.Map;

public class DesignedChipTemplate extends ChipTemplate {

    public Design design;

    public Design getDesign() {
        return design;
    }

    @Override
    public List<Pin> getPins() {
        return design.getPins();
    }

    @Override
    protected Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
