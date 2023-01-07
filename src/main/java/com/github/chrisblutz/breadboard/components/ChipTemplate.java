package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.*;

public abstract class ChipTemplate implements BreadboardSavable {

    public String id;
    public String name;

    public int width;
    public int height;

    public List<PinTemplate> inputPinTemplates = new ArrayList<>();
    public List<PinTemplate> outputPinTemplates = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
