package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.wires.WireRoutable;
import com.github.chrisblutz.breadboard.utils.Direction;

public record ChipPin(Chip chip, Pin pin) implements WireRoutable {

    @Override
    public Vertex getLocation() {
        // Calculate the design-space location of this chip pin
        if (chip == null)
            return pin.getDesignLocation();
        else
            return chip.getLocation().withOffset(pin.getChipLocation());
    }

    @Override
    public Direction getPreferredWireDirection() {
        if (chip == null)
            return pin.getDesignOrientation();
        else
            return pin.getChipOrientation();
    }
}
