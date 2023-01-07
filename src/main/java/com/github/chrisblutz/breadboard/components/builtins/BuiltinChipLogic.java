package com.github.chrisblutz.breadboard.components.builtins;

import com.github.chrisblutz.breadboard.components.BuiltinChipTemplate;
import com.github.chrisblutz.breadboard.components.PinTemplate;

import java.util.Map;

public interface BuiltinChipLogic {

    PinTemplate[] getInputPinTemplates();

    PinTemplate[] getOutputPinTemplates();

    Map<PinTemplate, Boolean> doTick(Map<PinTemplate, Boolean> inputStates);
}
