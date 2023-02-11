package com.github.chrisblutz.breadboard.designs.templates;

import com.github.chrisblutz.breadboard.designs.Pin;
import com.github.chrisblutz.breadboard.simulation.ChipState;
import com.github.chrisblutz.breadboard.simulation.LogicState;

public class ToggleState extends ChipState {

    private volatile LogicState drivenState;

    public ToggleState(LogicState drivenState) {
        this.drivenState = drivenState;
    }

    public LogicState getDrivenState() {
        return drivenState;
    }

    public synchronized void setDrivenState(LogicState drivenState) {
        this.drivenState = drivenState;
    }

    /*
        The following methods are overridden to avoid unnecessary writes/reads from
        maps, since the toggle chips will always produce a result based on their configured
        state (and they don't have any input pins anyway).
     */

    @Override
    public LogicState getDrivenInputState(Pin pin) {
        return LogicState.UNKNOWN;
    }

    @Override
    public void setDrivenInputState(Pin pin, LogicState state) {}

    @Override
    public LogicState getPulledInputState(Pin pin) {
        return LogicState.UNKNOWN;
    }

    @Override
    public void setPulledInputState(Pin pin, LogicState state) {}

    @Override
    public LogicState getDrivenOutputState(Pin pin) {
        return drivenState;
    }

    @Override
    public void setDrivenOutputState(Pin pin, LogicState state) {}

    @Override
    public LogicState getPulledOutputState(Pin pin) {
        return LogicState.UNKNOWN;
    }

    @Override
    public void setPulledOutputState(Pin pin, LogicState state) {}
}
