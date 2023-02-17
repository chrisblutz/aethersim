package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.ChipPin;
import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.Vertex;
import com.github.chrisblutz.breadboard.designs.exceptions.DesignException;

import java.util.*;

public class WireSegment {

    private final Design design;
    private final WireRoutable start, end;

    private Vertex[] routeWaypoints;
    private Vertex[] routeVertices;

    public WireSegment(Design design, WireRoutable start, WireRoutable end, Vertex... routeWaypoints) {
        this.design = design;
        this.start = start;
        this.end = end;
        this.routeWaypoints = routeWaypoints;
        reroute();
    }

    public WireRoutable getStart() {
        return start;
    }

    public WireRoutable getEnd() {
        return end;
    }

    public Vertex[] getRouteWaypoints() {
        return routeWaypoints;
    }

    public void setRouteWaypoints(Vertex[] routeWaypoints) {
        this.routeWaypoints = routeWaypoints;
        reroute();
    }

    public Vertex[] getRouteVertices() {
        return routeVertices;
    }

    public void reroute() {
        // Calculate the route vertices using the wire router
        routeVertices = WireRouter.route(
                design,
                getStart().getLocation(),
                getStart().getPreferredWireDirection(),
                getEnd().getLocation(),
                getEnd().getPreferredWireDirection(),
                getRouteWaypoints()
        );
    }
}
