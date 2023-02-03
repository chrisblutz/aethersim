package com.github.chrisblutz.breadboard.simulationproto.standard.mesh;

import com.github.chrisblutz.breadboard.simulationproto.standard.exceptions.MeshException;

public record MeshEdge(MeshVertex endpoint, MeshConnector connector) {

    public MeshEdge {
        // If either endpoint or connector is null, throw an exception
        if (endpoint == null)
            throw new MeshException("Simulation mesh endpoints may not be null.");
        if (connector == null)
            throw new MeshException("Simulation mesh connectors may not be null.");
    }

    public boolean isConnected() {
        return connector.isConnected();
    }
}
