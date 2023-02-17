package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.wires.WireNode;
import com.github.chrisblutz.breadboard.designs.wires.WireRoutable;
import com.github.chrisblutz.breadboard.designs.wires.WireSegment;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Wire implements BreadboardSavable {

    private final Set<WireNode> nodes = new LinkedHashSet<>();
    private final Set<ChipPin> connectedPins = new LinkedHashSet<>();
    private final Set<WireSegment> segments = new LinkedHashSet<>();

    public Set<WireNode> getNodes() {
        return nodes;
    }

    public Set<ChipPin> getConnectedPins() {
        return connectedPins;
    }

    public Set<WireSegment> getSegments() {
        return segments;
    }

    public void addSegment(final WireSegment segment) {
        segments.add(segment);
        // "Connect" this segment to its nodes and pins
        Set.of(segment.getStart(), segment.getEnd())
                .forEach(endpoint -> {
                    if (endpoint instanceof WireNode node) {
                        node.connect(segment);
                        nodes.add(node);
                    } else if (endpoint instanceof ChipPin pin) {
                        connectedPins.add(pin);
                    }
                }
        );
    }

    public void addSegments(WireSegment... segments) {
        // Add each segment individually
        for (WireSegment segment : segments)
            addSegment(segment);
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
