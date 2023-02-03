package com.github.chrisblutz.breadboard.ui.tests;

import com.github.chrisblutz.breadboard.tests.annotations.UITests;
import com.github.chrisblutz.breadboard.ui.tests.utils.Tripwire;
import com.github.chrisblutz.breadboard.ui.tests.utils.UIVirtualScreen;
import com.github.chrisblutz.breadboard.ui.tests.utils.UIVirtualWindow;
import com.github.chrisblutz.breadboard.ui.toolkit.UIParentable;
import com.github.chrisblutz.breadboard.ui.toolkit.UIWindow;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.containers.UIPadding;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.input.UIButton;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Padding;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@UITests
@DisplayName("UIToolkit -> UIButton")
public class TestUIButton {

    @Test
    @DisplayName("Hover State Transitions")
    void testHover() {
        Tripwire<Void> tripwire = new Tripwire<>(null);
        UIButton button = new UIButton("Test", tripwire::set);
        UIPadding padding = new UIPadding(button, new Padding(10));
        UIVirtualWindow virtualWindow = new UIVirtualWindow();
        virtualWindow.setContent(padding);
        UIVirtualScreen virtualScreen = new UIVirtualScreen(virtualWindow);

    }
}
