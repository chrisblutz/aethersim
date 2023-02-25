package com.aethersim.simulation.mesh.mesh.generation;

import com.aethersim.simulation.mesh.mesh.MeshConnector;
import com.aethersim.simulation.mesh.mesh.MeshFunction;
import com.aethersim.simulation.mesh.mesh.MeshSimulatedDesign;
import com.aethersim.simulation.mesh.mesh.MeshVertex;

import java.util.LinkedHashSet;
import java.util.Set;

public class MeshSimulationConfig {

    private MeshSimulatedDesign topLevelSimulatedDesign;
    private final Set<MeshVertex> meshVertices = new LinkedHashSet<>();
    private final Set<MeshFunction<?>> meshFunctions = new LinkedHashSet<>();
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

    public Set<MeshFunction<?>> getMeshChips() {
        return meshFunctions;
    }

    public Set<MeshConnector> getMeshConnectors() {
        return meshConnectors;
    }
}
