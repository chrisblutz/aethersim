package com.aethersim.ui.toolkit;

public interface UIInteractable {

    boolean onMouseClicked(int x, int y, int button);

    boolean onMousePressed(int x, int y, int button);

    void onMouseReleased(int x, int y, int button);

    void onMouseEntered();

    void onMouseExited();

    void onMouseDragged(int x, int y);

    boolean onMouseMoved(int x, int y);

    boolean onMouseScrolled(int scrollAmount);
}
