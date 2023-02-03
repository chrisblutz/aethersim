package com.github.chrisblutz.breadboard.simulationproto.timing;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.simulationproto.Simulator;

public class SimulationTickTimer {

    private long targetCycleDuration;
    private Simulator newSimulator = null, currentSimulator = null, oldSimulator = null;
    private final Runnable runnable;

    private volatile boolean running;
    private volatile long currentCycleStartTime;

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

                // Set the initial cycle start time for this tick
                currentCycleStartTime = System.currentTimeMillis();
                // Call the simulator tick method
                if (currentSimulator != null)
                    currentSimulator.tick(TickTrigger.TIMED);
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
                BreadboardLogging.getSimulationLogger().warn("The simulation timing thread encountered an error while synchronizing ticks.", e);
            }
        }
    }

    public void start() {
        // Initialize thread
        Thread timingThread = new Thread(runnable, "breadboard-simulation-timer");
        // Start the thread
        running = true;
        timingThread.start();
    }

    public void stop() {
        // Tell the running thread to stop and let it die naturally
        running = false;
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
