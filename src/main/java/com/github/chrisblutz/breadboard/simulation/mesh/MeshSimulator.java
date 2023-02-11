package com.github.chrisblutz.breadboard.simulation.mesh;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.simulation.SimulatedDesign;
import com.github.chrisblutz.breadboard.simulation.Simulator;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.generation.MeshGenerator;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.generation.MeshSimulationConfig;
import com.github.chrisblutz.breadboard.simulation.mesh.threading.MeshSimulationCoordinator;

public class MeshSimulator extends Simulator {

    private final MeshSimulationCoordinator coordinator = new MeshSimulationCoordinator();

    @Override
    public SimulatedDesign initialize(Design design) {
        // Generate the simulation config for the design
        MeshSimulationConfig simulationConfig = MeshGenerator.generate(design);
        // Configure the coordinator with the new configuration
        coordinator.configure(simulationConfig);
        // Return the generated simulated design
        return simulationConfig.getTopLevelSimulatedDesign();
    }

    @Override
    public void start() {
        // Start the coordinator
        coordinator.start();
    }

    @Override
    public void stop() {
        // Stop the coordinator
        coordinator.stop();
    }

    @Override
    public void tick() {
        // Tick the coordinator
        coordinator.tick();
    }

    @Override
    public void reset() {
        // TODO
        coordinator.reset();
    }
}
