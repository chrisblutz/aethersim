package com.github.chrisblutz.breadboard.ui.toolkit.builtin.input;

import com.github.chrisblutz.breadboard.ui.toolkit.UIColor;
import com.github.chrisblutz.breadboard.ui.toolkit.UIComponent;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.UIInteractable;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Direction;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

public class UIScrollbar extends UIComponent implements UIInteractable {

    private static final int SCROLLBAR_WIDTH = 10;

    private Direction direction;

    public UIScrollbar(Direction direction) {
        this.direction = direction;

        // Set the minimum size to the scrollbar width in the specified direction
        setMinimumSize(new UIDimension(
                direction == Direction.VERTICAL ? SCROLLBAR_WIDTH : 0,
                direction == Direction.VERTICAL ? 0 : SCROLLBAR_WIDTH
        ));
    }

    @Override
    public void render(UIGraphics graphics) {
        // TODO
        graphics.setColor(UIColor.rgb(200, 0, 0));
        graphics.getInternalGraphics().fillRoundRect(0, 0, getWidth(), getHeight(), SCROLLBAR_WIDTH / 2, SCROLLBAR_WIDTH / 2);
    }

    @Override
    public boolean onMouseClicked(int x, int y, int button) {
        return false;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        return false;
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {

    }

    @Override
    public void onMouseEntered() {

    }

    @Override
    public void onMouseExited() {

    }

    @Override
    public void onMouseDragged(int x, int y) {

    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean onMouseScrolled(int scrollAmount) {
        return false;
    }
}
