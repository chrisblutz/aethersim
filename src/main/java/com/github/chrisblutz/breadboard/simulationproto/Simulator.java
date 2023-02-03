package com.github.chrisblutz.breadboard.simulationproto;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.simulationproto.timing.TickTrigger;

public abstract class Simulator {

    public abstract SimulatedDesign initialize(Design design);

    public abstract void start();

    public abstract void stop();

    public abstract void tick(TickTrigger trigger);

    public abstract void reset();
}
