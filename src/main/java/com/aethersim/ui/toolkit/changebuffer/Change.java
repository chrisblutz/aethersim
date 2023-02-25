package com.aethersim.ui.toolkit.changebuffer;

public interface Change {

    void doChange();

    void undoChange();
}
