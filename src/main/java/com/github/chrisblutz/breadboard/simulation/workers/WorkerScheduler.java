package com.github.chrisblutz.breadboard.simulation.workers;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.simulation.components.BuiltinChip;
import com.github.chrisblutz.breadboard.simulation.generator.DesignInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class WorkerScheduler {

    private static final ExecutorService simulationThreadPool = Executors.newFixedThreadPool(8);
    public static List<BuiltinChip> builtinChips = new ArrayList<>();

    private static final List<Future<Worker[]>> currentChipWorkerFutures = new ArrayList<>();
    private static final List<Future<?>> currentWorkerFutures = new ArrayList<>();
    private static final List<Worker> currentQueuedWorkers = new ArrayList<>();
    private static final List<Worker> currentTickWorkers = new ArrayList<>();

    private static WorkerLoop workerLoop = new WorkerLoop(1000);
    private static DesignInstance topLevelDesignInstance = null;

    public static void registerBuiltinChip(BuiltinChip builtinChip) {
        builtinChips.add(builtinChip);
    }

    public static void queue(Worker worker) {
        currentQueuedWorkers.add(worker);
    }

    public static void queueAll(Collection<Worker> workers) {
        currentQueuedWorkers.addAll(workers);
    }

    public static void queueNow(Worker worker) {
        currentWorkerFutures.add(simulationThreadPool.submit(worker));
    }

    public static long tick() {
        BreadboardLogging.getSimulationLogger().debug("Beginning simulation tick...");

        long startTimeMillis = System.currentTimeMillis();

        // For each builtin chip, add it to the thread pool
        for (BuiltinChip builtinChip : builtinChips)
            currentChipWorkerFutures.add(simulationThreadPool.submit(builtinChip));

        // For each future we generated above, queue the returned workers
        for (int index = 0; index < currentChipWorkerFutures.size(); index++) {
            Future<Worker[]> result = currentChipWorkerFutures.get(index);
            try {
                queueAll(Arrays.asList(result.get()));
            } catch (ExecutionException|InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Calculate and log time information for the built-in chips
        long builtinChipEndTimeMillis = System.currentTimeMillis();
        long workerStartTime = System.currentTimeMillis();

        // Transfer all queued workers to the current worker list
        currentTickWorkers.addAll(currentQueuedWorkers);
        currentQueuedWorkers.clear();

        // For each worker we need to run, add it to the thread pool
        for (Worker worker : currentTickWorkers)
            currentWorkerFutures.add(simulationThreadPool.submit(worker));

        // For each future we generated above, wait for it to complete
        // Count workers as we go
        int count = 0;
        for (int index = 0; index < currentWorkerFutures.size(); index++) {
            Future<?> result = currentWorkerFutures.get(index);
            try {
                result.get();
                count++;
            } catch (ExecutionException|InterruptedException e) {
                throw new RuntimeException(e); // TODO
            }
        }

        // Clear the current tick workers and futures
        currentChipWorkerFutures.clear();
        currentTickWorkers.clear();
        currentWorkerFutures.clear();

        long endTimeMillis = System.currentTimeMillis();
        long builtinChipTime = builtinChipEndTimeMillis - startTimeMillis;
        long workerTime = endTimeMillis - workerStartTime;
        long totalTime = endTimeMillis - startTimeMillis;

        // Calculate and log time information for workers and the tick as a whole
        BreadboardLogging.getSimulationLogger().debug("Ran " + builtinChips.size() + " built-in chip(s) in " + builtinChipTime  + "ms.");
        BreadboardLogging.getSimulationLogger().debug("Ran " + count + " worker(s) in " + workerTime  + "ms.");
        BreadboardLogging.getSimulationLogger().debug("Simulation tick completed in " + totalTime  + "ms.");

        return totalTime;
    }

    public static DesignInstance getTopLevelDesignInstance() {
        return topLevelDesignInstance;
    }

    public static void setTopLevelDesignInstance(DesignInstance topLevelDesignInstance) {
        WorkerScheduler.topLevelDesignInstance = topLevelDesignInstance;
    }

    public static void startSimulation() {
        workerLoop.start();
    }

    public static void setTargetTicksPerSecond(int ticksPerSecond) {
        // Validate input
        if (ticksPerSecond < 1)
            ticksPerSecond = 1;
        else if (ticksPerSecond > 1000)
            ticksPerSecond = 1000;

        long targetTickDuration = 1000 / ticksPerSecond;

        workerLoop.setTargetTickDuration(targetTickDuration);
    }

    public static void stopSimulation() {
        workerLoop.stop();

        // Clear all active states in the design instance
        if (getTopLevelDesignInstance() != null)
            getTopLevelDesignInstance().clearSimulationStates();
    }
}
