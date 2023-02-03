package com.github.chrisblutz.breadboard.simulationproto.standard.threading;

import com.github.chrisblutz.breadboard.simulationproto.LogicState;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshEdge;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshVertex;

public class MeshStateProcessor implements Runnable {

    private final MeshSimulationCoordinator coordinator;
    private final MeshVertex vertex;
    private final LogicState desiredActualState, desiredSuggestedState;

    public MeshStateProcessor(MeshSimulationCoordinator coordinator, MeshVertex vertex, LogicState desiredActualState, LogicState desiredSuggestedState) {
        this.coordinator = coordinator;
        this.vertex = vertex;
        this.desiredActualState = desiredActualState;
        this.desiredSuggestedState = desiredSuggestedState;
    }

    @Override
    public void run() {
        // First, set the state of the current vertex
        if (desiredActualState != LogicState.UNKNOWN)
            vertex.setActualState(desiredActualState);
        if (desiredSuggestedState != LogicState.UNKNOWN)
            vertex.setSuggestedState(desiredSuggestedState);

        // Next, re-queue updates to all connected vertices that don't already have the specified value
        for (MeshEdge edge : vertex.getOutgoingEdges()) {
            // If the edge is connected, check the state
            if (!edge.isConnected())
                continue;

            // Check if the vertex needs to be updated
            if (edge.endpoint().compareStates(desiredActualState, desiredSuggestedState))
                coordinator.queueNow(new MeshStateProcessor(coordinator, edge.endpoint(), desiredActualState, desiredSuggestedState));
        }
    }
}
