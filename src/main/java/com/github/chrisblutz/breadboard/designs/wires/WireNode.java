package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.Vertex;

import java.util.LinkedHashSet;
import java.util.Set;

public class WireNode {

    private final Set<WireSegment> connectedSegments = new LinkedHashSet<>();

    private Vertex vertex;

    public WireNode(Vertex vertex) {
        this.vertex = vertex;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public void connect(WireSegment segment) {
        connectedSegments.add(segment);
    }
}
