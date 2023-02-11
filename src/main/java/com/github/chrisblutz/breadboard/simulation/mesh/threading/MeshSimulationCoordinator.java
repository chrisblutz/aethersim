package com.github.chrisblutz.breadboard.simulation.mesh.threading;

import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshChip;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshConnector;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshVertex;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.generation.MeshSimulationConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MeshSimulationCoordinator {

    private ExecutorService threadPool = null;

    private final List<Future<?>> meshProcessorFutures = new ArrayList<>();

    private final List<MeshStateProcessor> queuedMeshStateProcessors = new ArrayList<>();

    private MeshSimulationConfig simulationConfig;

    public void start() {
        // If the thread pool needs to be initialized, initialize it
        if (threadPool == null)
            threadPool = Executors.newFixedThreadPool(8); // TODO
    }

    public void configure(MeshSimulationConfig simulationConfig) {
        this.simulationConfig = simulationConfig;
    }

    public void stop() {
        threadPool.shutdown();
        threadPool = null;
    }

    public void reset() {
        // When resetting, reset all simulation components
        if (simulationConfig == null)
            return;

        // Reset all mesh connectors back to their default states
        for (MeshConnector connector : simulationConfig.getMeshConnectors())
            connector.reset();
    }

    public synchronized void queue(MeshStateProcessor processor) {
        // Add the processor to the queued list of processors
        queuedMeshStateProcessors.add(processor);
    }

    public synchronized void queueNow(Runnable runnable) {
        // Submit the runnable to the thread pool
        meshProcessorFutures.add(threadPool.submit(runnable));
    }

    public void tick() {
        // If the simulation configuration is null, we have nothing to simulate
        if (simulationConfig == null)
            return;

        // To start, queue all mesh chips and connectors for updates
        for (final MeshChip<?> chip : simulationConfig.getMeshChips())
            queueNow(() -> chip.tick(MeshSimulationCoordinator.this));

        for (MeshConnector connector : simulationConfig.getMeshConnectors())
            queueNow(connector::tick);

        // Wait for all currently-executing workers to finish
        waitForTaskCompletion();

        // Next, submit all queued mesh state processors
        for (MeshStateProcessor processor : queuedMeshStateProcessors)
            queueNow(processor);
        queuedMeshStateProcessors.clear();

        // Wait for all currently-executing workers to finish
        waitForTaskCompletion();

        // Finally, queue rectifiers for all vertices
        for (MeshVertex vertex : simulationConfig.getMeshVertices())
            queueNow(vertex::rectifyStates);

        // Wait for all currently-executing workers to finish
        waitForTaskCompletion();
    }

    private void waitForTaskCompletion() {
        // Wait for all currently-executing workers to finish
        // Enhanced for-loops aren't used here to avoid concurrency issues (where futures potentially
        // get added to the end of the list while we're still reading through it)
        for (int index = 0; index < meshProcessorFutures.size(); index++) {
            try {
                meshProcessorFutures.get(index).get();
            } catch (InterruptedException | ExecutionException e) {
                // TODO
                throw new RuntimeException(e);
            }
        }
        // Clear futures from list
        meshProcessorFutures.clear();
    }
}
