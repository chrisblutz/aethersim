package com.github.chrisblutz.breadboard.simulation.workers;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.simulation.components.BuiltinChip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WorkerScheduler {

    private static final ExecutorService simulationThreadPool = Executors.newFixedThreadPool(8);
    public static List<BuiltinChip> builtinChips = new ArrayList<>();

    private static final List<Future<?>> currentWorkerFutures = new ArrayList<>();
    private static final List<Worker> currentQueuedWorkers = new ArrayList<>();
    private static final List<Worker> currentTickWorkers = new ArrayList<>();

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

        List<Future<Worker[]>> chipResults = new ArrayList<>();

        // For each builtin chip, add it to the thread pool
        for (BuiltinChip builtinChip : builtinChips)
            chipResults.add(simulationThreadPool.submit(builtinChip));

        // For each future we generated above, queue the returned workers
        for (Future<Worker[]> result : chipResults) {
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
        for (Future<?> result : currentWorkerFutures) {
            try {
                result.get();
                count++;
            } catch (ExecutionException|InterruptedException e) {
                throw new RuntimeException(e); // TODO
            }
        }

        // Clear the current tick workers and futures
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
}
