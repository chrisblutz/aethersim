package com.aethersim.ui.toolkit.builtin.containers;

import com.aethersim.ui.toolkit.UIComponent;
import com.aethersim.ui.toolkit.UIContainer;
import com.aethersim.ui.toolkit.UIGraphics;
import com.aethersim.ui.toolkit.layout.Padding;
import com.aethersim.ui.toolkit.layout.UIDimension;

import java.util.Collections;
import java.util.List;

public class UIPadding extends UIContainer {

    private UIComponent component;
    private List<UIComponent> componentCollection;
    private Padding padding;

    public UIPadding(UIComponent component, Padding padding) {
        this.component = component;
        component.setParent(this);
        this.componentCollection = Collections.singletonList(component);

        // Prevent null padding
        if (padding == null)
            padding = new Padding(0);
        this.padding = padding;

        // Pack this component since we've set the container
        pack();
    }

    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
        component.setParent(this);
        this.componentCollection = Collections.singletonList(component);
        pack();
    }

    public Padding getPadding() {
        return padding;
    }

    public void setPadding(Padding padding) {
        // Prevent null padding
        if (padding == null)
            padding = new Padding(0);
        this.padding = padding;
        pack();
    }

    @Override
    public List<UIComponent> getComponents() {
        return componentCollection;
    }

    @Override
    public void pack() {
        // Set the minimum dimension of this component to be the sum of the child component
        // and all padding in that dimension
        if (component != null)
            setMinimumSize(new UIDimension(
                    component.getMinimumSize().getWidth() + padding.getPaddingLeft() + padding.getPaddingRight(),
                    component.getMinimumSize().getHeight() + padding.getPaddingTop() + padding.getPaddingBottom()
            ));
        else
            setMinimumSize(new UIDimension(
                    padding.getPaddingLeft() + padding.getPaddingRight(),
                    padding.getPaddingTop() + padding.getPaddingBottom()
            ));
    }

    @Override
    public void settle() {
        // Now that we know the size of this component, set the size of the child component
        // and set its render space
        if (component != null) {
            component.setSize(new UIDimension(
                    getWidth() - padding.getPaddingLeft() - padding.getPaddingRight(),
                    getHeight() - padding.getPaddingTop() - padding.getPaddingBottom()
            ));
            component.getRenderSpace().update(
                    getRenderSpace().getX() + padding.getPaddingLeft(),
                    getRenderSpace().getY() + padding.getPaddingTop(),
                    component.getWidth(),
                    component.getHeight()
            );
        }

        // If the child component is a container, settle it
        if (component instanceof UIContainer containerComponent)
            containerComponent.settle();
    }

    @Override
    public void render(UIGraphics graphics) {
        // Render the child component
        if (component != null)
            renderComponent(graphics, component);
    }
}
