package com.github.chrisblutz.breadboard.ui.toolkit.shape;

public class Rectangle implements Shape {

    private final double x1, x2, y1, y2;
    private final double width, height;

    public Rectangle(double x, double y, double width, double height) {
        this.x1 = x;
        this.y1 = y;
        this.x2 = x + width;
        this.y2 = y + height;
        this.width = width;
        this.height = height;
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

    public boolean contains(double x, double y) {
        return (x >= x1) && (x <= x2) && (y >= y1) && (y <= y2);
    }
}
