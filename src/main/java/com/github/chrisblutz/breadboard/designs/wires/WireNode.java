package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.Vertex;
import com.github.chrisblutz.breadboard.utils.Direction;

import java.util.LinkedHashSet;
import java.util.Set;

public class WireNode implements WireRoutable {

    private final Set<WireSegment> connectedSegments = new LinkedHashSet<>();

    private Vertex vertex;

    public WireNode(Vertex vertex) {
        this.vertex = vertex;
    }

    @Override
    public Vertex getLocation() {
        return vertex;
    }

    public void setLocation(Vertex vertex) {
        this.vertex = vertex;
    }

    @Override
    public Direction getPreferredWireDirection() {
        return null;
    }

    public void connect(WireSegment segment) {
        connectedSegments.add(segment);
    }
}
