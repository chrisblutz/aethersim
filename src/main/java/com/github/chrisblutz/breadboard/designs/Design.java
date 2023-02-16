package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.streams.VertexStatistics;
import com.github.chrisblutz.breadboard.designs.templates.DesignedTemplate;
import com.github.chrisblutz.breadboard.designs.templates.SimulatedTemplate;
import com.github.chrisblutz.breadboard.designs.templates.TransistorTemplate;
import com.github.chrisblutz.breadboard.designs.wires.WireNode;
import com.github.chrisblutz.breadboard.designs.wires.WireSegment;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.utils.Direction;

import java.util.*;
import java.util.stream.Collector;

public class Design implements BreadboardSavable {

    private int width;
    public int height;

    private final List<Pin> pins = new ArrayList<>();
    private final List<Chip> chips = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();
    private final Map<ChipPin, Set<Wire>> pinWireConnections = new HashMap<>();

    private final Map<Direction, Integer> openDistances = new HashMap<>();

    private int transistorCount = 0;

    public int getTransistorCount() {
        return transistorCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void resize(int width, int height, boolean shiftChipsX, boolean shiftChipsY) {
        // Move all elements of the design by the offset if necessary, then set the width/height of the design
        int xOffset = shiftChipsX ? width - getWidth() : 0;
        int yOffset = shiftChipsY ? height - getHeight() : 0;

        if (xOffset != 0 || yOffset != 0)
            moveElements(xOffset, yOffset);

        setWidth(width);
        setHeight(height);

        // Recalculate the open distances to each edge
        recalculateOpenDistances();
    }

    private void moveElements(int xOffset, int yOffset) {
        // Move all pins (unless they are at the edge of the design), chips, and wires
        pins.forEach(pin -> {
            // If this pin is on the edge of the design, don't offset it
            int pinX = pin.getDesignLocation().getX();
            int pinY = pin.getDesignLocation().getY();
            pin.setDesignLocation(
                    pin.getDesignLocation().withOffset(
                            (pinX > 0 && pinX < getWidth()) ? xOffset : 0,
                            (pinY > 0 && pinY < getHeight()) ? yOffset : 0
                    )
            );
        });
        chips.forEach(chip -> chip.setLocation(chip.getLocation().withOffset(xOffset, yOffset)));
        wires.forEach(wire -> {
            // Move all segment vertices and nodes
            wire.getNodes().forEach(node -> node.setVertex(node.getVertex().withOffset(xOffset, yOffset)));
            wire.getSegments().forEach(segment -> {
                // Move all vertices
                segment.setVertices(
                        Arrays.stream(segment.getVertices())
                                .map(vertex -> vertex.withOffset(xOffset, yOffset))
                                .toArray(Vertex[]::new)
                );
            });
        });
    }

    public List<Pin> getPins() {
        return pins;
    }

    public List<Chip> getChips() {
        return chips;
    }

    public void addPin(Pin pin) {
        pins.add(pin);
    }

    public void addPins(Pin... pins) {
        for (Pin pin : pins)
            addPin(pin);
    }

    public void removePin(Pin pin) {
        pins.remove(pin);
    }

    public void removePins(Pin... pins) {
        for (Pin pin : pins)
            removePin(pin);
    }

    public void addChip(Chip chip) {
        chips.add(chip);

        // If the chip has a simulated template, initialize the chip within the template
        if (chip.getChipTemplate() instanceof SimulatedTemplate<?> chipTemplate)
            chipTemplate.initialize(chip);

        // Update transistor count
        if (chip.getChipTemplate() instanceof TransistorTemplate)
            transistorCount++;
        else if (chip.getChipTemplate() instanceof DesignedTemplate designedTemplate)
            transistorCount += designedTemplate.getDesign().getTransistorCount();
    }

    public void addChips(Chip... chips) {
        for (Chip chip : chips)
            addChip(chip);
    }

    public void removeChip(Chip chip) {
        chips.remove(chip);

        // If the chip had a simulated template, dispose of the chip within the template
        if (chip.getChipTemplate() instanceof SimulatedTemplate<?> chipTemplate)
            chipTemplate.dispose(chip);

        // Update transistor count
        if (chip.getChipTemplate() instanceof TransistorTemplate)
            transistorCount--;
        else if (chip.getChipTemplate() instanceof DesignedTemplate designedTemplate)
            transistorCount -= designedTemplate.getDesign().getTransistorCount();
    }

    public void removeChips(Chip... chips) {
        for (Chip chip : chips)
            removeChip(chip);
    }

    public List<Wire> getWires() {
        return wires;
    }

    public void addWire(Wire wire) {
        wires.add(wire);

        // Update pin connections
        for (ChipPin pin : wire.getConnectedPins()) {
            if (!pinWireConnections.containsKey(pin))
                pinWireConnections.put(pin, new LinkedHashSet<>());
            pinWireConnections.get(pin).add(wire);
        }
    }

    public void addWires(Wire... wires) {
        for (Wire wire : wires)
            addWire(wire);
    }

    public void removeWire(Wire wire) {
        wires.remove(wire);

        // Remove wire from pin connections
        for (ChipPin pin : wire.getConnectedPins())
            if (pinWireConnections.containsKey(pin))
                pinWireConnections.get(pin).remove(wire);
    }

    public void removeWires(Wire... wires) {
        for (Wire wire : wires)
            removeWire(wire);
    }

    public Set<Wire> getWiresConnectedToPin(ChipPin pin) {
        return pinWireConnections.getOrDefault(pin, new HashSet<>());
    }

    public void recalculateOpenDistances() {
        // Recalculate how much open space exists between each edge and the first component in the design.
        // This value is used when resizing designs in the UI to determine how far a specific edge can
        // be moved before it would interfere with components.

        // For each component, calculate the necessary maximums and minimums
        VertexStatistics pinStatistics = pins.stream()
                .map(Pin::getDesignLocation)
                .collect(VertexStatistics.collector(
                        vertex -> vertex.getX() > 0 && vertex.getX() < getWidth(),
                        vertex -> vertex.getY() > 0 && vertex.getY() < getHeight()
                ));
        // For the chips, enforce a one-square boundary to each edge, so add/remove one where necessary
        VertexStatistics chipStatistics = chips.stream()
                .map(chip -> new Vertex[] {
                        chip.getLocation().withOffset(-1, -1),
                        chip.getLocation().withOffset(
                                chip.getChipTemplate().getWidth() + 1,
                                chip.getChipTemplate().getHeight() + 1
                        )
                }).flatMap(Arrays::stream)
                .collect(VertexStatistics.collector());
        VertexStatistics wireNodeStatistics = wires.stream()
                .map(Wire::getNodes)
                .flatMap(Set::stream)
                .map(WireNode::getVertex)
                .collect(VertexStatistics.collector());
        VertexStatistics wireSegmentStatistics = wires.stream()
                .map(Wire::getSegments)
                .flatMap(Set::stream)
                .map(WireSegment::getVertices)
                .flatMap(Arrays::stream)
                .collect(VertexStatistics.collector());

        // Combine all the statistics objects so we can get one minimum and maximum
        VertexStatistics overallStatistics = pinStatistics.combine(
                chipStatistics,
                wireNodeStatistics,
                wireSegmentStatistics
        );

        // Update all directions' open distances, but constrain them so the maximum possible value is
        // two less than the dimension (so you can't shrink the design to nothing) and the minimum
        // value is 0
        openDistances.put(
                Direction.LEFT,
                Math.min(
                        Math.max(overallStatistics.getMinimumX(), 0),
                        getWidth() - 2
                )
        );
        openDistances.put(
                Direction.RIGHT,
                Math.min(
                        Math.max(getWidth() - overallStatistics.getMaximumX(), 0),
                        getWidth() - 2
                )
        );
        openDistances.put(
                Direction.UP,
                Math.min(
                        Math.max(overallStatistics.getMinimumY(), 0),
                        getHeight() - 2
                )
        );
        openDistances.put(
                Direction.DOWN,
                Math.min(
                        Math.max(getHeight() - overallStatistics.getMaximumY(), 0),
                        getHeight() - 2
                )
        );
    }

    public int getOpenDistance(Direction direction) {
        return openDistances.getOrDefault(
                direction,
                (direction == Direction.LEFT || direction.equals(Direction.RIGHT))
                        ? getWidth() - 2
                        : getHeight() - 2
        );
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        Map<String, Object> yamlMapping = new LinkedHashMap<>();

        // Put basic information
        yamlMapping.put("Width", width);
        yamlMapping.put("Height", height);

        // Construct input array and set the mapping
//        List<Map<String, Object>> inputYamlMapping = new ArrayList<>();
//        for (Pin input : inputs)
//            inputYamlMapping.add(input.dumpToYAML(writer));
//        yamlMapping.put("Inputs", inputYamlMapping);

        // Construct output array and set the mapping
//        List<Map<String, Object>> outputYamlMapping = new ArrayList<>();
//        for (Pin output : outputs)
//            outputYamlMapping.add(output.dumpToYAML(writer));
//        yamlMapping.put("Outputs", outputYamlMapping);

        // Construct chip array and set the mapping
        List<Map<String, Object>> chipYamlMapping = new ArrayList<>();
        for (Chip chip : chips)
            chipYamlMapping.add(chip.dumpToYAML(writer));
        yamlMapping.put("Chips", chipYamlMapping);

        // Construct wire array and set the mapping
        List<Map<String, Object>> wireYamlMapping = new ArrayList<>();
//        for (Wire wire : wires)
//            wireYamlMapping.add(wire.dumpToYAML(writer));
        yamlMapping.put("Wires", wireYamlMapping);

        return yamlMapping;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
