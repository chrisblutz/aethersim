package com.aethersim.designs.wires;

import com.aethersim.designs.Point;
import com.aethersim.projects.io.data.DataSerializable;
import com.aethersim.utils.Direction;

public interface WireRoutable extends DataSerializable {

    Point getLocation();

    Direction getPreferredWireDirection();
}
