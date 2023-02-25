package com.aethersim.simulation;

import com.aethersim.designs.Design;

/**
 * A {@code Simulator} defines a simulation module that is responsible for simulating design elements and assigning
 * them {@link LogicState}s according to that simulation.  Simulation modules are modular by design, so simulation
 * modules must be able to initialize and dispose of themselves when necessary.
 */
public abstract class Simulator {

    /**
     * This method initializes any necessary resources in the simulation module, then converts the {@link Design}
     * provided into a {@link SimulatedDesign} that maps design elements to their simulated {@link LogicState}s.
     * <p>
     * Each simulation module may perform this action in a different way, but the resulting {@link SimulatedDesign}
     * is guaranteed to map design elements to {@link LogicState}s.
     *
     * @param design the {@link Design} to initialize with
     * @return The {@link SimulatedDesign} built from the specified {@link Design}
     */
    public abstract SimulatedDesign initialize(Design design);

    /**
     * This method should perform any actions necessary for the simulator module's first tick, as this method is
     * called directly before the first call to {@link #tick()}.
     */
    public abstract void start();

    /**
     * This method should perform any actions necessary to clean up after the simulation module.  This method is
     * <em>not</em> called if the simulation is stopped or paused (as state is maintained during these pauses),
     * but is called when the simulation module is overridden and disposed of.  As such, this method should dispose
     * of any resources that were allocated by the simulator and perform any final clean-up that is necessary
     * before the new simulator module starts.
     */
    public abstract void stop();

    /**
     * This method should perform any actions necessary for a simulation tick.  Since this method is called many times
     * in quick succession, its operation should be as quick as possible.  Additionally, any memory inefficiencies
     * in this method (or other called methods) will result in large memory efficiencies for the simulator as a whole,
     * due to the rapid rate of calls to this method.
     */
    public abstract void tick();

    /**
     * This method should reset the simulator module as close to its initial state as possible.  It is always called
     * on the same thread as {@link #start()}, {@link #stop()}, and {@link #tick()}.  It will be called before
     * {@link #tick()} but after {@link #start()} if this is a new simulator.
     */
    public abstract void reset();
}
