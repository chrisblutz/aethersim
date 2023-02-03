package com.github.chrisblutz.breadboard.ui.render;

import com.github.chrisblutz.breadboard.ui.toolkit.UIComponent;

import java.awt.*;

public class RenderSpace {

    private int x;
    private int y;
    private int width;
    private int height;
    private Rectangle rectangle;

    private UIComponent component = null;

    public RenderSpace(UIComponent component, int x, int y, int width, int height) {
        this.component = component;
        update(x, y, width, height);
    }

    public void update(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rectangle = new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public UIComponent getComponent() {
        return component;
    }
}
