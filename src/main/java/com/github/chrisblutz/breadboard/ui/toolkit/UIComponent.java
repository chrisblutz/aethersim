package com.github.chrisblutz.breadboard.ui.toolkit;

import com.github.chrisblutz.breadboard.ui.render.RenderSpace;
import com.github.chrisblutz.breadboard.ui.toolkit.display.helpers.GraphicsContext;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;


public abstract class UIComponent implements UIParentable {

    private static final GraphicsContext GRAPHICS_CONTEXT = new GraphicsContext();

    private final RenderSpace renderSpace = new RenderSpace(this, 0, 0, 0, 0);
    private UIDimension size, preferredSize, minimumSize;

    private UIContainer parent;
    private UIWindow parentWindow;

    public UIComponent() {
        // Default size is 0x0
        size = new UIDimension(0, 0);
        // Default minimum size is 0x0
        minimumSize = new UIDimension(0, 0);
    }

    public final RenderSpace getRenderSpace() {
        return renderSpace;
    }

    public final int getX() {
        return getRenderSpace().getX();
    }

    public final int getY() {
        return getRenderSpace().getY();
    }

    public final int getWidth() {
        return getSize() != null ? getSize().getWidth() : 0;
    }

    public final int getHeight() {
        return getSize() != null ? getSize().getHeight() : 0;
    }

    public final UIDimension getSize() {
        return size;
    }

    public void setSize(UIDimension size) {
        this.size = size;
    }

    public final UIDimension getPreferredSize() {
        // If this component has a preferred size, return it.  Otherwise, default to the minimum size
        if (preferredSize != null)
            return preferredSize;
        else
            return getMinimumSize();
    }

    public void setPreferredSize(UIDimension preferredSize) {
        this.preferredSize = preferredSize;

        // If this component has a parent, repack it
        if (getParent() != null)
            getParent().pack();
    }

    public final UIDimension getMinimumSize() {
        return minimumSize;
    }

    public void setMinimumSize(UIDimension minimumSize) {
        this.minimumSize = minimumSize;

        // If this component has a parent, repack it
        if (getParent() != null)
            getParent().pack();
    }

    @Override
    public final UIContainer getParent() {
        return parent;
    }

    @Override
    public void setParent(UIContainer parent) {
        this.parent = parent;

        // Set the parent window of this component
        setParentWindow(parent.getParentWindow());
    }

    @Override
    public final UIWindow getParentWindow() {
        return parentWindow;
    }

    @Override
    public void setParentWindow(UIWindow parentWindow) {
        this.parentWindow = parentWindow;
    }

    public GraphicsContext getGraphicsContext() {
        return GRAPHICS_CONTEXT;
    }

    public abstract void render(UIGraphics graphics);

    public void onRenderPass() {}
}
