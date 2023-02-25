package com.aethersim.simulation.mesh.mesh;

import com.aethersim.designs.Chip;
import com.aethersim.designs.Pin;
import com.aethersim.designs.templates.SimulatedTemplate;
import com.aethersim.simulation.ChipState;
import com.aethersim.simulation.LogicState;
import com.aethersim.simulation.mesh.threading.MeshSimulationCoordinator;
import com.aethersim.simulation.mesh.threading.MeshStateProcessor;

import java.util.HashMap;
import java.util.Map;

public class MeshFunction<T extends ChipState> {

    private final Chip chip;
    private final SimulatedTemplate<T> simulatedTemplate;

    private final Map<Pin, MeshVertex> inputVertices = new HashMap<>();
    private final Map<Pin, MeshVertex> outputVertices = new HashMap<>();

    public MeshFunction(Chip chip, SimulatedTemplate<T> simulatedTemplate) {
        this.chip = chip;
        this.simulatedTemplate = simulatedTemplate;
    }

    public Map<Pin, MeshVertex> getInputVertices() {
        return inputVertices;
    }

    public Map<Pin, MeshVertex> getOutputVertices() {
        return outputVertices;
    }

    public boolean tick(MeshSimulationCoordinator coordinator) {
        // Get the chip state for the current chip
        T state = simulatedTemplate.getState(chip);
        // For each input pin, set its value based on the state of the vertex
        for (Pin input : simulatedTemplate.getInputPins()) {
            MeshVertex pinVertex = inputVertices.get(input);
            state.setDrivenInputState(input, pinVertex.getActualState());
            state.setPulledInputState(input, pinVertex.getSuggestedState());
        }

        // Pass the state to the template simulate() method to perform the computation
        simulatedTemplate.simulate(state);

        // Queue simulated updated states based on the output pins
        boolean propagateChanges = false;
        for (Pin output : simulatedTemplate.getOutputPins()) {
            MeshVertex pinVertex = outputVertices.get(output);
            LogicState actualState = state.getDrivenOutputState(output);
            LogicState suggestedState = state.getPulledOutputState(output);
            // If the states are different, queue an update
            if (pinVertex.compareStates(actualState, suggestedState)) {
                coordinator.queue(
                        new MeshStateProcessor(
                                coordinator,
                                pinVertex,
                                actualState,
                                suggestedState
                        )
                );
                propagateChanges = true;
            }
        }

        return propagateChanges;
    }
}
