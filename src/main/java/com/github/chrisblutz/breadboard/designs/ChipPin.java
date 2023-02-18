package com.github.chrisblutz.breadboard.designs;

import com.github.chrisblutz.breadboard.designs.wires.WireRoutable;
import com.github.chrisblutz.breadboard.utils.Direction;

import java.util.Objects;

public class ChipPin extends DesignElement implements WireRoutable {

    private final Chip chip;
    private final Pin pin;

    public ChipPin(Chip chip, Pin pin) {
        this.chip = chip;
        this.pin = pin;
    }

    @Override
    public Point getLocation() {
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

    public Chip getChip() {
        return chip;
    }

    public Pin getPin() {
        return pin;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ChipPin) obj;
        return Objects.equals(this.chip, that.chip) &&
                Objects.equals(this.pin, that.pin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chip, pin);
    }

    @Override
    public String toString() {
        return "ChipPin[" +
                "chip=" + chip + ", " +
                "pin=" + pin + ']';
    }

    @Override
    protected void onTransformUpdated() {

    }

    @Override
    protected void onTransformAccepted() {

    }

    @Override
    public boolean contains(Point point) {
        return getLocation().equals(point);
    }
}
