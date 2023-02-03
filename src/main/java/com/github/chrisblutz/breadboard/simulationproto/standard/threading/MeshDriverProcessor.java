package com.github.chrisblutz.breadboard.simulationproto.standard.threading;

import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshDriver;

public class MeshDriverProcessor implements Runnable {

    private final MeshSimulationCoordinator coordinator;
    private final MeshDriver driver;

    public MeshDriverProcessor(MeshSimulationCoordinator coordinator, MeshDriver driver) {
        this.coordinator = coordinator;
        this.driver = driver;
    }

    @Override
    public void run() {
        // Queue the necessary vertex updates if necessary (should be necessary most of the time)
        if (driver.getVertex().compareStates(driver.getDrivenActualState(), driver.getDrivenSuggestedState()))
            coordinator.queue(new MeshStateProcessor(coordinator, driver.getVertex(), driver.getDrivenActualState(), driver.getDrivenSuggestedState()));
    }
}
