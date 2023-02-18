package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.DesignElement;
import com.github.chrisblutz.breadboard.designs.Point;

import java.util.Arrays;

public class WireSegment {

    private final Design design;
    private final WireRoutable start, end;

    private WireWaypoint[] routeWaypoints;
    private Point[] routePoints;

    public WireSegment(Design design, WireRoutable start, WireRoutable end, WireWaypoint... routeWaypoints) {
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

    public WireWaypoint[] getRouteWaypoints() {
        return routeWaypoints;
    }

    public void setRouteWaypoints(WireWaypoint[] routeWaypoints) {
        this.routeWaypoints = routeWaypoints;
        reroute();
    }

    public Point[] getRoutePoints() {
        return routePoints;
    }

    public void reroute() {
        // Calculate the route points using the wire router
        routePoints = WireRouter.route(
                design,
                getStart().getLocation(),
                getStart().getPreferredWireDirection(),
                getEnd().getLocation(),
                getEnd().getPreferredWireDirection(),
                Arrays.stream(getRouteWaypoints())
                        .map(WireWaypoint::getLocation)
                        .toArray(Point[]::new)
        );
    }
}
