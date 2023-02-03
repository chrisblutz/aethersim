package com.github.chrisblutz.breadboard.simulationproto.standard.mesh;

import com.github.chrisblutz.breadboard.simulationproto.LogicState;

public class MeshDriver {

    private final MeshVertex vertex;
    private LogicState drivenActualState, drivenSuggestedState;

    public MeshDriver(MeshVertex vertex, LogicState drivenActualState, LogicState drivenSuggestedState) {
        this.vertex = vertex;
        this.drivenActualState = drivenActualState;
        this.drivenSuggestedState = drivenSuggestedState;
    }

    public MeshVertex getVertex() {
        return vertex;
    }

    public LogicState getDrivenActualState() {
        return drivenActualState;
    }

    public void setDrivenActualState(LogicState drivenActualState) {
        this.drivenActualState = drivenActualState;
    }

    public LogicState getDrivenSuggestedState() {
        return drivenSuggestedState;
    }

    public void setDrivenSuggestedState(LogicState drivenSuggestedState) {
        this.drivenSuggestedState = drivenSuggestedState;
    }
}
