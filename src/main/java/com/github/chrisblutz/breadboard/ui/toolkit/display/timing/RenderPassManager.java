package com.github.chrisblutz.breadboard.ui.toolkit.display.timing;

public class RenderPassManager {

    private long targetFrameDuration;
    private final Runnable renderPassRunnable;
    private final Runnable renderRunnable;
    private Thread renderPassThread;

    private volatile boolean running;
    private volatile long currentCycleStartTime;

    public RenderPassManager(long targetFrameDuration, Runnable renderRunnable) {
        this.targetFrameDuration = targetFrameDuration;
        this.renderRunnable = renderRunnable;
        // Initialize the runnable that will be used in all threads created for the loop
        this.renderPassRunnable = () -> {
            // Set the initial cycle start time for this tick
            currentCycleStartTime = System.currentTimeMillis();
            // While the worker loop is running, continue
            while (running) {
                // Call the scheduler tick method
                RenderPassManager.this.renderRunnable.run();
                // Synchronize the tick time
                synchronize();
            }
        };
    }

    private synchronized void synchronize() {
        // Calculate the target start time of the next frame
        currentCycleStartTime = currentCycleStartTime + targetFrameDuration;
        // Determine the delay (if any) that is necessary to hit the target frame time
        long timeToDelay = currentCycleStartTime - System.currentTimeMillis();
        // Sleep the thread for that duration, unless it is 0 or below
        if (timeToDelay > 0){
            try {
                Thread.sleep(timeToDelay);
            } catch (InterruptedException e) { /* do nothing */ }
        }
    }

    public void start() {
        // Initialize thread
        renderPassThread = new Thread(renderPassRunnable, "Render Manager");
        // Start the thread
        running = true;
        renderPassThread.start();
    }

    public void stop() {
        // Tell the running thread to stop and let it die naturally
        running = false;
    }
}
