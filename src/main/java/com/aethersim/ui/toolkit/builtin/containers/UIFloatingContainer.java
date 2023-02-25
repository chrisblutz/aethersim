package com.aethersim.ui.toolkit.builtin.containers;

import com.aethersim.ui.toolkit.UIComponent;
import com.aethersim.ui.toolkit.UIContainer;
import com.aethersim.ui.toolkit.UIGraphics;
import com.aethersim.ui.toolkit.layout.Anchor;
import com.aethersim.ui.toolkit.layout.Padding;
import com.aethersim.ui.toolkit.layout.UIDimension;

import java.util.*;

public class UIFloatingContainer extends UIContainer {

    private static class FloatConfiguration {
        private Anchor anchor;
        private Padding padding;
        private int zLevel;

        FloatConfiguration(Anchor anchor, Padding padding, int zLevel) {
            this.anchor = anchor;
            this.padding = padding;
            this.zLevel = zLevel;
        }
    }

    private static final Anchor DEFAULT_ANCHOR = Anchor.TOP_LEFT;
    private static final Padding DEFAULT_PADDING = new Padding(0);
    private static final int DEFAULT_Z_LEVEL = 0;

    // Contains a mapping of the components in this container to their float configurations
    private final Map<UIComponent, FloatConfiguration> components = new LinkedHashMap<>();
    // Contains the component that acts as the "background" for the container
    private UIComponent backgroundComponent = null;
    // Contains the components within this container ordered by z-level
    private final List<UIComponent> componentsOrdered = new ArrayList<>();

    public void addComponent(UIComponent component, Anchor anchor) {
        addComponent(component, anchor, DEFAULT_PADDING, DEFAULT_Z_LEVEL);
    }

    public void addComponent(UIComponent component, Anchor anchor, Padding padding) {
        addComponent(component, anchor, padding, DEFAULT_Z_LEVEL);
    }

    public void addComponent(UIComponent component, Anchor anchor, int zLevel) {
        addComponent(component, anchor, DEFAULT_PADDING, zLevel);
    }

    public void addComponent(UIComponent component, Anchor anchor, Padding padding, int zLevel) {
        // Prevent null components being added
        if (component == null)
            return;

        // If any of the configuration elements are null, set them to the defaults
        if (anchor == null)
            anchor = DEFAULT_ANCHOR;
        if (padding == null)
            padding = DEFAULT_PADDING;

        // Set the component's parent
        component.setParent(this);
        // Add component and its float configuration
        components.put(component, new FloatConfiguration(anchor, padding, zLevel));
        // Rebuild the ordered list of components
        buildOrderedComponentList();
        // Pack this container
        pack();
    }

    public void setBackgroundComponent(UIComponent component) {
        // Prevent null components being added
        if (component == null)
            return;

        // Set the component's parent
        component.setParent(this);
        // Set the background component
        this.backgroundComponent = component;
        // Rebuild the ordered list of components
        buildOrderedComponentList();
        // Pack this container
        pack();
    }

    public void setComponentAnchor(UIComponent component, Anchor anchor) {
        // If the anchor is null, return
        if (anchor == null)
            return;

        // If this container contains the component, set its anchor and repack the container
        if (components.containsKey(component)) {
            components.get(component).anchor = anchor;
            pack();
        }
    }

    public void setComponentPadding(UIComponent component, Padding padding) {
        // If the padding is null, return
        if (padding == null)
            return;

        // If this container contains the component, set its padding and repack the container
        if (components.containsKey(component)) {
            components.get(component).padding = padding;
            pack();
        }
    }

    public void setComponentZLevel(UIComponent component, int zLevel) {
        // If this container contains the component, set its z-level and repack the container
        if (components.containsKey(component)) {
            components.get(component).zLevel = zLevel;
            // Since this is updating z-levels, we need to rebuild the ordered list
            buildOrderedComponentList();
            pack();
        }
    }

    public void removeComponent(UIComponent component) {
        // If this container contains the component, remove it and repack the container
        if (components.containsKey(component)) {
            // Unset the component's parent before removing it
            component.setParent(null);
            components.remove(component);
            componentsOrdered.remove(component);
            pack();
        }
    }

    private void buildOrderedComponentList() {
        // Clear the existing list of ordered components
        componentsOrdered.clear();

        // Since the background component is automatically the lowest z-level, set that as the first
        // entry in the component list
        if (backgroundComponent != null)
            componentsOrdered.add(backgroundComponent);

        // Build a temporary bucketed map of z-levels
        Map<Integer, List<UIComponent>> zIndexBuckets = new HashMap<>();

        // For each component, add it to the corresponding z-level bucket
        for (UIComponent component : components.keySet()) {
            int zLevel = components.get(component).zLevel;
            // If the z-level doesn't have a list yet, create one
            if (!zIndexBuckets.containsKey(zLevel))
                zIndexBuckets.put(zLevel, new ArrayList<>());
            // Add the component to the end of the list
            zIndexBuckets.get(zLevel).add(component);
        }

        // Build the list of ordered components starting with the lowest z-levels (e.g.,
        // the components that should receive mouse events latest) to the highest, and
        // when two components have the same z-level, the one that was added earlier should
        // be placed first (the same order from the bucketed lists above, based on the initial
        // order from the LinkedHashMap)
        Integer[] buckets = zIndexBuckets.keySet().toArray(new Integer[0]);
        Arrays.sort(buckets);
        for (Integer integer : buckets) {
            // Pull the list for the bucket, and add the items to the ordered components list
            List<UIComponent> bucket = zIndexBuckets.get(integer);
            componentsOrdered.addAll(bucket);
        }
    }

    @Override
    public List<UIComponent> getComponents() {
        return componentsOrdered;
    }

    @Override
    public void pack() {
        // If we have a background component, set the minimum size of this container based on the
        // minimum size of that container.  Otherwise, do nothing
        if (backgroundComponent != null)
            setMinimumSize(backgroundComponent.getMinimumSize());
    }

    @Override
    public void settle() {
        // Now that we know the size of this container, we can set the sizes of the child components.
        // Since the components in this container are not constrained by any layouts, their sizes will
        // be set according to their preferred sizes, adjusted for the size of this container and the
        // padding assigned to them

        // Calculate X and Y values for important anchor points
        int leftX = getX();
        int middleX = getX() + (getWidth() / 2);
        int rightX = getX() + getWidth();
        int topY = getY();
        int centerY = getY() + (getHeight() / 2);
        int bottomY = getY() + getHeight();

        // For each component, assign its size and render space
        for (UIComponent component : components.keySet()) {
            // Pull out references to padding and anchor, so we don't need to query them constantly
            Anchor anchor = components.get(component).anchor;
            Padding padding = components.get(component).padding;

            // Assign a size to the component.  The size is the preferred size for the component, unless
            // that size when combined with the padding exceeds the dimensions of the container
            int componentWidth = Math.min(component.getPreferredSize().getWidth(), Math.max(getWidth() - (padding.getPaddingLeft() + padding.getPaddingRight()), 0));
            int componentHeight = Math.min(component.getPreferredSize().getHeight(), Math.max(getHeight() - (padding.getPaddingTop() + padding.getPaddingBottom()), 0));
            component.setSize(new UIDimension(componentWidth, componentHeight));

            // Calculate important quantities for the components
            int halfComponentWidth = componentWidth / 2;
            int halfComponentHeight = componentHeight / 2;

            // Switch on anchor and assign render spaces
            switch (anchor) {
                case TOP_LEFT -> component.getRenderSpace().update(
                        leftX + padding.getPaddingLeft(),
                        topY + padding.getPaddingTop(),
                        componentWidth,
                        componentHeight
                );
                case TOP_MIDDLE -> component.getRenderSpace().update(
                        middleX - halfComponentWidth,
                        topY + padding.getPaddingTop(),
                        componentWidth,
                        componentHeight
                );
                case TOP_RIGHT -> component.getRenderSpace().update(
                        rightX - componentWidth - padding.getPaddingRight(),
                        topY + padding.getPaddingTop(),
                        componentWidth,
                        componentHeight
                );
                case CENTER_LEFT -> component.getRenderSpace().update(
                        leftX + padding.getPaddingLeft(),
                        centerY - halfComponentHeight,
                        componentWidth,
                        componentHeight
                );
                case CENTER -> component.getRenderSpace().update(
                        middleX - halfComponentWidth,
                        centerY - halfComponentHeight,
                        componentWidth,
                        componentHeight
                );
                case CENTER_RIGHT -> component.getRenderSpace().update(
                        rightX - componentWidth - padding.getPaddingRight(),
                        centerY - halfComponentHeight,
                        componentWidth,
                        componentHeight
                );
                case BOTTOM_LEFT -> component.getRenderSpace().update(
                        leftX + padding.getPaddingLeft(),
                        bottomY - componentHeight - padding.getPaddingBottom(),
                        componentWidth,
                        componentHeight
                );
                case BOTTOM_MIDDLE -> component.getRenderSpace().update(
                        middleX - halfComponentWidth,
                        bottomY - componentHeight - padding.getPaddingBottom(),
                        componentWidth,
                        componentHeight
                );
                case BOTTOM_RIGHT -> component.getRenderSpace().update(
                        rightX - componentWidth - padding.getPaddingRight(),
                        bottomY - componentHeight - padding.getPaddingBottom(),
                        componentWidth,
                        componentHeight
                );
            }
        }

        // If we have a background component, set its render space to the full space available to the container.
        if (backgroundComponent != null) {
            backgroundComponent.setSize(getSize());
            backgroundComponent.getRenderSpace().update(leftX, topY,getWidth(), getHeight());
        }

        // Settle all child containers
        for (UIComponent childComponent : componentsOrdered)
            if (childComponent instanceof UIContainer childContainer)
                childContainer.settle();
    }

    @Override
    public void render(UIGraphics graphics) {
        // Draw child components in order (to draw lowest z-level components first)
        for (UIComponent component : componentsOrdered)
            renderComponent(graphics, component);
    }
}
