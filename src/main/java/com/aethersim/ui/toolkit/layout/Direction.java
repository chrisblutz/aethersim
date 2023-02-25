package com.aethersim.ui.toolkit.layout;

/**
 * A {@code Direction} represents one of two directions in which UI elements can order themselves.  It is up to the
 * individual component how the layouts are implemented.
 */
public enum Direction {
    /**
     * Represents the horizontal direction for component layout.  For containers, this should represent components in a
     * side-to-side (either left-to-right or right-to-left) layout.  For components, this should represent the
     * orientation a component would take in a container with the same direction.
     */
    HORIZONTAL,
    /**
     * Represents the vertical direction for component layout.  For containers, this should represent components in
     * either a top-to-bottom or a bottom-to-top layout.  For components, this should represent the
     * orientation a component would take in a container with the same direction.
     */
    VERTICAL
}
