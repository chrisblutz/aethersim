package com.github.chrisblutz.breadboard.simulation.mesh.mesh;

import com.github.chrisblutz.breadboard.simulation.LogicState;

import java.util.Random;

public class MeshConnector {

    private static final int MAXIMUM_RANDOM_DELAY = 3; // TODO

    private final Random random = new Random(hashCode() * System.currentTimeMillis());

    private boolean currentlyConnected;
    private boolean connecting = false;
    public int currentDelay = -1;

    private final MeshVertex decider;
    private final boolean activeLow;

    public MeshConnector(MeshVertex decider, boolean activeLow) {
        this.decider = decider;
        this.activeLow = activeLow;
        this.currentlyConnected = activeLow;
        this.connecting = activeLow;
    }

    public boolean isConnected() {
        return currentlyConnected;
    }

    public void reset() {
        // Reset this connector to its default state
        this.currentlyConnected = activeLow;
        this.connecting = activeLow;
        this.currentDelay = -1;
    }

    public void tick() {
        boolean deciderActive = decider.getActualState() == (activeLow ? LogicState.LOW : LogicState.HIGH);

        // If the state has changed, reset the delay.  Otherwise, if the delay is still counting, handle this tick.
        if (connecting != deciderActive) {
            connecting = deciderActive;
            currentDelay = random.nextInt(MAXIMUM_RANDOM_DELAY) + 1;
        } else if (currentDelay >= 0){
            // If the current delay is 0, we've reached the end of the delay, so set the status
            if (currentDelay == 0)
                currentlyConnected = connecting;

            // Decrement the delay
            currentDelay--;
        }
    }
}