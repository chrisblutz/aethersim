package com.github.chrisblutz.breadboard.simulationproto.standard;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.simulationproto.SimulatedDesign;
import com.github.chrisblutz.breadboard.simulationproto.Simulator;
import com.github.chrisblutz.breadboard.simulationproto.timing.TickTrigger;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.generation.MeshGenerator;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.generation.MeshSimulationConfig;
import com.github.chrisblutz.breadboard.simulationproto.standard.threading.MeshSimulationCoordinator;

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
    public void tick(TickTrigger trigger) {
        // Tick the coordinator
        coordinator.tick(trigger);
    }

    @Override
    public void reset() {
        // TODO
        coordinator.reset();
    }
}
