package com.aethersim.ui.toolkit.shape;

public interface Shape {

    boolean contains(double x, double y);

    Shape translate(double x, double y);

    Shape scale(double scale);
}
