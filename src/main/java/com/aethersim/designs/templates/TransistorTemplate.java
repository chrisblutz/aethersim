package com.aethersim.designs.templates;

import com.aethersim.designs.Chip;
import com.aethersim.designs.Pin;
import com.aethersim.designs.Point;
import com.aethersim.projects.Scope;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.simulation.LogicState;
import com.aethersim.simulation.SimulatedDesign;
import com.aethersim.ui.render.designs.DesignEditorUtils;
import com.aethersim.ui.toolkit.UIColor;
import com.aethersim.ui.toolkit.UIGraphics;
import com.aethersim.ui.toolkit.UIStroke;
import com.aethersim.ui.toolkit.UITheme;
import com.aethersim.ui.toolkit.display.theming.ThemeKeys;
import com.aethersim.ui.toolkit.layout.TextAlignment;
import com.aethersim.utils.Direction;

import java.util.Set;

public class TransistorTemplate extends ChipTemplate {

    public static final Pin NPN_EMITTER = new Pin();
    public static final Pin NPN_COLLECTOR = new Pin();
    public static final Pin NPN_BASE = new Pin();
    public static final Pin PNP_EMITTER = new Pin();
    public static final Pin PNP_COLLECTOR = new Pin();
    public static final Pin PNP_BASE = new Pin();

    private final boolean activeLow;

    private TransistorTemplate(boolean activeLow) {
        this.activeLow = activeLow;
    }

    public boolean isActiveLow() {
        return activeLow;
    }

    public Pin getBase() {
        return isActiveLow() ? PNP_BASE : NPN_BASE;
    }

    public Pin getActiveSignalInput() {
        return isActiveLow() ? PNP_EMITTER : NPN_COLLECTOR;
    }

    public Pin getActiveSignalOutput() {
        return isActiveLow() ? PNP_COLLECTOR : NPN_EMITTER;
    }

    @Override
    public void renderChipPackage(UIGraphics graphics, Chip chip, SimulatedDesign design) {
        // Set base color
        UIColor baseColor = DesignEditorUtils.getColorForLogicState(design.getStateForPin(getBase()));
        graphics.setColor(baseColor);
        graphics.setStroke(UIStroke.dashed(0.1f, UIStroke.Cap.BUTT, UIStroke.Join.ROUND, new float[] {0.2f, 0.2f}));
        graphics.drawPolyline(new double[] {0, 2, 2}, new double[] {3, 3, 2}, 3);

        // Set input color
        UIColor inputColor = DesignEditorUtils.getColorForLogicState(design.getStateForPin(getActiveSignalInput()));
        graphics.setColor(inputColor);
        graphics.setStroke(UIStroke.solid(0.2f, UIStroke.Cap.BUTT, UIStroke.Join.ROUND));
        graphics.drawPolyline(new double[] {0, 1, 1.5}, new double[] {1, 1, 2}, 3);

        // Set output color
        UIColor outputColor = DesignEditorUtils.getColorForLogicState(design.getStateForPin(getActiveSignalOutput()));
        graphics.setColor(outputColor);
        graphics.drawPolyline(new double[] {2.5, 3, 4, 4.5, 6}, new double[] {2, 1, 1, 2, 2}, 5);
        graphics.setStroke(UIStroke.solid(0.1f, UIStroke.Cap.BUTT, UIStroke.Join.MITER)); // TODO PNP
        graphics.fillPolygon(new double[] {3, 3, 2.6}, new double[] {1, 1.5, 1.3}, 3);
        graphics.drawPolygon(new double[] {3, 3, 2.6}, new double[] {1, 1.5, 1.3}, 3);
        graphics.setStroke(UIStroke.solid(0.2f, UIStroke.Cap.BUTT, UIStroke.Join.ROUND));

        // Set connector color if connected
        if (design.getStateForPin(activeLow ? PNP_BASE : NPN_BASE) == (activeLow ? LogicState.LOW : LogicState.HIGH))
            graphics.setColor(inputColor);
        else
            graphics.setColor(DesignEditorUtils.getColorForLogicState(LogicState.UNCONNECTED));
        graphics.drawLine(1, 2, 3, 2);

        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_FOREGROUND));
        graphics.setFont(UITheme.getFont(ThemeKeys.Fonts.UI.TEXT_DEFAULT).derive(1f)); // TODO

        // Draw the chip text in the lower right of the chip
        graphics.drawString(getName(), getWidth() - 0.5f, getHeight() - 0.5f, TextAlignment.Horizontal.RIGHT, TextAlignment.Vertical.BASELINE);
    }

    public static void registerAll() {
        // +---------------------------+
        // |      NPN Transistors      |
        // +---------------------------+

        // Configure pins
        NPN_COLLECTOR.setId("transistor_npn_collector");
        NPN_COLLECTOR.setName("Collector");
        NPN_COLLECTOR.setChipLocation(new Point(0, 1));
        NPN_COLLECTOR.setChipOrientation(Direction.LEFT);
        NPN_BASE.setId("transistor_npn_base");
        NPN_BASE.setName("Base");
        NPN_BASE.setChipLocation(new Point(0, 3));
        NPN_BASE.setChipOrientation(Direction.LEFT);
        NPN_EMITTER.setId("transistor_npn_emitter");
        NPN_EMITTER.setName("Emitter");
        NPN_EMITTER.setChipLocation(new Point(6, 2));
        NPN_EMITTER.setChipOrientation(Direction.RIGHT);

        // Configure template
        TransistorTemplate npnTransistorTemplate = new TransistorTemplate(false); // NPN transistors are active high
        npnTransistorTemplate.setId("transistor_npn");
        npnTransistorTemplate.setName("NPN");
        npnTransistorTemplate.setWidth(6);
        npnTransistorTemplate.setHeight(4);
        npnTransistorTemplate.setPins(Set.of(NPN_COLLECTOR, NPN_BASE, NPN_EMITTER));

        // Register
        ChipTemplate.register(npnTransistorTemplate, Scope.NATIVE);

        // +---------------------------+
        // |      PNP Transistors      |
        // +---------------------------+

        // Configure pins
        PNP_EMITTER.setId("transistor_pnp_emitter");
        PNP_EMITTER.setName("Emitter");
        PNP_EMITTER.setChipLocation(new Point(0, 1));
        PNP_EMITTER.setChipOrientation(Direction.LEFT);
        PNP_BASE.setId("transistor_pnp_base");
        PNP_BASE.setName("Base");
        PNP_BASE.setChipLocation(new Point(0, 3));
        PNP_BASE.setChipOrientation(Direction.LEFT);
        PNP_COLLECTOR.setId("transistor_pnp_collector");
        PNP_COLLECTOR.setName("Collector");
        PNP_COLLECTOR.setChipLocation(new Point(6, 2));
        PNP_COLLECTOR.setChipOrientation(Direction.RIGHT);

        // Configure template
        TransistorTemplate pnpTransistorTemplate = new TransistorTemplate(true); // PNP transistors are active low
        pnpTransistorTemplate.setId("transistor_pnp");
        pnpTransistorTemplate.setName("PNP");
        pnpTransistorTemplate.setWidth(6);
        pnpTransistorTemplate.setHeight(4);
        pnpTransistorTemplate.setPins(Set.of(PNP_EMITTER, PNP_BASE, PNP_COLLECTOR));

        // Register
        ChipTemplate.register(pnpTransistorTemplate, Scope.NATIVE);
    }

    @Override
    public void deserialize(DataMap data, DataContext context) { /* do nothing */ }

    @Override
    public void serialize(DataMap data, DataContext context) { /* do nothing */ }
}
