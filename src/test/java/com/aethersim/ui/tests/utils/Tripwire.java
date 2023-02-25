package com.aethersim.ui.tests.utils;

public class Tripwire<T> {

    private boolean tripped = false;
    private final T returnValue;

    public Tripwire(T returnValue) {
        this.returnValue = returnValue;
    }

    public T set() {
        tripped = true;
        return returnValue;
    }

    public boolean isTripped() {
        return tripped;
    }
}
