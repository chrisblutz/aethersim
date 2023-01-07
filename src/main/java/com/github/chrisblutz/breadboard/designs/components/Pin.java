package com.github.chrisblutz.breadboard.designs.components;

import com.github.chrisblutz.breadboard.components.PinTemplate;
import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.simulation.components.Node;

import java.util.*;

public class Pin implements BreadboardSavable {

    public PinTemplate pinTemplate;
    public int x;
    public int y;

    private boolean input;
    private List<Wire> connectedWires;
    private Design parentDesign;
    private Pin correspondingPin;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Node getSimulationNode() {
        return simulationNode;
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        Map<String, Object> yamlMapping = new LinkedHashMap<>();

        yamlMapping.put("Id", pinTemplate.id);
        yamlMapping.put("X", x);
        yamlMapping.put("Y", y);

        return yamlMapping;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {
        // TODO Get pin template from ID

        this.x = (int) yamlMapping.get("X");
        this.y = (int) yamlMapping.get("Y");
    }
}
