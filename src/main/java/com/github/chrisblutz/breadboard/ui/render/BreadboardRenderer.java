package com.github.chrisblutz.breadboard.ui.render;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulation.components.Node;
import com.github.chrisblutz.breadboard.simulation.components.NodeConnector;
import com.github.chrisblutz.breadboard.simulation.generator.DesignInstance;
import com.github.chrisblutz.breadboard.utils.Vertex;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to render a single design.
 */
public class BreadboardRenderer {

    private static final Color DESIGNER_BACKGROUND = new Color(30, 34, 46);
    private static final Color DESIGNER_BORDER = new Color(36, 41, 56);
    private static final Color DESIGNER_GRID_DOTS = new Color(48, 55, 74);

    private static final Color CHIP_FILL = new Color(26, 26, 26);
    private static final Color CHIP_FONT_COLOR = new Color(146, 146, 146);

    private static final Color PIN_INACTIVE_BORDER = new Color(10, 1, 1);
    private static final Color PIN_INACTIVE_FILL = new Color(20, 1, 1);
    private static final Color PIN_ACTIVE_BORDER = new Color(74, 7, 7);
    private static final Color PIN_ACTIVE_FILL = new Color(132, 8, 8);

    private static final int GRID_SQUARE_SIZE = 20;
    private static final int GRID_DOT_DIAMETER = (GRID_SQUARE_SIZE / 10);
    private static final int CHIP_BORDER_WIDTH = (GRID_SQUARE_SIZE / 6);
    private static final int PIN_DIAMETER = (GRID_SQUARE_SIZE * 4 / 5);
    private static final int WIRE_WIDTH = (GRID_SQUARE_SIZE * 2 / 5);

    private static final float CORNER_ARC_MIDPOINT_DISTANCE = ((float) GRID_SQUARE_SIZE / 2) - (((float) Math.sqrt(2) / 2) * ((float)GRID_SQUARE_SIZE / 2));

    private final Design design;
    private final DesignInstance designInstance;

    public BreadboardRenderer(Design design) {
        this(design, null);
    }

    public BreadboardRenderer(Design design, DesignInstance designInstance) {
        this.design = design;
        this.designInstance = designInstance;
    }

    public void render(Graphics2D g, int width, int height) {
        g.setColor(DESIGNER_BACKGROUND);
        g.fillRect(0, 0, width, height);

        g.setColor(DESIGNER_GRID_DOTS);
        int gridSquareCountX = width / GRID_SQUARE_SIZE;
        int gridSquareCountY = height / GRID_SQUARE_SIZE;

        for (int x = 0; x <= gridSquareCountX; x++) {
            for (int y = 0; y <= gridSquareCountY; y++) {
                g.fillOval((x * GRID_SQUARE_SIZE) - (GRID_DOT_DIAMETER / 2) + 20, (y * GRID_SQUARE_SIZE) - (GRID_DOT_DIAMETER / 2) + 20, GRID_DOT_DIAMETER, GRID_DOT_DIAMETER);
            }
        }
        drawDesign(g, design);

//        Path2D.Float test = new Path2D.Float();
//        test.moveTo(500, 200);
//        test.lineTo(500, 150);
//        //test.quadTo(500 - ((Math.sqrt(2) / 2) * 50) + (50 - ((Math.sqrt(2) / 2) * 50)),150, 500 - ((Math.sqrt(2) / 2) * 50), 200 - ((Math.sqrt(2) / 2) * 50));
//        test.curveTo(500, 150 - (50 * 0.4), 500 - ((Math.sqrt(2) / 2) * 1.4 * 50),200 - ((Math.sqrt(2) / 2) * 1.4 * 50), 500 - ((Math.sqrt(2) / 2) * 50), 200 - ((Math.sqrt(2) / 2) * 50));
//        test.lineTo(500, 200);
//        g.setStroke(new BasicStroke(10, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
//        g.setColor(Color.BLACK);
//        g.fill(test);
    }

    private void drawDesign(Graphics2D g, Design design) {
        // Draw all design chips
        for (Chip chip : design.getChips())
            drawChip((Graphics2D) g.create(), chip);

        // Draw all design pins
        for (Pin pin : design.getPins())
            drawDesignPin((Graphics2D) g.create(), pin, designInstance != null ? designInstance.getNodeForPin(pin) : null);

        // Draw all design wires
        for (Wire wire : design.getWires())
            drawWire((Graphics2D) g.create(), wire, designInstance != null ? designInstance.getNodeConnectorForWire(wire) : null);
    }

    private void drawDesignPin(Graphics2D g, Pin pin, Node simulationNode) {
        // Determine whether this pin is active
        boolean pinActive = simulationNode != null && simulationNode.isActive();
        drawPin(g, getActualX(pin.getDesignX()), getActualY(pin.getDesignY()), pinActive);
    }

    private void drawChipPin(Graphics2D g, Chip chip, Pin pin, Node simulationNode) {
        // Determine whether this pin is active
        boolean pinActive = simulationNode != null && simulationNode.isActive();
        drawPin(g, getActualX(chip.getX() + pin.getChipX()), getActualY(chip.getY() + pin.getChipY()), pinActive);
    }

    private void drawPin(Graphics2D g, int x, int y, boolean active) {
        g.setColor(active ? PIN_ACTIVE_FILL : PIN_INACTIVE_FILL);
        g.fillOval(x - (PIN_DIAMETER / 2), y - (PIN_DIAMETER / 2), PIN_DIAMETER, PIN_DIAMETER);
        g.setColor(active ? PIN_ACTIVE_BORDER : PIN_INACTIVE_BORDER);
        Stroke current = g.getStroke();
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - (PIN_DIAMETER / 2), y - (PIN_DIAMETER / 2), PIN_DIAMETER, PIN_DIAMETER);
        g.setStroke(current);
    }

    private void drawWire(Graphics2D g, Wire wire, NodeConnector simulationNodeConnector) {
        Path2D.Float wirePath = new Path2D.Float();

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
                    wirePath.moveTo(getActualX(startX), getActualY(startY));

                // If we're on a corner, render segments in halves
                if ((vertexIndex > 0 && segmentIndex == 0) || (vertexIndex < vertices.length - 2 && segmentIndex == length - 1)) {
                    // Since we can have a corner directly into another corner, handle both cases here
                    float edgeMidX = getActualX(startX) + (xOffset * ((float) GRID_SQUARE_SIZE / 2));
                    float edgeMidY = getActualY(startY) + (yOffset * ((float) GRID_SQUARE_SIZE / 2));

                    // If we're coming out of a corner, draw the second half of the corner
                    // Otherwise, draw the straight line
                    if (vertexIndex > 0 && segmentIndex == 0) {
                        // Determine previous vertex direction
                        Vertex previousStart = vertices[vertexIndex - 1];
                        int previousLength = Math.abs((vertexStart.getX() - previousStart.getX()) + (vertexStart.getY() - previousStart.getY()));
                        int previousXOffset = (vertexStart.getX() - previousStart.getX()) / previousLength;
                        int previousYOffset = (vertexStart.getY() - previousStart.getY()) / previousLength;

                        // Calculate the "center" of the arc the corners will follow
                        int cornerArcMidXOffset = xOffset - previousXOffset;
                        int cornerArcMidYOffset = yOffset - previousYOffset;
                        float cornerArcMidX = getActualX(startX) + (cornerArcMidXOffset * CORNER_ARC_MIDPOINT_DISTANCE);
                        float cornerArcMidY = getActualY(startY) + (cornerArcMidYOffset * CORNER_ARC_MIDPOINT_DISTANCE);

                        // Calculate the quad control point
                        float quadControlX = getActualX(startX) + (2 * xOffset * CORNER_ARC_MIDPOINT_DISTANCE);
                        float quadControlY = getActualY(startY) + (2 * yOffset * CORNER_ARC_MIDPOINT_DISTANCE);

                        // Draw the quad curve
                        wirePath.quadTo(quadControlX, quadControlY, edgeMidX, edgeMidY);
                    } else {
                        // Draw the straight line
                        wirePath.lineTo(edgeMidX, edgeMidY);
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
                        float cornerArcMidX = getActualX(endX) + (cornerArcMidXOffset * CORNER_ARC_MIDPOINT_DISTANCE);
                        float cornerArcMidY = getActualY(endY) + (cornerArcMidYOffset * CORNER_ARC_MIDPOINT_DISTANCE);

                        // Calculate the quad control point
                        float quadControlX = getActualX(endX) + (-2 * xOffset * CORNER_ARC_MIDPOINT_DISTANCE);
                        float quadControlY = getActualY(endY) + (-2 * yOffset * CORNER_ARC_MIDPOINT_DISTANCE);

                        // Draw the quad curve
                        wirePath.quadTo(quadControlX, quadControlY, cornerArcMidX, cornerArcMidY);
                    } else {
                        // Draw the straight line
                        wirePath.lineTo(getActualX(endX), getActualY(endY));
                    }
                } else {
                    // Draw the straight line
                    wirePath.lineTo(getActualX(endX), getActualY(endY));
                }
            }
        }

        // Set the stroke used for the path
        g.setStroke(new BasicStroke(WIRE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

        // Now draw the wire path we calculated above
        g.setColor((simulationNodeConnector != null && simulationNodeConnector.isActive()) ? PIN_ACTIVE_FILL : PIN_INACTIVE_FILL);
        g.draw(wirePath);
    }

    private void drawChip(Graphics2D g, Chip chip) {
        // Calculate initial actual values from grid values
        int chipX = getActualX(chip.getX());
        int chipY = getActualY(chip.getY());
        int chipWidth = getActualDimension(chip.getChipTemplate().getWidth());
        int chipHeight = getActualDimension(chip.getChipTemplate().getHeight());

        // Draw the chip background
        g.setColor(CHIP_FILL);
        g.fillRoundRect(chipX, chipY, chipWidth, chipHeight, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);

        // Create a new graphics object so we can apply clips to it
        Graphics2D gBorder = (Graphics2D) g.create();
        gBorder.setStroke(new BasicStroke(CHIP_BORDER_WIDTH));

        // Draw the "lighter" side of the border
        gBorder.setClip(new Polygon(new int[] {chipX - CHIP_BORDER_WIDTH, chipX + chipWidth + CHIP_BORDER_WIDTH, chipX - CHIP_BORDER_WIDTH}, new int[] {chipY - CHIP_BORDER_WIDTH, chipY - CHIP_BORDER_WIDTH, chipY + chipHeight + CHIP_BORDER_WIDTH}, 3));
        gBorder.setColor(CHIP_FILL.brighter());
        gBorder.drawRoundRect(chipX, chipY, chipWidth, chipHeight, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);

        // Draw the "darker" side of the border
        gBorder.setClip(new Polygon(new int[] {chipX - CHIP_BORDER_WIDTH, chipX + chipWidth + CHIP_BORDER_WIDTH, chipX + chipWidth + CHIP_BORDER_WIDTH}, new int[] {chipY + chipHeight + CHIP_BORDER_WIDTH, chipY - CHIP_BORDER_WIDTH, chipY + chipHeight + CHIP_BORDER_WIDTH}, 3));
        gBorder.setColor(CHIP_FILL.darker());
        gBorder.drawRoundRect(chipX, chipY, chipWidth, chipHeight, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);

        // Set the font and font color
        g.setColor(CHIP_FONT_COLOR);
        g.setFont(new Font("Montserrat", Font.PLAIN, 20));

        // Draw the chip text in the center of the chip
        String chipText = chip.getChipTemplate().getName();
        int stringWidth = g.getFontMetrics().stringWidth(chipText);
        int stringHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
        g.drawString(chipText, chipX + (chipWidth / 2) - (stringWidth / 2), chipY + (chipHeight / 2) + (stringHeight / 2));

        // Get the design instance for this chip
        DesignInstance chipDesignInstance = designInstance != null ? designInstance.getDesignInstanceForChip(chip) : null;

        // Draw pins for chip
        for (Pin pin : chip.getChipTemplate().getPins())
            drawChipPin(g, chip, pin, chipDesignInstance != null ? chipDesignInstance.getNodeForPin(pin) : null);
    }

    private int getActualX(int gridX) {
        return gridX * GRID_SQUARE_SIZE + 20;
    }

    private int getActualY(int gridY) {
        return gridY * GRID_SQUARE_SIZE + 20;
    }

    private int getActualDimension(int gridDimension) {
        return gridDimension * GRID_SQUARE_SIZE;
    }
}
