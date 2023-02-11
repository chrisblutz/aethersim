package com.github.chrisblutz.breadboard.designs.templates;

import com.github.chrisblutz.breadboard.designs.Chip;
import com.github.chrisblutz.breadboard.designs.Pin;
import com.github.chrisblutz.breadboard.simulation.ChipState;

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
}
