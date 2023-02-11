package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.designs.wires.WireVertex;

import java.util.Map;
import java.util.Set;

public class Wire implements BreadboardSavable {

    private Pin startPin = null, endPin = null;
    private Chip startChip = null, endChip = null; // "null" indicates an internal node (i.e. a design input/output)

    private WireVertex[] vertices;

    public void setStartPin(Pin startPin) {
        setStartPin(null, startPin);
    }

    public void setStartPin(Chip startChip, Pin startPin) {
        this.startChip = startChip;
        this.startPin = startPin;
    }

    public void setEndPin(Pin endPin) {
        setEndPin(null, endPin);
    }

    public void setEndPin(Chip endChip, Pin endPin) {
        this.endChip = endChip;
        this.endPin = endPin;
    }

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

    public WireVertex[] getVertices() {
        return vertices;
    }

    public void setVertices(WireVertex[] vertices) {
        this.vertices = vertices;
    }

    public Set<Pin> getConnectedPins() {
        if (startPin != null && endPin != null)
            return Set.of(startPin, endPin);
        else if (startPin != null)
            return Set.of(startPin);
        else if (endPin != null)
            return Set.of(endPin);
        else
            return Set.of();
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
