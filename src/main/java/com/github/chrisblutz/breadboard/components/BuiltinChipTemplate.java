package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;
import com.github.chrisblutz.breadboard.simulation.components.Node;
import com.github.chrisblutz.breadboard.simulation.workers.Worker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BuiltinChipTemplate extends ChipTemplate {

    @Override
    protected Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {
    }
}
