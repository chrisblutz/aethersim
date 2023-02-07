package com.github.chrisblutz.breadboard.ui.render.designs;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.ChipPin;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Ellipse;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Rectangle;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.RoundRectangle;
import com.github.chrisblutz.breadboard.utils.Vertex;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DesignRenderer {

    private static final double PIN_RADIUS = 0.4;
    private static final double WIRE_ARC_MIDPOINT_GUIDE = 0.5 - ((Math.sqrt(2) / 2) / 2);

    private final Map<ChipPin, Ellipse> pinShapes = new LinkedHashMap<>();
    private final Map<Wire, Path2D.Double> wireShapes = new LinkedHashMap<>();
    private final Map<Chip, RoundRectangle> chipShapes = new LinkedHashMap<>();

    public Ellipse getPinShape(ChipPin pin) {
        return pinShapes.get(pin);
    }

    public Path2D.Double getWireShape(Wire wire) {
        return wireShapes.get(wire);
    }

    public RoundRectangle getChipShape(Chip chip) {
        return chipShapes.get(chip);
    }

    public RoundRectangle getNewChipShape(Chip chip, int x, int y) {
        RoundRectangle chipShape = new RoundRectangle(
                x,
                y,
                chip.getChipTemplate().getWidth(),
                chip.getChipTemplate().getHeight(),
                0.75,
                0.75 // TODO default
        );
        return chipShape;
    }

    public Ellipse getNewPinShape(Chip chip, Pin pin, int x, int y) {
        // Calculate pin X/Y based on whether it's attached to a chip
        double pinX = (chip == null ? pin.getDesignX() : x + pin.getChipX());
        double pinY = (chip == null ? pin.getDesignY() : y + pin.getChipY());
        // Create the shape
        Ellipse pinShape = new Ellipse(
                (pinX - PIN_RADIUS),
                (pinY - PIN_RADIUS),
                (PIN_RADIUS * 2),
                (PIN_RADIUS * 2)
        );
        return pinShape;
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
        wireShapes.clear();
        chipShapes.clear();

        // Generate shapes for all chips in the design
        for (Chip chip : design.getChips()) {
            generateChipShape(chip);

            // Generate shapes for all pins in the chip
            for (Pin pin : chip.getChipTemplate().getPins()) {
                generatePinShape(chip, pin);
            }
        }

        // Generate shapes for all wires in the design
        for (Wire wire : design.getWires()) {
            generateWireShape(wire);
        }

        // Generate shapes for all pins in the design
        for (Pin pin : design.getPins()) {
            generatePinShape(null, pin);
        }
    }

    private void generateChipShape(Chip chip) {
        RoundRectangle chipShape = new RoundRectangle(
                chip.getX(),
                chip.getY(),
                chip.getChipTemplate().getWidth(),
                chip.getChipTemplate().getHeight(),
                0.75,
                0.75 // TODO default
        );
        chipShapes.put(chip, chipShape);
    }

    private void generatePinShape(Chip chip, Pin pin) {
        // Calculate pin X/Y based on whether it's attached to a chip
        double pinX = (chip == null ? pin.getDesignX() : chip.getX() + pin.getChipX());
        double pinY = (chip == null ? pin.getDesignY() : chip.getY() + pin.getChipY());
        // Create the shape
        Ellipse pinShape = new Ellipse(
                (pinX - PIN_RADIUS),
                (pinY - PIN_RADIUS),
                (PIN_RADIUS * 2),
                (PIN_RADIUS * 2)
        );
        pinShapes.put(new ChipPin(chip, pin), pinShape);
    }

    private void generateWireShape(Wire wire) {
        Path2D.Double wireShape = new Path2D.Double();

        // Build path based on wire vertices
        Vertex[] vertices = wire.getVertices();
        for (int vertexIndex = 0; vertexIndex < vertices.length - 1; vertexIndex++) {
            Vertex vertexStart = vertices[vertexIndex];
            Vertex vertexEnd = vertices[vertexIndex + 1];

            // Calculate the distance between these vertices
            // Since wire segments must be either horizontal or vertical, we can simplify
            int length = Math.abs((vertexEnd.getX() - vertexStart.getX()) + (vertexEnd.getY() - vertexStart.getY()));
            int xOffset = (vertexEnd.getX() - vertexStart.getX()) / length;
            int yOffset = (vertexEnd.getY() - vertexStart.getY()) / length;

            for (int segmentIndex = 0; segmentIndex < length; segmentIndex++) {
                int startX = vertexStart.getX() + (xOffset * segmentIndex);
                int startY = vertexStart.getY() + (yOffset * segmentIndex);
                int endX = vertexStart.getX() + (xOffset * (segmentIndex + 1));
                int endY = vertexStart.getY() + (yOffset * (segmentIndex + 1));

                // If this is the first point on the path, set the initial point
                if (vertexIndex == 0 && segmentIndex == 0)
                    wireShape.moveTo(startX, startY);

                // If we're on a corner, render segments in halves
                if ((vertexIndex > 0 && segmentIndex == 0) || (vertexIndex < vertices.length - 2 && segmentIndex == length - 1)) {
                    // Since we can have a corner directly into another corner, handle both cases here
                    double edgeMidX = startX + ((double) xOffset / 2);
                    double edgeMidY = startY + ((double) yOffset / 2);

                    // If we're coming out of a corner, draw the second half of the corner
                    // Otherwise, draw the straight line
                    if (vertexIndex > 0 && segmentIndex == 0) {
                        // Calculate the quad control point
                        double quadControlX = startX + (2 * xOffset * WIRE_ARC_MIDPOINT_GUIDE);
                        double quadControlY = startY + (2 * yOffset * WIRE_ARC_MIDPOINT_GUIDE);

                        // Draw the quad curve
                        wireShape.quadTo(quadControlX, quadControlY, edgeMidX, edgeMidY);
                    } else {
                        // Draw the straight line
                        wireShape.lineTo(edgeMidX, edgeMidY);
                    }

                    // If we're going into a corner, draw the first half of a corner
                    // Otherwise, draw the straight line
                    if (vertexIndex < vertices.length - 2 && segmentIndex == length - 1) {
                        // Determine next vertex direction
                        Vertex nextEnd = vertices[vertexIndex + 2];
                        int nextLength = Math.abs((nextEnd.getX() - vertexEnd.getX()) + (nextEnd.getY() - vertexEnd.getY()));
                        int nextXOffset = (nextEnd.getX() - vertexEnd.getX()) / nextLength;
                        int nextYOffset = (nextEnd.getY() - vertexEnd.getY()) / nextLength;

                        // Calculate the "center" of the arc the corners will follow
                        int cornerArcMidXOffset = nextXOffset - xOffset;
                        int cornerArcMidYOffset = nextYOffset - yOffset;
                        double cornerArcMidX = endX + (cornerArcMidXOffset * WIRE_ARC_MIDPOINT_GUIDE);
                        double cornerArcMidY = endY + (cornerArcMidYOffset * WIRE_ARC_MIDPOINT_GUIDE);

                        // Calculate the quad control point
                        double quadControlX = endX + (-2 * xOffset * WIRE_ARC_MIDPOINT_GUIDE);
                        double quadControlY = endY + (-2 * yOffset * WIRE_ARC_MIDPOINT_GUIDE);

                        // Draw the quad curve
                        wireShape.quadTo(quadControlX, quadControlY, cornerArcMidX, cornerArcMidY);
                    } else {
                        // Draw the straight line
                        wireShape.lineTo(endX, endY);
                    }
                } else {
                    // Draw the straight line
                    wireShape.lineTo(endX, endY);
                }
            }
        }

        wireShapes.put(wire, wireShape);
    }
}
