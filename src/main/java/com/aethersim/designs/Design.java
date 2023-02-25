package com.aethersim.designs;

import com.aethersim.designs.streams.PointStatistics;
import com.aethersim.designs.templates.DesignedTemplate;
import com.aethersim.designs.templates.SimulatedTemplate;
import com.aethersim.designs.templates.TransistorTemplate;
import com.aethersim.designs.wires.WireNode;
import com.aethersim.designs.wires.WireRoutable;
import com.aethersim.designs.wires.WireSegment;
import com.aethersim.designs.wires.WireWaypoint;
import com.aethersim.projects.io.data.*;
import com.aethersim.utils.Direction;

import java.util.*;
import java.util.stream.Collectors;

public class Design implements DataSerializable {

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

    private final Set<Pin> pins = new LinkedHashSet<>();
    private final Map<Integer, Chip> chips = new LinkedHashMap<>();

    private final Map<Integer, WireNode> wireNodes = new LinkedHashMap<>();
    private final Set<WireSegment> wireSegments = new LinkedHashSet<>();

    private final Map<Direction, Integer> openDistances = new HashMap<>();

    private int transistorCount = 0;

    private final Random idRandom = new Random();

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
        rerouteWires();

        // Recalculate the open distances to each edge and clear the point content cache
        recalculateOpenDistances();
//        clearPointContentCache();
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
        chips.values().forEach(chip -> chip.getTransform().addOffset(xOffset, yOffset));
        wireNodes.values().forEach(node -> node.getTransform().addOffset(xOffset, yOffset));
        wireSegments.stream()
                .map(WireSegment::getRouteWaypoints)
                .flatMap(Arrays::stream)
                .forEach(waypoint -> waypoint.getTransform().addOffset(xOffset, yOffset));
    }

    private void acceptTransformOnAll() {
        // Accept the transform on all pins, chips, and wires
        pins.forEach(Pin::acceptTransform);
        chips.values().forEach(Chip::acceptTransform);
        wireNodes.values().forEach(WireNode::acceptTransform);
        wireSegments.stream()
                .map(WireSegment::getRouteWaypoints)
                .flatMap(Arrays::stream)
                .forEach(WireWaypoint::acceptTransform);
    }

    public Collection<Pin> getPins() {
        return pins;
    }

    public Collection<Chip> getChips() {
        return chips.values();
    }

    private int getUniqueId(Set<Integer> existing) {
        int id = idRandom.nextInt(1, Integer.MAX_VALUE);
        while (existing.contains(id))
            id = idRandom.nextInt(1, Integer.MAX_VALUE);
        return id;
    }

    public void addPin(Pin pin) {
        pins.add(pin);
//        clearPointContentCache();
    }

    public void addPins(Pin... pins) {
        for (Pin pin : pins)
            addPin(pin);
    }

    public void removePin(Pin pin) {
        pins.remove(pin);
//        clearPointContentCache();
    }

    public void removePins(Pin... pins) {
        for (Pin pin : pins)
            removePin(pin);
    }

    public void addChip(Chip chip) {
        // If the chip doesn't have an ID, assign one to it
        if (chip.getId() <= 0)
            chip.setId(getUniqueId(chips.keySet()));

        chips.put(chip.getId(), chip);

        // If the chip has a simulated template, initialize the chip within the template
        if (chip.getChipTemplate() instanceof SimulatedTemplate<?> chipTemplate)
            chipTemplate.initialize(chip);

        // Update transistor count
        if (chip.getChipTemplate() instanceof TransistorTemplate)
            transistorCount++;
        else if (chip.getChipTemplate() instanceof DesignedTemplate designedTemplate)
            transistorCount += designedTemplate.getDesign().getTransistorCount();

//        clearPointContentCache();
    }

    public void addChips(Chip... chips) {
        for (Chip chip : chips)
            addChip(chip);
    }

    public void removeChip(Chip chip) {
        chips.remove(chip.getId());

        // If the chip had a simulated template, dispose of the chip within the template
        if (chip.getChipTemplate() instanceof SimulatedTemplate<?> chipTemplate)
            chipTemplate.dispose(chip);

        // Update transistor count
        if (chip.getChipTemplate() instanceof TransistorTemplate)
            transistorCount--;
        else if (chip.getChipTemplate() instanceof DesignedTemplate designedTemplate)
            transistorCount -= designedTemplate.getDesign().getTransistorCount();

//        clearPointContentCache();
    }

    public void removeChips(Chip... chips) {
        for (Chip chip : chips)
            removeChip(chip);
    }

    public Collection<WireNode> getWireNodes() {
        return wireNodes.values();
    }

    public void addWireNode(WireNode node) {
        // If the node doesn't have an ID, assign one to it
        if (node.getId() <= 0)
            node.setId(getUniqueId(wireNodes.keySet()));

        wireNodes.put(node.getId(), node);
    }

    public void addWireNodes(WireNode... nodes) {
        for (WireNode node : nodes)
            addWireNode(node);
    }

    public void removeWireNode(WireNode node) {
        wireNodes.remove(node.getId());
    }

    public void removeWireNodes(WireNode... nodes) {
        for (WireNode node : nodes)
            removeWireNode(node);
    }

    public Set<WireSegment> getWireSegments() {
        return wireSegments;
    }

    public void addWireSegment(WireSegment wireSegment) {
        wireSegments.add(wireSegment);
    }

    public void addWireSegments(WireSegment... wireSegments) {
        for (WireSegment wireSegment : wireSegments)
            addWireSegment(wireSegment);
    }

    public void addWireSegment(Point startPoint, Point endPoint, Point[] waypointPoints) {
        // Find the endpoints of the new segment and calculate the new waypoints
        WireRoutable start = getWireRoutableAt(startPoint);
        WireRoutable end = getWireRoutableAt(endPoint);
        WireWaypoint[] waypoints = Arrays.stream(waypointPoints).map(WireWaypoint::new).toArray(WireWaypoint[]::new);

        // Create a new wire segment and register it
        addWireSegment(new WireSegment(this, start, end, waypoints));
    }

    private WireRoutable getWireRoutableAt(Point point) {
        // Get the element at the current point
        DesignElement element = getElementAt(point);

        // If the element is null, there is no routable here
        if (element == null)
            return null;

        // Determine which type of element is at the current point.
        // If it is a...
        //   - WireNode - return the element, since it's already part of the design
        //   - ChipPin - return the element, since it's already part of the design
        //   - Pin - wrap the pin in a ChipPin with a null chip
        //   - WireSegment or WireWaypoint - split the wire segment and return the new node
        if (element instanceof WireNode wireNode) {
            return wireNode;
        } else if (element instanceof ChipPin chipPin) {
            return chipPin;
        } else if (element instanceof Pin pin) {
            return new ChipPin(null, pin);
        } else if (element instanceof WireSegment || element instanceof WireWaypoint) {
            // If the start point is a point on a wire segment, we need to split the wire segment
            WireSegment wireSegment;
            if (element instanceof WireSegment segment)
                wireSegment = segment;
            else
                wireSegment = ((WireWaypoint) element).getWireSegment();

            // Now split the segment at the start point
            return wireSegment.splitAt(point);
        } else {
            // If we get here, we didn't recognize the element, so return null
            return null;
        }
    }

    public void removeWireSegment(WireSegment wireSegment) {
        wireSegments.remove(wireSegment);

        // Check if any nodes need to be removed or merged
        cleanHangingNodes();
    }

    public void removeWireSegments(WireSegment... wireSegments) {
        for (WireSegment wireSegment : wireSegments)
            removeWireSegment(wireSegment);
    }

    private void cleanHangingNodes() {
        Map<WireNode, Set<WireSegment>> nodeConnections = wireNodes.values().stream()
                .collect(Collectors.toMap(
                        node -> node,
                        node -> wireSegments.stream()
                                .filter(wireSegment -> wireSegment.getEndpoints().contains(node))
                                .collect(Collectors.toSet())
                ));

        // For each node, if a node has more than two connections, it is still healthy.
        // If it has 2 connections, the segments need to be merged.
        // If it has 1 connection, the connected segment should be removed.
        // If it has none, the node itself should be removed
        for (WireNode node : nodeConnections.keySet()) {
            Set<WireSegment> connections = nodeConnections.get(node);
            if (connections.size() > 2)
                continue;

            if (connections.size() == 2) {
                WireSegment[] segments = connections.toArray(new WireSegment[0]);
                segments[0].mergeAt(segments[1], node);
                wireNodes.remove(node.getId());
                wireSegments.remove(segments[1]);
            } else if (connections.size() == 1) {
                removeWireSegment(connections.iterator().next());
            } else {
                wireNodes.remove(node.getId());
            }
        }
    }

    public void rerouteWires() {
        wireSegments.forEach(WireSegment::reroute);
    }

    public DesignElement getElementAt(Point point) {
        // First, search for pins at the specified point
        Pin matchedPin = pins.stream()
                .filter(pin -> pin.contains(point))
                .findAny()
                .orElse(null);
        if (matchedPin != null)
            return matchedPin;

        // Next, find any pins attached to chips
        ChipPin matchedChipPin = chips.values().stream()
                .map(chip ->
                    chip.getChipTemplate().getPins().stream()
                            .map(pin -> new ChipPin(chip, pin))
                            .collect(Collectors.toSet())
                ).flatMap(Set::stream)
                .filter(chipPin -> chipPin.contains(point))
                .findAny()
                .orElse(null);
        if (matchedChipPin != null)
            return matchedChipPin;

        // Next, find any chips at the specified point
        Chip matchedChip = chips.values().stream()
                .filter(chip -> chip.contains(point))
                .findAny()
                .orElse(null);
        if (matchedChip != null)
            return matchedChip;

        // Next, search for any wire nodes
        WireNode matchedNode = wireNodes.values().stream()
                .filter(node -> node.contains(point))
                .findAny()
                .orElse(null);
        if (matchedNode != null)
            return matchedNode;

        // Next, search for any wire segment waypoints
        WireWaypoint matchedWaypoint = wireSegments.stream()
                .map(WireSegment::getRouteWaypoints)
                .flatMap(Arrays::stream)
                .filter(waypoint -> waypoint.contains(point))
                .findAny()
                .orElse(null);
        if (matchedWaypoint != null)
            return matchedWaypoint;

        // Finally, search for wire segments under the specified point
        return wireSegments.stream()
                .filter(segment -> segment.contains(point))
                .findAny()
                .orElse(null);
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
        PointStatistics chipStatistics = chips.values().parallelStream()
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
        PointStatistics wireNodeStatistics = wireNodes.values().parallelStream()
                .map(WireNode::getLocation)
                .map(point -> Set.of(point.withOffset(-1, -1), point.withOffset(1, 1)))
                .flatMap(Set::stream)
                .collect(PointStatistics.collector());
        PointStatistics wireSegmentStatistics = wireSegments.parallelStream()
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

    public int getOpenDistance(Direction direction) {
        return openDistances.getOrDefault(
                direction,
                (direction == Direction.LEFT || direction.equals(Direction.RIGHT))
                        ? getWidth() - 2
                        : getHeight() - 2
        );
    }

    @Override
    public void deserialize(DataMap data, DataContext context) {

    }

    @Override
    public void serialize(DataMap data, final DataContext context) {
        // Store width and height values
        data.put("Width", DataValue.from(width));
        data.put("Height", DataValue.from(height));

        // Store any pins from the design (avoid adding the pins section if none exist)
        if (getPins().size() > 0) {
            final DataArray pinArray = new DataArray();
            getPins().forEach(pin-> pinArray.add(context.serialize(pin)));
            data.put("Pins", pinArray);
        }

        // Store any chips from the design (avoid adding the chips section if none exist)
        if (getChips().size() > 0) {
            final DataArray chipArray = new DataArray();
            getChips().forEach(chip-> chipArray.add(context.serialize(chip)));
            data.put("Chips", chipArray);
        }

        // Store any wire nodes from the design (avoid adding the wire nodes section if none exist)
        if (getWireNodes().size() > 0) {
            final DataArray wireNodeArray = new DataArray();
            getWireNodes().forEach(wireNode-> wireNodeArray.add(context.serialize(wireNode)));
            data.put("WireNodes", wireNodeArray);
        }

        // Store any wire segments from the design (avoid adding the wires section if none exist)
        if (getWireSegments().size() > 0) {
            final DataArray wireSegmentArray = new DataArray();
            getWireSegments().forEach(wireSegment-> wireSegmentArray.add(context.serialize(wireSegment)));
            data.put("Wires", wireSegmentArray);
        }
    }
}
