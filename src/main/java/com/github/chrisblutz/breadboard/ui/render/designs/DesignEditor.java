package com.github.chrisblutz.breadboard.ui.render.designs;

import com.github.chrisblutz.breadboard.designs.*;
import com.github.chrisblutz.breadboard.designs.templates.ToggleTemplate;
import com.github.chrisblutz.breadboard.designs.templates.TransistorTemplate;
import com.github.chrisblutz.breadboard.simulation.LogicState;
import com.github.chrisblutz.breadboard.simulation.SimulatedDesign;
import com.github.chrisblutz.breadboard.ui.toolkit.*;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Ellipse;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Rectangle;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.RoundRectangle;

import java.awt.event.KeyEvent;

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

    private double zoom = 20, translateX = 0, translateY = 0, dragStartTranslateX = 0, dragStartTranslateY = 0;
    private double gridScaleX = -1, gridScaleY = -1;
    private int gridMouseX = -1, gridMouseY = -1;
    private int mouseX = -1, mouseY = -1, mouseDragStartX = -1, mouseDragStartY = -1;
    private DesignRenderer renderer = new DesignRenderer();
    private ChipPin hoveredPin = null;
    private Chip hoveredChip = null;

    private boolean panning = false;

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
        // Each tick, update the colors for the "conflicted" wire state so it flickers
        DesignEditorUtils.updateRandomConflictedState();

        // Calculate initial render space parameters
        calculateRenderSpaceParameters();

        graphics.withCopy(scaledGraphics -> {
            scaledGraphics.scale(zoom);
            scaledGraphics.translate(translateX, translateY);
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

    private void calculateRenderSpaceParameters() {
        // Calculate the "actual" size of the grid in the render space
        this.designGridActualWidth = getActualDimension(design.getWidth());
        this.designGridActualHeight = getActualDimension(design.getHeight());
        // Calculate the "origin" of the design in the render space (centers the design if it doesn't fill it)
        this.renderOriginX = designGridActualWidth < getRenderSpace().getWidth() ? ((getRenderSpace().getWidth() - designGridActualWidth) / 2) : 0; // TODO: mouse drag origin
        this.renderOriginY = designGridActualHeight < getRenderSpace().getHeight() ? ((getRenderSpace().getHeight() - designGridActualHeight) / 2) : 0; // TODO: mouse drag origin
    }

    private void drawGrid(UIGraphics uiGraphics, UIGraphics scaledGraphics) {
        // Draw background
        uiGraphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BACKGROUND_PRIMARY));
        uiGraphics.fillRect(0, 0, getRenderSpace().getWidth(), getRenderSpace().getHeight());

        // Draw solid border around edges
        scaledGraphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BORDER_PRIMARY));
        scaledGraphics.setStroke(UIStroke.solid(0.2f));
        scaledGraphics.drawRect(0, 0, design.getWidth(), design.getHeight());

        // Draw dots for the interior grid
        for (int x = 1; x < design.getWidth(); x++) {
            for (int y = 1; y < design.getHeight(); y++) {
                scaledGraphics.fillEllipse(x - 0.1f, y - 0.1f, 0.2f, 0.2f);
            }
        }
    }

    private void drawDesign(UIGraphics graphics, Design design) {
        // Draw all design chips
        for (Chip chip : design.getChips())
            graphics.withCopy(chipGraphics -> drawChip(chipGraphics, chip));

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
        // Set the stroke used for the path
        graphics.setStroke(UIStroke.solid(0.3f, UIStroke.Cap.BUTT, UIStroke.Join.ROUND)); // TODO default

        // Now draw the wire path we calculated above
        graphics.setColor(DesignEditorUtils.getColorForLogicState(state));
        graphics.drawPath(renderer.getWireShape(wire));
    }

    private void drawChip(UIGraphics graphics, Chip chip) {
        drawChip(graphics, chip, simulatedDesign.getSimulatedChipDesign(chip), renderer.getChipShape(chip), hoveredPin == null && hoveredChip == chip);
    }

    private void drawChip(UIGraphics graphics, Chip chip, SimulatedDesign design, RoundRectangle chipShape, boolean hovered) {
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_BACKGROUND));
        if (hovered)
            graphics.setColor(UIColor.rgb(255, 255, 0));//Color.YELLOW);
        graphics.fill(chipShape);

        graphics.withCopy(chipGraphics -> {
            chipGraphics.translate(chipShape.getX(), chipShape.getY()); // TODO
            chip.getChipTemplate().renderChipPackage(chipGraphics, chip, design);
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

//        if (hoveredPin == null && hoveredChip == null) {
//            Chip chip = new Chip();
//            chip.setChipTemplate(TransistorTemplate.getNPNTransistorTemplate());
//            int newChipX = gridMouseX - (chip.getChipTemplate().getWidth() / 2);
//            int newChipY = gridMouseY - (chip.getChipTemplate().getHeight() / 2);
//            scaledGraphics.setAlpha(0.8f);
//            drawChip(scaledGraphics, chip, SimulatedDesign.none(), renderer.getNewChipShape(chip, newChipX, newChipY), false);
//            for (Pin pin : chip.getChipTemplate().getPins()) {
//                drawPinBackground(scaledGraphics, renderer.getNewPinShape(chip, pin, newChipX, newChipY), LogicState.UNCONNECTED, false);
//                drawPinForeground(scaledGraphics, renderer.getNewPinShape(chip, pin, newChipX, newChipY), LogicState.UNCONNECTED, false);
//            }
//        }
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

    private void calculateHover() {
        gridScaleX = (mouseX / zoom) - translateX;
        gridScaleY = (mouseY / zoom) - translateY;
        gridMouseX = (int) Math.round(gridScaleX);
        gridMouseY = (int) Math.round(gridScaleY);
        hoveredPin = renderer.getHoveredPin(gridScaleX, gridScaleY);
        hoveredChip = renderer.getHoveredChip(gridScaleX, gridScaleY);
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
        if (button == 2) {
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
        if (panning) {
            mouseX = x;
            mouseY = y;
            calculateHover();
            translateX = dragStartTranslateX + (x - mouseDragStartX) / zoom;
            translateY = dragStartTranslateY + (y - mouseDragStartY) / zoom;
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
