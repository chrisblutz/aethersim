package com.github.chrisblutz.breadboard.simulationproto.standard.mesh;

import com.github.chrisblutz.breadboard.simulationproto.LogicState;

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
    }

    public boolean isConnected() {
        return currentlyConnected;
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
