package com.github.chrisblutz.breadboard.ui.toolkit.changebuffer;

public interface Change {

    void doChange();

    void undoChange();
}
