package com.aethersim.ui.toolkit;

public interface UIParentable {

    UIContainer getParent();

    void setParent(UIContainer parent);

    UIWindow getParentWindow();

    void setParentWindow(UIWindow parentWindow);
}
