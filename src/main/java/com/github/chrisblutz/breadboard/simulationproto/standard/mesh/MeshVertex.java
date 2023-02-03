package com.github.chrisblutz.breadboard.simulationproto.standard.mesh;

import com.github.chrisblutz.breadboard.simulationproto.LogicState;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MeshVertex {

    private final Set<MeshEdge> outgoingEdges = new LinkedHashSet<>();

    private LogicState actualState = LogicState.UNCONNECTED, suggestedState = LogicState.UNCONNECTED;
    private LogicState simulatedActualState = LogicState.UNCONNECTED, simulatedSuggestedState = LogicState.UNCONNECTED;
    
    public Set<MeshEdge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public LogicState getActualState() {
        return actualState;
    }

    public synchronized void setActualState(LogicState actualState) {
        // If the actual state of this vertex is conflicted, do not overwrite it.
        if (simulatedActualState == LogicState.CONFLICTED)
            return;
        // If this has an exclusive logical state, and the incoming state is different, set it as conflicted
        if (simulatedActualState.isExclusive() && actualState.isExclusive() && simulatedActualState != actualState)
            actualState = LogicState.CONFLICTED;
        this.simulatedActualState = actualState;
    }

    public LogicState getSuggestedState() {
        return suggestedState;
    }

    public synchronized void setSuggestedState(LogicState suggestedState) {
        // If the suggested state of this vertex is conflicted, do not overwrite it
        if (simulatedSuggestedState == LogicState.CONFLICTED)
            return;
        // If this has an exclusive logical state, and the incoming state is different, set it as conflicted
        if (simulatedSuggestedState.isExclusive() && suggestedState.isExclusive() && simulatedSuggestedState != suggestedState)
            suggestedState = LogicState.CONFLICTED;
        this.simulatedSuggestedState = suggestedState;
    }

    public void rectifyStates() {
        // If the "actual" state is unconnected, set it to the suggested state
        if (simulatedActualState == LogicState.UNCONNECTED)
            simulatedActualState = simulatedSuggestedState;

        // Set the current states to the simulated states
        actualState = simulatedActualState;
        suggestedState = simulatedSuggestedState;
        simulatedActualState = LogicState.UNCONNECTED;
        simulatedSuggestedState = LogicState.UNCONNECTED;
    }

    public boolean compareStates(LogicState otherActualState, LogicState otherSuggestedState) {
        // If this vertex is already conflicted, there is no need to update it again
        if (simulatedActualState == LogicState.CONFLICTED)
            return false;

        // If the new actual state differs from the current one, it needs to be updated unless the new state is unknown
        if (otherActualState != LogicState.UNKNOWN && simulatedActualState != otherActualState)
            return true;

        // If the suggested state is already conflicted, there is no need to update it again
        if (simulatedSuggestedState == LogicState.CONFLICTED)
            return false;

        // If the new suggested state differs from the current one, it needs to be updated unless the new state
        // is unknown.  Otherwise, there are no discrepancies, so it doesn't need to be updated
        return otherSuggestedState != LogicState.UNKNOWN && simulatedSuggestedState != otherSuggestedState;
    }
}
