package com.aethersim.simulation.mesh.mesh.generation;

import com.aethersim.designs.Chip;
import com.aethersim.designs.ChipPin;
import com.aethersim.designs.Design;
import com.aethersim.designs.Pin;
import com.aethersim.designs.templates.DesignedTemplate;
import com.aethersim.designs.templates.SimulatedTemplate;
import com.aethersim.designs.templates.TransistorTemplate;
import com.aethersim.designs.wires.WireNode;
import com.aethersim.designs.wires.WireSegment;
import com.aethersim.logging.AetherSimLogging;
import com.aethersim.simulation.mesh.mesh.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MeshGenerator {

    public static MeshSimulationConfig generate(Design design) {
        AetherSimLogging.getSimulationLogger().info("Generating mesh for top-level design ({} transistors)...", design.getTransistorCount());

        // First, create a mesh design that contains all the connection information for the pins and wires
        MeshDesign meshDesign = MeshDesign.from(design);

        // Create the overall simulation configuration so we can fill it in as we go in generation
        MeshSimulationConfig simulationConfig = new MeshSimulationConfig();

        // Build all the mesh vertices according to the unique sets of pins determined by the mesh design
        Map<MeshPin, MeshVertex> pinVertices = generateVerticesForPins(simulationConfig, meshDesign);

        // Generate the top-level simulated design
        MeshSimulatedDesign topLevelDesign = generateFromChip(simulationConfig, meshDesign, pinVertices, null, new Chip[0], design);
        simulationConfig.setTopLevelSimulatedDesign(topLevelDesign);

        // Log details about the generated simulated design
        AetherSimLogging.getSimulationLogger().info(
                "Generated mesh for top-level design ({} vertices, {} edges, and {} simulated chips).",
                simulationConfig.getMeshVertices().size(),
                simulationConfig.getMeshConnectors().size(),
                simulationConfig.getMeshChips().size()
        );

        // Return the finalized instance
        return simulationConfig;
    }

    private static Map<MeshPin, MeshVertex> generateVerticesForPins(MeshSimulationConfig simulationConfig, MeshDesign meshDesign) {
        // For each set of unique pins in our design, create a new mesh vertex and assign it to all of them
        Map<MeshPin, MeshVertex> pinVertices = new HashMap<>();

        for (Set<MeshPin> uniqueSet : meshDesign.getUniqueMeshedPinSets()) {
            // Create a new mesh vertex
            MeshVertex vertex = new MeshVertex();
            // Assign the mesh vertex to all the pins in this set
            for (MeshPin pin : uniqueSet)
                pinVertices.put(pin, vertex);
            // Add the vertex to the simulation configuration
            simulationConfig.getMeshVertices().add(vertex);
        }

        return pinVertices;
    }

    private static MeshSimulatedDesign generateFromChip(MeshSimulationConfig simulationConfig, MeshDesign meshDesign, Map<MeshPin, MeshVertex> pinVertices, Chip chip, Chip[] ancestors, Design design) {
        // Create a new simulated design for this chip
        MeshSimulatedDesign simulatedDesign = new MeshSimulatedDesign();

        // For each pin the chip has, assign its mesh vertex to it in the simulated design.
        // If the chip is null, use the design's pins.
        Collection<Pin> pins;
        if (chip == null)
            pins = design.getPins();
        else
            pins = chip.getChipTemplate().getPins();

        for (Pin pin : pins)
            simulatedDesign.getPinMapping().put(pin, pinVertices.get(new MeshPin(pin, chip, ancestors)));

        // If the design is null, this is a built-in chip, so process the chip to determine if it provides
        // chips or connectors.  Then return and skip the rest of the generation.
        if (design == null) {
            generateFromBuiltinChip(simulatedDesign, simulationConfig, pinVertices, chip, ancestors);
            return simulatedDesign;
        }

        // Calculate new chip ancestry for future sub-chips
        Chip[] newAncestors;
        if (chip != null) {
            newAncestors = new Chip[ancestors.length + 1];
            System.arraycopy(ancestors, 0, newAncestors, 0, ancestors.length);
            newAncestors[ancestors.length] = chip;
        } else {
            newAncestors = ancestors;
        }

        // For all wire elements in the design, identify the mesh vertex they're attached to using a random pin (since
        // they are connected, all pins should have the same vertex).
        Set<MeshWireSet> meshWireSets = meshDesign.getMeshWireSetForDesign(new MeshChip(chip, ancestors));
        for (MeshWireSet wireSet : meshWireSets) {
            ChipPin pin = wireSet.getConnectedPins().iterator().next();
            MeshPin meshPin = new MeshPin(pin.getPin(), pin.getChip(), newAncestors);
            MeshVertex pinVertex = pinVertices.get(meshPin);
            // Add all node and segment map elements
            for (WireNode node : wireSet.getWireNodes())
                simulatedDesign.getWireNodeMapping().put(node, pinVertex);
            for (WireSegment segment : wireSet.getWireSegments())
                simulatedDesign.getWireSegmentMapping().put(segment, pinVertex);
        }

        // For all pins within this chip, build simulation designs for those and add them to this one
        for (Chip innerChip : design.getChips()) {
            Design innerChipDesign;
            if (innerChip.getChipTemplate() instanceof DesignedTemplate chipTemplate)
                innerChipDesign = chipTemplate.getDesign();
            else
                innerChipDesign = null;
            // Build the simulated design for the chip and assign it to the chip in the top level design
            MeshSimulatedDesign chipSimulatedDesign = generateFromChip(simulationConfig, meshDesign, pinVertices, innerChip, newAncestors, innerChipDesign);
            simulatedDesign.getChipMapping().put(innerChip, chipSimulatedDesign);
        }

        return simulatedDesign;
    }

    private static void generateFromBuiltinChip(MeshSimulatedDesign simulatedDesign, MeshSimulationConfig simulationConfig, Map<MeshPin, MeshVertex> pinVertices, Chip chip, Chip[] ancestors) {
        // If the chip has a transistor template, create a mesh connector and edge for it
        // If the chip has a simulated template, create the mesh chip for the chip and get the associated drivers
        if (chip.getChipTemplate() instanceof TransistorTemplate template) {
            // Get the base mesh vertex for the chip
            MeshVertex baseVertex = pinVertices.get(new MeshPin(template.getBase(), chip, ancestors));
            // Create a mesh connector for the transistor
            MeshConnector connector = new MeshConnector(baseVertex, template.isActiveLow());
            // Determine the start and end mesh vertices of the mesh edge
            MeshVertex startVertex = pinVertices.get(new MeshPin(template.getActiveSignalInput(), chip, ancestors));
            MeshVertex endVertex = pinVertices.get(new MeshPin(template.getActiveSignalOutput(), chip, ancestors));
            // Create a mesh edge for the transistor
            MeshEdge edge = new MeshEdge(endVertex, connector);
            startVertex.getOutgoingEdges().add(edge);
            // Add connector to the simulation configuration
            simulationConfig.getMeshConnectors().add(connector);
        } else if (chip.getChipTemplate() instanceof SimulatedTemplate<?> chipTemplate) {
            MeshFunction<?> meshFunction = simulatedDesign.generateMeshChip(chip, chipTemplate);
            // Register new mesh chip and all associated mesh drivers
            simulationConfig.getMeshChips().add(meshFunction);
        }
    }
}
