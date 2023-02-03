package com.github.chrisblutz.breadboard.simulationproto.standard.threading;

import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshConnector;

public class MeshConnectorProcessor implements Runnable {

    private final MeshSimulationCoordinator coordinator;
    private final MeshConnector connector;

    public MeshConnectorProcessor(MeshSimulationCoordinator coordinator, MeshConnector connector) {
        this.coordinator = coordinator;
        this.connector = connector;
    }

    @Override
    public void run() {
        // Call the tick function of the connector
        connector.tick();
    }
}
