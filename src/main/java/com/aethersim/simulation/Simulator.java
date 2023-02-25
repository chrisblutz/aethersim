package com.aethersim.simulation;

import com.aethersim.designs.Design;

public abstract class Simulator {

    public abstract SimulatedDesign initialize(Design design);

    public abstract void start();

    public abstract void stop();

    public abstract void tick();

    public abstract void reset();
}
