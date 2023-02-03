package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

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
