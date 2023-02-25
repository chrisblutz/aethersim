package com.aethersim.designs;

import com.aethersim.designs.exceptions.DesignException;
import com.aethersim.designs.states.Transform;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataSerializable;
import com.aethersim.projects.io.data.DataValue;

import java.util.Objects;

public class Point implements DataSerializable {

    private Transform transform;
    // These X/Y coordinates are not modified on the object after creation, unless the object is deserialized
    // from save data.
    private int x, y;

    public Point() {
        this(Transform.NONE);
    }

    public Point(Transform transform) {
        this(0, 0, transform);
    }

    public Point(int x, int y) {
        this(x, y, Transform.NONE);
    }

    public Point(int x, int y, Transform transform) {
        this.x = x;
        this.y = y;

        if (transform == null)
            throw new DesignException("Point transforms cannot be null.  Use Transform.NONE instead.");
        this.transform = transform;
    }

    public boolean hasTransform() {
        return transform != Transform.NONE;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public int getX() {
        return x + transform.getXOffset();
    }

    public int getUntransformedX() {
        return x;
    }

    public int getY() {
        return y + transform.getYOffset();
    }

    public int getUntransformedY() {
        return y;
    }

    public Point withOffset(int xOffset, int yOffset) {
        return new Point(getX() + xOffset, getY() + yOffset, transform);
    }

    public Point withOffset(Point offset) {
        return withOffset(offset.getX(), offset.getY());
    }

    public Point withTransform() {
        // Return withOffset using offsets of 0, so the only offset that gets applied is the one applied
        // by the transform values
        return withOffset(0, 0);
    }

    public boolean isBetween(Point start, Point end) {
        if (start.getX() == end.getX() && this.getX() == start.getX()
                && ((this.getY() >= start.getY() && this.getY() <= end.getY())
                || (this.getY() <= start.getY() && this.getY() >= end.getY()))) {
            return true;
        } else return start.getY() == end.getY() && this.getY() == start.getY()
                && ((this.getX() >= start.getX() && this.getX() <= end.getX())
                || (this.getX() <= start.getX() && this.getX() >= end.getX()));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Point point = (Point) other;
        return getX() == point.getX() && getY() == point.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return String.format("Point{x=%d, y=%d, xOffset=%d, yOffset=%d}", getX(), getY(), getTransform().getXOffset(), getTransform().getYOffset());
    }

    @Override
    public void deserialize(DataMap data, DataContext context) {
        x = data.get("X").getScalar().getInt();
        y = data.get("Y").getScalar().getInt();
    }

    @Override
    public void serialize(DataMap data, DataContext context) {
        data.put("X", DataValue.from(getUntransformedX()));
        data.put("Y", DataValue.from(getUntransformedY()));
    }
}
