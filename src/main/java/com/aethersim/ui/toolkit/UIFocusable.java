package com.aethersim.ui.toolkit;

import java.awt.event.KeyEvent;

public interface UIFocusable extends UIParentable {

    default boolean isFocused() {
        if (getParentWindow() != null)
            return getParentWindow().isFocused(this);
        else
            return false;
    }

    default void focus(boolean keyboardTriggered) {
        if (getParentWindow() != null)
            getParentWindow().focus(this, keyboardTriggered);
    }

    default void unfocus(boolean keyboardTriggered) {
        // If this component is currently focused, remove all focused components
        // If this component is not focused, do nothing
        if (isFocused() && getParentWindow() != null)
            getParentWindow().focus(null, keyboardTriggered);
    }

    default void setFocusState(boolean focused, boolean keyboardTriggered) {
        // Call any listeners that are triggered by a change in focus
        if (!isFocused() && focused) {
            onFocusGained(keyboardTriggered);
            if (getParent() != null)
                getParent().onChildFocused();
        } else if (isFocused() && !focused) {
            onFocusLost(keyboardTriggered);
            if (getParent() != null)
                getParent().onChildUnfocused();
        }
    }

    boolean onKeyTyped(KeyEvent e);

    boolean onKeyPressed(KeyEvent e);

    boolean onKeyReleased(KeyEvent e);

    void onFocusGained(boolean keyboardTriggered);

    void onFocusLost(boolean keyboardTriggered);
}
