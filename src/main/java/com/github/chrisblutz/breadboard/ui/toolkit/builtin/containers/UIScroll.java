package com.github.chrisblutz.breadboard.ui.toolkit.builtin.containers;

import com.github.chrisblutz.breadboard.ui.toolkit.*;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.input.UIScrollbar;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Direction;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class UIScroll extends UIContainer implements UIInteractable, UIFocusable {

    private UIComponent component;
    private UIScrollbar horizontalScrollbar, verticalScrollbar;
    private List<UIComponent> components = new ArrayList<>();

    public UIScroll(UIComponent component) {
        this.component = component;
        component.setParent(this);
        this.horizontalScrollbar = new UIScrollbar(Direction.HORIZONTAL);
        horizontalScrollbar.setParent(this);
        this.verticalScrollbar = new UIScrollbar(Direction.VERTICAL);
        verticalScrollbar.setParent(this);

        // Set the list of components
        components.add(component);
        components.add(horizontalScrollbar);
        components.add(verticalScrollbar);

        calculateMinimumSize();
    }

    private void calculateMinimumSize() {
        // Set the minimum size of this component to the minimum sizes of the scrollbars
        setMinimumSize(new UIDimension(
                verticalScrollbar.getMinimumSize().getWidth(),
                horizontalScrollbar.getMinimumSize().getHeight()
        ));
    }

    @Override
    public List<UIComponent> getComponents() {
        return components;
    }

    @Override
    public void onChildFocused() {
        super.onChildFocused();

        // When a child of the scroll pane is focused, it needs to be placed in the viewport
        // if it's not already there
    }

    @Override
    public void pack() {
    }

    @Override
    public void settle() {
        // Set the sizes of the scrollbars to the minimum size of the scrollbar (unless that overflows
        // the size of this container)
        horizontalScrollbar.setSize(new UIDimension(
                getWidth(),
                Math.min(getHeight(), horizontalScrollbar.getMinimumSize().getHeight())
        ));
        verticalScrollbar.setSize(new UIDimension(
                Math.min(getWidth(), verticalScrollbar.getMinimumSize().getWidth()),
                getHeight()
        ));

        // Set the size of the component to the size of the scroll area after subtracting the sizes
        // of the scrollbars
        component.setSize(new UIDimension(
                getWidth() - verticalScrollbar.getWidth(),
                getHeight() - horizontalScrollbar.getHeight()
        ));

        // Set up the render spaces for the components
        horizontalScrollbar.getRenderSpace().update(
                getX(),
                getY() + getHeight() - horizontalScrollbar.getHeight(),
                horizontalScrollbar.getWidth(),
                horizontalScrollbar.getHeight()
        );
        verticalScrollbar.getRenderSpace().update(
                getX() + getWidth() - verticalScrollbar.getWidth(),
                getY(),
                verticalScrollbar.getWidth(),
                verticalScrollbar.getHeight()
        );
        component.getRenderSpace().update(
                getX(),
                getY(),
                component.getWidth(),
                component.getHeight()
        );

        // If the child component is a container, settle it
        if (component instanceof UIContainer containerComponent)
            containerComponent.settle();
    }

    @Override
    public void render(UIGraphics graphics) {
        // Draw child components
        for (UIComponent component : components)
            renderComponent(graphics, component);
    }

    @Override
    public boolean onMouseClicked(int x, int y, int button) {
        return false;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        return false;
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {

    }

    @Override
    public void onMouseEntered() {

    }

    @Override
    public void onMouseExited() {

    }

    @Override
    public void onMouseDragged(int x, int y) {

    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean onMouseScrolled(int scrollAmount) {
        return false;
    }

    @Override
    public boolean onKeyTyped(KeyEvent e) {
        return false;
    }

    @Override
    public boolean onKeyPressed(KeyEvent e) {
        return false;
    }

    @Override
    public boolean onKeyReleased(KeyEvent e) {
        return false;
    }

    @Override
    public void onFocusGained(boolean keyboardTriggered) {

    }

    @Override
    public void onFocusLost(boolean keyboardTriggered) {

    }
}
