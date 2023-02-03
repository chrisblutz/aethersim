package com.github.chrisblutz.breadboard.simulationproto.standard.threading;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.simulation.workers.WorkerScheduler;

public class CoordinationThreadTimer {

    private long targetCycleDuration;
    private final Runnable innerRunnable, timerRunnable;

    private volatile boolean running;
    private volatile long currentCycleStartTime;

    public CoordinationThreadTimer(long targetCycleDuration, final Runnable runnable) {
        this.targetCycleDuration = targetCycleDuration;
        this.innerRunnable = runnable;
        this.timerRunnable = () -> {
            // While the worker loop is running, continue
            while (running) {
                // Set the initial cycle start time for this tick
                currentCycleStartTime = System.currentTimeMillis();
                // Call the scheduler tick method
                innerRunnable.run();
                // Synchronize the tick time
                synchronize();
            }
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
        Thread timingThread = new Thread(timerRunnable, "SimulationTimingCoordinator");
        // Start the thread
        running = true;
        timingThread.start();
    }

    public void setTargetCycleDuration(long targetCycleDuration) {
        this.targetCycleDuration = targetCycleDuration;
    }

    public void stop() {
        // Tell the running thread to stop and let it die naturally
        running = false;
    }
}
