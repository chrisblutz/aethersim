package com.aethersim.ui.tests;

import com.aethersim.tests.annotations.AetherSimTest;
import com.aethersim.tests.annotations.AetherSimTests;
import com.aethersim.tests.annotations.UITests;
import com.aethersim.ui.tests.utils.Tripwire;
import com.aethersim.ui.tests.utils.UIVirtualScreen;
import com.aethersim.ui.tests.utils.UIVirtualWindow;
import com.aethersim.ui.toolkit.builtin.containers.UIPadding;
import com.aethersim.ui.toolkit.builtin.input.UIButton;
import com.aethersim.ui.toolkit.layout.Padding;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@UITests
@AetherSimTests("UIButton")
public class TestUIButton {

    @AetherSimTest("Hover State Transitions")
    void testHover() {
        Tripwire<Void> tripwire = new Tripwire<>(null);
        UIButton button = new UIButton("Test", tripwire::set);
        UIPadding padding = new UIPadding(button, new Padding(10));
        UIVirtualWindow virtualWindow = new UIVirtualWindow();
        virtualWindow.setContent(padding);
        UIVirtualScreen virtualScreen = new UIVirtualScreen(virtualWindow);

    }
}
