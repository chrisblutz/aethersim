package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.Map;

public class DesignedChipTemplate extends ChipTemplate {

    @Override
    protected Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
