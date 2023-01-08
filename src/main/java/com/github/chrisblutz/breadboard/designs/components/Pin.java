package com.github.chrisblutz.breadboard.designs.components;

import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Pin implements BreadboardSavable {

    public String id;
    public String name;
    public boolean input;
    public int chipX;
    public int chipY;
    public int designX;
    public int designY;

    public boolean isInput() {
        return input;
    }

    public int getChipX() {
        return chipX;
    }

    public int getChipY() {
        return chipY;
    }

    public int getDesignX() {
        return designX;
    }

    public int getDesignY() {
        return designY;
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        Map<String, Object> yamlMapping = new LinkedHashMap<>();

        yamlMapping.put("Id", id);
//        yamlMapping.put("X", x);
//        yamlMapping.put("Y", y);

        return yamlMapping;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {
        // TODO Get pin template from ID

//        this.x = (int) yamlMapping.get("X");
//        this.y = (int) yamlMapping.get("Y");
    }
}
