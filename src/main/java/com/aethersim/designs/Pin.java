package com.aethersim.designs;

import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataValue;
import com.aethersim.utils.Direction;

public class Pin extends DesignElement {

    private String id;
    private String name;
    private Point chipLocation = new Point();
    private Point designLocation = new Point(getTransform());
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
        return getDesignLocation().equals(point);
    }

    @Override
    public void deserialize(DataMap data, DataContext context) {

    }

    @Override
    public void serialize(DataMap data, DataContext context) {
        // Store all necessary information into the data map
        if (id != null)
            data.put("Id", DataValue.from(id));
        if (name != null)
            data.put("Name", DataValue.from(name));
        if (getChipOrientation() != null)
            data.put("ChipOrientation", DataValue.from(getChipOrientation().toString().toLowerCase()));
        if (getDesignOrientation() != null)
            data.put("DesignOrientation", DataValue.from(getDesignOrientation().toString().toLowerCase()));
        if (getChipLocation() != null)
            data.put("ChipLocation", context.serialize(getChipLocation()));
        if (getDesignLocation() != null)
            data.put("DesignLocation", context.serialize(getDesignLocation()));
    }
}
