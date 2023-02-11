package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshVertex;

import java.util.LinkedHashSet;
import java.util.Set;

public class WireNode {

    private final Set<WireSegment> connectedSegments = new LinkedHashSet<>();

    private WireVertex vertex;

    public WireNode(WireVertex vertex) {
        this.vertex = vertex;
    }

    public WireVertex getVertex() {
        return vertex;
    }

    public void connect(WireSegment segment) {
        connectedSegments.add(segment);
    }
}
