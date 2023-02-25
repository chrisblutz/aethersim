package com.aethersim.designs.templates;

import com.aethersim.designs.Pin;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataValue;
import com.aethersim.simulation.ChipState;
import com.aethersim.simulation.LogicState;

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

    @Override
    public void deserialize(DataMap data, DataContext context) {
    }

    @Override
    public void serialize(DataMap data, DataContext context) {
        if (getDrivenState() != null)
            data.put("DrivenState", DataValue.from(getDrivenState().toString().toLowerCase()));
    }
}
