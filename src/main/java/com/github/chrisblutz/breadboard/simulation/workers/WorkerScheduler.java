package com.github.chrisblutz.breadboard.simulation.workers;

import com.github.chrisblutz.breadboard.simulation.components.BuiltinChip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class WorkerScheduler {

    private static final ExecutorService simulationThreadPool = Executors.newFixedThreadPool(8);
    public static List<Worker> nextTickWorkers = new ArrayList<>();
    public static List<BuiltinChip> builtinChips = new ArrayList<>();

    public static long tick() {
        long startTimeMillis = System.currentTimeMillis();

        List<Future<Worker[]>> results = new ArrayList<>(nextTickWorkers.size());

        // For each builtin chip, add it to the thread pool
        for (BuiltinChip builtinChip : builtinChips)
            results.add(simulationThreadPool.submit(builtinChip));

        // For each future we generated above, add the continuing workers to the list
        for (Future<Worker[]> result : results) {
            try {
                nextTickWorkers.addAll(Arrays.asList(result.get()));
            } catch (ExecutionException|InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        results.clear();

        // For each worker we need to run, add it to the thread pool
        for (Worker worker : nextTickWorkers)
            results.add(simulationThreadPool.submit(worker));

        // For each future we generated above, add the continuing workers to the list
        List<Worker> continuingWorkers = new ArrayList<>(nextTickWorkers.size());
        for (Future<Worker[]> result : results) {
            try {
                continuingWorkers.addAll(Arrays.asList(result.get()));
            } catch (ExecutionException|InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Set the next tick workers to the continuing workers derived above
        nextTickWorkers = continuingWorkers;

        long endTimeMillis = System.currentTimeMillis();

        return endTimeMillis - startTimeMillis;
    }
}
