package com.github.chrisblutz.breadboard.simulation.mesh.mesh.generation;

import com.github.chrisblutz.breadboard.designs.Chip;
import com.github.chrisblutz.breadboard.designs.Pin;

import java.util.Arrays;
import java.util.Objects;

public class MeshPin {

    private final Chip[] chipAncestors;
    private final Pin pin;

    public MeshPin(Pin pin, Chip chip, Chip... ancestors) {
        if (chip != null)
            chipAncestors = new Chip[ancestors.length + 1];
        else
            chipAncestors = new Chip[ancestors.length];
        System.arraycopy(ancestors, 0, chipAncestors, 0, ancestors.length);
        if (chip != null)
            chipAncestors[ancestors.length] = chip;
        this.pin = pin;
    }

    public Chip[] getChipAncestors() {
        return chipAncestors;
    }

    public Pin getPin() {
        return pin;
    }

    @Override
    public String toString() {
        return "MeshPin{" +
                "chipAncestors=" + Arrays.toString(chipAncestors) +
                ", pin=" + pin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeshPin meshPin = (MeshPin) o;
        return Arrays.equals(chipAncestors, meshPin.chipAncestors) && Objects.equals(pin, meshPin.pin);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(pin);
        result = 31 * result + Arrays.hashCode(chipAncestors);
        return result;
    }
}
