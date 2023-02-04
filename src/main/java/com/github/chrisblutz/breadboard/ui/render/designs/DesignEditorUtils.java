package com.github.chrisblutz.breadboard.ui.render.designs;

import com.github.chrisblutz.breadboard.simulationproto.LogicState;
import com.github.chrisblutz.breadboard.ui.toolkit.UIColor;

public class DesignEditorUtils {

    public static UIColor getColorForLogicState(LogicState state) {
        switch (state) {
            case LOW -> {
                return UIColor.rgb(255, 0, 0);
            }
            case HIGH -> {
                return UIColor.rgb(100, 0, 0);
            }
            case UNCONNECTED -> {
                return UIColor.rgb(10, 10, 10);
            }
            case CONFLICTED -> {
                return UIColor.rgb(0, 255, 0);
            }
            default -> {
                return UIColor.rgb(0, 0, 255);
            }
        }
    }
}
