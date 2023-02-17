package com.github.chrisblutz.breadboard.ui.render.designs;

import com.github.chrisblutz.breadboard.designs.*;
import com.github.chrisblutz.breadboard.designs.templates.ChipTemplate;
import com.github.chrisblutz.breadboard.designs.templates.ToggleTemplate;
import com.github.chrisblutz.breadboard.designs.templates.TransistorTemplate;
import com.github.chrisblutz.breadboard.designs.wires.WireNode;
import com.github.chrisblutz.breadboard.designs.wires.WireSegment;
import com.github.chrisblutz.breadboard.simulation.LogicState;
import com.github.chrisblutz.breadboard.simulation.SimulatedDesign;
import com.github.chrisblutz.breadboard.ui.render.designs.changes.DesignResizeChange;
import com.github.chrisblutz.breadboard.ui.render.designs.changes.EditorChange;
import com.github.chrisblutz.breadboard.ui.render.designs.changes.ViewTranslateChange;
import com.github.chrisblutz.breadboard.ui.toolkit.*;
import com.github.chrisblutz.breadboard.ui.toolkit.changebuffer.ChangeBuffer;
import com.github.chrisblutz.breadboard.ui.toolkit.changebuffer.Changeset;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Ellipse;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Rectangle;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.RoundRectangle;
import com.github.chrisblutz.breadboard.utils.Direction;

import java.awt.event.KeyEvent;

/**
 * This class is used to render a single design.
 */
public class DesignEditor extends UIComponent implements UIInteractable, UIFocusable, UIUndoable<EditorChange> {

    private static final int DEFAULT_GRID_UNIT = 20;

    private final Design design;
    private final SimulatedDesign simulatedDesign;

    public double zoom = 20, translateX = 0, translateY = 0, dragStartTranslateX = 0, dragStartTranslateY = 0;
    private double gridScaleX = -1, gridScaleY = -1;
    private int gridMouseX = -1, gridMouseY = -1;
    private int mouseX = -1, mouseY = -1, mouseDragStartX = -1, mouseDragStartY = -1;
    public DesignRenderer renderer = new DesignRenderer();
    private ChipPin hoveredPin = null;
    private Chip hoveredChip = null;

    private boolean hoveredLeftDesignEdge = false, hoveredRightDesignEdge = false, hoveredTopDesignEdge = false, hoveredBottomDesignEdge = false;
    private boolean pressedLeftDesignEdge = false, pressedRightDesignEdge = false, pressedTopDesignEdge = false, pressedBottomDesignEdge = false;
    private int renderedDesignOffsetX = 0, renderedDesignOffsetY = 0, renderedDesignOffsetWidth = 0, renderedDesignOffsetHeight = 0;

    private ChipTemplate selectedAddingChipTemplate = TransistorTemplate.getNPNTransistorTemplate();

    private boolean adjusted = false, panning = false;

    private final ChangeBuffer<EditorChange> changeBuffer = new ChangeBuffer<>();

    public DesignEditor(Design design) {
        this(design, null);
    }

    public DesignEditor(Design design, SimulatedDesign simulatedDesign) {
        this.design = design;
        this.simulatedDesign = simulatedDesign;

        renderer.generate(design);

        setMinimumSize(new UIDimension(200, 200));
    }

    @Override
    public void setSize(UIDimension size) {
        super.setSize(size);

        // Adjust the design's location and scaling in the view if it hasn't been adjusted previously
        if (!adjusted) {
            double actualDesignWidth = design.getWidth() * DEFAULT_GRID_UNIT;
            double actualDesignHeight = design.getHeight() * DEFAULT_GRID_UNIT;

            double fitToWidthZoom = DEFAULT_GRID_UNIT, fitToHeightZoom = DEFAULT_GRID_UNIT;
            if (actualDesignWidth > getWidth())
                fitToWidthZoom = (double) getWidth() / design.getWidth();
            if (actualDesignHeight > getHeight())
                fitToHeightZoom = (double) getHeight() / design.getHeight();

            double targetZoom = Math.min(DEFAULT_GRID_UNIT, Math.min(fitToWidthZoom, fitToHeightZoom));

            actualDesignWidth = design.getWidth() * targetZoom;
            actualDesignHeight = design.getHeight() * targetZoom;

            zoom = targetZoom;
            translateX = (((double) getWidth() / 2) - (actualDesignWidth / 2)) / zoom;
            translateY = (((double) getHeight() / 2) - (actualDesignHeight / 2)) / zoom;

            calculateHover();
        }
    }

    @Override
    public void render(UIGraphics graphics) {
        // Each tick, update the colors for the "conflicted" wire state so it flickers
        DesignEditorUtils.updateRandomConflictedState();

        graphics.withCopy(scaledGraphics -> {
            scaledGraphics.scale(zoom);
            scaledGraphics.translate(translateX + renderedDesignOffsetX, translateY + renderedDesignOffsetY);
            graphics.withCopy(uiGraphics -> drawGrid(uiGraphics, scaledGraphics));
        });
        graphics.withCopy(scaledGraphics -> {
            scaledGraphics.scale(zoom);
            scaledGraphics.translate(translateX, translateY);
            scaledGraphics.withCopy(designGraphics -> drawDesign(designGraphics, design));
        });
        graphics.withCopy(scaledGraphics -> {
            scaledGraphics.scale(zoom);
            scaledGraphics.translate(translateX, translateY);
            graphics.withCopy(uiGraphics -> drawMouseObjects(uiGraphics, scaledGraphics));
        });
    }

    private void drawGrid(UIGraphics uiGraphics, UIGraphics scaledGraphics) {
        // Draw background
        uiGraphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BACKGROUND_PRIMARY));
        uiGraphics.fillRect(0, 0, getRenderSpace().getWidth(), getRenderSpace().getHeight());

        // Draw solid border around edges
        scaledGraphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BORDER_PRIMARY));
        scaledGraphics.setStroke(UIStroke.solid(0.2f));
        scaledGraphics.drawRect(0, 0, design.getWidth() + renderedDesignOffsetWidth, design.getHeight() + renderedDesignOffsetHeight);

        // Draw dots for the interior grid
        for (int x = 1; x < design.getWidth() + renderedDesignOffsetWidth; x++) {
            for (int y = 1; y < design.getHeight() + renderedDesignOffsetHeight; y++) {
                scaledGraphics.fillEllipse(x - 0.1f, y - 0.1f, 0.2f, 0.2f);
            }
        }
    }

    private void drawDesign(UIGraphics graphics, Design design) {
        // Draw all design chips
        for (Chip chip : design.getChips())
            graphics.withCopy(chipGraphics -> drawChip(chipGraphics, chip.getChipTemplate(), chip));

        // Draw all design chip pin backgrounds
        for (Chip chip : design.getChips())
            graphics.withCopy(chipGraphics -> drawChipPinBackgrounds(chipGraphics, chip));

        // Draw all design pin backgrounds
        for (Pin pin : design.getPins())
            graphics.withCopy(pinGraphics -> drawPinBackground(pinGraphics, pin, simulatedDesign != null ? simulatedDesign.getStateForPin(pin) : null, hoveredPin != null && hoveredPin.chip() == null && hoveredPin.pin() == pin));

        // Draw all design wires
        for (Wire wire : design.getWires())
            graphics.withCopy(wireGraphics -> drawWire(wireGraphics, wire, simulatedDesign != null ? simulatedDesign.getStateForWire(wire) : null));

        // Draw all design chip pin foregrounds
        for (Chip chip : design.getChips())
            graphics.withCopy(chipGraphics -> drawChipPinForegrounds(chipGraphics, chip));

        // Draw all design pin foregrounds
        for (Pin pin : design.getPins())
            graphics.withCopy(pinGraphics -> drawPinForeground(pinGraphics, pin, simulatedDesign != null ? simulatedDesign.getStateForPin(pin) : null, hoveredPin != null && hoveredPin.chip() == null && hoveredPin.pin() == pin));
    }

    private void drawPinBackground(UIGraphics graphics, Pin pin, LogicState state, boolean hovered) {
        drawPinBackground(graphics, null, pin, state, hovered);
    }

    private void drawPinBackground(UIGraphics graphics, Chip chip, Pin pin, LogicState state, boolean hovered) {
        drawPinBackground(graphics, renderer.getPinShape(new ChipPin(chip, pin)), state, hovered);
    }

    private void drawPinBackground(UIGraphics graphics, Ellipse ellipse, LogicState state, boolean hovered) {
        // Draw border around the pin
        graphics.setColor(DesignEditorUtils.getColorForLogicState(state).darker());
        graphics.setStroke(UIStroke.solid(0.3f)); // TODO default
        graphics.draw(ellipse);
    }

    private void drawPinForeground(UIGraphics graphics, Pin pin, LogicState state, boolean hovered) {
        drawPinForeground(graphics, null, pin, state, hovered);
    }

    private void drawPinForeground(UIGraphics graphics, Chip chip, Pin pin, LogicState state, boolean hovered) {
        drawPinForeground(graphics, renderer.getPinShape(new ChipPin(chip, pin)), state, hovered);
    }

    private void drawPinForeground(UIGraphics graphics, Ellipse ellipse, LogicState state, boolean hovered) {
        // Draw the pin itself
        graphics.setColor(DesignEditorUtils.getColorForLogicState(state));
        if (hovered)
            graphics.setColor(UIColor.rgb(255, 255, 0));
        graphics.fill(ellipse);
    }

    private void drawWire(UIGraphics graphics, Wire wire, LogicState state) {
        // Set the stroke and color used for the paths and nodes
        graphics.setStroke(UIStroke.solid(0.3f, UIStroke.Cap.BUTT, UIStroke.Join.ROUND)); // TODO default
        graphics.setColor(DesignEditorUtils.getColorForLogicState(state));

        // Draw all wire segments
        for (WireSegment segment : wire.getSegments())
            graphics.drawPath(renderer.getWireSegmentShape(segment));

        // Draw all wire nodes
        for (WireNode node : wire.getNodes())
            graphics.fill(renderer.getWireNodeShape(node));
    }

    private void drawChip(UIGraphics graphics, ChipTemplate template, Chip chip) {
        drawChip(graphics, template, chip, simulatedDesign.getSimulatedChipDesign(chip), renderer.getChipShape(chip), hoveredPin == null && hoveredChip == chip);
    }

    private void drawChip(UIGraphics graphics, ChipTemplate template, Chip chip, SimulatedDesign design, RoundRectangle chipShape, boolean hovered) {
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_BACKGROUND));
        if (hovered)
            graphics.setColor(UIColor.rgb(255, 255, 0));//Color.YELLOW);
        graphics.fill(chipShape);

        graphics.withCopy(chipGraphics -> {
            chipGraphics.translate(chipShape.getX(), chipShape.getY()); // TODO
            template.renderChipPackage(chipGraphics, chip, design);
        });
    }

    private void drawChipPinBackgrounds(UIGraphics graphics, Chip chip) {
        // Get the simulated design for this chip
        SimulatedDesign chipDesignInstance = simulatedDesign != null ? simulatedDesign.getSimulatedChipDesign(chip) : null;

        // Draw pins for chip
        for (Pin pin : chip.getChipTemplate().getPins())
            drawPinBackground(graphics, chip, pin, chipDesignInstance != null ? chipDesignInstance.getStateForPin(pin) : null, hoveredPin != null && hoveredPin.chip() == chip && hoveredPin.pin() == pin);
    }

    private void drawChipPinForegrounds(UIGraphics graphics, Chip chip) {
        // Get the simulated design for this chip
        SimulatedDesign chipDesignInstance = simulatedDesign != null ? simulatedDesign.getSimulatedChipDesign(chip) : null;

        // Draw pins for chip
        for (Pin pin : chip.getChipTemplate().getPins())
            drawPinForeground(graphics, chip, pin, chipDesignInstance != null ? chipDesignInstance.getStateForPin(pin) : null, hoveredPin != null && hoveredPin.chip() == chip && hoveredPin.pin() == pin);
    }

    private void drawMouseObjects(UIGraphics uiGraphics, UIGraphics scaledGraphics) {
        if (gridMouseX > 0 && gridMouseX < design.getWidth() && gridMouseY > 0 && gridMouseY < design.getHeight()) {
            scaledGraphics.setColor(UIColor.rgb(0xFFFFFF));
            scaledGraphics.drawRect(gridMouseX - 0.5, gridMouseY - 0.5, 1, 1);
        }
        // If there is a pin hovered, draw its tooltip
        if (hoveredPin != null) {
            // Get "actual" non-scaled X/Y coordinates for the pin
            Ellipse ellipse = renderer.getPinShape(hoveredPin);
            ellipse = (Ellipse) ellipse.translate(translateX, translateY);
            ellipse = (Ellipse) ellipse.scale(zoom);
            float ellipseCenterX = (float) (ellipse.getX() + (ellipse.getWidth() / 2));
            float ellipseCenterY = (float) (ellipse.getY() + (ellipse.getHeight() / 2));

            // Set font for graphics
            uiGraphics.setFont(UITheme.getFont(ThemeKeys.Fonts.Design.TOOLTIP)); // TODO

            // Get tooltip text information
            String tooltipText = hoveredPin.pin().getName().toUpperCase();
            Rectangle tooltipTextBounds = uiGraphics.getStringBounds(tooltipText);
            float tooltipSpacing = 4, xPadding = 10, yPadding = 5;

            // Calculate X/Y positions for tooltip
            float x;
            float y;
            int arrowDirection = 0; // 0 - up, 1 - down, 2 - right, 3 - left
            if (ellipseCenterX - (tooltipTextBounds.getWidth() / 2) - xPadding < 0) {
                x = (float) (ellipse.getX() + ellipse.getWidth() + tooltipSpacing);
                y = (float) (ellipseCenterY - (tooltipTextBounds.getHeight() / 2) - yPadding);
                arrowDirection = 2;
            } else if (ellipseCenterX + (tooltipTextBounds.getWidth() / 2) + xPadding > getWidth()) {
                x = (float) (ellipse.getX() - tooltipSpacing - tooltipTextBounds.getWidth() - (xPadding * 2));
                y = (float) (ellipseCenterY - (tooltipTextBounds.getHeight() / 2) - yPadding);
                arrowDirection = 3;
            } else if (ellipse.getY() - tooltipSpacing - tooltipTextBounds.getHeight() - yPadding < 0) {
                x = (float) (ellipseCenterX - (tooltipTextBounds.getWidth() / 2) - 10);
                y = (float) (ellipse.getY() + ellipse.getHeight() + tooltipSpacing);
                arrowDirection = 1;
            } else {
                x = (float) (ellipseCenterX - (tooltipTextBounds.getWidth() / 2) - 10);
                y = (float) (ellipse.getY() - tooltipSpacing - tooltipTextBounds.getHeight() - (yPadding * 2));
                arrowDirection = 0;
            }

            uiGraphics.setColor(UIColor.rgba(0, 0, 0, 0.9f));
            uiGraphics.fillRoundRect(x, y, tooltipTextBounds.getWidth() + (xPadding * 2), tooltipTextBounds.getHeight() + (yPadding * 2), 5, 5); // TODO default
            uiGraphics.setColor(UIColor.rgb(220, 220, 220));
            uiGraphics.drawStringCentered(tooltipText, (float) (x + 10 + (tooltipTextBounds.getWidth() / 2)), (float) (y + 5 + (tooltipTextBounds.getHeight() / 2)));
        }

        if (selectedAddingChipTemplate != null && gridMouseX > 0 && gridMouseX < design.getWidth() && gridMouseY > 0 && gridMouseY < design.getHeight()) {
            int newChipX = gridMouseX - (selectedAddingChipTemplate.getWidth() / 2);
            int newChipY = gridMouseY - (selectedAddingChipTemplate.getHeight() / 2);

            // Check that all vertices within the chip are empty
            boolean conflict = false;
            for (int chipX = newChipX; chipX < newChipX + selectedAddingChipTemplate.getWidth(); chipX++) {
                for (int chipY = newChipY; chipY < newChipY + selectedAddingChipTemplate.getHeight(); chipY++) {
                    if (!design.getVertexContents(new Vertex(chipX, chipY)).isEmpty()) {
                        conflict = true;
                        break;
                    }
                }
            }

            if (!conflict) {
                scaledGraphics.setAlpha(0.8f);
                drawChip(scaledGraphics, selectedAddingChipTemplate, null, SimulatedDesign.none(), renderer.getNewChipShape(selectedAddingChipTemplate, newChipX, newChipY), false);
                for (Pin pin : selectedAddingChipTemplate.getPins()) {
                    drawPinBackground(scaledGraphics, renderer.getNewPinShape(selectedAddingChipTemplate, pin, newChipX, newChipY), LogicState.UNCONNECTED, false);
                    drawPinForeground(scaledGraphics, renderer.getNewPinShape(selectedAddingChipTemplate, pin, newChipX, newChipY), LogicState.UNCONNECTED, false);
                }
            }
        }
    }

    private void calculateHover() {
        gridScaleX = (mouseX / zoom) - translateX;
        gridScaleY = (mouseY / zoom) - translateY;
        gridMouseX = (int) Math.round(gridScaleX);
        gridMouseY = (int) Math.round(gridScaleY);
        hoveredPin = renderer.getHoveredPin(gridScaleX, gridScaleY);
        hoveredChip = renderer.getHoveredChip(gridScaleX, gridScaleY);

        int designBorderX = (int) (translateX * zoom);
        int designBorderY = (int) (translateY * zoom);
        int designWidth = (int) (design.getWidth() * zoom);
        int designHeight = (int) (design.getHeight() * zoom);
        hoveredLeftDesignEdge = mouseX >= (designBorderX - 2) && mouseX <= (designBorderX + 2);
        hoveredRightDesignEdge = mouseX >= (designBorderX + designWidth - 2) && mouseX <= (designBorderX + designWidth + 2);
        hoveredTopDesignEdge = mouseY >= (designBorderY - 2) && mouseY <= (designBorderY + 2);
        hoveredBottomDesignEdge = mouseY >= (designBorderY + designHeight - 2) && mouseY <= (designBorderY + designHeight + 2);
    }

    private boolean isDesignInsideView() {
        return (design.getWidth() * zoom) <= getWidth() && (design.getHeight() * zoom) <= getHeight();
    }

    private void zoom(double newZoom) {
        double oldZoom = zoom;
        zoom = newZoom;

        double gridX = (mouseX / oldZoom) - translateX;
        double gridY = (mouseY / oldZoom) - translateY;
        translateX = (((translateX + gridX) * oldZoom) / newZoom) - gridX;
        translateY = (((translateY + gridY) * oldZoom) / newZoom) - gridY;

        calculateHover();
    }

    @Override
    public boolean onMouseClicked(int x, int y, int button) {
        return true;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        if (button == 1) {
            if (hoveredLeftDesignEdge)
                pressedLeftDesignEdge = true;
            else if (hoveredRightDesignEdge)
                pressedRightDesignEdge = true;
            if (hoveredTopDesignEdge)
                pressedTopDesignEdge = true;
            else if (hoveredBottomDesignEdge)
                pressedBottomDesignEdge = true;
        } else if (button == 2) {
            mouseDragStartX = x;
            mouseDragStartY = y;
            dragStartTranslateX = translateX;
            dragStartTranslateY = translateY;
            panning = true;
        }
        return true;
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        if (button == 1 && hoveredChip != null && hoveredPin == null) {
            if (hoveredChip.getChipTemplate() instanceof ToggleTemplate toggleTemplate) {
                LogicState currentState = toggleTemplate.getDrivenState(hoveredChip);
                LogicState newState = currentState == LogicState.LOW ? LogicState.HIGH : LogicState.LOW;
                toggleTemplate.setDrivenState(hoveredChip, newState);
            }
        } else if (button == 1) {
            pressedLeftDesignEdge = false;
            pressedRightDesignEdge = false;
            pressedTopDesignEdge = false;
            pressedBottomDesignEdge = false;
            if (renderedDesignOffsetWidth != 0 || renderedDesignOffsetHeight != 0) {
                getChangeBuffer().doAndAppend(
                        new Changeset<>(
                                new DesignResizeChange(
                                        this,
                                        design,
                                        design.getWidth() + renderedDesignOffsetWidth,
                                        design.getHeight() + renderedDesignOffsetHeight,
                                        renderedDesignOffsetX != 0,
                                        renderedDesignOffsetY != 0
                                ),
                                new ViewTranslateChange(
                                        this,
                                        design,
                                        renderedDesignOffsetX,
                                        renderedDesignOffsetY
                                )
                        )
                );
                renderedDesignOffsetWidth = 0;
                renderedDesignOffsetHeight = 0;
                renderedDesignOffsetX = 0;
                renderedDesignOffsetY = 0;
            }
        } else if (button == 2) {
            panning = false;
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
        mouseX = x;
        mouseY = y;
        int designBorderX = (int) (translateX * zoom);
        int designBorderY = (int) (translateY * zoom);
        int designWidth = (int) (design.getWidth() * zoom);
        int designHeight = (int) (design.getHeight() * zoom);
        if (pressedLeftDesignEdge) {
            int gridSquareToAdd = -Math.min(
                    (int) -Math.round((double) (designBorderX - mouseX) / zoom),
                    design.getOpenDistance(Direction.LEFT)
            );
            renderedDesignOffsetX = -gridSquareToAdd;
            renderedDesignOffsetWidth = gridSquareToAdd;
        } else if (pressedRightDesignEdge) {
            renderedDesignOffsetWidth = -Math.min(
                    (int) -Math.round((double) (mouseX - designBorderX - designWidth) / zoom),
                    design.getOpenDistance(Direction.RIGHT)
            );
        }
        if (pressedTopDesignEdge) {
            int gridSquareToAdd = -Math.min(
                    (int) -Math.round((double) (designBorderY - mouseY) / zoom),
                    design.getOpenDistance(Direction.UP)
            );
            renderedDesignOffsetY = -gridSquareToAdd;
            renderedDesignOffsetHeight = gridSquareToAdd;
        } else if (pressedBottomDesignEdge) {
            renderedDesignOffsetHeight = -Math.min(
                    (int) -Math.round((double) (mouseY - designBorderY - designHeight) / zoom),
                    design.getOpenDistance(Direction.DOWN)
            );
        }
        if (panning) {
            calculateHover();
            translateX = dragStartTranslateX + (x - mouseDragStartX) / zoom;
            translateY = dragStartTranslateY + (y - mouseDragStartY) / zoom;

            adjusted = true;
        }
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        mouseX = x;
        mouseY = y;
        calculateHover();
        return true;
    }

    double ox = 0, oy =0;

    @Override
    public boolean onMouseScrolled(int scrollAmount) {
        double newZoom = zoom * (1 + ((double) -scrollAmount / 25));
        if (newZoom < 1)
            newZoom = 1;
        zoom(newZoom);
        adjusted = true;
        return true;
    }

    @Override
    public boolean onKeyTyped(KeyEvent e) {
        return false;
    }

    @Override
    public boolean onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
            getChangeBuffer().undo();
        } else if (e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown()) {
            getChangeBuffer().redo();
        }

        return true;
    }

    @Override
    public boolean onKeyReleased(KeyEvent e) {
        return false;
    }

    @Override
    public void onFocusLost(boolean keyboardTriggered) {}

    @Override
    public void onFocusGained(boolean keyboardTriggered) {}

    @Override
    public ChangeBuffer<EditorChange> getChangeBuffer() {
        return changeBuffer;
    }
}
