package com.aethersim.ui.toolkit.builtin.containers;

import com.aethersim.ui.toolkit.UIComponent;
import com.aethersim.ui.toolkit.UIContainer;
import com.aethersim.ui.toolkit.UIGraphics;
import com.aethersim.ui.toolkit.layout.Direction;
import com.aethersim.ui.toolkit.layout.UIDimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UIAbstractFlexContainer extends UIContainer {

    private final List<UIComponent> components = new ArrayList<>();
    private final Map<UIComponent, Float> componentWeights = new HashMap<>();
    private final Direction direction;

    protected UIAbstractFlexContainer(Direction direction) {
        this.direction = direction;
    }

    protected Direction getDirection() {
        return direction;
    }

    protected void addComponent(UIComponent component, float weight) {
        // Prevent null components being added
        if (component == null)
            return;

        // Set the component's parent
        component.setParent(this);
        // Add component and weight to mappings, then repack the container
        components.add(component);
        componentWeights.put(component, weight);
        pack();
    }

    protected void addComponent(int index, UIComponent component, float weight) {
        // Prevent null components being added
        if (component == null)
            return;

        // Set the component's parent
        component.setParent(this);
        // Add component and weight to mappings, then repack the container
        components.add(index, component);
        componentWeights.put(component, weight);
        pack();
    }

    protected void setComponentWeight(UIComponent component, float weight) {
        // If this container contains the component, set its weight and repack the container
        if (components.contains(component)) {
            componentWeights.put(component, weight);
            pack();
        }
    }

    protected void removeComponent(UIComponent component) {
        // If this container contains the component, remove it and repack the container
        if (components.contains(component)) {
            // Unset the component's parent before removing it
            component.setParent(null);
            components.remove(component);
            pack();
        }
    }

    protected void removeComponent(int index) {
        // If this container contains the index, remove it and repack the container
        if (index < components.size()) {
            UIComponent component = components.get(index);
            // Unset the component's parent before removing it
            component.setParent(null);
            components.remove(index);
            pack();
        }
    }

    @Override
    public List<UIComponent> getComponents() {
        return components;
    }

    @Override
    public void pack() {
        // For a flex container, we need to calculate the minimum dimension as
        // the sum of the minimum sizes of the inner components in the
        // main "direction" of the container, and as the "maximum" minimum
        // size in the other direction.
        int minimumPrimaryDimension = 0;
        int minimumSecondaryDimension = 0;
        for (UIComponent childComponent : components) {
            int primaryDimension = direction == Direction.VERTICAL ?
                    childComponent.getMinimumSize().getHeight() :
                    childComponent.getMinimumSize().getWidth();
            int secondaryDimension = direction == Direction.VERTICAL ?
                    childComponent.getMinimumSize().getWidth() :
                    childComponent.getMinimumSize().getHeight();

            minimumPrimaryDimension += primaryDimension;
            if (secondaryDimension > minimumSecondaryDimension)
                minimumSecondaryDimension = secondaryDimension;
        }
        // Set the container's minimum size (which will then repack its parent)
        setMinimumSize(new UIDimension(
                direction == Direction.VERTICAL ? minimumSecondaryDimension : minimumPrimaryDimension,
                direction == Direction.VERTICAL ? minimumPrimaryDimension : minimumSecondaryDimension
        ));
    }

    @Override
    public void settle() {
        // Now that we know the size of this container, we can set the sizes of the child
        // components.  Track the primary dimensions in a map, so we can calculate them
        // with the weights they have assigned.
        Map<UIComponent, Integer> primaryDimensions = new HashMap<>();
        int availablePrimarySpace = direction == Direction.VERTICAL ?
                getSize().getHeight() :
                getSize().getWidth();

        // Set initial minimum sizes (based on child component minimum sizes)
        // Also calculate total weight as we go
        float totalWeight = 0f;
        for (UIComponent childComponent : components) {
            int primaryDimension = direction == Direction.VERTICAL ?
                    childComponent.getMinimumSize().getHeight() :
                    childComponent.getMinimumSize().getWidth();
            primaryDimensions.put(childComponent, primaryDimension);
            // Subtract the minimum dimension from the overall available space
            availablePrimarySpace -= primaryDimension;
            // Add component weight to total
            totalWeight += componentWeights.getOrDefault(childComponent, 0f);
        }

        // Now, distribute the remaining space by weight.  Keep track of the largest
        // weighted component, as we'll assign any leftover (rounding error) space
        // there later.
        UIComponent maxWeightComponent = null;
        float maxWeight = 0f;
        int totalAvailablePrimarySpace = availablePrimarySpace;
        for (UIComponent childComponent : components) {
            float weight = componentWeights.getOrDefault(childComponent, 0f);
            // If this component has a weight of 0, skip it
            if (weight == 0)
                continue;

            // Calculate this component's share of the remaining space
            int spaceAllocation = (int) Math.floor((weight / totalWeight) * totalAvailablePrimarySpace);
            primaryDimensions.put(childComponent,
                    primaryDimensions.get(childComponent) + spaceAllocation
            );
            // Subtract this component's allocation from the total
            availablePrimarySpace -= spaceAllocation;

            // If this component has the maximum weight, track it
            if (weight > maxWeight) {
                maxWeightComponent = childComponent;
                maxWeight = weight;
            }
        }

        // If we have space remaining, add it to the maximum weighted component, if there is one.
        // If all components have a weight of 0, the space remains unassigned.
        if (availablePrimarySpace > 0 && maxWeightComponent != null)
            primaryDimensions.put(maxWeightComponent,
                    primaryDimensions.get(maxWeightComponent) + availablePrimarySpace
            );

        // Set the size of the child components
        for (UIComponent childComponent : components) {
            int primaryDimension = primaryDimensions.get(childComponent);
            int secondaryDimension = direction == Direction.VERTICAL ?
                    getSize().getWidth() :
                    getSize().getHeight();

            childComponent.setSize(new UIDimension(
                    direction == Direction.VERTICAL ? secondaryDimension : primaryDimension,
                    direction == Direction.VERTICAL ? primaryDimension : secondaryDimension
            ));
        }

        // Now that sizes are determined, calculate render spaces for child nodes.
        // This method assumes that this container's render space is already set.
        calculateRenderSpaces();

        // Settle all child containers
        for (UIComponent childComponent : components)
            if (childComponent instanceof UIContainer childContainer)
                childContainer.settle();
    }

    private void calculateRenderSpaces() {
        // If the render space for this container is null, do nothing.  This container's
        // render space is used as the baseline for the other render spaces.
        if (getRenderSpace() == null)
            return;

        // For each component, calculate its render spaces (and calculate X/Y values as we go)
        int componentX = getRenderSpace().getX();
        int componentY = getRenderSpace().getY();

        for (UIComponent childComponent : components) {
            int width = childComponent.getSize().getWidth();
            int height = childComponent.getSize().getHeight();

            // Set the render space for the child based on current X/Y values
            // and the component's width and height
            childComponent.getRenderSpace().update(componentX, componentY, width, height);

            // Add to the X or Y value depending on the direction of this container
            if (direction == Direction.VERTICAL)
                componentY += height;
            else
                componentX += width;
        }
    }

    @Override
    public void render(UIGraphics graphics) {
        // Draw child components
        for (UIComponent childComponent : components)
            renderComponent(graphics, childComponent);
    }
}
