package com.github.chrisblutz.breadboard.simulation.generator;

import com.github.chrisblutz.breadboard.components.BuiltinChipTemplate;
import com.github.chrisblutz.breadboard.components.DesignedChipTemplate;
import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulation.components.BuiltinChip;
import com.github.chrisblutz.breadboard.simulation.components.BuiltinNode;
import com.github.chrisblutz.breadboard.simulation.components.Node;
import com.github.chrisblutz.breadboard.simulation.components.NodeConnector;
import com.github.chrisblutz.breadboard.simulation.workers.WorkerScheduler;

import java.util.ArrayList;
import java.util.List;

public class MeshGenerator {

    public static DesignInstance generateMeshForDesign(Design design) {
        // Create a top-level design instance for this design
        DesignInstance designInstance = new DesignInstance();

        // For each chip in the design, generate an inner design instance and attach it to this one
        for (Chip chip : design.getChips())
            designInstance.setDesignInstanceForChip(chip, generateDesignInstanceForChip(chip));

        // For each pin in this design, create a node for it and add it to the design instance
        for (Pin pin : design.getPins())
            designInstance.setNodeForPin(pin, new Node());

        // For each wire in this design, create a new connector.  Then, attach it to the node it connects to
        // and attach the prior node to it
        for (Wire wire : design.getWires()) {
            Node startNode, endNode;

            // Determine if the start pin is on this design or an inner chip
            if (wire.getStartChip() != null) // if this is not null, the pin is on an inner chip
                startNode = designInstance.getDesignInstanceForChip(wire.getStartChip()).getNodeForPin(wire.getStartPin());
            else // if it is, the pin is on this design (e.g., a design-level input or output)
                startNode = designInstance.getNodeForPin(wire.getStartPin());

            // Determine if the end pin is on this design or an inner chip
            if (wire.getEndChip() != null) // if this is not null, the pin is on an inner chip
                endNode = designInstance.getDesignInstanceForChip(wire.getEndChip()).getNodeForPin(wire.getEndPin());
            else // if it is, the pin is on this design (e.g., a design-level input or output)
                endNode = designInstance.getNodeForPin(wire.getEndPin());

            // Configure the nodes and connectors in the correct order
            NodeConnector nodeConnector = new NodeConnector(endNode);
            startNode.addNextTraversableElement(nodeConnector);
            // Assign this connector to its wire in the design instance
            designInstance.setNodeConnectorForWire(wire, nodeConnector);
        }

        // Generate all
        return designInstance;
    }

    private static DesignInstance generateDesignInstanceForChip(Chip chip) {
        // If the chip is a designed chip (i.e. it has an internal design and isn't built-in),
        // then generate the mesh for the internal design and return it
        if (chip.getChipTemplate() instanceof DesignedChipTemplate)
            return generateMeshForDesign(((DesignedChipTemplate) chip.getChipTemplate()).getDesign());

        // If the chip is a built-in, create a boilerplate "design" that has the nodes for each pin in it
        if (chip.getChipTemplate() instanceof BuiltinChipTemplate) {
            DesignInstance designInstance = new DesignInstance();

            // Create a node for every pin and link it to its "design" pin
            // Also track which ones are inputs/outputs and store them, so we can attach them to the chip logic
            List<BuiltinNode> inputNodes = new ArrayList<>();
            List<BuiltinNode> outputNodes = new ArrayList<>();
            for (Pin pin : chip.getChipTemplate().getPins()) {
                // Create a built-in pin linked to the chip pin
                BuiltinNode node = new BuiltinNode(pin);

                // Determine if this is an input or output and add it to the correct list
                if (pin.isInput())
                    inputNodes.add(node);
                else
                    outputNodes.add(node);

                // Register the node with the design instance
                designInstance.setNodeForPin(pin, node);
            }

            try {
                // Create a built-in chip entry in the scheduler
                BuiltinChip builtinChip = new BuiltinChip(
                        ((BuiltinChipTemplate) chip.getChipTemplate()).getChipLogicBuilder().call(),
                        inputNodes.toArray(new BuiltinNode[0]),
                        outputNodes.toArray(new BuiltinNode[0])
                );
                WorkerScheduler.registerBuiltinChip(builtinChip);
            } catch (Exception e) {
                // TODO Error
                throw new RuntimeException(e);
            }

            return designInstance;
        }

        // If we get here, we've hit an unrecognized template type
        // TODO Error
        return null;
    }
}
