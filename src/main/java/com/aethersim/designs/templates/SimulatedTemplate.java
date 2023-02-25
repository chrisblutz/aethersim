package com.aethersim.designs.templates;

import com.aethersim.designs.Chip;
import com.aethersim.designs.Pin;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.simulation.ChipState;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SimulatedTemplate<T extends ChipState> extends ChipTemplate {

    private final Set<Pin> inputPins = new LinkedHashSet<>();
    private final Set<Pin> outputPins = new LinkedHashSet<>();

    public Set<Pin> getInputPins() {
        return inputPins;
    }

    public Set<Pin> getOutputPins() {
        return outputPins;
    }

    public abstract void initialize(Chip chip);

    public abstract void dispose(Chip chip);

    public abstract T getState(Chip chip);

    public abstract void simulate(T state);

    @Override
    public void serialize(DataMap data, DataContext context) { /* do nothing */ }

    @Override
    public void deserialize(DataMap data, DataContext context) { /* do nothing */ }
}
