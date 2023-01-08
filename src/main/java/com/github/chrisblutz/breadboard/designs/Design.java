package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Design implements BreadboardSavable {

    public int width;
    public int height;

    public List<Pin> pins = new ArrayList<>();
    public List<Chip> chips = new ArrayList<>();
    public List<Wire> wires = new ArrayList<>();

    public List<Pin> getPins() {
        return pins;
    }

    public List<Chip> getChips() {
        return chips;
    }

    public List<Wire> getWires() {
        return wires;
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
