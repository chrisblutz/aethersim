package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.Vertex;
import com.github.chrisblutz.breadboard.utils.Direction;

public interface WireRoutable {

    Vertex getLocation();

    Direction getPreferredWireDirection();
}
