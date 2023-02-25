package com.aethersim.simulation;

import com.aethersim.designs.Pin;
import com.aethersim.projects.io.data.DataSerializable;

import java.util.HashMap;
import java.util.Map;

public abstract class ChipState implements DataSerializable {

    private final Map<Pin, LogicState> drivenInputPinStates = new HashMap<>();
    private final Map<Pin, LogicState> pulledInputPinStates = new HashMap<>();
    private final Map<Pin, LogicState> drivenOutputPinStates = new HashMap<>();
    private final Map<Pin, LogicState> pulledOutputPinStates = new HashMap<>();

    public LogicState getDrivenInputState(Pin pin) {
        return drivenInputPinStates.getOrDefault(pin, LogicState.UNKNOWN);
    }

    public void setDrivenInputState(Pin pin, LogicState state) {
        // If the state is null, set it to be unknown
        if (state == null)
            state = LogicState.UNKNOWN;
        // Update the pin state
        drivenInputPinStates.put(pin, state);
    }

    public LogicState getPulledInputState(Pin pin) {
        return pulledInputPinStates.getOrDefault(pin, LogicState.UNKNOWN);
    }

    public void setPulledInputState(Pin pin, LogicState state) {
        // If the state is null, set it to be unknown
        if (state == null)
            state = LogicState.UNKNOWN;
        // Update the pin state
        pulledInputPinStates.put(pin, state);
    }

    public LogicState getDrivenOutputState(Pin pin) {
        return drivenOutputPinStates.getOrDefault(pin, LogicState.UNKNOWN);
    }

    public void setDrivenOutputState(Pin pin, LogicState state) {
        // If the state is null, set it to be unknown
        if (state == null)
            state = LogicState.UNKNOWN;
        // Update the pin state
        drivenOutputPinStates.put(pin, state);
    }

    public LogicState getPulledOutputState(Pin pin) {
        return pulledOutputPinStates.getOrDefault(pin, LogicState.UNKNOWN);
    }

    public void setPulledOutputState(Pin pin, LogicState state) {
        // If the state is null, set it to be unknown
        if (state == null)
            state = LogicState.UNKNOWN;
        // Update the pin state
        pulledOutputPinStates.put(pin, state);
    }
}
