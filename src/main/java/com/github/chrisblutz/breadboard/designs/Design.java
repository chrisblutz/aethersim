package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.streams.PointStatistics;
import com.github.chrisblutz.breadboard.designs.templates.DesignedTemplate;
import com.github.chrisblutz.breadboard.designs.templates.SimulatedTemplate;
import com.github.chrisblutz.breadboard.designs.templates.TransistorTemplate;
import com.github.chrisblutz.breadboard.designs.wires.WireNode;
import com.github.chrisblutz.breadboard.designs.wires.WireSegment;
import com.github.chrisblutz.breadboard.designs.wires.WireWaypoint;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.utils.Direction;

import java.util.*;

public class Design implements BreadboardSavable {

    public enum Content {
        EMPTY,
        EDGE,
        CHIP,
        PIN,
        WIRE_CONTROL,
        OUT_OF_BOUNDS;

        public boolean isEmpty() {
            return this == EMPTY;
        }

        public boolean isOutOfBounds() {
            return this == OUT_OF_BOUNDS;
        }
    }

    private int width;
    public int height;

    private final List<Pin> pins = new ArrayList<>();
    private final List<Chip> chips = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();
    private final Map<ChipPin, Set<Wire>> pinWireConnections = new HashMap<>();

    private final Map<Direction, Integer> openDistances = new HashMap<>();

    private final Map<Point, Content> pointContentCache = new HashMap<>();

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

    public void resize(int width, int height, boolean shiftElementsX, boolean shiftElementsY) {
        // Move all elements of the design by the offset if necessary, then set the width/height of the design
        int xOffset = shiftElementsX ? width - getWidth() : 0;
        int yOffset = shiftElementsY ? height - getHeight() : 0;

        if (xOffset != 0 || yOffset != 0)
            transformAll(xOffset, yOffset);

        // Store the old width/height for calculations and set the new ones
        int oldWidth = getWidth();
        int oldHeight = getHeight();
        setWidth(width);
        setHeight(height);

        // Move all pins on the right and bottom edges to the correct value now that the dimensions have changed
        // Pins at the top and left are anchored at 0, so they don't need to be moved
        pins.forEach(pin -> {
            int pinX = pin.getDesignLocation().getX();
            int pinY = pin.getDesignLocation().getY();
            pin.getTransform().addOffset(
                    (pinX == oldWidth && pinY > 0 && pinY < oldHeight) ? (getWidth() - pinX) : 0,
                    (pinY == oldHeight && pinX > 0 && pinX < oldWidth) ? (getHeight() - pinY) : 0
            );
        });

        // Apply all transforms
        acceptTransformOnAll();

        // Reroute all wires in the design
        wires.stream()
                .map(Wire::getSegments)
                .flatMap(Set::stream)
                .forEach(WireSegment::reroute);

        // Recalculate the open distances to each edge and clear the point content cache
        recalculateOpenDistances();
        clearPointContentCache();
    }

    private void transformAll(int xOffset, int yOffset) {
        // Add to the transform to all chips and wires, and to all pins that aren't locked to the affected edges
        pins.forEach(pin -> {
            int pinX = pin.getDesignLocation().getX();
            int pinY = pin.getDesignLocation().getY();
            pin.getTransform().addOffset(
                (pinX > 0 && pinX < getWidth()) ? xOffset : 0,
                (pinY > 0 && pinY < getHeight()) ? yOffset : 0
            );
        });
        chips.forEach(chip -> chip.getTransform().addOffset(xOffset, yOffset));
        wires.forEach(wire -> {
            wire.getNodes().forEach(node -> node.getTransform().addOffset(xOffset, yOffset));
            wire.getSegments().stream()
                    .map(WireSegment::getRouteWaypoints)
                    .flatMap(Arrays::stream)
                    .forEach(waypoint -> waypoint.getTransform().addOffset(xOffset, yOffset));
        });
    }

    private void acceptTransformOnAll() {
        // Accept the transform on all pins, chips, and wires
        pins.forEach(Pin::acceptTransform);
        chips.forEach(DesignElement::acceptTransform);
        wires.forEach(wire -> {
            wire.getNodes().forEach(WireNode::acceptTransform);
            wire.getSegments().stream()
                    .map(WireSegment::getRouteWaypoints)
                    .flatMap(Arrays::stream)
                    .forEach(WireWaypoint::acceptTransform);
        });
    }

//    private void moveElements(int xOffset, int yOffset) {
//        // Move all pins (unless they are at the edge of the design), chips, and wires
//        pins.forEach(pin -> {
//            int pinX = pin.getDesignLocation().getX();
//            int pinY = pin.getDesignLocation().getY();
//            pin.setDesignLocation(
//                    pin.getDesignLocation().withOffset(
//                            (pinX > 0 && pinX < getWidth()) ? xOffset : 0,
//                            (pinY > 0 && pinY < getHeight()) ? yOffset : 0
//                    )
//            );
//        });
//        chips.forEach(chip -> chip.setLocation(chip.getLocation().withOffset(xOffset, yOffset)));
//        wires.forEach(wire -> {
//            // Move all segment waypoints and nodes
//            wire.getNodes().forEach(node -> node.setLocation(node.getLocation().withOffset(xOffset, yOffset)));
//            wire.getSegments().forEach(segment -> {
//                segment.setRouteWaypoints(
//                        Arrays.stream(segment.getRouteWaypoints())
//                                .map(waypoint -> waypoint.withOffset(xOffset, yOffset))
//                                .toArray(Point[]::new)
//                );
//            });
//        });
//
//        clearPointContentCache();
//    }

    public List<Pin> getPins() {
        return pins;
    }

    public List<Chip> getChips() {
        return chips;
    }

    public void addPin(Pin pin) {
        pins.add(pin);
        clearPointContentCache();
    }

    public void addPins(Pin... pins) {
        for (Pin pin : pins)
            addPin(pin);
    }

    public void removePin(Pin pin) {
        pins.remove(pin);
        clearPointContentCache();
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

        clearPointContentCache();
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

        clearPointContentCache();
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

        clearPointContentCache();
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

        clearPointContentCache();
    }

    public void removeWires(Wire... wires) {
        for (Wire wire : wires)
            removeWire(wire);
    }

    public void rerouteWires() {
        wires.stream()
                .map(Wire::getSegments)
                .flatMap(Set::stream)
                .forEach(WireSegment::reroute);
    }

    public Set<Wire> getWiresConnectedToPin(ChipPin pin) {
        return pinWireConnections.getOrDefault(pin, new HashSet<>());
    }

    public void recalculateOpenDistances() {
        // Recalculate how much open space exists between each edge and the first component in the design.
        // This value is used when resizing designs in the UI to determine how far a specific edge can
        // be moved before it would interfere with components.

        // For each component, calculate the necessary maximums and minimums
        PointStatistics pinStatistics = pins.parallelStream()
                .map(Pin::getDesignLocation)
                .collect(PointStatistics.collector(
                        point -> point.getX() > 0 && point.getX() < getWidth(),
                        point -> point.getY() > 0 && point.getY() < getHeight()
                ));
        // For the chips, enforce a one-square boundary to each edge, so add/remove one where necessary
        PointStatistics chipStatistics = chips.parallelStream()
                .map(chip -> new Point[] {
                        chip.getLocation().withOffset(-1, -1),
                        chip.getLocation().withOffset(
                                chip.getChipTemplate().getWidth() + 1,
                                chip.getChipTemplate().getHeight() + 1
                        )
                }).flatMap(Arrays::stream)
                .collect(PointStatistics.collector());
        // Add a buffer around nodes and waypoints to ensure these cannot reside on the edge of a design
        // Wires will still be routable around edges, but only if necessary, and if the wire router
        // cannot find another way to get them to their pins
        PointStatistics wireNodeStatistics = wires.parallelStream()
                .map(Wire::getNodes)
                .flatMap(Set::stream)
                .map(WireNode::getLocation)
                .map(point -> Set.of(point.withOffset(-1, -1), point.withOffset(1, 1)))
                .flatMap(Set::stream)
                .collect(PointStatistics.collector());
        PointStatistics wireSegmentStatistics = wires.parallelStream()
                .map(Wire::getSegments)
                .flatMap(Set::stream)
                .map(WireSegment::getRouteWaypoints)
                .flatMap(Arrays::stream)
                .map(waypoint -> Set.of(waypoint.getLocation().withOffset(-1, -1), waypoint.getLocation().withOffset(1, 1)))
                .flatMap(Set::stream)
                .collect(PointStatistics.collector());

        // Combine all the statistics objects so we can get one minimum and maximum
        PointStatistics overallStatistics = pinStatistics.combine(
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

    public Content getPointContents(Point point) {
        // If the point is already in the cache, return the cached value
        if (pointContentCache.containsKey(point))
            return pointContentCache.get(point);

        // Assume the space is empty unless we find something there
        Content pointContent = Content.EMPTY;

        // Start by eliminating any out-of-bounds points
        int x = point.getX();
        int y = point.getY();
        if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
            pointContent = Content.OUT_OF_BOUNDS;
        } else if (isPinAt(point)) {
            pointContent = Content.PIN;
        } else if (x == 0 || x == getWidth() || y == 0 || y == getHeight()) {
            pointContent = Content.EDGE;
        } else if (isWireControlPointAt(point)) {
            pointContent = Content.WIRE_CONTROL;
        } else if (isChipAt(point)) {
            pointContent = Content.CHIP;
        }

        // Cache the result and then return it
        pointContentCache.put(point, pointContent);
        return pointContent;
    }

    private boolean isPinAt(final Point point) {
        return pins.parallelStream()
                .anyMatch(pin -> pin.getDesignLocation().equals(point));
    }

    private boolean isWireControlPointAt(final Point point) {
        return (
                wires.parallelStream()
                        .map(Wire::getNodes)
                        .flatMap(Set::stream)
                        .anyMatch(node -> node.getLocation().equals(point))
        ) || (
                wires.parallelStream()
                        .map(Wire::getSegments)
                        .flatMap(Set::stream)
                        .map(WireSegment::getRouteWaypoints)
                        .flatMap(Arrays::stream)
                        .anyMatch(waypoint -> waypoint.equals(point))
        );
    }

    private boolean isChipAt(final Point point) {
        return chips.parallelStream()
                .anyMatch(chip -> chip.getBoundingBox().contains(point.getX(), point.getY()));
    }

    protected void clearPointContentCache() {
        pointContentCache.clear();
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
