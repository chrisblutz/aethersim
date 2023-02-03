package com.github.chrisblutz.breadboard.simulationproto.standard.threading;

import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshVertex;

public class MeshRectifierProcessor implements Runnable {

    private final MeshSimulationCoordinator coordinator;
    private final MeshVertex vertex;

    public MeshRectifierProcessor(MeshSimulationCoordinator coordinator, MeshVertex vertex) {
        this.coordinator = coordinator;
        this.vertex = vertex;
    }

    @Override
    public void run() {
        // Call the state rectifier on the vertex
        vertex.rectifyStates();
    }
}
