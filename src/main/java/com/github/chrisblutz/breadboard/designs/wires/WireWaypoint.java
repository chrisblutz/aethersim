package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.DesignElement;
import com.github.chrisblutz.breadboard.designs.Point;
import com.github.chrisblutz.breadboard.utils.Direction;

public class WireWaypoint extends DesignElement implements WireRoutable {

    private WireSegment wireSegment;

    private Point location;

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
}
