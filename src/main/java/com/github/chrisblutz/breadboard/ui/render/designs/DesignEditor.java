package com.github.chrisblutz.breadboard.ui.render.designs;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulationproto.LogicState;
import com.github.chrisblutz.breadboard.simulationproto.SimulatedDesign;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshSimulatedDesign;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshVertex;
import com.github.chrisblutz.breadboard.ui.toolkit.*;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;
import com.github.chrisblutz.breadboard.ui.window.BreadboardWindow;
import com.github.chrisblutz.breadboard.utils.Vertex;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;

/**
 * This class is used to render a single design.
 */
public class DesignEditor extends UIComponent implements UIInteractable, UIFocusable {

    private static final int DEFAULT_GRID_UNIT = 20;

    private final EditorParameters rendererParameters;

    private final Design design;
    private final SimulatedDesign simulatedDesign;

    private int designGridActualWidth, designGridActualHeight;
    private int renderOriginX, renderOriginY;

    private DesignRenderer renderer = new DesignRenderer();

    public DesignEditor(Design design) {
        this(design, null);
    }

    public DesignEditor(Design design, SimulatedDesign simulatedDesign) {
        this.rendererParameters = new EditorParameters(DEFAULT_GRID_UNIT);

        this.design = design;
        this.simulatedDesign = simulatedDesign;

        renderer.generate(design);

        setMinimumSize(new UIDimension(200, 200));
    }

    @Override
    public void render(UIGraphics graphics) {
        // Calculate initial render space parameters
        calculateRenderSpaceParameters();

        // TODO
        Graphics2D g = graphics.getInternalGraphics();

        Graphics2D gridGraphics = (Graphics2D) g.create();
        drawGrid(gridGraphics);
        gridGraphics.dispose();

        g.scale(10, 10);

        //Graphics2D designGraphics = (Graphics2D) g.create();
        //drawDesign(designGraphics, design);
        //designGraphics.dispose();
    }

    private void calculateRenderSpaceParameters() {
        // Calculate the "actual" size of the grid in the render space
        this.designGridActualWidth = getActualDimension(design.getWidth());
        this.designGridActualHeight = getActualDimension(design.getHeight());
        // Calculate the "origin" of the design in the render space (centers the design if it doesn't fill it)
        this.renderOriginX = designGridActualWidth < getRenderSpace().getWidth() ? ((getRenderSpace().getWidth() - designGridActualWidth) / 2) : 0; // TODO: mouse drag origin
        this.renderOriginY = designGridActualHeight < getRenderSpace().getHeight() ? ((getRenderSpace().getHeight() - designGridActualHeight) / 2) : 0; // TODO: mouse drag origin
    }

    private void drawGrid(Graphics2D g) {
        // Store grid divider thickness value, so we can use it without referring to the method multiple times
        int dividerThickness = rendererParameters.getGridDividerThickness();

        // Draw background
        g.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BACKGROUND_PRIMARY).getInternalColor());
        g.fillRect(0, 0, getRenderSpace().getWidth(), getRenderSpace().getHeight());

        // Draw solid border around edges
        g.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BORDER_PRIMARY).getInternalColor());
        g.setStroke(new BasicStroke(dividerThickness));
        g.drawRect(renderOriginX, renderOriginY, designGridActualWidth, designGridActualHeight);

        // Draw dots for the interior grid
        for (int x = 1; x < design.getWidth(); x++) {
            for (int y = 1; y < design.getHeight(); y++) {
                g.fillOval(getActualX(x) - (dividerThickness / 2), getActualY(y) - (dividerThickness / 2), dividerThickness, dividerThickness);
            }
        }
    }

    private void drawDesign(Graphics2D g, Design design) {
        // Draw all design chips
        for (Chip chip : design.getChips()) {
            Graphics2D chipGraphics = (Graphics2D) g.create();
            drawChip(chipGraphics, chip);
            chipGraphics.dispose();
        }

        // Draw all design pins
        for (Pin pin : design.getPins()) {
            Graphics2D pinGraphics = (Graphics2D) g.create();
            drawDesignPin(pinGraphics, pin, simulatedDesign != null ? simulatedDesign.getStateForPin(pin) : null);
            pinGraphics.dispose();
        }

        // Draw all design wires
        for (Wire wire : design.getWires()) {
            Graphics2D wireGraphics = (Graphics2D) g.create();
            drawWire(wireGraphics, wire, simulatedDesign != null ? simulatedDesign.getStateForWire(wire) : null);
            wireGraphics.dispose();
        }
    }

    private void drawDesignPin(Graphics2D g, Pin pin, LogicState state) {
        drawPin(g, getActualX(pin.getDesignX()), getActualY(pin.getDesignY()), state, null, pin, ((MeshSimulatedDesign) simulatedDesign).getPinMapping().get(pin));
    }

    private void drawChipPin(Graphics2D g, Chip chip, Pin pin, LogicState state) {
        drawPin(g, getActualX(chip.getX() + pin.getChipX()), getActualY(chip.getY() + pin.getChipY()), state, chip, pin, ((MeshSimulatedDesign) simulatedDesign).getChipMapping().get(chip).getPinMapping().get(pin));
    }

    private void drawPin(Graphics2D g, int x, int y, LogicState state, Chip chip, Pin pin, MeshVertex vertex) {
        // Store pin diameter value, so we can use it without referring to the method multiple times
        int pinDiameter = rendererParameters.getPinDiameter();

        // Draw the pin itself
        g.setColor( // TODO
                (state == LogicState.HIGH) ?
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BACKGROUND_ACTIVE).getInternalColor() :
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BACKGROUND_INACTIVE).getInternalColor()
        );
        g.fillOval(x - (pinDiameter / 2), y - (pinDiameter / 2), pinDiameter, pinDiameter);
        // Draw border around the pin
        g.setColor( // TODO
                (state == LogicState.HIGH) ?
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BORDER_ACTIVE).getInternalColor() :
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BORDER_INACTIVE).getInternalColor()
        );
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - (pinDiameter / 2), y - (pinDiameter / 2), pinDiameter, pinDiameter);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString(Integer.toString(vertex.hashCode()), x, y-10);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString(Integer.toString(pin.hashCode()), x, y-20);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString(chip == null ? "NULL" : Integer.toString(chip.hashCode()), x, y-30);
    }

    private void drawWire(Graphics2D g, Wire wire, LogicState state) {
        // Store midpoint guide value, so we can use it without referring to the method multiple times
        float midpointGuide = rendererParameters.getWireCornerArcMidpointGuide();

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
                    float edgeMidX = getActualXFloat(startX + ((float) xOffset / 2));
                    float edgeMidY = getActualYFloat(startY + ((float) yOffset / 2));

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
                        float cornerArcMidX = getActualX(startX) + (cornerArcMidXOffset * midpointGuide);
                        float cornerArcMidY = getActualY(startY) + (cornerArcMidYOffset * midpointGuide);

                        // Calculate the quad control point
                        float quadControlX = getActualX(startX) + (2 * xOffset * midpointGuide);
                        float quadControlY = getActualY(startY) + (2 * yOffset * midpointGuide);

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
                        float cornerArcMidX = getActualX(endX) + (cornerArcMidXOffset * midpointGuide);
                        float cornerArcMidY = getActualY(endY) + (cornerArcMidYOffset * midpointGuide);

                        // Calculate the quad control point
                        float quadControlX = getActualX(endX) + (-2 * xOffset * midpointGuide);
                        float quadControlY = getActualY(endY) + (-2 * yOffset * midpointGuide);

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
        g.setStroke(new BasicStroke(rendererParameters.getWireThickness(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

        // Now draw the wire path we calculated above
        g.setColor( // TODO
                (state == LogicState.HIGH) ?
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BACKGROUND_ACTIVE).getInternalColor() :
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BACKGROUND_INACTIVE).getInternalColor()
        );
        g.draw(wirePath);
    }

    private void drawChip(Graphics2D g, Chip chip) {
        // Store renderer values, so we can use them without referring to the methods multiple times
        int cornerRadius = rendererParameters.getGridUnit();
        int chipBorderThickness = rendererParameters.getChipBorderThickness();
        Color chipFillColor = UITheme.getColor(ThemeKeys.Colors.Design.CHIP_BACKGROUND).getInternalColor();
        Color chipTextColor = UITheme.getColor(ThemeKeys.Colors.Design.CHIP_FOREGROUND).getInternalColor();

        // Calculate initial actual values from grid values
        int chipX = getActualX(chip.getX());
        int chipY = getActualY(chip.getY());
        int chipWidth = getActualDimension(chip.getChipTemplate().getWidth());
        int chipHeight = getActualDimension(chip.getChipTemplate().getHeight());

        // Draw the chip background
        g.setColor(chipFillColor);
        g.fillRoundRect(chipX, chipY, chipWidth, chipHeight, cornerRadius, cornerRadius);

        // Create a new graphics object, so we can apply clips to it
        Graphics2D borderGraphics = (Graphics2D) g.create();
        borderGraphics.setStroke(new BasicStroke(chipBorderThickness));

        // Draw the "lighter" side of the border
        borderGraphics.setClip(new Polygon(
                new int[] {chipX - chipBorderThickness, chipX + chipWidth + chipBorderThickness, chipX - chipBorderThickness},
                new int[] {chipY - chipBorderThickness, chipY - chipBorderThickness, chipY + chipHeight + chipBorderThickness},
                3)
        );
        borderGraphics.setColor(chipFillColor.brighter());
        borderGraphics.drawRoundRect(chipX, chipY, chipWidth, chipHeight, cornerRadius, cornerRadius);

        // Draw the "darker" side of the border
        borderGraphics.setClip(new Polygon(
                new int[] {chipX - chipBorderThickness, chipX + chipWidth + chipBorderThickness, chipX + chipWidth + chipBorderThickness},
                new int[] {chipY + chipHeight + chipBorderThickness, chipY - chipBorderThickness, chipY + chipHeight + chipBorderThickness},
                3)
        );
        borderGraphics.setColor(chipFillColor.darker());
        borderGraphics.drawRoundRect(chipX, chipY, chipWidth, chipHeight, cornerRadius, cornerRadius);

        // Dispose of border graphics object
        borderGraphics.dispose();

        // Set the font and font color
        g.setColor(chipTextColor);
        g.setFont(new Font("Montserrat", Font.PLAIN, 20));

        // Draw the chip text in the center of the chip
        String chipText = chip.getChipTemplate().getName();
        int stringWidth = g.getFontMetrics().stringWidth(chipText);
        int stringHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
        g.drawString(chipText, chipX + (chipWidth / 2) - (stringWidth / 2), chipY + (chipHeight / 2) + (stringHeight / 2));

        // Get the simulated design for this chip
        SimulatedDesign chipDesignInstance = simulatedDesign != null ? simulatedDesign.getSimulatedChipDesign(chip) : null;

        // Draw pins for chip
        for (Pin pin : chip.getChipTemplate().getPins())
            drawChipPin(g, chip, pin, chipDesignInstance != null ? chipDesignInstance.getStateForPin(pin) : null);
    }

    private int getActualX(int gridX) {
        return renderOriginX + (gridX * rendererParameters.getGridUnit());
    }

    private float getActualXFloat(float gridX) {
        return renderOriginX + (gridX * rendererParameters.getGridUnit());
    }

    private int getActualY(int gridY) {
        return renderOriginY + (gridY * rendererParameters.getGridUnit());
    }

    private float getActualYFloat(float gridY) {
        return renderOriginY + (gridY * rendererParameters.getGridUnit());
    }

    private int getActualDimension(int gridDimension) {
        return gridDimension * rendererParameters.getGridUnit();
    }

    @Override
    public boolean onMouseClicked(int x, int y, int button) {
        return true;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        return true;
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        if (button == 1) {
            if (BreadboardWindow.resetDriver.getDrivenActualState() == LogicState.LOW) {
                BreadboardWindow.resetDriver.setDrivenActualState(LogicState.HIGH);
            } else {
                BreadboardWindow.resetDriver.setDrivenActualState(LogicState.LOW);
            }
        } else if (button == 3) {
            if (BreadboardWindow.setDriver.getDrivenActualState() == LogicState.LOW) {
                BreadboardWindow.setDriver.setDrivenActualState(LogicState.HIGH);
            } else {
                BreadboardWindow.setDriver.setDrivenActualState(LogicState.LOW);
            }
        }
    }

    @Override
    public void onMouseEntered() {
    }

    @Override
    public void onMouseExited() {
    }

    @Override
    public void onMouseDragged(int x, int y) {
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        return true;
    }

    @Override
    public boolean onMouseScrolled(int scrollAmount) {
        float gridUnit = rendererParameters.getPreciseGridUnit();
        rendererParameters.recalculate(gridUnit + (gridUnit * ((float) scrollAmount / 20)));

        return true;
    }

    @Override
    public boolean onKeyTyped(KeyEvent e) {
        return false;
    }

    @Override
    public boolean onKeyPressed(KeyEvent e) {
        return false;
    }

    @Override
    public boolean onKeyReleased(KeyEvent e) {
        return false;
    }

    @Override
    public void onFocusLost(boolean keyboardTriggered) {}

    @Override
    public void onFocusGained(boolean keyboardTriggered) {}
}
