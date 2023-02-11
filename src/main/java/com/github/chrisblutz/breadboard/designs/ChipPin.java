package com.github.chrisblutz.breadboard.designs;

public record ChipPin(Chip chip, Pin pin) {

    public ChipPin withChip(Chip chip) {
        return new ChipPin(chip, pin);
    }
}
