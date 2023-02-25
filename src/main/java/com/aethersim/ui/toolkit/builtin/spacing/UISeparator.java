package com.aethersim.ui.toolkit.builtin.spacing;

import com.aethersim.ui.toolkit.UIComponent;
import com.aethersim.ui.toolkit.UIGraphics;
import com.aethersim.ui.toolkit.UITheme;
import com.aethersim.ui.toolkit.display.theming.ThemeKeys;
import com.aethersim.ui.toolkit.layout.Direction;
import com.aethersim.ui.toolkit.layout.UIDimension;

public class UISeparator extends UIComponent {

    private Direction direction;
    private int separatorWidth;

    public UISeparator(Direction direction) {
        this(direction, 1);
    }

    public UISeparator(Direction direction, int separatorWidth) {
        this.direction = direction;
        this.separatorWidth = separatorWidth;

        // Set the minimum dimension in the primary direction
        setMinimumSize(new UIDimension(
                direction == Direction.VERTICAL ? 0 : this.separatorWidth,
                direction == Direction.VERTICAL ? this.separatorWidth : 0
        ));
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSeparatorWidth() {
        return separatorWidth;
    }

    public void setSeparatorWidth(int separatorWidth) {
        this.separatorWidth = separatorWidth;

        // Set the minimum dimension in the primary direction
        setMinimumSize(new UIDimension(
                direction == Direction.VERTICAL ? 0 : this.separatorWidth,
                direction == Direction.VERTICAL ? this.separatorWidth : 0
        ));

        if (getParent() != null)
            getParent().pack();
    }

    @Override
    public void render(UIGraphics graphics) {
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BORDER_PRIMARY_ACCENT));
        if (direction == Direction.HORIZONTAL)
            graphics.fillRect((getWidth() / 2) - (separatorWidth / 2), 0, separatorWidth, getHeight());
        else
            graphics.drawLine(0, (getHeight() / 2) - (separatorWidth / 2), getWidth(), separatorWidth);
    }
}
