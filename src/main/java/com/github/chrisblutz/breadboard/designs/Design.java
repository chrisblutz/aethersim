package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.templates.DesignedTemplate;
import com.github.chrisblutz.breadboard.designs.templates.SimulatedTemplate;
import com.github.chrisblutz.breadboard.designs.templates.TransistorTemplate;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.*;

public class Design implements BreadboardSavable {

    private int width;
    public int height;

    private final List<Pin> pins = new ArrayList<>();
    private final List<Chip> chips = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();
    private final Map<ChipPin, Set<Wire>> pinWireConnections = new HashMap<>();

    private int transistorCount = 0;

    public int getTransistorCount() {
        return transistorCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Pin> getPins() {
        return pins;
    }

    public List<Chip> getChips() {
        return chips;
    }

    public void addPin(Pin pin) {
        pins.add(pin);
    }

    public void addPins(Pin... pins) {
        for (Pin pin : pins)
            addPin(pin);
    }

    public void removePin(Pin pin) {
        pins.remove(pin);
    }

    public void removePins(Pin... pins) {
        for (Pin pin : pins)
            removePin(pin);
    }

    public void addChip(Chip chip) {
        chips.add(chip);

        // If the chip has a simulated template, initialize the chip within the template
        if (chip.getChipTemplate() instanceof SimulatedTemplate<?> chipTemplate)
            chipTemplate.initialize(chip);

        // Update transistor count
        if (chip.getChipTemplate() instanceof TransistorTemplate)
            transistorCount++;
        else if (chip.getChipTemplate() instanceof DesignedTemplate designedTemplate)
            transistorCount += designedTemplate.getDesign().getTransistorCount();
    }

    public void addChips(Chip... chips) {
        for (Chip chip : chips)
            addChip(chip);
    }

    public void removeChip(Chip chip) {
        chips.remove(chip);

        // If the chip had a simulated template, dispose of the chip within the template
        if (chip.getChipTemplate() instanceof SimulatedTemplate<?> chipTemplate)
            chipTemplate.dispose(chip);

        // Update transistor count
        if (chip.getChipTemplate() instanceof TransistorTemplate)
            transistorCount--;
        else if (chip.getChipTemplate() instanceof DesignedTemplate designedTemplate)
            transistorCount -= designedTemplate.getDesign().getTransistorCount();
    }

    public void removeChips(Chip... chips) {
        for (Chip chip : chips)
            removeChip(chip);
    }

    public List<Wire> getWires() {
        return wires;
    }

    public void addWire(Wire wire) {
        wires.add(wire);

        // Update pin connections
        for (ChipPin pin : wire.getConnectedPins()) {
            if (!pinWireConnections.containsKey(pin))
                pinWireConnections.put(pin, new LinkedHashSet<>());
            pinWireConnections.get(pin).add(wire);
        }
    }

    public void addWires(Wire... wires) {
        for (Wire wire : wires)
            addWire(wire);
    }

    public void removeWire(Wire wire) {
        wires.remove(wire);

        // Remove wire from pin connections
        for (ChipPin pin : wire.getConnectedPins())
            if (pinWireConnections.containsKey(pin))
                pinWireConnections.get(pin).remove(wire);
    }

    public void removeWires(Wire... wires) {
        for (Wire wire : wires)
            removeWire(wire);
    }

    public Set<Wire> getWiresConnectedToPin(ChipPin pin) {
        return pinWireConnections.getOrDefault(pin, new HashSet<>());
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        Map<String, Object> yamlMapping = new LinkedHashMap<>();

        // Put basic information
        yamlMapping.put("Width", width);
        yamlMapping.put("Height", height);

        // Construct input array and set the mapping
//        List<Map<String, Object>> inputYamlMapping = new ArrayList<>();
//        for (Pin input : inputs)
//            inputYamlMapping.add(input.dumpToYAML(writer));
//        yamlMapping.put("Inputs", inputYamlMapping);

        // Construct output array and set the mapping
//        List<Map<String, Object>> outputYamlMapping = new ArrayList<>();
//        for (Pin output : outputs)
//            outputYamlMapping.add(output.dumpToYAML(writer));
//        yamlMapping.put("Outputs", outputYamlMapping);

        // Construct chip array and set the mapping
        List<Map<String, Object>> chipYamlMapping = new ArrayList<>();
        for (Chip chip : chips)
            chipYamlMapping.add(chip.dumpToYAML(writer));
        yamlMapping.put("Chips", chipYamlMapping);

        // Construct wire array and set the mapping
        List<Map<String, Object>> wireYamlMapping = new ArrayList<>();
//        for (Wire wire : wires)
//            wireYamlMapping.add(wire.dumpToYAML(writer));
        yamlMapping.put("Wires", wireYamlMapping);

        return yamlMapping;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
