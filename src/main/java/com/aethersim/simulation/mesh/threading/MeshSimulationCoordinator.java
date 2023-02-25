package com.aethersim.simulation.mesh.threading;

import com.aethersim.simulation.mesh.mesh.MeshConnector;
import com.aethersim.simulation.mesh.mesh.MeshFunction;
import com.aethersim.simulation.mesh.mesh.MeshVertex;
import com.aethersim.simulation.mesh.mesh.generation.MeshSimulationConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MeshSimulationCoordinator {

    private ExecutorService threadPool = null;

    private final List<Future<Boolean>> booleanFutures = new ArrayList<>();
    private final List<Future<?>> voidFutures = new ArrayList<>();

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
        voidFutures.add(threadPool.submit(runnable));
    }

    public synchronized void queueNow(Callable<Boolean> callable) {
        // Submit the runnable to the thread pool
        booleanFutures.add(threadPool.submit(callable));
    }

    public void tick() {
        // If the simulation configuration is null, we have nothing to simulate
        if (simulationConfig == null)
            return;

        // To start, queue all mesh chips and connectors for updates
        for (final MeshFunction<?> chip : simulationConfig.getMeshChips())
            queueNow(() -> chip.tick(MeshSimulationCoordinator.this));

        for (MeshConnector connector : simulationConfig.getMeshConnectors())
            queueNow(connector::tick);

        // Wait for all currently-executing workers to finish.  If we don't need to update any vertices
        // (e.g., if nothing changed) then exit early after clearing queued processors
        if(!waitForBooleanTaskCompletion()) {
            queuedMeshStateProcessors.clear();
            return;
        }

        // Next, submit all queued mesh state processors
        for (MeshStateProcessor processor : queuedMeshStateProcessors)
            queueNow(processor);
        queuedMeshStateProcessors.clear();

        // Wait for all currently-executing workers to finish
        waitForVoidTaskCompletion();

        // Finally, queue rectifiers for all vertices
        for (MeshVertex vertex : simulationConfig.getMeshVertices())
            queueNow(vertex::rectifyStates);

        // Wait for all currently-executing workers to finish
        waitForVoidTaskCompletion();
    }

    private boolean waitForBooleanTaskCompletion() {
        boolean result = false;
        // Wait for all currently-executing workers to finish
        // Enhanced for-loops aren't used here to avoid concurrency issues (where futures potentially
        // get added to the end of the list while we're still reading through it)
        for (int index = 0; index < booleanFutures.size(); index++) {
            try {
                if (booleanFutures.get(index).get())
                    result = true;
            } catch (InterruptedException | ExecutionException e) {
                // TODO
                throw new RuntimeException(e);
            }
        }
        // Clear futures from list
        booleanFutures.clear();
        return result;
    }

    private void waitForVoidTaskCompletion() {
        // Wait for all currently-executing workers to finish
        // Enhanced for-loops aren't used here to avoid concurrency issues (where futures potentially
        // get added to the end of the list while we're still reading through it)
        for (int index = 0; index < voidFutures.size(); index++) {
            try {
                voidFutures.get(index).get();
            } catch (InterruptedException | ExecutionException e) {
                // TODO
                throw new RuntimeException(e);
            }
        }
        // Clear futures from list
        voidFutures.clear();
    }
}
