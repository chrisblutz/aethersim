package com.github.chrisblutz.breadboard.simulation.generator;

import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulation.components.Node;
import com.github.chrisblutz.breadboard.simulation.components.NodeConnector;

import java.util.HashMap;
import java.util.Map;

public class DesignInstance {

    private final Map<Pin, Node> nodeMapping = new HashMap<>();
    private final Map<Wire, NodeConnector> nodeConnectorMapping = new HashMap<>();
    private final Map<Chip, DesignInstance> designInstanceMapping = new HashMap<>();


    public Node getNodeForPin(Pin pin) {
        return nodeMapping.getOrDefault(pin, null);
    }

    public void setNodeForPin(Pin pin, Node node) {
        nodeMapping.put(pin, node);
    }

    public NodeConnector getNodeConnectorForWire(Wire wire) {
        return nodeConnectorMapping.getOrDefault(wire, null);
    }

    public void setNodeConnectorForWire(Wire wire, NodeConnector nodeConnector) {
        nodeConnectorMapping.put(wire, nodeConnector);
    }

    public DesignInstance getDesignInstanceForChip(Chip chip) {
        return designInstanceMapping.getOrDefault(chip, null);
    }

    public void setDesignInstanceForChip(Chip chip, DesignInstance designInstance) {
        designInstanceMapping.put(chip, designInstance);
    }
}
