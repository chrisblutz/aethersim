package com.github.chrisblutz.breadboard.designs.components;

import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.utils.Vertex;

import java.util.Map;

public class Wire implements BreadboardSavable {

    public Pin startPin = null, endPin = null;
    public Chip startChip = null, endChip = null; // "null" indicates an internal node (i.e. a design input/output)

    public Vertex[] vertices;

    public Pin getStartPin() {
        return startPin;
    }

    public Pin getEndPin() {
        return endPin;
    }

    public Chip getStartChip() {
        return startChip;
    }

    public Chip getEndChip() {
        return endChip;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
