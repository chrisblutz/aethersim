package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.components.DesignedChipTemplate;
import com.github.chrisblutz.breadboard.components.TransistorTemplate;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.*;

public class Design implements BreadboardSavable {

    private record DesignPin(Chip chip, Pin pin) {}

    private int width;
    public int height;

    private final List<Pin> pins = new ArrayList<>();
    private final List<Chip> chips = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();

    private final Map<DesignPin, Set<Wire>> pinWireConnections = new HashMap<>();

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

    public void addChip(Chip chip) {
        chips.add(chip);

        // Update transistor count
        if (chip.getChipTemplate() instanceof TransistorTemplate)
            transistorCount++;
        else if (chip.getChipTemplate() instanceof DesignedChipTemplate designedTemplate)
            transistorCount += designedTemplate.getDesign().getTransistorCount();
    }

    public void addChips(Collection<Chip> chips) {
        for (Chip chip : chips)
            addChip(chip);
    }

    public void removeChip(Chip chip) {
        chips.remove(chip);

        // Update transistor count
        if (chip.getChipTemplate() instanceof TransistorTemplate)
            transistorCount--;
        else if (chip.getChipTemplate() instanceof DesignedChipTemplate designedTemplate)
            transistorCount -= designedTemplate.getDesign().getTransistorCount();
    }

    public void removeChips(Collection<Chip> chips) {
        for (Chip chip : chips)
            removeChip(chip);
    }

    public List<Wire> getWires() {
        return wires;
    }

    public void addWire(Wire wire) {
        wires.add(wire);

        // Update pin connections
        DesignPin startPin = new DesignPin(wire.getStartChip(), wire.getStartPin());
        if (!pinWireConnections.containsKey(startPin))
            pinWireConnections.put(startPin, new HashSet<>());
        pinWireConnections.get(startPin).add(wire);

        DesignPin endPin = new DesignPin(wire.getEndChip(), wire.getEndPin());
        if (!pinWireConnections.containsKey(endPin))
            pinWireConnections.put(endPin, new HashSet<>());
        pinWireConnections.get(endPin).add(wire);
    }

    public void addWires(Collection<Wire> wires) {
        for (Wire wire : wires)
            addWire(wire);
    }

    public void removeWire(Wire wire) {
        wires.remove(wire);

        // Update pin connections
        DesignPin startPin = new DesignPin(wire.getStartChip(), wire.getStartPin());
        pinWireConnections.get(startPin).remove(wire);

        DesignPin endPin = new DesignPin(wire.getEndChip(), wire.getEndPin());
        pinWireConnections.get(endPin).remove(wire);
    }

    public void removeWires(Collection<Wire> wires) {
        for (Wire wire : wires)
            removeWire(wire);
    }

    public Set<Wire> getWiresConnectedToPin(Chip chip, Pin pin) {
        return pinWireConnections.getOrDefault(new DesignPin(chip, pin), new HashSet<>());
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
        for (Wire wire : wires)
            wireYamlMapping.add(wire.dumpToYAML(writer));
        yamlMapping.put("Wires", wireYamlMapping);

        return yamlMapping;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
