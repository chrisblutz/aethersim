package com.github.chrisblutz.breadboard.designs.components;

import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.simulation.components.WireSegment;
import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;
import com.github.chrisblutz.breadboard.utils.Vertex;

import java.awt.*;
import java.util.Map;

public class Wire implements BreadboardSavable {

    private Pin start, end;
    public Vertex[] vertices;

    public WireSegment[] simulationSegments;

    public Vertex[] getVertices() {
        return vertices;
    }

    public WireSegment[] getSimulationSegments() {
        return simulationSegments;
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
