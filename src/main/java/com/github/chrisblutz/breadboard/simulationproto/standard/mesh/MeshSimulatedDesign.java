package com.github.chrisblutz.breadboard.simulationproto.standard.mesh;

import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulationproto.LogicState;
import com.github.chrisblutz.breadboard.simulationproto.SimulatedDesign;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.generation.MeshDesign;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
}
