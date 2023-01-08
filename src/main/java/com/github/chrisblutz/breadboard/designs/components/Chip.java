package com.github.chrisblutz.breadboard.designs.components;

import com.github.chrisblutz.breadboard.components.ChipTemplate;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chip implements BreadboardSavable {

    public int x;
    public int y;

    public ChipTemplate chipTemplate;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ChipTemplate getChipTemplate() {
        return chipTemplate;
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
