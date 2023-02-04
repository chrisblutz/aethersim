package com.github.chrisblutz.breadboard.simulationproto.standard.threading;

import com.github.chrisblutz.breadboard.simulationproto.timing.TickTrigger;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshConnector;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshDriver;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshVertex;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.generation.MeshSimulationConfig;

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

    public synchronized void queueNow(MeshDriverProcessor processor) {
        // Submit the processor to the thread pool
        meshProcessorFutures.add(threadPool.submit(processor));
    }

    public synchronized void queueNow(MeshConnectorProcessor processor) {
        // Submit the processor to the thread pool
        meshProcessorFutures.add(threadPool.submit(processor));
    }

    public synchronized void queue(MeshStateProcessor processor) {
        // Add the processor to the queued list of processors
        queuedMeshStateProcessors.add(processor);
    }

    public synchronized void queueNow(MeshStateProcessor processor) {
        // Submit the processor to the thread pool
        meshProcessorFutures.add(threadPool.submit(processor));
    }

    public synchronized void queueNow(MeshRectifierProcessor processor) {
        // Submit the processor to the thread pool
        meshProcessorFutures.add(threadPool.submit(processor));
    }

    public void tick(TickTrigger trigger) {
        // If the simulation configuration is null, we have nothing to simulate
        if (simulationConfig == null)
            return;

        // To start, queue all mesh drivers and connectors for updates
        for (MeshDriver driver : simulationConfig.getMeshDrivers())
            queueNow(new MeshDriverProcessor(this, driver));
        // If we're processing a manual tick, skip the connectors
        if (trigger != TickTrigger.MANUAL)
            for (MeshConnector connector : simulationConfig.getMeshConnectors())
                queueNow(new MeshConnectorProcessor(this, connector));

        // Wait for all currently-executing workers to finish
        for (int index = 0; index < meshProcessorFutures.size(); index++) {
            try {
                meshProcessorFutures.get(index).get();
            } catch (InterruptedException | ExecutionException e) {
                // TODO
                throw new RuntimeException(e);
            }
        }
        meshProcessorFutures.clear();

        // Next, submit all queued mesh state processors
        for (MeshStateProcessor processor : queuedMeshStateProcessors)
            queueNow(processor);
        queuedMeshStateProcessors.clear();

        // Wait for all currently-executing workers to finish
        for (int index = 0; index < meshProcessorFutures.size(); index++) {
            try {
                meshProcessorFutures.get(index).get();
            } catch (InterruptedException | ExecutionException e) {
                // TODO
                throw new RuntimeException(e);
            }
        }
        meshProcessorFutures.clear();

        // Finally, queue rectifiers for all vertices
        for (MeshVertex vertex : simulationConfig.getMeshVertices())
            queueNow(new MeshRectifierProcessor(this, vertex));

        // Wait for all currently-executing workers to finish
        for (int index = 0; index < meshProcessorFutures.size(); index++) {
            try {
                meshProcessorFutures.get(index).get();
            } catch (InterruptedException | ExecutionException e) {
                // TODO
                throw new RuntimeException(e);
            }
        }
        meshProcessorFutures.clear();
    }
}
