package com.github.chrisblutz.breadboard.ui.render.designs;

public class EditorParameters {

    private float preciseGridUnit;

    private int gridUnit;
    private int gridDividerThickness;
    private int chipBorderThickness;
    private int pinDiameter;
    private int wireThickness;

    private float wireCornerArcMidpointGuide;

    public EditorParameters(int defaultGridUnit) {
        recalculate(defaultGridUnit);
    }

    public EditorParameters(float defaultPreciseGridUnit) {
        recalculate(defaultPreciseGridUnit);
    }

    public float getPreciseGridUnit() {
        return preciseGridUnit;
    }

    public int getGridUnit() {
        return gridUnit;
    }

    public int getGridDividerThickness() {
        return gridDividerThickness;
    }

    public int getChipBorderThickness() {
        return chipBorderThickness;
    }

    public int getPinDiameter() {
        return pinDiameter;
    }

    public int getWireThickness() {
        return wireThickness;
    }

    public float getWireCornerArcMidpointGuide() {
        return wireCornerArcMidpointGuide;
    }

    public void recalculate(int gridUnit) {
        recalculate((float) gridUnit);
    }

    public void recalculate(float preciseGridUnit) {
        // Set the current precise grid unit
        this.preciseGridUnit = preciseGridUnit;
        // Calculate design parameters from new grid unit
        this.gridUnit = Math.round(preciseGridUnit);
        this.gridDividerThickness = gridUnit / 10;
        this.chipBorderThickness = gridUnit / 6;
        this.pinDiameter = gridUnit * 4 / 5;
        this.wireThickness = gridUnit * 2 / 5;

        // Calculate new midpoint guide distance
        this.wireCornerArcMidpointGuide = ((float) gridUnit / 2) - (((float) Math.sqrt(2) / 2) * ((float) gridUnit / 2));
    }
}
