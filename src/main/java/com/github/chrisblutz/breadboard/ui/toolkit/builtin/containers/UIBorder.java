package com.github.chrisblutz.breadboard.ui.toolkit.builtin.containers;

import com.github.chrisblutz.breadboard.ui.toolkit.UIComponent;
import com.github.chrisblutz.breadboard.ui.toolkit.UIContainer;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class UIBorder extends UIContainer {

    public enum BorderLayout {
        TOP (new boolean[] {true, false, false, false}),
        RIGHT (new boolean[] {false, true, false, false}),
        BOTTOM (new boolean[] {false, false, true, false}),
        LEFT (new boolean[] {false, false, false, true}),
        TOP_RIGHT (new boolean[] {true, true, false, false}),
        TOP_BOTTOM (new boolean[] {true, false, true, false}),
        TOP_LEFT (new boolean[] {true, false, false, true}),
        BOTTOM_RIGHT (new boolean[] {false, true, true, false}),
        BOTTOM_LEFT (new boolean[] {false, false, true, true}),
        LEFT_RIGHT (new boolean[] {false, true, false, true}),
        TOP_RIGHT_BOTTOM (new boolean[] {true, true, true, false}),
        TOP_LEFT_BOTTOM (new boolean[] {true, false, true, true}),
        LEFT_TOP_RIGHT (new boolean[] {true, true, false, true}),
        LEFT_BOTTOM_RIGHT (new boolean[] {false, true, true, true}),
        ALL (new boolean[] {true, true, true, true});

        private final boolean[] borderLayout;

        BorderLayout(boolean[] borderLayout) {
            this.borderLayout = borderLayout;
        }

        public boolean[] getBorderLayout() {
            return borderLayout;
        }
    }

    private UIComponent component;
    private List<UIComponent> componentCollection;
    private int borderWidth;

    private boolean borderTop;
    private boolean borderRight;
    private boolean borderBottom;
    private boolean borderLeft;

    public UIBorder(UIComponent component, int borderWidth) {
        this(component, borderWidth, BorderLayout.ALL);
    }

    public UIBorder(UIComponent component, BorderLayout borderLayout) {
        this(component, 1, borderLayout);
    }

    public UIBorder(UIComponent component, int borderWidth, BorderLayout borderLayout) {
        this.component = component;
        component.setParent(this);
        this.componentCollection = Collections.singletonList(component);
        this.borderWidth = borderWidth;
        this.borderTop = borderLayout.getBorderLayout()[0];
        this.borderRight = borderLayout.getBorderLayout()[1];
        this.borderBottom = borderLayout.getBorderLayout()[2];
        this.borderLeft = borderLayout.getBorderLayout()[3];

        // Pack this component since we've set the container
        pack();
    }

    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
        component.setParent(this);
        this.componentCollection = Collections.singletonList(component);
        pack();
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        pack();
    }

    public boolean hasBorderTop() {
        return borderTop;
    }

    public void setBorderTop(boolean borderTop) {
        this.borderTop = borderTop;
        pack();
    }

    public boolean hasBorderRight() {
        return borderRight;
    }

    public void setBorderRight(boolean borderRight) {
        this.borderRight = borderRight;
    }

    public boolean hasBorderBottom() {
        return borderBottom;
    }

    public void setBorderBottom(boolean borderBottom) {
        this.borderBottom = borderBottom;
        pack();
    }

    public boolean hasBorderLeft() {
        return borderLeft;
    }

    public void setBorderLeft(boolean borderLeft) {
        this.borderLeft = borderLeft;
        pack();
    }

    @Override
    public List<UIComponent> getComponents() {
        return componentCollection;
    }

    @Override
    public void pack() {
        // Set the minimum dimension of this component to be the sum of the child component
        // and all padding in that dimension
        if (component != null)
            setMinimumSize(new UIDimension(
                    component.getMinimumSize().getWidth() + (borderLeft ? borderWidth : 0) + (borderRight ? borderWidth : 0),
                    component.getMinimumSize().getHeight() + (borderTop ? borderWidth : 0) + (borderBottom ? borderWidth : 0)
            ));
        else
            setMinimumSize(new UIDimension(
                    (borderLeft ? borderWidth : 0) + (borderRight ? borderWidth : 0),
                    (borderTop ? borderWidth : 0) + (borderBottom ? borderWidth : 0)
            ));
    }

    @Override
    public void settle() {
        // Now that we know the size of this component, set the size of the child component
        // and set its render space
        if (component != null) {
            component.setSize(new UIDimension(
                    getWidth() - (borderLeft ? borderWidth : 0) - (borderRight ? borderWidth : 0),
                    getHeight() - (borderTop ? borderWidth : 0) - (borderBottom ? borderWidth : 0)
            ));
            component.getRenderSpace().update(
                    getRenderSpace().getX() + (borderLeft ? borderWidth : 0),
                    getRenderSpace().getY() + (borderTop ? borderWidth : 0),
                    component.getWidth(),
                    component.getHeight()
            );
        }

        // If the child component is a container, settle it
        if (component instanceof UIContainer containerComponent)
            containerComponent.settle();
    }

    @Override
    public void render(UIGraphics graphics) {
        // Render the child component
        if (component != null)
            renderComponent(graphics, component);

        // Render the border
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BORDER_PRIMARY));
        if (borderTop)
            graphics.fillRect(0, 0, getWidth(), borderWidth);
        if (borderRight)
            graphics.fillRect(getWidth() - borderWidth, 0, borderWidth, getHeight());
        if (borderBottom)
            graphics.fillRect(0, getHeight() - borderWidth, getWidth(), borderWidth);
        if (borderLeft)
            graphics.fillRect(0, 0, borderWidth, getHeight());
    }
}
