package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.ChipPin;
import com.github.chrisblutz.breadboard.designs.exceptions.DesignException;
import com.github.chrisblutz.breadboard.ui.toolkit.exceptions.UIToolkitException;

import java.util.LinkedHashSet;
import java.util.Set;

public class WireSegment {

    private final Set<ChipPin> endpointPins = new LinkedHashSet<>();
    private final Set<WireNode> endpointNodes = new LinkedHashSet<>();

    private final WireVertex[] vertices;

    public WireSegment(WireVertex... vertices) {
        this.vertices = vertices;
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

    public WireVertex[] getVertices() {
        return vertices;
    }

    private void checkSizeConstraints() {
        if (endpointPins.size() + endpointNodes.size() > 2)
            throw new DesignException("Wire segments may not have more than 2 endpoints.");
    }
}
