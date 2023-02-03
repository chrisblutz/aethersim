package com.github.chrisblutz.breadboard.ui.toolkit.layout;

public class UIDimension {

    private int width, height;

    public UIDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public UIDimension add(int width, int height) {
        UIDimension copy = new UIDimension(getWidth(), getHeight());
        // Add to the width and height, but clamp them to 0 so we don't get negatives
        copy.width += width;
        if (copy.width < 0)
            copy.width = 0;
        copy.height += height;
        if (copy.height < 0)
            copy.height = 0;

        // Return the copy
        return copy;
    }

    public UIDimension remove(int width, int height) {
        return add(-width, -height);
    }
}
