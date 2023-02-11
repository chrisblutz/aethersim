package com.github.chrisblutz.breadboard.simulation.mesh.mesh.generation;

import com.github.chrisblutz.breadboard.designs.*;
import com.github.chrisblutz.breadboard.designs.templates.DesignedTemplate;
import com.github.chrisblutz.breadboard.designs.templates.SimulatedTemplate;
import com.github.chrisblutz.breadboard.designs.templates.TransistorTemplate;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MeshGenerator {

    public static MeshSimulationConfig generate(Design design) {
        // First, create a mesh design that contains all the connection information for the pins and wires
        MeshDesign meshDesign = MeshDesign.from(design);

        // Create the overall simulation configuration so we can fill it in as we go in generation
        MeshSimulationConfig simulationConfig = new MeshSimulationConfig();

        // Build all the mesh vertices according to the unique sets of pins determined by the mesh design
        Map<ChipPin, MeshVertex> pinVertices = generateVerticesForPins(simulationConfig, meshDesign);

        // Generate the top-level simulated design
        MeshSimulatedDesign topLevelDesign = generateFromChip(simulationConfig, pinVertices, null, design);
        simulationConfig.setTopLevelSimulatedDesign(topLevelDesign);

        // Return the finalized instance
        return simulationConfig;
    }

    private static Map<ChipPin, MeshVertex> generateVerticesForPins(MeshSimulationConfig simulationConfig, MeshDesign meshDesign) {
        // For each set of unique pins in our design, create a new mesh vertex and assign it to all of them
        Map<ChipPin, MeshVertex> pinVertices = new HashMap<>();

        for (Set<ChipPin> uniqueSet : meshDesign.getUniqueMeshedPinSets()) {
            // Create a new mesh vertex
            MeshVertex vertex = new MeshVertex();
            // Assign the mesh vertex to all the pins in this set
            for (ChipPin pin : uniqueSet)
                pinVertices.put(pin, vertex);
            // Add the vertex to the simulation configuration
            simulationConfig.getMeshVertices().add(vertex);
        }

        return pinVertices;
    }

    private static MeshSimulatedDesign generateFromChip(MeshSimulationConfig simulationConfig, Map<ChipPin, MeshVertex> pinVertices, Chip chip, Design design) {
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
            simulatedDesign.getPinMapping().put(pin, pinVertices.get(new ChipPin(chip, pin)));

        // If this is a simulated chip, create the mesh chip for the chip and get the associated drivers
        if (chip != null && chip.getChipTemplate() instanceof SimulatedTemplate<?> chipTemplate) {
            MeshChip<?> meshChip = simulatedDesign.generateMeshChip(chip, chipTemplate);
            // Register new mesh chip and all associated mesh drivers
            simulationConfig.getMeshChips().add(meshChip);
        }

        // If the design is null, this is a built-in chip, so process the chip to determine if it provides
        // drivers or connectors.  Then return and skip the rest of the generation.
        if (design == null) {
            generateFromBuiltinChip(simulationConfig, pinVertices, chip);
            return simulatedDesign;
        }

        // For all wires in the design, identify the mesh vertex it's attached to using the start pin (since they are
        // connected, both pins should have the same vertex).
        for (Wire wire : design.getWires()) {
            ChipPin startPin = new ChipPin(wire.getStartChip(), wire.getStartPin());
            simulatedDesign.getWireMapping().put(wire, pinVertices.get(startPin));
        }

        // For all pins within this chip, build simulation designs for those and add them to this one
        for (Chip innerChip : design.getChips()) {
            Design innerChipDesign;
            if (innerChip.getChipTemplate() instanceof DesignedTemplate chipTemplate)
                innerChipDesign = chipTemplate.getDesign();
            else
                innerChipDesign = null;
            // Build the simulated design for the chip and assign it to the chip in the top level design
            MeshSimulatedDesign chipSimulatedDesign = generateFromChip(simulationConfig, pinVertices, innerChip, innerChipDesign);
            simulatedDesign.getChipMapping().put(innerChip, chipSimulatedDesign);
        }

        return simulatedDesign;
    }

    private static void generateFromBuiltinChip(MeshSimulationConfig simulationConfig, Map<ChipPin, MeshVertex> pinVertices, Chip chip) {
        // If the chip has a transistor template, create a mesh connector and edge for it
        if (chip.getChipTemplate() instanceof TransistorTemplate template) {
            // Get the base mesh vertex for the chip
            MeshVertex baseVertex = pinVertices.get(new ChipPin(chip, template.getBase()));
            // Create a mesh connector for the transistor
            MeshConnector connector = new MeshConnector(baseVertex, template.isActiveLow());
            // Determine the start and end mesh vertices of the mesh edge
            MeshVertex startVertex = pinVertices.get(new ChipPin(chip, template.getActiveSignalInput()));
            MeshVertex endVertex = pinVertices.get(new ChipPin(chip, template.getActiveSignalOutput()));
            // Create a mesh edge for the transistor
            MeshEdge edge = new MeshEdge(endVertex, connector);
            startVertex.getOutgoingEdges().add(edge);
            // Add connector to the simulation configuration
            simulationConfig.getMeshConnectors().add(connector);
        }
    }
}