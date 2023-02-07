package com.github.chrisblutz.breadboard.ui.toolkit.shape;

public class RoundRectangle implements Shape {

    private final double x1, x2, y1, y2;
    private final double width, height, arcWidth, arcHeight;

    public RoundRectangle(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        this.x1 = x;
        this.y1 = y;
        this.x2 = x + width;
        this.y2 = y + height;
        this.width = width;
        this.height = height;
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
    }

    public double getX() {
        return x1;
    }

    public double getY() {
        return y1;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getArcWidth() {
        return arcWidth;
    }

    public double getArcHeight() {
        return arcHeight;
    }

    @Override
    public boolean contains(double x, double y) {
        // TODO include arcs
        return (x >= x1) && (x <= x2) && (y >= y1) && (y <= y2);
    }

    @Override
    public Shape translate(double x, double y) {
        return null;
    }

    @Override
    public Shape scale(double scale) {
        return null;
    }
}
