package com.github.chrisblutz.breadboard.ui.toolkit.layout;

public class Padding {

    private final int paddingTop;
    private final int paddingRight;
    private final int paddingBottom;
    private final int paddingLeft;

    public Padding(int padding) {
        this(padding, padding);
    }

    public Padding(int paddingX, int paddingY) {
        this(paddingY, paddingX, paddingY, paddingX);
    }

    public Padding(int paddingTop, int paddingRight, int paddingBottom, int paddingLeft) {
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }
}
