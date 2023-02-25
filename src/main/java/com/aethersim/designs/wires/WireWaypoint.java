package com.aethersim.designs.wires;

import com.aethersim.designs.DesignElement;
import com.aethersim.designs.Point;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.utils.Direction;

public class WireWaypoint extends DesignElement implements WireRoutable {

    private WireSegment wireSegment;

    private Point location = new Point(getTransform());

    public WireWaypoint() {}

    public WireWaypoint(Point location) {
        this.location = location;
        // Attach the transform to the point
        location.setTransform(getTransform());
    }

    public void attach(WireSegment segment) {
        this.wireSegment = segment;
    }

    public WireSegment getWireSegment() {
        return wireSegment;
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
        // Update the location of the waypoint to accept the transformed values
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
        if (getLocation() != null)
            data.put("Location", context.serialize(getLocation()));
    }
}
