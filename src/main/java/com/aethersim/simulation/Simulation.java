package com.aethersim.simulation;

import com.aethersim.designs.Design;
import com.aethersim.simulation.timing.SimulationTickTimer;

/**
 * The {@code Simulation} class controls AetherSim's simulation module.  Since simulators are modular, this class
 * is used to dispatch all necessary events to whichever module is selected.
 */
public final class Simulation {

    // Don't allow instances of this class
    private Simulation() {}

    // This timer is used to run the "ticks" for the simulator
    private static final SimulationTickTimer TIMER = new SimulationTickTimer(2); // TODO default
    // This refers to the currently-active simulator module
    private static Simulator simulator;

    /**
     * This method returns the simulator module that is currently active.  If no module is active, this method returns
     * {@code null}.
     *
     * @return The current simulator module, or {@code null} if there is no module active
     */
    public static Simulator getSimulator() {
        return simulator;
    }

    /**
     * This method sets the currently-active simulator module.  If there was another module active prior to this,
     * that simulator module is disposed of and overridden.
     *
     * @param simulator the new {@link Simulator} module to use for circuit simulation
     */
    public static void setSimulator(Simulator simulator) {
        Simulation.simulator = simulator;
        // Update the simulator in the timer thread
        TIMER.setSimulator(simulator);
    }

    /**
     * This method initializes the currently-active simulator module with the specified {@link Design}.  The simulator
     * is responsible for converting that design to its internal representation and then returning a
     * {@link SimulatedDesign}, which links design elements to {@link LogicState}s assigned by the simulator during
     * operation.
     *
     * @param design the {@link Design} to build
     * @return The {@link SimulatedDesign} built from the specified {@link Design}
     */
    public static SimulatedDesign initialize(Design design) {
        // Reset the simulator so it's ready to pick up the next design (necessary if it had one already)
        reset();
        // Initialize the simulator if it's not null
        if (simulator != null)
            return simulator.initialize(design);
        else
            return null;
    }

    /**
     * This method starts the simulation timing module, which will provide ticks to the currently-active simulator
     * module.
     */
    public static void start() {
        // Start the timing thread, which will then start the simulator
        TIMER.start();
    }

    /**
     * This method stops the simulation timing module, which will stop providing ticks to the currently-active
     * simulator module.  To maintain thread safety, the simulator is allowed to complete its current tick if
     * a tick is ongoing.
     */
    public static void stop() {
        // Stop the timing thread, which will then stop the simulator
        TIMER.stop();
    }

    /**
     * This method flags the simulator module for a reset during the next simulation tick in order to maintain thread
     * safety.  If the simulator is currently ticking, the current tick's operation will be unaffected.
     */
    public static void reset() {
        // Mark the simulator for reset via the timer
        TIMER.setResetFlag(true);
    }

    /**
     * This method returns whether the simulation is currently running (i.e. ticking) or not.
     *
     * @return {@code true} if the simulation is running, {@code false} otherwise
     */
    public static boolean isRunning() {
        return TIMER.isRunning();
    }
}
