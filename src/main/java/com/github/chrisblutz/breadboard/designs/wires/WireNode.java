package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.DesignElement;
import com.github.chrisblutz.breadboard.designs.Point;
import com.github.chrisblutz.breadboard.utils.Direction;

import java.util.LinkedHashSet;
import java.util.Set;

public class WireNode extends DesignElement implements WireRoutable {

    private final Set<WireSegment> connectedSegments = new LinkedHashSet<>();

    private Point location;

    public WireNode(Point location) {
        this.location = location;
        // Attach the transform to the point
        location.setTransform(getTransform());
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

    public void connect(WireSegment segment) {
        connectedSegments.add(segment);
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
}
