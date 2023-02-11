package com.github.chrisblutz.breadboard.ui.render.designs;

import com.github.chrisblutz.breadboard.simulation.LogicState;
import com.github.chrisblutz.breadboard.ui.toolkit.UIColor;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;

public class DesignEditorUtils {

    private static boolean conflictedState = false;

    public static UIColor getColorForLogicState(LogicState state) {
        // The unconnected state is treated as the "default" catch-all in the switch below
        switch (state) {
            case LOW -> {
                return UITheme.getColor(ThemeKeys.Colors.Design.LOGIC_STATE_LOW);
            }
            case HIGH -> {
                return UITheme.getColor(ThemeKeys.Colors.Design.LOGIC_STATE_HIGH);
            }
            case CONFLICTED -> {
                if (conflictedState)
                    return UITheme.getColor(ThemeKeys.Colors.Design.LOGIC_STATE_HIGH);
                else
                    return UITheme.getColor(ThemeKeys.Colors.Design.LOGIC_STATE_LOW);
            }
            default -> {
                return UITheme.getColor(ThemeKeys.Colors.Design.LOGIC_STATE_UNCONNECTED);
            }
        }
    }

    public static void updateRandomConflictedState() {
        if (Math.random() > 0.66)
            conflictedState = !conflictedState;
    }
}
