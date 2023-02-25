package com.aethersim.ui.toolkit.builtin.containers;

import com.aethersim.ui.toolkit.UIComponent;
import com.aethersim.ui.toolkit.layout.Direction;

public class UIFlexContainer extends UIAbstractFlexContainer {

    public UIFlexContainer(Direction direction) {
        super(direction);
    }

    @Override
    public Direction getDirection() {
        return super.getDirection();
    }

    @Override
    public void addComponent(UIComponent component, float weight) {
        super.addComponent(component, weight);
    }

    @Override
    public void addComponent(int index, UIComponent component, float weight) {
        super.addComponent(index, component, weight);
    }

    @Override
    public void setComponentWeight(UIComponent component, float weight) {
        super.setComponentWeight(component, weight);
    }

    @Override
    public void removeComponent(UIComponent component) {
        super.removeComponent(component);
    }

    @Override
    public void removeComponent(int index) {
        super.removeComponent(index);
    }
}
