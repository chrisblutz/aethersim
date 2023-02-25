package com.aethersim.simulation;

import com.aethersim.designs.Design;
import com.aethersim.simulation.timing.SimulationTickTimer;

public class Simulation {

    private static final SimulationTickTimer TIMER = new SimulationTickTimer(2); // TODO default
    private static Simulator simulator;

    public static Simulator getSimulator() {
        return simulator;
    }

    public static void setSimulator(Simulator simulator) {
        Simulation.simulator = simulator;
        // Update the simulator in the timer thread
        TIMER.setSimulator(simulator);
    }

    public static SimulatedDesign initialize(Design design) {
        // Reset the simulator so it's ready to pick up the next design (necessary if it had one already)
        reset();
        // Initialize the simulator if it's not null
        if (simulator != null)
            return simulator.initialize(design);
        else
            return null;
    }

    public static void start() {
        // Start the timing thread, which will then start the simulator
        TIMER.start();
    }

    public static void stop() {
        // Stop the timing thread, which will then stop the simulator
        TIMER.stop();
    }

    public static void reset() {
        // Mark the simulator for reset via the timer
        TIMER.setResetFlag(true);
    }

    public static boolean isRunning() {
        return TIMER.isRunning();
    }
}