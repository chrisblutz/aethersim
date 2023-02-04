package com.github.chrisblutz.breadboard.ui.render.designs;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.simulationproto.LogicState;
import com.github.chrisblutz.breadboard.simulationproto.SimulatedDesign;

import java.awt.*;

public class DesignGraphics {

    private Graphics2D graphics;
    private SimulatedDesign design;
    private Chip chip;

    public DesignGraphics(Graphics2D graphics, SimulatedDesign design, Chip chip) {
        this.graphics = graphics;
        this.design = design;
        this.chip = chip;
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public SimulatedDesign getDesign() {
        return design;
    }

    public Chip getChip() {
        return chip;
    }

    public Color getColorForLogicState(LogicState state) {
        switch (state) {
            case LOW -> {
                return Color.BLUE;
            }
            case HIGH -> {
                return Color.RED;
            }
            case UNCONNECTED -> {
                return Color.GREEN;
            }
            case CONFLICTED -> {
                return Color.YELLOW;
            }
            default -> {
                return Color.PINK;
            }
        }
    }
}
