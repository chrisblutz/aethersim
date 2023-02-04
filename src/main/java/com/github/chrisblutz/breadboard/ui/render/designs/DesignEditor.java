package com.github.chrisblutz.breadboard.ui.render.designs;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.ChipPin;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulationproto.LogicState;
import com.github.chrisblutz.breadboard.simulationproto.SimulatedDesign;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshSimulatedDesign;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshVertex;
import com.github.chrisblutz.breadboard.ui.toolkit.*;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;
import com.github.chrisblutz.breadboard.ui.window.BreadboardWindow;

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

    private double zoom = 20;
    private DesignRenderer renderer = new DesignRenderer();
    private ChipPin hoveredPin = null;
    private Chip hoveredChip = null;

    public DesignEditor(Design design) {
        this(design, null);
    }

    public DesignEditor(Design design, SimulatedDesign simulatedDesign) {
        this.rendererParameters = new EditorParameters(DEFAULT_GRID_UNIT);

        this.design = design;
        this.simulatedDesign = simulatedDesign;

        setMinimumSize(new UIDimension(200, 200));
    }

    @Override
    public void render(UIGraphics graphics) {
        // Calculate initial render space parameters
        calculateRenderSpaceParameters();

        // TODO
        //Graphics2D g = graphics.getInternalGraphics();
        renderer.generate(design, zoom, 0, 0);

        graphics.withCopy(this::drawGrid);

//        g.scale(zoom, zoom);

        graphics.withCopy(designGraphics -> drawDesign(designGraphics, design));
    }

    private void calculateRenderSpaceParameters() {
        // Calculate the "actual" size of the grid in the render space
        this.designGridActualWidth = getActualDimension(design.getWidth());
        this.designGridActualHeight = getActualDimension(design.getHeight());
        // Calculate the "origin" of the design in the render space (centers the design if it doesn't fill it)
        this.renderOriginX = designGridActualWidth < getRenderSpace().getWidth() ? ((getRenderSpace().getWidth() - designGridActualWidth) / 2) : 0; // TODO: mouse drag origin
        this.renderOriginY = designGridActualHeight < getRenderSpace().getHeight() ? ((getRenderSpace().getHeight() - designGridActualHeight) / 2) : 0; // TODO: mouse drag origin
    }

    private void drawGrid(UIGraphics graphics) {
        // Store grid divider thickness value, so we can use it without referring to the method multiple times
        int dividerThickness = rendererParameters.getGridDividerThickness();

        // Draw background
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BACKGROUND_PRIMARY));
        graphics.fillRect(0, 0, getRenderSpace().getWidth(), getRenderSpace().getHeight());

        // Draw solid border around edges
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BORDER_PRIMARY));
        graphics.setStroke(UIStroke.solid(dividerThickness));
        graphics.drawRect(renderOriginX, renderOriginY, designGridActualWidth, designGridActualHeight);

        // Draw dots for the interior grid
        for (int x = 1; x < design.getWidth(); x++) {
            for (int y = 1; y < design.getHeight(); y++) {
                graphics.fillEllipse(getActualX(x) - ((double) dividerThickness / 2), getActualY(y) - ((double) dividerThickness / 2), dividerThickness, dividerThickness);
            }
        }
    }

    private void drawDesign(UIGraphics graphics, Design design) {
        // Draw all design chips
        for (Chip chip : design.getChips())
            graphics.withCopy(chipGraphics -> drawChip(chipGraphics, chip));

        // Draw all design pins
        for (Pin pin : design.getPins())
            graphics.withCopy(pinGraphics -> drawDesignPin(pinGraphics, pin, simulatedDesign != null ? simulatedDesign.getStateForPin(pin) : null));

        // Draw all design wires
        for (Wire wire : design.getWires())
            graphics.withCopy(wireGraphics -> drawWire(wireGraphics, wire, simulatedDesign != null ? simulatedDesign.getStateForWire(wire) : null));
    }

    private void drawDesignPin(UIGraphics graphics, Pin pin, LogicState state) {
        drawPin(graphics, getActualX(pin.getDesignX()), getActualY(pin.getDesignY()), state, null, pin, ((MeshSimulatedDesign) simulatedDesign).getPinMapping().get(pin));
    }

    private void drawChipPin(UIGraphics graphics, Chip chip, Pin pin, LogicState state) {
        drawPin(graphics, getActualX(chip.getX() + pin.getChipX()), getActualY(chip.getY() + pin.getChipY()), state, chip, pin, ((MeshSimulatedDesign) simulatedDesign).getChipMapping().get(chip).getPinMapping().get(pin));
    }

    private void drawPin(UIGraphics graphics, int x, int y, LogicState state, Chip chip, Pin pin, MeshVertex vertex) {
        // Draw the pin itself
        graphics.setColor( // TODO
                (state == LogicState.HIGH) ?
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BACKGROUND_ACTIVE) :
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BACKGROUND_INACTIVE)
        );
        if (hoveredPin != null && hoveredPin.chip() == chip && hoveredPin.pin() == pin)
            graphics.setColor(UIColor.rgb(255, 255, 0));//Color.YELLOW);
        graphics.fill(renderer.getPinShape(new ChipPin(chip, pin))); // TODO

        // Draw border around the pin
        graphics.setColor( // TODO
                (state == LogicState.HIGH) ?
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BORDER_ACTIVE) :
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BORDER_INACTIVE)
        );
        graphics.setStroke(UIStroke.solid((float) (zoom * 0.2))); // TODO default
        graphics.draw(renderer.getPinShape(new ChipPin(chip, pin)));

//        g.setColor(Color.WHITE);
//        g.setFont(new Font("Arial", Font.PLAIN, 10));
//        g.drawString(Integer.toString(vertex.hashCode()), x, y-10);
//
//        g.setColor(Color.YELLOW);
//        g.setFont(new Font("Arial", Font.PLAIN, 10));
//        g.drawString(Integer.toString(pin.hashCode()), x, y-20);
//
//        g.setColor(Color.GREEN);
//        g.setFont(new Font("Arial", Font.PLAIN, 10));
//        g.drawString(chip == null ? "NULL" : Integer.toString(chip.hashCode()), x, y-30);
    }

    private void drawWire(UIGraphics graphics, Wire wire, LogicState state) {
        // Set the stroke used for the path
       // graphics.setStroke(new BasicStroke((float) (zoom * 0.3), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)); // TODO default, convert

        // Now draw the wire path we calculated above
        graphics.setColor( // TODO
                (state == LogicState.HIGH) ?
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BACKGROUND_ACTIVE) :
                UITheme.getColor(ThemeKeys.Colors.Design.PIN_BACKGROUND_INACTIVE)
        );
       // graphics.draw(renderer.getWireShape(wire));
    }

    private void drawChip(UIGraphics graphics, Chip chip) {
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_BACKGROUND));
        if (hoveredChip == chip)
            graphics.setColor(UIColor.rgb(255, 255, 0));//Color.YELLOW);
        graphics.fill(renderer.getChipShape(chip));

        graphics.withCopy(chipGraphics -> {
            chipGraphics.translate(renderer.getChipShape(chip).getX(), renderer.getChipShape(chip).getY()); // TODO
            chip.getChipTemplate().renderChipPackage(chipGraphics, simulatedDesign.getSimulatedChipDesign(chip), zoom, 0, 0);
        });

        // Get the simulated design for this chip
        SimulatedDesign chipDesignInstance = simulatedDesign != null ? simulatedDesign.getSimulatedChipDesign(chip) : null;

        // Draw pins for chip
        for (Pin pin : chip.getChipTemplate().getPins())
            drawChipPin(graphics, chip, pin, chipDesignInstance != null ? chipDesignInstance.getStateForPin(pin) : null);
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
        hoveredPin = renderer.getHoveredPin(x, y);
        hoveredChip = renderer.getHoveredChip(x, y);
        return true;
    }

    @Override
    public boolean onMouseScrolled(int scrollAmount) {
        zoom += ((double) scrollAmount / 4);
        if (zoom < 1)
            zoom = 1;
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
