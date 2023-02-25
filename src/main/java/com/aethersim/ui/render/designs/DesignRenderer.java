package com.aethersim.ui.render.designs;

import com.aethersim.designs.*;
import com.aethersim.designs.wires.WireNode;
import com.aethersim.designs.wires.WireSegment;
import com.aethersim.ui.toolkit.shape.Ellipse;
import com.aethersim.ui.toolkit.shape.RoundRectangle;

import java.awt.geom.Path2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class DesignRenderer {

    private static final double PIN_RADIUS = 0.4;
    private static final double NODE_RADIUS = 0.3;
    private static final double WIRE_ARC_MIDPOINT_GUIDE = 0.5 - ((Math.sqrt(2) / 2) / 2);

    private final Map<ChipPin, Ellipse> pinShapes = new LinkedHashMap<>();
    private final Map<WireNode, Ellipse> wireNodeShapes = new LinkedHashMap<>();
    private final Map<WireSegment, Path2D.Double> wireSegmentShapes = new LinkedHashMap<>();
    private final Map<Chip, RoundRectangle> chipShapes = new LinkedHashMap<>();

    public Ellipse getPinShape(ChipPin pin) {
        return pinShapes.get(pin);
    }

    public Ellipse getWireNodeShape(WireNode node) {
        return wireNodeShapes.get(node);
    }

    public Path2D.Double getWireSegmentShape(WireSegment segment) {
        return wireSegmentShapes.get(segment);
    }

    public RoundRectangle getChipShape(Chip chip) {
        return chipShapes.get(chip);
    }

    public RoundRectangle getNewChipShape(Chip chip, int x, int y) {
        return new RoundRectangle(
                x,
                y,
                chip.getChipTemplate().getWidth(),
                chip.getChipTemplate().getHeight(),
                0.75,
                0.75 // TODO default
        );
    }

    public Ellipse getNewPinShape(Chip chip, Pin pin, int x, int y) {
        // Calculate pin X/Y based on whether it's attached to a chip
        double pinX = (chip == null ? pin.getDesignLocation().getX() : x + pin.getChipLocation().getX());
        double pinY = (chip == null ? pin.getDesignLocation().getY() : y + pin.getChipLocation().getY());
        // Create the shape
        return new Ellipse(
                (pinX - PIN_RADIUS),
                (pinY - PIN_RADIUS),
                (PIN_RADIUS * 2),
                (PIN_RADIUS * 2)
        );
    }

    public Path2D.Double getNewWireSegmentShape(WireSegment wireSegment) {
        return generateWireSegmentPath(wireSegment);
    }

    public ChipPin getHoveredPin(double x, double y) {
        // Check each pin to see if the coordinates are within it
        for (ChipPin pin : pinShapes.keySet())
            if (pinShapes.get(pin).contains(x, y))
                return pin;

        return null;
    }

    public Chip getHoveredChip(double x, double y) {
        // Check each chip to see if the coordinates are within it
        for (Chip chip : chipShapes.keySet())
            if (chipShapes.get(chip).contains(x, y))
                return chip;

        return null;
    }

    public void generate(Design design) {
        // Clear all existing shapes
        pinShapes.clear();
        wireNodeShapes.clear();
        wireSegmentShapes.clear();
        chipShapes.clear();

        // Generate shapes for all chips in the design
        for (Chip chip : design.getChips()) {
            generateChipShape(chip);

            // Generate shapes for all pins in the chip
            for (Pin pin : chip.getChipTemplate().getPins()) {
                generatePinShape(chip, pin);
            }
        }

        // Generate shapes for all wire nodes in the design
        for (WireNode wireNode : design.getWireNodes()) {
            generateWireNodeShape(wireNode);
        }

        // Generate shapes for all wire segments in the design
        for (WireSegment wireSegment : design.getWireSegments()) {
            generateWireSegmentShape(wireSegment);
        }

        // Generate shapes for all pins in the design
        for (Pin pin : design.getPins()) {
            generatePinShape(null, pin);
        }
    }

    private void generateChipShape(Chip chip) {
        RoundRectangle chipShape = new RoundRectangle(
                chip.getLocation().getX(),
                chip.getLocation().getY(),
                chip.getChipTemplate().getWidth(),
                chip.getChipTemplate().getHeight(),
                0.75,
                0.75 // TODO default
        );
        chipShapes.put(chip, chipShape);
    }

    private void generatePinShape(Chip chip, Pin pin) {
        // Calculate pin X/Y based on whether it's attached to a chip
        double pinX = (chip == null ? pin.getDesignLocation().getX() : chip.getLocation().getX() + pin.getChipLocation().getX());
        double pinY = (chip == null ? pin.getDesignLocation().getY() : chip.getLocation().getY() + pin.getChipLocation().getY());
        // Create the shape
        Ellipse pinShape = new Ellipse(
                (pinX - PIN_RADIUS),
                (pinY - PIN_RADIUS),
                (PIN_RADIUS * 2),
                (PIN_RADIUS * 2)
        );
        pinShapes.put(new ChipPin(chip, pin), pinShape);
    }

    private void generateWireNodeShape(WireNode wireNode) {
        Ellipse nodeShape = new Ellipse(
                (wireNode.getLocation().getX() - NODE_RADIUS),
                (wireNode.getLocation().getY() - NODE_RADIUS),
                (NODE_RADIUS * 2),
                (NODE_RADIUS * 2)
        );
        wireNodeShapes.put(wireNode, nodeShape);
    }

    private void generateWireSegmentShape(WireSegment segment) {
        wireSegmentShapes.put(segment, generateWireSegmentPath(segment));
    }

    private Path2D.Double generateWireSegmentPath(WireSegment segment) {
        Path2D.Double segmentPath = new Path2D.Double();

        // Build path based on wire segment points
        Point[] points = segment.getRoutePoints();

        // If the points array is null, don't generate anything
        if (points == null)
            return new Path2D.Double();

        for (int pointIndex = 0; pointIndex < points.length - 1; pointIndex++) {
            Point startPoint = points[pointIndex];
            Point endPoint = points[pointIndex + 1];

            // Calculate the distance between these points
            // Since wire segments must be either horizontal or vertical, we can simplify
            int length = Math.abs((endPoint.getX() - startPoint.getX()) + (endPoint.getY() - startPoint.getY()));
            int xOffset = (endPoint.getX() - startPoint.getX()) / length;
            int yOffset = (endPoint.getY() - startPoint.getY()) / length;

            for (int segmentIndex = 0; segmentIndex < length; segmentIndex++) {
                int startX = startPoint.getX() + (xOffset * segmentIndex);
                int startY = startPoint.getY() + (yOffset * segmentIndex);
                int endX = startPoint.getX() + (xOffset * (segmentIndex + 1));
                int endY = startPoint.getY() + (yOffset * (segmentIndex + 1));

                // If this is the first point on the path, set the initial point
                if (pointIndex == 0 && segmentIndex == 0)
                    segmentPath.moveTo(startX, startY);

                // If we're on a corner, render segments in halves
                if ((pointIndex > 0 && segmentIndex == 0) || (pointIndex < points.length - 2 && segmentIndex == length - 1)) {
                    // Since we can have a corner directly into another corner, handle both cases here
                    double edgeMidX = startX + ((double) xOffset / 2);
                    double edgeMidY = startY + ((double) yOffset / 2);

                    // If we're coming out of a corner, draw the second half of the corner
                    // Otherwise, draw the straight line
                    if (pointIndex > 0 && segmentIndex == 0) {
                        // Calculate the quad control point
                        double quadControlX = startX + (2 * xOffset * WIRE_ARC_MIDPOINT_GUIDE);
                        double quadControlY = startY + (2 * yOffset * WIRE_ARC_MIDPOINT_GUIDE);

                        // Draw the quad curve
                        segmentPath.quadTo(quadControlX, quadControlY, edgeMidX, edgeMidY);
                    } else {
                        // Draw the straight line
                        segmentPath.lineTo(edgeMidX, edgeMidY);
                    }

                    // If we're going into a corner, draw the first half of a corner
                    // Otherwise, draw the straight line
                    if (pointIndex < points.length - 2 && segmentIndex == length - 1) {
                        // Determine next point direction
                        Point nextEnd = points[pointIndex + 2];
                        int nextLength = Math.abs((nextEnd.getX() - endPoint.getX()) + (nextEnd.getY() - endPoint.getY()));
                        int nextXOffset = (nextEnd.getX() - endPoint.getX()) / nextLength;
                        int nextYOffset = (nextEnd.getY() - endPoint.getY()) / nextLength;

                        // Calculate the "center" of the arc the corners will follow
                        int cornerArcMidXOffset = nextXOffset - xOffset;
                        int cornerArcMidYOffset = nextYOffset - yOffset;
                        double cornerArcMidX = endX + (cornerArcMidXOffset * WIRE_ARC_MIDPOINT_GUIDE);
                        double cornerArcMidY = endY + (cornerArcMidYOffset * WIRE_ARC_MIDPOINT_GUIDE);

                        // Calculate the quad control point
                        double quadControlX = endX + (-2 * xOffset * WIRE_ARC_MIDPOINT_GUIDE);
                        double quadControlY = endY + (-2 * yOffset * WIRE_ARC_MIDPOINT_GUIDE);

                        // Draw the quad curve
                        segmentPath.quadTo(quadControlX, quadControlY, cornerArcMidX, cornerArcMidY);
                    } else {
                        // Draw the straight line
                        segmentPath.lineTo(endX, endY);
                    }
                } else {
                    // Draw the straight line
                    segmentPath.lineTo(endX, endY);
                }
            }
        }

        return segmentPath;
    }
}
