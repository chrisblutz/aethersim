package com.github.chrisblutz.breadboard.designs.templates;

import com.github.chrisblutz.breadboard.designs.Pin;
import com.github.chrisblutz.breadboard.simulation.ChipState;
import com.github.chrisblutz.breadboard.simulation.LogicState;

public class ConstantState extends ChipState {

    private final LogicState drivenState, pulledState;

    public ConstantState(LogicState drivenState, LogicState pulledState) {
        this.drivenState = drivenState;
        this.pulledState = pulledState;
    }

    public LogicState getDrivenState() {
        return drivenState;
    }

    public LogicState getPulledState() {
        return pulledState;
    }

    /*
        The following methods are overridden to avoid unnecessary writes/reads from
        maps, since the constant chips will always produce the same results (and
        don't have any input pins anyway).
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
        return pulledState;
    }

    @Override
    public void setPulledOutputState(Pin pin, LogicState state) {}
}
