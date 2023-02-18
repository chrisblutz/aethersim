package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.states.Transform;

import java.util.Collection;
import java.util.Set;

public abstract class DesignElement {

    private static final Set<DesignElement> EMPTY_SET = Set.of();

    private final Transform transform = new Transform(this);

    public Transform getTransform() {
        return transform;
    }

    public void updateTransform() {
        onTransformUpdated();
    }

    protected abstract void onTransformUpdated();

    public void acceptTransform() {
        onTransformAccepted();
        getTransform().reset();
    }

    protected abstract void onTransformAccepted();

    public void rejectTransform() {
        getTransform().reset();
    }

    public abstract boolean contains(Point point);

    public Collection<DesignElement> getNestedElements() {
        return EMPTY_SET;
    }
}
