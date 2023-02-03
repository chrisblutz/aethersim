package com.github.chrisblutz.breadboard.simulation.workers;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;

public class WorkerLoop {

    private long targetTickDuration;
    private Runnable managerRunnable;
    private Thread managerThread;

    private volatile boolean running;
    private volatile long currentCycleStartTime;

    public WorkerLoop(long defaultTargetTickDuration) {
        this.targetTickDuration = defaultTargetTickDuration;
        // Initialize the runnable that will be used in all threads created for the loop
        this.managerRunnable = () -> {
            // While the worker loop is running, continue
            while (running) {
                // Set the initial cycle start time for this tick
                currentCycleStartTime = System.currentTimeMillis();
                // Call the scheduler tick method
                WorkerScheduler.tick();
                // Synchronize the tick time
                synchronize();
            }
        };
    }

    private synchronized void synchronize() {
        // Calculate the duration of the current tick
        long currentTickDuration = System.currentTimeMillis() - currentCycleStartTime;
        // Determine the delay (if any) that is necessary to hit the target tick duration
        long timeToDelay = targetTickDuration - currentTickDuration;
        // Sleep the thread for that duration, unless it is 0 or below
        if (timeToDelay > 0){
            try {
                Thread.sleep(timeToDelay);
            } catch (InterruptedException e) {
                // Log an error and continue
                BreadboardLogging.getSimulationLogger().warn("The worker management thread encountered an error while synchronizing ticks.", e);
            }
        }
    }

    public void start() {
        // Initialize thread
        managerThread = new Thread(managerRunnable, "Simulation Manager");
        // Start the thread
        running = true;
        managerThread.start();
    }

    public void setTargetTickDuration(long targetTickDuration) {
        this.targetTickDuration = targetTickDuration;
    }

    public void stop() {
        // Tell the running thread to stop and let it die naturally
        running = false;
    }
}
