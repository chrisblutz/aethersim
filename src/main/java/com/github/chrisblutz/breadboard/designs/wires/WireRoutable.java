package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.Point;
import com.github.chrisblutz.breadboard.utils.Direction;

public interface WireRoutable {

    Point getLocation();

    Direction getPreferredWireDirection();
}
