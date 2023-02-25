package com.aethersim.designs.wires;

import com.aethersim.designs.ChipPin;
import com.aethersim.designs.Design;
import com.aethersim.designs.DesignElement;
import com.aethersim.designs.Point;
import com.aethersim.projects.io.data.DataArray;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WireSegment extends DesignElement {

    private final Design design;
    private WireRoutable start, end;
    private final List<WireRoutable> endpoints;

    private WireWaypoint[] routeWaypoints;
    private Point[] routePoints;

    public WireSegment(Design design, WireRoutable start, WireRoutable end, WireWaypoint... routeWaypoints) {
        this.design = design;
        this.start = start;
        this.end = end;
        this.endpoints = Arrays.asList(start, end);
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
        endpoints.set(0, start);
    }

    public WireRoutable getEnd() {
        return end;
    }

    public void setEnd(WireRoutable end) {
        this.end = end;
        endpoints.set(1, end);
    }

    public List<WireRoutable> getEndpoints() {
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

    public WireNode splitAt(Point point) {
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
        design.addWireNode(newSplitNode);

        // Create a new wire segment
        WireSegment otherSegment = new WireSegment(design, newSplitNode, getEnd(), otherWaypoints);
        design.addWireSegment(otherSegment);

        // Update the endpoint and waypoints of the current wire segment
        setEnd(newSplitNode);
        setRouteWaypoints(thisWaypoints);

        // Return the newly-created node
        return newSplitNode;
    }

    public void mergeAt(WireSegment other, WireNode mergeNode) {
        // Determine if we're merging at the start or end of this node
        boolean thisMergeAtStart = getStart().equals(mergeNode);
        boolean otherMergeAtStart = other.getStart().equals(mergeNode);

        // Merge the waypoints and add the merge node as a waypoint in the middle
        List<WireWaypoint> newWaypoints = new ArrayList<>(routeWaypoints.length + other.routeWaypoints.length + 1);
        newWaypoints.addAll(Arrays.asList(routeWaypoints));

        List<WireWaypoint> otherWaypoints = Arrays.asList(other.routeWaypoints);
        if (thisMergeAtStart == otherMergeAtStart)
            Collections.reverse(otherWaypoints);
        if (thisMergeAtStart)
            newWaypoints.addAll(0, otherWaypoints);
        else
            newWaypoints.addAll(otherWaypoints);

        WireWaypoint mergeWaypoint = new WireWaypoint(new Point(mergeNode.getLocation().getX(), mergeNode.getLocation().getY()));
        if (thisMergeAtStart)
            newWaypoints.add(otherWaypoints.size(), mergeWaypoint);
        else
            newWaypoints.add(routeWaypoints.length, mergeWaypoint);

        if (thisMergeAtStart) {
            if (otherMergeAtStart)
                setStart(other.getEnd());
            else
                setStart(other.getStart());
        } else {
            if (otherMergeAtStart)
                setEnd(other.getEnd());
            else
                setEnd(other.getStart());
        }
        setRouteWaypoints(newWaypoints.toArray(new WireWaypoint[0]));
        reroute();
    }

    @Override
    protected void onTransformUpdated() {

    }

    @Override
    protected void onTransformAccepted() {

    }

    @Override
    public boolean contains(Point point) {
        // If the route points haven't been generated for this wire, return false
        if (routePoints == null)
            return false;

        // Check all vertex->vertex segments to see if the point lies between them
        for (int index = 0; index < routePoints.length - 1; index++) {
            Point start = routePoints[index];
            Point end = routePoints[index + 1];
            if (point.isBetween(start, end))
                return true;
        }

        return false;
    }

    @Override
    public void deserialize(DataMap data, DataContext context) {

    }

    @Override
    public void serialize(DataMap data, DataContext context) {
        // Store all necessary information into the data map
        serializeEndpoint("Start", getStart(), data, context);
        serializeEndpoint("End", getEnd(), data, context);

        if (getRouteWaypoints() != null && getRouteWaypoints().length > 0) {
            DataArray routeWaypointArray = new DataArray();
            Arrays.stream(getRouteWaypoints()).forEach(waypoint -> routeWaypointArray.add(context.serialize(waypoint)));
            data.put("Waypoints", routeWaypointArray);
        }
    }

    private void serializeEndpoint(String key, WireRoutable endpoint, DataMap data, DataContext context) {
        if (endpoint != null) {
            DataMap endpointData = new DataMap();

            if (endpoint instanceof ChipPin endpointPin) {
                endpointData.put("Type", DataValue.from("pin"));
                endpointPin.serialize(endpointData, context);
            } else if (endpoint instanceof WireNode endpointNode) {
                endpointData.put("Type", DataValue.from("node"));
                endpointData.put("Id", DataValue.from(endpointNode.getId()));
            }

            data.put(key, endpointData);
        }
    }
}
