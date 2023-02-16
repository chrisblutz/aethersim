package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Pin implements BreadboardSavable {

    private String id;
    private String name;
    private Vertex chipLocation;
    private Vertex designLocation;

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

    public Vertex getChipLocation() {
        return chipLocation;
    }

    public void setChipLocation(Vertex chipLocation) {
        this.chipLocation = chipLocation;
    }

    public Vertex getDesignLocation() {
        return designLocation;
    }

    public void setDesignLocation(Vertex designLocation) {
        this.designLocation = designLocation;
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
