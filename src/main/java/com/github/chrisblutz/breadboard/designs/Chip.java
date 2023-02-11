package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.templates.ChipTemplate;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.Map;

public class Chip implements BreadboardSavable {

    private int x;
    private int y;

    private ChipTemplate chipTemplate;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ChipTemplate getChipTemplate() {
        return chipTemplate;
    }

    public void setChipTemplate(ChipTemplate chipTemplate) {
        this.chipTemplate = chipTemplate;
    }

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
