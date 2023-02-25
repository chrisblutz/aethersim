package com.aethersim.designs.wires;

import com.aethersim.designs.Chip;
import com.aethersim.designs.Design;
import com.aethersim.designs.DesignElement;
import com.aethersim.designs.Point;
import com.aethersim.utils.Direction;

import java.util.*;

/**
 * This class performs the pathfinding necessary to route wires in designs.  It does so by using a modified
 * version of the A* pathfinding algorithm.
 * <p>
 * The algorithm used by this class uses heuristic (estimated) costs to determine the nodes it searches.  The
 * heuristic cost to route a wire from one point to another point is defined as the Manhattan distance between
 * the two points, plus an expected penalty if the path goes around corners.
 * <p>
 * The actual cost function is designed to minimize corners (to produce straight wires as often as possible,
 * as opposed to wires that zigzag across the design).  It does this by imposing a penalty for each corner
 * the route takes.  Additionally, the algorithm imposes a penalty for passing along a square that contains
 * the edge of a chip or the edge of the design, as these are valid and occasionally necessary routing locations,
 * but they should be avoided as much as possible.
 * <p>
 * In order to facilitate the use of the waypoint system when drawing wires (where the waypoints dictate points
 * the wire must cross in its route), the algorithm actually performs multiple passes of the A* algorithm.
 * To make sure the route still avoids corners as much as possible, the algorithm calculates the optimal path
 * to each waypoint from all four possible directions (from the top, bottom, left, and right), and then
 * chooses the optimal one for each subsequent segment.  This allows optimal routing across many waypoints,
 * instead of just from start to end.
 */
public class WireRouter {

    /**
     * This class represents a single traversed node in a route.  It consists of information about the current point,
     * the previously-visited point, the cost of the path to the current point, the heuristic cost of traversing
     * to the final point, and information about the incoming waypoint path (if applicable).
     */
    private static class Node implements Comparable<Node> {

        // Represents the current point
        final Point point;
        // Represents the previously-visited point adjacent to this one
        Point previousPoint;
        // Contains the previous waypoint path (only applicable to some nodes, most will leave this null)
        Point[] incomingRoute = null;
        // Contains the actual cost of the route from the start point to the current one
        double currentCost;
        // Contains the heuristic cost of traversing from the start node to the end node (including data
        // already known about the cost of traversal to the current node)
        double totalCostHeuristic;

        /**
         * Creates a new {@code Node} that represents the specified current point, with no prior point
         * and infinite cost.
         *
         * @param point the current {@link Point} being traversed
         */
        Node(Point point) {
            this(point, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        /**
         * Creates a new {@code Node} with the specified information about current and prior nodes and route
         * cost.
         *
         * @param point             the current {@link Point} being traversed
         * @param previousPoint     the {@link Point} traversed before this one
         * @param currentCost        the actual cost of traversal from the start point to the current point
         * @param totalCostHeuristic the estimated cost of traversal from the start point to the end point
         *                           via the current point
         */
        Node(Point point, Point previousPoint, double currentCost, double totalCostHeuristic) {
            this.point = point;
            this.previousPoint = previousPoint;
            this.currentCost = currentCost;
            this.totalCostHeuristic = totalCostHeuristic;
        }

        /**
         * Compares two {@code Node} objects and returns the one with the lower (cheaper)
         * heuristic cost of traversal from the start point to the end point.
         *
         * @param other the {@code Node} to be compared
         * @return The {@code Node} with the lower heuristic cost
         */
        @Override
        public int compareTo(Node other) {
            return Double.compare(totalCostHeuristic, other.totalCostHeuristic);
        }
    }

    /**
     * This record contains information about a known optimal path to a target point, including the specific
     * route information.
     *
     * @param node  the final {@link Node} that produced this route
     * @param route the ordered list of {@link Point} objects that create a route from the start point
     *              to the target point
     */
    private record Path(Node node, Point[] route) {}

    /**
     * Calculates the optimal path (or one of the optimal paths, if multiple equally optimal paths exist) from the
     * specified start point to the specified end point.
     * <p>
     * If possible, it also takes into account the preferred direction of wires going into and out of these terminal
     * points.  In cases where honoring these preferences would create highly undesirable (costly) paths, they may
     * be ignored.  Note that the directions are from the perspective of the points themselves, so a "left" start
     * direction would indicate a wire leaving the start pin to the left (negative X).  For end pins, this means
     * that the direction indicates the direction a wire should go to reach the pin, so a "left" end direction
     * indicates that a wire should <i>come from</i> the left (i.e. entering the point from the left, resulting from
     * a wire pointing to the right).
     * <p>
     * If specified, the algorithm will also route through each waypoint point in order.  Unlike the start and end
     * directions, which are noted as preferences (vs. requirements), the resulting path generated by this method
     * <i>will</i> pass through all the waypoints in the order that they are specified.  The incoming
     * and outgoing directions of wires to and from each waypoint may differ, but the point will be contained
     * within the resulting path.
     *
     * @param design                  the {@link Design} that contains the wire being routed (used for determining
     *                                route obstacles)
     * @param startPoint             the {@link Point} where the route should start
     * @param preferredStartDirection the {@link Direction} in which the wire should leave the start point,
     *                                if possible
     * @param endPoint               the {@link Point} where the route should end
     * @param preferredEndDirection   the {@link Direction} in which the wire should reach the end point,
     *                                if possible
     * @param waypoints        the ordered array of {@link Point} waypoints that the wire should travel
     *                                through on the way to the end point, or empty or {@code null} for a wire with
     *                                no waypoints
     * @return The ordered list of {@link Point} objects that form the optimal wire path to from the start
     * point to the end point.  The returned points will represent the start point, end point, and all
     * necessary corners in the path, and will not necessarily include any of the waypoints (though the path itself
     * will pass through the waypoints).
     */
    public static Point[] route(
            final Design design,
            Point startPoint, Direction preferredStartDirection,
            Point endPoint, Direction preferredEndDirection,
            Point[] waypoints) {
        Path[] intermediatePaths = null;
        // Initialize the first "from" (starting) point to the starting point of the wire
        Point fromPoint = startPoint;

        // If we have waypoints, process each of them one at a time
        for (Point waypoint : waypoints) {
            // Find all paths to the waypoint (taking into account any previous intermediate paths)
            intermediatePaths = routeToWaypoint(
                    design,
                    fromPoint, fromPoint.equals(startPoint) ? preferredStartDirection : null,
                    waypoint,
                    intermediatePaths
            );
            // Now set the "from" point to use this waypoint instead
            fromPoint = waypoint;
        }

        // Find the ultimate route by finding the route from the current "from" point to the final point
        Path path = routeToPoint(
                design,
                fromPoint, fromPoint.equals(startPoint) ? preferredStartDirection : null,
                endPoint, preferredEndDirection,
                intermediatePaths
        );
        if (path == null)
            return null;

        List<Point> cornerPoints = new ArrayList<>();
        for (int pointIndex = 0; pointIndex < path.route.length; pointIndex++) {
            // If this is the first or last point in the route, include it in the list
            if (pointIndex == 0 || pointIndex == path.route.length - 1) {
                cornerPoints.add(path.route[pointIndex]);
            } else {
                // Otherwise, pull the previous and next nodes and determine if this node represents a corner
                Point prevPoint = path.route[pointIndex - 1];
                Point thisPoint = path.route[pointIndex];
                Point nextPoint = path.route[pointIndex + 1];

                int xDiff = nextPoint.getX() - thisPoint.getX();
                int yDiff = nextPoint.getY() - thisPoint.getY();
                if ((nextPoint.getX() - thisPoint.getX()) != (thisPoint.getX() - prevPoint.getX())
                        || (nextPoint.getY() - thisPoint.getY()) != (thisPoint.getY() - prevPoint.getY()))
                    cornerPoints.add(thisPoint);
            }
        }

        return cornerPoints.toArray(new Point[0]);
    }

    private static Path[] routeToWaypoint(
            final Design design,
            Point startPoint, Direction preferredStartDirection,
            Point waypoint,
            Path[] incomingPaths) {
        // For each of the horizontally- or vertically-adjacent points, build the quickest path to the node
        // from the start node.  Then, filter that list down to avoid null paths and paths where the waypoint is the
        // second-to-last node (since this would cause doubling-back in future paths),
        // and then add in the destination node
        return Set.of(
                waypoint.withOffset(1, 0),
                waypoint.withOffset(-1, 0),
                waypoint.withOffset(0, 1),
                waypoint.withOffset(0, -1)
        ).stream().map(point ->
                routeToPoint(
                        design,
                        startPoint,
                        preferredStartDirection,
                        point,
                        null,
                        incomingPaths
                )
        ).filter(
                path -> path != null && !path.node.previousPoint.equals(waypoint)
        ).map(path -> {
            // Calculate the overall cost of traversing to the waypoint from the adjacent point
            double cost = path.node.currentCost + calculateActualCost(
                    path.node.previousPoint, path.node.point, waypoint,
                    null, null, false
            );
            // Create the end node in this path by adding the waypoint itself and calculating the total cost
            Node endNode = new Node(waypoint, path.node.point, cost, cost);
            // Create the new list of points in the path by appending the waypoint
            Point[] newRoute = Arrays.copyOf(path.route, path.route.length + 1);
            newRoute[path.route.length] = waypoint;
            // Return the new cheapest path object
            return new Path(endNode, newRoute);
        }).toArray(Path[]::new);
    }

    private static Path routeToPoint(
            final Design design,
            Point startPoint, Direction preferredStartDirection,
            Point endPoint, Direction preferredEndDirection,
            Path[] incomingPaths) {
        // If one of the endpoints is not valid (i.e. we can't route to it), return null now
        if (!isValidPoint(design, startPoint) || !isValidPoint(design, endPoint))
            return null;

        // If the start and end point are the same, choose the cheapest path from the incoming (if available) or
        // return null
        if (startPoint.equals(endPoint)) {
            if (incomingPaths == null || incomingPaths.length == 0)
                return null;

            return Arrays
                    .stream(incomingPaths)
                    .min(Comparator.comparingDouble(path -> path.node.currentCost))
                    .orElse(null);
        }

        final Map<Point, Node> allNodes = new HashMap<>();
        final Queue<Node> nodesToSearch = new PriorityQueue<>();

        // Create the node for the start of the path and add it to the processing queue
        Node start = new Node(startPoint, null, 0d, calculateTotalHeuristicCost(startPoint, endPoint));
        allNodes.put(startPoint, start);
        nodesToSearch.add(start);

        // While nodes remain in the search queue, continue searching
        while (!nodesToSearch.isEmpty()) {
            // Pull the current cheapest node from the queue
            final Node current = nodesToSearch.poll();
            final Point currentPoint = current.point;

            // If the current node is the target node, build the path and return it
            if (currentPoint.equals(endPoint)) {
                // Build a list of points that make up the path by adding the current point to the beginning of
                // the list and then traversing backward through points until we reach the start
                List<Point> route = new ArrayList<>();
                Node pathCurrent = current;
                Point[] incomingRoute = null;
                while (pathCurrent != null) {
                    route.add(0, pathCurrent.point);
                    // If this point has incoming route info, it means it's the second-to-last point (since
                    // which incoming route we use depends on which initial "next" point was chosen), so store
                    // it and break to avoid adding the final node twice
                    if (pathCurrent.incomingRoute != null && pathCurrent.incomingRoute.length > 0) {
                        incomingRoute = pathCurrent.incomingRoute;
                        break;
                    }
                    // Continue along the chain of previous nodes
                    pathCurrent = allNodes.get(pathCurrent.previousPoint);
                }

                // If we have incoming route data, add that to the complete route
                if (incomingRoute != null)
                    route.addAll(0, Arrays.asList(incomingRoute));

                // Create an object that contains the pathfinder node and complete route
                return new Path(current, route.toArray(new Point[0]));
            }

            // Build the list of possible points that can be reached from this one (this will be all
            // points within 1 square horizontally or vertically).  Then, filter the list of points for validity
            // and strip out the previous point so we don't double back on it.  Then, calculate costs for that
            // point and check for a new cheapest path.
            Set.of(
                    currentPoint.withOffset(1, 0),
                    currentPoint.withOffset(-1, 0),
                    currentPoint.withOffset(0, 1),
                    currentPoint.withOffset(0, -1)
            ).stream().filter(point -> isValidPoint(design, point)).forEach(nextPoint -> {
                // If we had incoming paths passed to this method, we need to patch them in here if the previous point
                // is null (indicating we're at the start)
                double currentCost = current.currentCost;
                Point previousPoint = current.previousPoint;
                Point[] incomingPreviousPath = null;
                if (previousPoint == null && incomingPaths != null && incomingPaths.length > 0) {
                    // Calculate cost of the first path, while filtering out any paths where the "next" point is the
                    // same as the "previous" point so we don't double back
                    Path cheapestIncoming = Arrays.stream(incomingPaths)
                            .filter(path ->
                                    !path.node.previousPoint.equals(nextPoint)
                            ).min((path1, path2) -> {
                                // Calculate cost of the first path (we don't care about "preferred" directions here
                                // as these are waypoint paths, not start or end points)
                                double path1Cost = path1.node.currentCost + calculateActualCost(path1.node.previousPoint, currentPoint, nextPoint, null, null, true);
                                double path2Cost = path2.node.currentCost + calculateActualCost(path2.node.previousPoint, currentPoint, nextPoint, null, null, true);
                                return Double.compare(path1Cost, path2Cost);
                            }).orElse(null);

                    // If we found a valid cheapest incoming path, set the current cost and previous point values
                    // If we didn't find a valid cheapest incoming path, then this is not a valid "next" point,
                    // so return early
                    if (cheapestIncoming != null) {
                        currentCost = cheapestIncoming.node.currentCost;
                        previousPoint = cheapestIncoming.node.previousPoint;
                        incomingPreviousPath = cheapestIncoming.route;
                    } else {
                        return;
                    }
                }

                // Find the actual cost of traversal to the next node
                double cost = currentCost + calculateActualCost(
                        previousPoint,
                        currentPoint,
                        nextPoint,
                        currentPoint.equals(startPoint) ? preferredStartDirection : null,
                        nextPoint.equals(endPoint) ? preferredEndDirection : null,
                        false
                );
                // Pull the pathfinder node from the map if it exists, or create one if not (with infinite cost)
                Node nextNode = allNodes.getOrDefault(nextPoint, new Node(nextPoint));
                // Insert the pathfinder node back into the map, in case it wasn't there already
                allNodes.put(nextPoint, nextNode);

                // If we've found a cheaper path, note the new cost and re-add it to the queue
                if (cost < nextNode.currentCost) {
                    nextNode.previousPoint = currentPoint;
                    nextNode.currentCost = cost;
                    // Calculate the new heuristic value (now that we know the cost to this node)
                    nextNode.totalCostHeuristic = cost + calculateTotalHeuristicCost(nextPoint, endPoint);
                    // If the incoming previous path array has data, we need to set that data in the node
                    nextNode.incomingRoute = incomingPreviousPath;
                    // Append the node back to the processing queue
                    nodesToSearch.add(nextNode);
                }
            });
        }

        return null;
    }

    private static boolean isValidPoint(Design design, Point point) {
        DesignElement elementAtPoint = design.getElementAt(point);
        return (point.getX() >= 0 && point.getX() <= design.getWidth()) &&
                (point.getY() >= 0 && point.getY() <= design.getHeight()) &&
                !(elementAtPoint instanceof Chip);
         // TODO
    }

    private static double calculateTotalHeuristicCost(Point startPoint, Point endPoint) {
        // Base cost of moving between grid squares is always 1, so we can just use the Manhattan distance here
        int xDiff = endPoint.getX() - startPoint.getX();
        int yDiff = endPoint.getY() - startPoint.getY();
        double baseCost = Math.abs(xDiff) + Math.abs(yDiff);
        // If the path involves at least one corner, add the penalty
        if (xDiff != 0 && yDiff != 0)
            baseCost += 0.5;
        return baseCost;
    }

    private static double calculateActualCost(Point previousPoint, Point currentPoint, Point nextPoint, Direction preferredStartDirection, Direction preferredEndDirection, boolean isCurrentWaypoint) {
        // Base cost of moving between grid squares is always 1, so we can just use the Manhattan distance here
        int xDiff = nextPoint.getX() - currentPoint.getX();
        int yDiff = nextPoint.getY() - currentPoint.getY();
        double baseCost = Math.abs(xDiff) + Math.abs(yDiff);
        // If we change direction with this movement, add a penalty
        // If we're changing direction at a waypoint, use a lower penalty to encourage
        // cornering at waypoints (instead of next to them)
        if (previousPoint != null &&
                ((currentPoint.getX() - previousPoint.getX()) != xDiff ||
                        (currentPoint.getY() - previousPoint.getY()) != yDiff))
            baseCost += isCurrentWaypoint ? 0.4 : 0.5;
        // If we're not going the preferred direction, add a penalty
        if (preferredStartDirection != null &&
                (xDiff != preferredStartDirection.getXDiff() || yDiff != preferredStartDirection.getYDiff()))
            baseCost += 5;
        // We invert xDiff and yDiff here to reverse the direction to that seen by the end point (since we do the
        // initial calculations from the standpoint of the previous point, not the end point, which provides
        // the preferred direction)
        if (preferredEndDirection != null &&
                (-xDiff != preferredEndDirection.getXDiff() || -yDiff != preferredEndDirection.getYDiff()))
            baseCost += 5;

        return baseCost;
    }
}
