package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.ChipPin;
import com.github.chrisblutz.breadboard.designs.Vertex;
import com.github.chrisblutz.breadboard.designs.exceptions.DesignException;

import java.util.LinkedHashSet;
import java.util.Set;

public class WireSegment {

    private final Set<ChipPin> endpointPins = new LinkedHashSet<>();
    private final Set<WireNode> endpointNodes = new LinkedHashSet<>();

    private Vertex[] vertices;

    public WireSegment(Vertex... vertices) {
        this.vertices = vertices;
    }

    public WireSegment(ChipPin endpoint1, ChipPin endpoint2, Vertex... vertices) {
        this(vertices);

        addEndpoint(endpoint1);
        addEndpoint(endpoint2);
    }

    public WireSegment(ChipPin endpoint1, WireNode endpoint2, Vertex... vertices) {
        this(vertices);

        addEndpoint(endpoint1);
        addEndpoint(endpoint2);
    }

    public WireSegment(WireNode endpoint1, ChipPin endpoint2, Vertex... vertices) {
        this(vertices);

        addEndpoint(endpoint1);
        addEndpoint(endpoint2);
    }

    public WireSegment(WireNode endpoint1, WireNode endpoint2, Vertex... vertices) {
        this(vertices);

        addEndpoint(endpoint1);
        addEndpoint(endpoint2);
    }

    public Set<ChipPin> getEndpointPins() {
        return endpointPins;
    }

    public void addEndpoint(ChipPin pin) {
        // Check the size of the node, as we can only have 2 endpoints
        checkSizeConstraints();
        endpointPins.add(pin);
    }

    public Set<WireNode> getEndpointNodes() {
        return endpointNodes;
    }

    public void addEndpoint(WireNode node) {
        // Check the size of the node, as we can only have 2 endpoints
        checkSizeConstraints();
        endpointNodes.add(node);
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public void setVertices(Vertex[] vertices) {
        this.vertices = vertices;
    }

    private void checkSizeConstraints() {
        if (endpointPins.size() + endpointNodes.size() > 2)
            throw new DesignException("Wire segments may not have more than 2 endpoints.");
    }
}
