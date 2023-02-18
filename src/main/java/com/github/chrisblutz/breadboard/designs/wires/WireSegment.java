package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.DesignElement;
import com.github.chrisblutz.breadboard.designs.Point;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WireSegment extends DesignElement {

    private final Design design;
    private WireRoutable start, end;
    private final WireRoutable[] endpoints;

    private WireWaypoint[] routeWaypoints;
    private Point[] routePoints;

    public WireSegment(Design design, WireRoutable start, WireRoutable end, WireWaypoint... routeWaypoints) {
        this.design = design;
        this.start = start;
        this.end = end;
        this.endpoints = new WireRoutable[] {start, end};
        this.routeWaypoints = routeWaypoints;
        for (WireWaypoint waypoint : this.routeWaypoints)
            waypoint.attach(this);
        reroute();
    }

    public WireRoutable getStart() {
        return start;
    }

    public void setStart(WireRoutable start) {
        this.start = start;
        this.endpoints[0] = start;
    }

    public WireRoutable getEnd() {
        return end;
    }

    public void setEnd(WireRoutable end) {
        this.end = end;
        this.endpoints[1] = end;
    }

    public WireRoutable[] getEndpoints() {
        return endpoints;
    }

    public WireWaypoint[] getRouteWaypoints() {
        return routeWaypoints;
    }

    public void setRouteWaypoints(WireWaypoint[] routeWaypoints) {
        this.routeWaypoints = routeWaypoints;
        for (WireWaypoint waypoint : routeWaypoints)
            waypoint.attach(this);
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

    public WireSegment splitAt(Point point) {
        // First, determine if this new point is already a waypoint (if it is, we don't want to copy that waypoint)
        int waypointIndex = Arrays.stream(routeWaypoints)
                .map(WireWaypoint::getLocation)
                .toList()
                .indexOf(point);
        boolean waypointExists = waypointIndex >= 0;

        // If the waypoint doesn't exist, we need to determine where to split the waypoint array
        if (!waypointExists) {
            waypointIndex = 0;
            for (int segmentIndex = 0; segmentIndex < routePoints.length - 1 && waypointIndex < routeWaypoints.length; segmentIndex++) {
                Point start = routePoints[segmentIndex];
                Point end = routePoints[segmentIndex + 1];
                boolean waypointHere = routeWaypoints[waypointIndex].getLocation().isBetween(start, end);
                boolean pointHere = point.isBetween(start, end);
                // If the waypoint is here but we haven't reached the point yet, increment the waypoint counter (and
                // decrement the segment index to check this segment again)
                if (waypointHere && !pointHere) {
                    waypointIndex++;
                    segmentIndex--;
                } else if (!waypointHere && pointHere) {
                    break;
                } else {
                    // In this case, both the split point and waypoint are on this segment.  We need to determine
                    // which is first
                    int segmentXDiff = end.getX() - start.getX();
                    int segmentYDiff = end.getY() - start.getY();
                    int waypointXDiff = routeWaypoints[waypointIndex].getLocation().getX() - point.getX();
                    int waypointYDiff = routeWaypoints[waypointIndex].getLocation().getY() - point.getY();
                    // If the X's are the same and both have the same sign, then the waypoint is after the split point
                    // and vice versa if the Y's are the same.  If the signs aren't the same, we need to increment
                    // the waypoint index, as the current waypoint is before the split point
                    if ((segmentXDiff == 0 && (segmentYDiff * waypointYDiff < 0))
                            || (segmentYDiff == 0 && (segmentXDiff * waypointXDiff < 0)))
                        waypointIndex++;
                }
            }
        }

        // Split the waypoints for the wires
        WireWaypoint[] thisWaypoints = new WireWaypoint[waypointIndex];
        System.arraycopy(routeWaypoints, 0, thisWaypoints, 0, waypointIndex);
        int otherWaypointCount = routeWaypoints.length - waypointIndex - (waypointExists ? 1 : 0);
        WireWaypoint[] otherWaypoints = new WireWaypoint[otherWaypointCount];
        System.arraycopy(routeWaypoints, waypointIndex + (waypointExists ? 1 : 0), otherWaypoints, 0, otherWaypointCount);

        WireNode newSplitNode = new WireNode(point);

        // Create a new wire segment
        WireSegment otherSegment = new WireSegment(design, newSplitNode, getEnd(), otherWaypoints);
        setEnd(newSplitNode);
        setRouteWaypoints(thisWaypoints);

        return otherSegment;
    }

    @Override
    protected void onTransformUpdated() {

    }

    @Override
    protected void onTransformAccepted() {

    }

    @Override
    public boolean contains(Point point) {
        // Check all vertex->vertex segments to see if the point lies between them
        for (int index = 0; index < routePoints.length - 1; index++) {
            Point start = routePoints[index];
            Point end = routePoints[index + 1];
            if (point.isBetween(start, end))
                return true;
        }

        return false;
    }
}
