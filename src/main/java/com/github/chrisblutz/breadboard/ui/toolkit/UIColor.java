package com.github.chrisblutz.breadboard.ui.toolkit;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;

import java.awt.*;

public class UIColor implements Cloneable {

    static final float EPSILON = 0.0001f;

    private final Color internalColor;
    private final int red;
    private final int green;
    private final int blue;
    private float alpha;

    private UIColor(int red, int green, int blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.internalColor = new Color(red, green, blue);
        this.alpha = alpha;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public UIColor darker() {
        return derive(0.7f);
    }

    public UIColor brighter() {
        return derive(1.3f);
    }

    public UIColor derive(float luminanceRatio) {
        return new UIColor(
                (int) (red * luminanceRatio),
                (int) (green * luminanceRatio),
                (int) (blue * luminanceRatio),
                alpha
        );
    }

    public UIColor withAlpha(float alpha) {
        // If the requested alpha is 1, return this color
        if (Math.abs(1f - alpha) < EPSILON)
            return this;

        UIColor cloneColor = clone();
        // Combine alpha values, so the new alpha is the fractional alpha of the original
        // For example, an original color with 0.5 alpha that requests a new color with
        // 0.5 alpha results in a new color with 0.25 alpha
        cloneColor.alpha *= alpha;
        return cloneColor;
    }

    @Deprecated
    public Color getInternalColor() {
        return internalColor;
    }

    @Override
    protected UIColor clone() {
        try {
            return (UIColor) super.clone();
        } catch (CloneNotSupportedException e) {
            // We should never get here, since this class is Cloneable
            BreadboardLogging.getInterfaceLogger().error("Color object could not be cloned.", e);
            return null;
        }
    }

    public static UIColor rgb(int red, int green, int blue) {
        return rgba(red, green, blue, 1f);
    }

    public static UIColor rgb(int rgb) {
        return rgb(
                (rgb & 0xFF0000) >> 16,
                (rgb & 0x00FF00) >> 8,
                rgb & 0x0000FF
        );
    }

    public static UIColor rgba(int red, int green, int blue, float alpha) {
        // TODO Range checks
        return new UIColor(red, green, blue, alpha);
    }

    public static UIColor rgba(int rgba) {
        return rgba(
                (rgba & 0xFF000000) >> 24,
                (rgba & 0x00FF0000) >> 16,
                (rgba & 0x0000FF00) >> 8,
                (float) (rgba & 0x000000FF) / 255
        );
    }
}
