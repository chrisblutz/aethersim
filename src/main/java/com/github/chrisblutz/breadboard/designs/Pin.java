package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.states.Transform;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.utils.Direction;

import java.util.LinkedHashMap;
import java.util.Map;

public class Pin extends DesignElement implements BreadboardSavable {

    private String id;
    private String name;
    private Point chipLocation;
    private Point designLocation;
    private Direction chipOrientation;
    private Direction designOrientation;

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

    public Point getChipLocation() {
        return chipLocation;
    }

    public void setChipLocation(Point chipLocation) {
        this.chipLocation = chipLocation;
    }

    public Point getDesignLocation() {
        return designLocation;
    }

    public void setDesignLocation(Point designLocation) {
        this.designLocation = designLocation;
        // Attach the pin's transform to the point
        designLocation.setTransform(getTransform());
    }

    public Direction getChipOrientation() {
        return chipOrientation;
    }

    public void setChipOrientation(Direction chipOrientation) {
        this.chipOrientation = chipOrientation;
    }

    public Direction getDesignOrientation() {
        return designOrientation;
    }

    public void setDesignOrientation(Direction designOrientation) {
        this.designOrientation = designOrientation;
    }

    @Override
    protected void onTransformUpdated() {

    }

    @Override
    protected void onTransformAccepted() {
        // Update the design location of the pin to accept the transformed values
        designLocation = designLocation.withTransform();
    }

    @Override
    public boolean contains(Point point) {
        return false;
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
