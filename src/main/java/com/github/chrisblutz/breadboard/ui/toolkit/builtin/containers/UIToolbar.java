package com.github.chrisblutz.breadboard.ui.toolkit.builtin.containers;

import com.github.chrisblutz.breadboard.ui.toolkit.UIComponent;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.spacing.UISeparator;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.spacing.UISpacer;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Direction;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Padding;

public class UIToolbar extends UIAbstractFlexContainer {

    private static class UIToolbarSpacer extends UISpacer {

        public UIToolbarSpacer(Direction direction, int spacerWidth) {
            super(direction, spacerWidth);
        }
    }

    private static class UIToolbarPadding extends UIPadding {

        public UIToolbarPadding(UIComponent component, int padding) {
            super(component, new Padding(padding, padding, padding, 0));
        }

        private void setPadding(int padding) {
            setPadding(new Padding(padding, padding, padding, 0));
            pack();
        }
    }

    private int padding;

    public UIToolbar(Direction direction) {
        this(direction, 5);
    }

    public UIToolbar(Direction direction, int padding) {
        super(direction);

        this.padding = padding;

        // Add the first spacer to the toolbar to facilitate padding
        addComponent(new UIToolbarSpacer(direction, padding), 0f);
    }

    @Override
    public Direction getDirection() {
        return super.getDirection();
    }

    public void add(UIComponent component) {
        addComponent(new UIToolbarPadding(component, padding), 0f);
    }

    public void addSeparator() {
        addComponent(new UIToolbarPadding(new UISeparator(getDirection()), padding), 0f);
    }

    public void addFlexSpace() {
        addComponent(new UIToolbarPadding(new UISpacer(getDirection()), padding), 1f);
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;

        // Update padding on all child toolbar control components
        for (UIComponent childComponent : getComponents()) {
            if (childComponent instanceof UIToolbarSpacer toolbarSpacer)
                toolbarSpacer.setSpacerWidth(padding);
            else if (childComponent instanceof UIToolbarPadding toolbarPadding)
                toolbarPadding.setPadding(padding);
        }
    }

    @Override
    public void render(UIGraphics graphics) {
        // Draw the background
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BACKGROUND_PRIMARY));
        graphics.fillRect(0, 0, getWidth(), getHeight());

        // Render internal components
        super.render(graphics);
    }
}
