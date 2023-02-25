package com.aethersim.designs.states;

import com.aethersim.designs.DesignElement;

public class Transform {

    public static final Transform NONE = new Transform(null, 0, 0) {
        @Override
        public void setOffset(int xOffset, int yOffset) {
            // Do nothing, as this object represents a no-op transform
        }

        @Override
        public void addOffset(int xOffset, int yOffset) {
            // Do nothing, as this object represents a no-op transform
        }

        @Override
        public void reset() {
            // Do nothing, as this object represents a no-op transform
        }
    };

    private final DesignElement associatedElement;
    private int xOffset, yOffset;

    public Transform(DesignElement associatedElement) {
        this(associatedElement, 0, 0);
    }

    public Transform(DesignElement associatedElement, int xOffset, int yOffset) {
        this.associatedElement = associatedElement;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public boolean hasOffsets() {
        return xOffset != 0 || yOffset != 0;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        // Notify the associated element that the transform has updated
        if (associatedElement != null)
            associatedElement.updateTransform();
    }

    public void addOffset(int xOffset, int yOffset) {
        this.xOffset += xOffset;
        this.yOffset += yOffset;

        // Notify the associated element that the transform has updated
        if (associatedElement != null)
            associatedElement.updateTransform();
    }

    public void reset() {
        setOffset(0, 0);
    }
}
