package com.aethersim.ui.toolkit;

import java.util.List;

public abstract class UIContainer extends UIComponent {

    private boolean hasFocusedChild = false;

    public abstract List<UIComponent> getComponents();

    public abstract void pack();

    public abstract void settle();

    protected void renderComponent(UIGraphics graphics, UIComponent component) {
        // Create a copy of the graphics object, then transform it for the component render space
        // Note that the graphics object is already translated for the container, so we only
        // need to do the relative translation to the component.
        graphics.withCopy(componentGraphics -> {
            // Translate and clip to the component being rendered
            componentGraphics.translate(
                    component.getRenderSpace().getX() - getRenderSpace().getX(),
                    component.getRenderSpace().getY() - getRenderSpace().getY()
            );
            componentGraphics.clip(
                    component.getRenderSpace().getWidth(),
                    component.getRenderSpace().getHeight()
            );

            // Render the component
            component.render(componentGraphics);
        });
    }

    @Override
    public void setParentWindow(UIWindow parentWindow) {
        // Set the parent of this component
        super.setParentWindow(parentWindow);

        // Then, set the parent of all child components
        for (UIComponent component : getComponents())
            component.setParentWindow(parentWindow);
    }

    public void onChildFocused() {
        this.hasFocusedChild = true;
        // Propagate up the chain
        if (getParent() != null)
            getParent().onChildFocused();
    }

    public void onChildUnfocused() {
        this.hasFocusedChild = false;
        // Propagate up the chain
        if (getParent() != null)
            getParent().onChildUnfocused();
    }

    protected boolean hasFocusedChild() {
        return hasFocusedChild;
    }

    public UIFocusable getFirstFocusable() {
        // Find the first boundary-adjacent focusable element while searching from the front
        return getBoundaryAdjacentFocusable(true);
    }

    public UIFocusable getLastFocusable() {
        // Find the first boundary-adjacent focusable element while searching from the back
        return getBoundaryAdjacentFocusable(false);
    }

    protected UIFocusable getBoundaryAdjacentFocusable(boolean searchForward) {
        // Searching in the specified direction, find the first focusable element.  If this container
        // is focusable, it is the first focusable component if searching forward
        if (this instanceof UIFocusable focusable && searchForward) {
            return focusable;
        } else {
            for (
                    int index = (searchForward ? 0 : getComponents().size() - 1);
                    (searchForward ? index < getComponents().size() : index >= 0);
                    index += (searchForward ? 1 : -1)
            ) {
                // Pull component out of the collection
                UIComponent component = getComponents().get(index);
                // If the component is a container, try to find its first available component
                if (component instanceof UIContainer containerComponent) {
                    UIFocusable nextFromChild = containerComponent.getBoundaryAdjacentFocusable(searchForward);
                    // If we found one, return it
                    if (nextFromChild != null)
                        return nextFromChild;
                } else if (component instanceof UIFocusable focusable) {
                    // If we get here, this component is focusable, so return it
                    return focusable;
                }
            }
        }

        // If we get here, this container has no children and is not focusable, and thus does not have any
        // focusable elements
        return null;
    }

    public UIFocusable getNextFocusable() {
        // Get next adjacent focusable element searching from the front
        return getAdjacentFocusable(true);
    }

    public UIFocusable getPreviousFocusable() {
        // Get next adjacent focusable element searching from the back
        return getAdjacentFocusable(false);
    }

    protected UIFocusable getAdjacentFocusable(boolean searchForward) {
        // If there is a currently-focused element in this container,
        // find the next one, if there is one.  If this container itself is focused,
        // find the next one internally if searching forward.  If neither of those is true,
        // find the first available focusable component.
        boolean thisFocused = this instanceof UIFocusable focusable && focusable.isFocused();
        if (hasFocusedChild() || (thisFocused && searchForward)) {
            // First, find the focused child (recursively if necessary).  If this container itself is focused,
            // mark that we've already found the focused component.
            boolean foundFocus = thisFocused;
            UIFocusable nextFocusable = null;
            for (
                    int index = (searchForward ? 0 : getComponents().size() - 1);
                    (searchForward ? index < getComponents().size() : index >= 0);
                    index += (searchForward ? 1 : -1)
            ) {
                // Pull the component out of the collection
                UIComponent component = getComponents().get(index);
                // If we haven't yet found the focused element, find it
                if (!foundFocus) {
                    if (component instanceof UIFocusable focusable &&
                            focusable.isFocused()) {
                        // If we get here, the current component is focused.  If it's a container,
                        // see if it has a focusable child.  Otherwise, mark that we found the
                        // focused component and continue
                        if (component instanceof UIContainer containerComponent) {
                            nextFocusable = containerComponent.getAdjacentFocusable(searchForward);
                            if (nextFocusable != null)
                                break;
                        }

                        foundFocus = true;
                    } else if (component instanceof UIContainer containerComponent &&
                            containerComponent.hasFocusedChild()) {
                        // Get the next focusable element from the child container
                        nextFocusable = containerComponent.getAdjacentFocusable(searchForward);
                        // If there is no adjacent one in the child, we've found the focused element
                        // but need to find the adjacent element in this container.  Otherwise,
                        // return the component we found.
                        if (nextFocusable == null)
                            foundFocus = true;
                        else
                            break;
                    }
                } else {
                    // If we're here, we found the focused element, so now we need to identify the
                    // adjacent one.
                    // If this is a container, check if it has a focusable child
                    if (component instanceof UIContainer containerComponent) {
                        nextFocusable = containerComponent.getBoundaryAdjacentFocusable(searchForward);
                        // If we found one, return it
                        if (nextFocusable != null)
                            break;
                    } else if (component instanceof UIFocusable focusable) {
                        // If we get here, this component is focusable, so return it
                        nextFocusable = focusable;
                        break;
                    }
                }
            }

            // If we found a next-focusable element, return it
            if (nextFocusable != null)
                return nextFocusable;

            // If we get here, and didn't find an adjacent focusable element, check if this container itself is
            // focusable.  If it is and we're searching backwards, return it.  Otherwise, return null.
            if (this instanceof UIFocusable focusable && !searchForward)
                nextFocusable = focusable;
            // "nextFocusable" will be null if not set in the line above, so here we either return this container
            // or null
            return nextFocusable;
        } else if (thisFocused) {
            // If this container is focused and we're searching backward, there is no further focusable element
            return null;
        } else {
            // If we don't have a focusable element currently, find the first one
            return getBoundaryAdjacentFocusable(searchForward);
        }
    }

    @Override
    public void onRenderPass() {
        // Call the render pass listener for all children
        for (UIComponent component : getComponents())
            component.onRenderPass();
    }
}
