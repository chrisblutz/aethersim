package com.aethersim.simulation.mesh.mesh;

import com.aethersim.designs.Chip;
import com.aethersim.designs.Pin;
import com.aethersim.designs.templates.SimulatedTemplate;
import com.aethersim.designs.wires.WireNode;
import com.aethersim.designs.wires.WireSegment;
import com.aethersim.simulation.ChipState;
import com.aethersim.simulation.LogicState;
import com.aethersim.simulation.SimulatedDesign;

import java.util.HashMap;
import java.util.Map;

public class MeshSimulatedDesign extends SimulatedDesign {

    private final Map<Pin, MeshVertex> pinMapping = new HashMap<>();
    private final Map<WireNode, MeshVertex> wireNodeMapping = new HashMap<>();
    private final Map<WireSegment, MeshVertex> wireSegmentMapping = new HashMap<>();
    private final Map<Chip, MeshSimulatedDesign> chipMapping = new HashMap<>();

    public Map<Pin, MeshVertex> getPinMapping() {
        return pinMapping;
    }

    public Map<WireNode, MeshVertex> getWireNodeMapping() {
        return wireNodeMapping;
    }

    public Map<WireSegment, MeshVertex> getWireSegmentMapping() {
        return wireSegmentMapping;
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
    public LogicState getStateForWireNode(WireNode wireNode) {
        MeshVertex vertex = wireNodeMapping.get(wireNode);
        // Default to UNCONNECTED if no vertex found
        if (vertex == null)
            return LogicState.UNCONNECTED;
        else
            return vertex.getActualState();
    }

    @Override
    public LogicState getStateForWireSegment(WireSegment wireSegment) {
        MeshVertex vertex = wireSegmentMapping.get(wireSegment);
        // Default to UNCONNECTED if no vertex found
        if (vertex == null)
            return LogicState.UNCONNECTED;
        else
            return vertex.getActualState();
    }

    @Override
    public SimulatedDesign getSimulatedChipDesign(Chip chip) {
        MeshSimulatedDesign design = chipMapping.get(chip);
        if (design != null)
            return design;
        else
            return none();
    }

    public <T extends ChipState> MeshFunction<T> generateMeshChip(Chip chip, SimulatedTemplate<T> simulatedTemplate) {
        MeshFunction<T> meshFunction = new MeshFunction<>(chip, simulatedTemplate);

        // Populate the mesh drivers and vertices
        for (Pin input : simulatedTemplate.getInputPins()) {
            MeshVertex pinVertex = getPinMapping().get(input);
            meshFunction.getInputVertices().put(input, pinVertex);
        }

        for (Pin output : simulatedTemplate.getOutputPins()) {
            MeshVertex pinVertex = getPinMapping().get(output);
            meshFunction.getOutputVertices().put(output, pinVertex);
        }

        return meshFunction;
    }
}
