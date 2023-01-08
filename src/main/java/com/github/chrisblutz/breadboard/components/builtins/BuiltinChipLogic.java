package com.github.chrisblutz.breadboard.components.builtins;

import com.github.chrisblutz.breadboard.designs.components.Pin;

import java.util.Map;

public interface BuiltinChipLogic {

    Pin[] getInputPins();

    Pin[] getOutputPins();

    Map<Pin, Boolean> doTick(Map<Pin, Boolean> inputStates);
}
