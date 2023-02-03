package com.github.chrisblutz.breadboard.simulationproto.standard.mesh.generation;

import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshConnector;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshDriver;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshSimulatedDesign;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshVertex;

import java.util.LinkedHashSet;
import java.util.Set;

public class MeshSimulationConfig {

    private MeshSimulatedDesign topLevelSimulatedDesign;
    private final Set<MeshVertex> meshVertices = new LinkedHashSet<>();
    private final Set<MeshDriver> meshDrivers = new LinkedHashSet<>();
    private final Set<MeshConnector> meshConnectors = new LinkedHashSet<>();

    public MeshSimulatedDesign getTopLevelSimulatedDesign() {
        return topLevelSimulatedDesign;
    }

    public void setTopLevelSimulatedDesign(MeshSimulatedDesign topLevelSimulatedDesign) {
        this.topLevelSimulatedDesign = topLevelSimulatedDesign;
    }

    public Set<MeshVertex> getMeshVertices() {
        return meshVertices;
    }

    public Set<MeshDriver> getMeshDrivers() {
        return meshDrivers;
    }

    public Set<MeshConnector> getMeshConnectors() {
        return meshConnectors;
    }
}
