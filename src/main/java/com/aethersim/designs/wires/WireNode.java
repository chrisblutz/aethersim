package com.aethersim.designs.wires;

import com.aethersim.designs.DesignElement;
import com.aethersim.designs.Point;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataValue;
import com.aethersim.utils.Direction;

public class WireNode extends DesignElement implements WireRoutable {

    // Default to -1, since IDs must be greater than 0
    private int id = -1;

    private Point location;

    public WireNode(Point location) {
        this.location = location;
        // Attach the transform to the point
        location.setTransform(getTransform());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Point getLocation() {
        return location;
    }

    public void setLocation(Point point) {
        this.location = point;
        // Attach the transform to the point
        location.setTransform(getTransform());
    }

    @Override
    public Direction getPreferredWireDirection() {
        return null;
    }

    @Override
    protected void onTransformUpdated() {

    }

    @Override
    protected void onTransformAccepted() {
        // Update the location of the node to accept the transformed values
        location = location.withTransform();
    }

    @Override
    public boolean contains(Point point) {
        return getLocation().equals(point);
    }

    @Override
    public void deserialize(DataMap data, DataContext context) {

    }

    @Override
    public void serialize(DataMap data, DataContext context) {
        // Store all necessary information into the data map
        if (getId() > 0)
            data.put("Id", DataValue.from(getId()));
        if (getLocation() != null)
            data.put("Location", context.serialize(getLocation()));
    }
}
