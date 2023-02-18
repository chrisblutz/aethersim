package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.wires.WireNode;
import com.github.chrisblutz.breadboard.designs.wires.WireRoutable;
import com.github.chrisblutz.breadboard.designs.wires.WireSegment;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Wire implements BreadboardSavable {

    private final Set<WireSegment> segments = new LinkedHashSet<>();

    public Set<WireNode> getNodes() {
        return segments.stream()
                .map(WireSegment::getEndpoints)
                .flatMap(Arrays::stream)
                .filter(endpoint -> endpoint instanceof WireNode)
                .map(endpoint -> (WireNode) endpoint)
                .collect(Collectors.toSet());
    }

    public Set<ChipPin> getConnectedPins() {
        return segments.stream()
                .map(WireSegment::getEndpoints)
                .flatMap(Arrays::stream)
                .filter(endpoint -> endpoint instanceof ChipPin)
                .map(endpoint -> (ChipPin) endpoint)
                .collect(Collectors.toSet());
    }

    public Set<WireSegment> getSegments() {
        return segments;
    }

    public void addSegment(final WireSegment segment) {
        segments.add(segment);
    }

    public void addSegments(WireSegment... segments) {
        // Add each segment individually
        for (WireSegment segment : segments)
            addSegment(segment);
    }

    public void merge(Wire other) {
        segments.addAll(other.segments);
        other.segments.clear();
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
