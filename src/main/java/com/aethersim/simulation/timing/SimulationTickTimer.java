package com.aethersim.simulation.timing;

import com.aethersim.logging.AetherSimLogging;
import com.aethersim.simulation.Simulator;

public class SimulationTickTimer {

    private long targetCycleDuration;
    private Simulator newSimulator = null, currentSimulator = null, oldSimulator = null;
    private final Runnable runnable;

    private volatile boolean running;
    private volatile long currentCycleStartTime;

    // Gets set to true by simulation manager to indicate that the reset() method of the simulator
    // itself should be called
    private volatile boolean simulationResetFlag = false;

    public SimulationTickTimer(long targetCycleDuration) {
        this.targetCycleDuration = targetCycleDuration;
        this.runnable = () -> {
            // While the worker loop is running, continue
            while (running) {
                // If there is an old simulator, stop it before we do anything else this tick
                if (oldSimulator != null) {
                    oldSimulator.stop();
                    // Clear the old simulator
                    oldSimulator = null;
                }

                // If there is a new simulator, start it before we do anything else this tick
                if (newSimulator != null) {
                    newSimulator.start();
                    // Update the current simulator and clear the new one
                    currentSimulator = newSimulator;
                    newSimulator = null;
                }

                // If the simulation has been marked for reset, reset it before doing anything
                if (simulationResetFlag) {
                    simulationResetFlag = false;
                    if (currentSimulator != null)
                        currentSimulator.reset();
                }

                // Set the initial cycle start time for this tick
                currentCycleStartTime = System.currentTimeMillis();
                // Call the simulator tick method
                if (currentSimulator != null)
                    currentSimulator.tick();
                // Synchronize the tick time
                synchronize();
            }

            // When we exit the loop, stop the current simulator
            if (currentSimulator != null)
                currentSimulator.stop();
        };
    }

    private synchronized void synchronize() {
        // Calculate the duration of the current tick
        long currentTickDuration = System.currentTimeMillis() - currentCycleStartTime;
        // Determine the delay (if any) that is necessary to hit the target tick duration
        long timeToDelay = targetCycleDuration - currentTickDuration;
        // Sleep the thread for that duration, unless it is 0 or below
        if (timeToDelay > 0){
            try {
                Thread.sleep(timeToDelay);
            } catch (InterruptedException e) {
                // Log an error and continue
                AetherSimLogging.getSimulationLogger().warn("The simulation timing thread encountered an error while synchronizing ticks.", e);
            }
        }
    }

    public void start() {
        // Initialize thread
        Thread timingThread = new Thread(runnable, "Simulation-Timer");
        // Start the thread
        running = true;
        timingThread.start();
    }

    public void stop() {
        // Tell the running thread to stop and let it die naturally
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void setResetFlag(boolean resetFlag) {
        this.simulationResetFlag = resetFlag;
    }

    public void setTargetCycleDuration(long targetCycleDuration) {
        this.targetCycleDuration = targetCycleDuration;
    }

    public void setSimulator(Simulator simulator) {
        // Move the current simulator to the old simulator so it can be stopped properly
        this.oldSimulator = currentSimulator;
        this.currentSimulator = null;
        // Move the new simulator to the correct variable
        this.newSimulator = simulator;
    }
}
