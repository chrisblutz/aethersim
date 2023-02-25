package com.aethersim.simulation;

import com.aethersim.designs.Design;
import com.aethersim.simulation.SimulatedDesign;
import com.aethersim.simulation.Simulation;
import com.aethersim.simulation.Simulator;
import com.aethersim.tests.annotations.AetherSimTest;
import com.aethersim.tests.annotations.AetherSimTests;
import com.aethersim.tests.annotations.SimulationTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.CountDownLatch;

@SimulationTests
@AetherSimTests("Timing and Synchronization")
@Execution(ExecutionMode.SAME_THREAD)
public class TestSimulation {

    @AetherSimTest("Non-Concurrency Check")
    @Timeout(value = 10)
    void testNonConcurrency() {
        final int[] tickCount = {0};
        final CountDownLatch simLatch = new CountDownLatch(1);
        Simulator testSimulator = new Simulator() {
            @Override
            public SimulatedDesign initialize(Design design) {
                return SimulatedDesign.none();
            }

            @Override
            public void start() {}

            @Override
            public void stop() {}

            @Override
            public void tick() {
                tickCount[0]++;
                // Sleep, and then stop the simulation
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Simulation.stop();
                // Notify the test that the simulator has stopped
                simLatch.countDown();
            }

            @Override
            public void reset() {}
        };
        Simulation.setSimulator(testSimulator);

        // Verify that after starting the simulator (which automatically stops itself),
        // that the simulator has only ticked once (no concurrent runs while the test simulator
        // is sleeping)
        Simulation.start();

        // Wait for the simulator to stop before checking
        while (Simulation.isRunning()) {
            try {
                simLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Assertions.assertEquals(1, tickCount[0]);
    }
}
