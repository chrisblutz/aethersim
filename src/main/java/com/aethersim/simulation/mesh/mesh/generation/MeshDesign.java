package com.aethersim.simulation.mesh.mesh.generation;

import com.aethersim.designs.Chip;
import com.aethersim.designs.ChipPin;
import com.aethersim.designs.Design;
import com.aethersim.designs.Pin;
import com.aethersim.designs.templates.DesignedTemplate;

import java.util.*;

public class MeshDesign {

    private final Set<MeshPin> pins = new LinkedHashSet<>();
    private final Map<MeshPin, Set<MeshPin>> pinToPinConnections = new HashMap<>();
    private final List<Set<MeshPin>> uniqueMeshedPinSets = new ArrayList<>();
    private final Map<MeshChip, Set<MeshWireSet>> designMeshWireSets = new HashMap<>();

    private MeshDesign() {}

    public Set<MeshPin> getPins() {
        return pins;
    }


    private Set<MeshPin> getConnections(MeshPin pin) {
        return pinToPinConnections.getOrDefault(pin, new HashSet<>());
    }

    public List<Set<MeshPin>> getUniqueMeshedPinSets() {
        return uniqueMeshedPinSets;
    }

    public Set<MeshWireSet> getMeshWireSetForDesign(MeshChip meshChip) {
        return designMeshWireSets.getOrDefault(meshChip, Set.of());
    }

    private void generateFromChipDesign(Chip chip, Chip[] ancestors, Design design) {
        // If the chip is null, this is the top-level design, so only look at design pins
        // Otherwise, look at the chip template
        Collection<Pin> chipPins;
        if (chip == null)
            chipPins = design.getPins();
        else
            chipPins = chip.getChipTemplate().getPins();

        // For each pin, create an entry in our pin set
        for (Pin pin : chipPins)
            pins.add(new MeshPin(pin, chip, ancestors));

        // If this chip is a built-in, skip the rest of the generation here
        if (design == null)
            return;

        // Calculate new chip ancestry for future sub-chips
        Chip[] newAncestors;
        if (chip != null) {
            newAncestors = new Chip[ancestors.length + 1];
            System.arraycopy(ancestors, 0, newAncestors, 0, ancestors.length);
            newAncestors[ancestors.length] = chip;
        } else {
            newAncestors = ancestors;
        }

        // Generate the set of mesh wire sets in this design and store it
        Set<MeshWireSet> meshWireSets = MeshWireSet.generate(design);
        designMeshWireSets.put(new MeshChip(chip, ancestors), meshWireSets);

        // For each wire set in this design, add it to the set of connections for the pins it connects to
        for (MeshWireSet wireSet : meshWireSets) {
            // Build a set of mesh pins for all the pins connected to this wire
            Set<MeshPin> wireMeshPins = new LinkedHashSet<>();
            for (ChipPin pin : wireSet.getConnectedPins()) {
                // Convert the chip pin into a mesh pin that accounts for chip ancestry
                MeshPin meshPin = new MeshPin(pin.getPin(), pin.getChip(), newAncestors);
                wireMeshPins.add(meshPin);
            }

            // For each mesh pin connected to this wire, map that pin to all other pins in the set
            for (MeshPin meshPin : wireMeshPins) {
                if (!pinToPinConnections.containsKey(meshPin))
                    pinToPinConnections.put(meshPin, new LinkedHashSet<>());
                pinToPinConnections.get(meshPin).addAll(wireMeshPins);
            }
        }

        // For all inner chips in this design, recursively call this method
        for (Chip innerChip : design.getChips()) {
            Design innerChipDesign;
            if (innerChip.getChipTemplate() instanceof DesignedTemplate chipTemplate)
                innerChipDesign = chipTemplate.getDesign();
            else
                innerChipDesign = null;
            generateFromChipDesign(innerChip, newAncestors, innerChipDesign);
        }
    }

    private void generateUniqueSets() {
        // Start with the total set of pins in this design, then work backwards until we have none left
        Set<MeshPin> remainingSet = new LinkedHashSet<>(pins);
        while (!remainingSet.isEmpty()) {
            // Starting with a random pin from the set, build a set of all pins that one is connected to
            Set<MeshPin> currentSet = new LinkedHashSet<>();
            MeshPin origin = remainingSet.iterator().next();
            currentSet.add(origin);
            remainingSet.remove(origin);
            generateUniqueSetFromPin(remainingSet, currentSet, origin);

            // Add the current set to the list of meshed pins
            uniqueMeshedPinSets.add(currentSet);
        }
    }

    private void generateUniqueSetFromPin(Set<MeshPin> remainingSet, Set<MeshPin> uniqueSet, MeshPin origin) {
        // Get all the wires that this pin connects to, and iterate through them.  If the pin we arrive
        // at is not already in the set, add it and continue recursively.
        for (MeshPin meshPin : getConnections(origin)) {
            // If the mesh pin is itself, continue (since we don't prune nodes out of their own connection sets
            if (meshPin == origin)
                continue;

            if (!uniqueSet.contains(meshPin)) {
                uniqueSet.add(meshPin);
                remainingSet.remove(meshPin);
                generateUniqueSetFromPin(remainingSet, uniqueSet, meshPin);
            }
        }
    }

    public static MeshDesign from(Design design) {
        // Create a new mesh design and populate it with pins and wires
        MeshDesign meshDesign = new MeshDesign();
        meshDesign.generateFromChipDesign(null, new Chip[0], design);
        // Generate the list of unique "meshed" pin sets (all pins that are connected to one another)
        meshDesign.generateUniqueSets();
        return meshDesign;
    }
}
