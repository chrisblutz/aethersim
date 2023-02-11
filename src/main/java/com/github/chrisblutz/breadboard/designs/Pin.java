package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Pin implements BreadboardSavable {

    private String id;
    private String name;
    private int chipX;
    private int chipY;
    private int designX;
    private int designY;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChipX() {
        return chipX;
    }

    public void setChipX(int chipX) {
        this.chipX = chipX;
    }

    public int getChipY() {
        return chipY;
    }

    public void setChipY(int chipY) {
        this.chipY = chipY;
    }

    public int getDesignX() {
        return designX;
    }

    public void setDesignX(int designX) {
        this.designX = designX;
    }

    public int getDesignY() {
        return designY;
    }

    public void setDesignY(int designY) {
        this.designY = designY;
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
