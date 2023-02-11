package com.github.chrisblutz.breadboard.simulation.mesh.mesh.generation;

import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshChip;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshConnector;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshSimulatedDesign;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshVertex;

import java.util.LinkedHashSet;
import java.util.Set;

public class MeshSimulationConfig {

    private MeshSimulatedDesign topLevelSimulatedDesign;
    private final Set<MeshVertex> meshVertices = new LinkedHashSet<>();
    private final Set<MeshChip<?>> meshChips = new LinkedHashSet<>();
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

    public Set<MeshChip<?>> getMeshChips() {
        return meshChips;
    }

    public Set<MeshConnector> getMeshConnectors() {
        return meshConnectors;
    }
}
