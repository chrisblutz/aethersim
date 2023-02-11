package com.github.chrisblutz.breadboard.simulation.mesh.mesh.generation;

import com.github.chrisblutz.breadboard.designs.*;
import com.github.chrisblutz.breadboard.designs.templates.DesignedTemplate;

import java.util.*;

public class MeshDesign {

    private final Set<ChipPin> pins = new LinkedHashSet<>();
    private final Map<ChipPin, Set<Wire>> wireConnections = new HashMap<>();
    private final List<Set<ChipPin>> uniqueMeshedPinSets = new ArrayList<>();

    private MeshDesign() {}

    public Set<ChipPin> getPins() {
        return pins;
    }

    public List<Set<ChipPin>> getUniqueMeshedPinSets() {
        return uniqueMeshedPinSets;
    }

    private Set<Wire> getConnections(ChipPin pin) {
        return wireConnections.getOrDefault(pin, new HashSet<>());
    }

    private void generateFromChipDesign(Chip chip, Design design) {
        // If the chip is null, this is the top-level design, so only look at design pins
        // Otherwise, look at the chip template
        Collection<Pin> chipPins;
        if (chip == null)
            chipPins = design.getPins();
        else
            chipPins = chip.getChipTemplate().getPins();
        // For each pin, create an entry in our pin set
        for (Pin pin : chipPins)
            pins.add(new ChipPin(chip, pin));

        // If this chip is a built-in, skip the rest of the generation here
        if (design == null)
            return;

        // For each wire in this design, add it to the set of connections for the pins it connects to
        for (Wire wire : design.getWires()) {
            // Create mesh pins for the ends of the wire
            ChipPin startPin = new ChipPin(wire.getStartChip(), wire.getStartPin());
            ChipPin endPin = new ChipPin(wire.getEndChip(), wire.getEndPin());

            // For the pins, add this wire to the connection sets
            if (!wireConnections.containsKey(startPin))
                wireConnections.put(startPin, new LinkedHashSet<>());
            wireConnections.get(startPin).add(wire);
            if (!wireConnections.containsKey(endPin))
                wireConnections.put(endPin, new LinkedHashSet<>());
            wireConnections.get(endPin).add(wire);
        }

        // For all inner chips in this design, recursively call this method
        for (Chip innerChip : design.getChips()) {
            Design innerChipDesign;
            if (innerChip.getChipTemplate() instanceof DesignedTemplate chipTemplate)
                innerChipDesign = chipTemplate.getDesign();
            else
                innerChipDesign = null;
            generateFromChipDesign(innerChip, innerChipDesign);
        }
    }

    private void generateUniqueSets() {
        // Start with the total set of pins in this design, then work backwards until we have none left
        Set<ChipPin> remainingSet = new LinkedHashSet<>(pins);
        while (!remainingSet.isEmpty()) {
            // Starting with a random pin from the set, build a set of all pins that one is connected to
            Set<ChipPin> currentSet = new LinkedHashSet<>();
            ChipPin origin = remainingSet.iterator().next();
            currentSet.add(origin);
            remainingSet.remove(origin);
            generateUniqueSetFromPin(remainingSet, currentSet, origin);

            // Add the current set to the list of meshed pins
            uniqueMeshedPinSets.add(currentSet);
        }
    }

    private void generateUniqueSetFromPin(Set<ChipPin> remainingSet, Set<ChipPin> uniqueSet, ChipPin origin) {
        // Get all the wires that this pin connects to, and iterate through them.  If the pin we arrive
        // at is not already in the set, add it and continue recursively.
        for (Wire wire : getConnections(origin)) {
            ChipPin startPin = new ChipPin(wire.getStartChip(), wire.getStartPin());
            if (!uniqueSet.contains(startPin))  {
                uniqueSet.add(startPin);
                remainingSet.remove(startPin);
                generateUniqueSetFromPin(remainingSet, uniqueSet, startPin);
            }

            ChipPin endPin = new ChipPin(wire.getEndChip(), wire.getEndPin());
            if (!uniqueSet.contains(endPin))  {
                uniqueSet.add(endPin);
                remainingSet.remove(endPin);
                generateUniqueSetFromPin(remainingSet, uniqueSet, endPin);
            }
        }
    }

    public static MeshDesign from(Design design) {
        // Create a new mesh design and populate it with pins and wires
        MeshDesign meshDesign = new MeshDesign();
        meshDesign.generateFromChipDesign(null, design);
        // Generate the list of unique "meshed" pin sets (all pins that are connected to one another)
        meshDesign.generateUniqueSets();
        // After generation, clear the wire connection map to avoid keeping it in memory unnecessarily
        meshDesign.wireConnections.clear();
        return meshDesign;
    }
}
