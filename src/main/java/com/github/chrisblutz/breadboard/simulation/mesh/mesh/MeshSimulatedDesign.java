package com.github.chrisblutz.breadboard.simulation.mesh.mesh;

import com.github.chrisblutz.breadboard.designs.Chip;
import com.github.chrisblutz.breadboard.designs.Pin;
import com.github.chrisblutz.breadboard.designs.Wire;
import com.github.chrisblutz.breadboard.designs.templates.SimulatedTemplate;
import com.github.chrisblutz.breadboard.simulation.ChipState;
import com.github.chrisblutz.breadboard.simulation.LogicState;
import com.github.chrisblutz.breadboard.simulation.SimulatedDesign;

import java.util.HashMap;
import java.util.Map;

public class MeshSimulatedDesign extends SimulatedDesign {

    private final Map<Pin, MeshVertex> pinMapping = new HashMap<>();
    private final Map<Wire, MeshVertex> wireMapping = new HashMap<>();
    private final Map<Chip, MeshSimulatedDesign> chipMapping = new HashMap<>();

    public Map<Pin, MeshVertex> getPinMapping() {
        return pinMapping;
    }

    public Map<Wire, MeshVertex> getWireMapping() {
        return wireMapping;
    }

    public Map<Chip, MeshSimulatedDesign> getChipMapping() {
        return chipMapping;
    }

    @Override
    public LogicState getStateForPin(Pin pin) {
        MeshVertex vertex = pinMapping.get(pin);
        // Default to UNCONNECTED if no vertex found
        if (vertex == null)
            return LogicState.UNCONNECTED;
        else
            return vertex.getActualState();
    }

    @Override
    public LogicState getStateForWire(Wire wire) {
        MeshVertex vertex = wireMapping.get(wire);
        // Default to UNCONNECTED if no vertex found
        if (vertex == null)
            return LogicState.UNCONNECTED;
        else
            return vertex.getActualState();
    }

    @Override
    public SimulatedDesign getSimulatedChipDesign(Chip chip) {
        return chipMapping.get(chip);
    }

    public <T extends ChipState> MeshChip<T> generateMeshChip(Chip chip, SimulatedTemplate<T> simulatedTemplate) {
        MeshChip<T> meshChip = new MeshChip<>(chip, simulatedTemplate);

        // Populate the mesh drivers and vertices
        for (Pin input : simulatedTemplate.getInputPins()) {
            MeshVertex pinVertex = getPinMapping().get(input);
            meshChip.getInputVertices().put(input, pinVertex);
        }

        for (Pin output : simulatedTemplate.getOutputPins()) {
            MeshVertex pinVertex = getPinMapping().get(output);
            meshChip.getOutputVertices().put(output, pinVertex);
        }

        return meshChip;
    }
}
