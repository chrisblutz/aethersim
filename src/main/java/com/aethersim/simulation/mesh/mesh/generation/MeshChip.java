package com.aethersim.simulation.mesh.mesh.generation;

import com.aethersim.designs.Chip;

import java.util.Arrays;

public class MeshChip {

    private final Chip[] chipAncestors;

    public MeshChip(Chip chip, Chip... ancestors) {
        if (chip != null)
            chipAncestors = new Chip[ancestors.length + 1];
        else
            chipAncestors = new Chip[ancestors.length];
        System.arraycopy(ancestors, 0, chipAncestors, 0, ancestors.length);
        if (chip != null)
            chipAncestors[ancestors.length] = chip;
    }

    public Chip[] getChipAncestors() {
        return chipAncestors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeshChip meshChip = (MeshChip) o;
        return Arrays.equals(chipAncestors, meshChip.chipAncestors);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(chipAncestors);
    }
}
