package com.github.chrisblutz.breadboard.designs.wires;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.Vertex;
import com.github.chrisblutz.breadboard.utils.Direction;

import java.util.*;

/**
 * This class performs the pathfinding necessary to route wires in designs.  It does so by using a modified
 * version of the A* pathfinding algorithm.
 * <p>
 * The algorithm used by this class uses heuristic (estimated) costs to determine the nodes it searches.  The
 * heuristic cost to route a wire from one vertex to another vertex is defined as the Manhattan distance between
 * the two vertices, plus an expected penalty if the path goes around corners.
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
     * This class represents a single traversed node in a route.  It consists of information about the current vertex,
     * the previously-visited vertex, the cost of the path to the current vertex, the heuristic cost of traversing
     * to the final vertex, and information about the incoming waypoint path (if applicable).
     */
    private static class Node implements Comparable<Node> {

        // Represents the current vertex
        final Vertex vertex;
        // Represents the previously-visited vertex adjacent to this one
        Vertex previousVertex;
        // Contains the previous waypoint path (only applicable to some nodes, most will leave this null)
        Vertex[] incomingRoute = null;
        // Contains the actual cost of the route from the start vertex to the current one
        double currentCost;
        // Contains the heuristic cost of traversing from the start node to the end node (including data
        // already known about the cost of traversal to the current node)
        double totalCostHeuristic;

        /**
         * Creates a new {@code Node} that represents the specified current vertex, with no prior vertex
         * and infinite cost.
         *
         * @param vertex the current {@link Vertex} being traversed
         */
        Node(Vertex vertex) {
            this(vertex, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        /**
         * Creates a new {@code Node} with the specified information about current and prior nodes and route
         * cost.
         *
         * @param vertex             the current {@link Vertex} being traversed
         * @param previousVertex     the {@link Vertex} traversed before this one
         * @param currentCost        the actual cost of traversal from the start vertex to the current vertex
         * @param totalCostHeuristic the estimated cost of traversal from the start vertex to the end vertex
         *                           via the current vertex
         */
        Node(Vertex vertex, Vertex previousVertex, double currentCost, double totalCostHeuristic) {
            this.vertex = vertex;
            this.previousVertex = previousVertex;
            this.currentCost = currentCost;
            this.totalCostHeuristic = totalCostHeuristic;
        }

        /**
         * Compares two {@code Node} objects and returns the one with the lower (cheaper)
         * heuristic cost of traversal from the start vertex to the end vertex.
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
     * This record contains information about a known optimal path to a target vertex, including the specific
     * route information.
     *
     * @param node  the final {@link Node} that produced this route
     * @param route the ordered list of {@link Vertex} objects that create a route from the start vertex
     *              to the target vertex
     */
    private record Path(Node node, Vertex[] route) {
    }

    /**
     * Calculates the optimal path (or one of the optimal paths, if multiple equally optimal paths exist) from the
     * specified start vertex to the specified end vertex.
     * <p>
     * If possible, it also takes into account the preferred direction of wires going into and out of these terminal
     * vertices.  In cases where honoring these preferences would create highly undesirable (costly) paths, they may
     * be ignored.  Note that the directions are from the perspective of the vertices themselves, so a "left" start
     * direction would indicate a wire leaving the start pin to the left (negative X).  For end pins, this means
     * that the direction indicates the direction a wire should go to reach the pin, so a "left" end direction
     * indicates that a wire should <i>come from</i> the left (i.e. entering the vertex from the left, resulting from
     * a wire pointing to the right).
     * <p>
     * If specified, the algorithm will also route through each waypoint vertex in order.  Unlike the start and end
     * directions, which are noted as preferences (vs. requirements), the resulting path generated by this method
     * <i>will</i> pass through all the waypoint vertices in the order that they are specified.  The incoming
     * and outgoing directions of wires to and from each waypoint may differ, but the vertex will be contained
     * within the resulting path.
     *
     * @param design                  the {@link Design} that contains the wire being routed (used for determining
     *                                route obstacles)
     * @param startVertex             the {@link Vertex} where the route should start
     * @param preferredStartDirection the {@link Direction} in which the wire should leave the start vertex,
     *                                if possible
     * @param endVertex               the {@link Vertex} where the route should end
     * @param preferredEndDirection   the {@link Direction} in which the wire should reach the end vertex,
     *                                if possible
     * @param waypointVertices        the ordered array of {@link Vertex} waypoints that the wire should travel
     *                                through on the way to the end vertex, or empty or {@code null} for a wire with
     *                                no waypoints
     * @return The ordered list of {@link Vertex} objects that form the optimal wire path to from the start
     * vertex to the end vertex.  The returned vertices will represent the start vertex, end vertex, and all
     * necessary corners in the path, and will not necessarily include any of the waypoint vertices.
     */
    public static Vertex[] route(
            final Design design,
            Vertex startVertex, Direction preferredStartDirection,
            Vertex endVertex, Direction preferredEndDirection,
            Vertex[] waypointVertices) {
        Path[] intermediatePaths = null;
        // Initialize the first "from" (starting) vertex to the starting vertex of the wire
        Vertex fromVertex = startVertex;

        // If we have waypoints, process each of them one at a time
        for (Vertex waypoint : waypointVertices) {
            // Find all paths to the waypoint (taking into account any previous intermediate paths)
            intermediatePaths = routeToWaypoint(
                    design,
                    fromVertex, fromVertex.equals(startVertex) ? preferredStartDirection : null,
                    waypoint,
                    intermediatePaths
            );
            // Now set the "from" vertex to use this waypoint instead
            fromVertex = waypoint;
        }

        // Find the ultimate route by finding the route from the current "from" vertex to the final vertex
        Path path = routeToVertex(
                design,
                fromVertex, fromVertex.equals(startVertex) ? preferredStartDirection : null,
                endVertex, preferredEndDirection,
                intermediatePaths
        );
        if (path == null)
            return null;

        // TODO Limit to corner vertices (since these are the only vertices that the renderer needs)

        return path.route;
    }

    private static Path[] routeToWaypoint(
            final Design design,
            Vertex startVertex, Direction preferredStartDirection,
            Vertex waypointVertex,
            Path[] incomingPaths) {
        // For each of the horizontally- or vertically-adjacent vertices, build the quickest path to the node
        // from the start node.  Then, filter that list down to avoid null paths and paths where the waypoint is the
        // second-to-last node (since this would cause doubling-back in future paths),
        // and then add in the destination node
        return Set.of(
                waypointVertex.withOffset(1, 0),
                waypointVertex.withOffset(-1, 0),
                waypointVertex.withOffset(0, 1),
                waypointVertex.withOffset(0, -1)
        ).stream().map(vertex ->
                routeToVertex(
                        design,
                        startVertex,
                        preferredStartDirection,
                        vertex,
                        null,
                        incomingPaths
                )
        ).filter(
                path -> path != null && !path.node.previousVertex.equals(waypointVertex)
        ).map(path -> {
            // Calculate the overall cost of traversing to the waypoint vertex from the adjacent vertex
            double cost = path.node.currentCost + calculateActualCost(
                    path.node.previousVertex, path.node.vertex, waypointVertex,
                    null, null, false
            );
            // Create the end node in this path by adding the waypoint vertex itself and calculating the total cost
            Node endNode = new Node(waypointVertex, path.node.vertex, cost, cost);
            // Create the new list of vertices in the path by appending the waypoint vertex
            Vertex[] newRoute = Arrays.copyOf(path.route, path.route.length + 1);
            newRoute[path.route.length] = waypointVertex;
            // Return the new cheapest path object
            return new Path(endNode, newRoute);
        }).toArray(Path[]::new);
    }

    private static Path routeToVertex(
            final Design design,
            Vertex startVertex, Direction preferredStartDirection,
            Vertex endVertex, Direction preferredEndDirection,
            Path[] incomingPaths) {
        // If one of the endpoints is not valid (i.e. we can't route to it), return null now
        if (!isValidVertex(design, startVertex) || !isValidVertex(design, endVertex))
            return null;

        // If the start and end vertex are the same, choose the cheapest path from the incoming (if available) or
        // return null
        if (startVertex.equals(endVertex)) {
            if (incomingPaths == null || incomingPaths.length == 0)
                return null;

            return Arrays
                    .stream(incomingPaths)
                    .min(Comparator.comparingDouble(path -> path.node.currentCost))
                    .orElse(null);
        }

        final Map<Vertex, Node> allNodes = new HashMap<>();
        final Queue<Node> nodesToSearch = new PriorityQueue<>();

        // Create the node for the start of the path and add it to the processing queue
        Node start = new Node(startVertex, null, 0d, calculateTotalHeuristicCost(startVertex, endVertex));
        allNodes.put(startVertex, start);
        nodesToSearch.add(start);

        // While nodes remain in the search queue, continue searching
        while (!nodesToSearch.isEmpty()) {
            // Pull the current cheapest node from the queue
            final Node current = nodesToSearch.poll();
            final Vertex currentVertex = current.vertex;

            // If the current node is the target node, build the path and return it
            if (currentVertex.equals(endVertex)) {
                // Build a list of vertices that make up the path by adding the current vertex to the beginning of
                // the list and then traversing backward through vertices until we reach the start
                List<Vertex> route = new ArrayList<>();
                Node pathCurrent = current;
                Vertex[] incomingRoute = null;
                while (pathCurrent != null) {
                    route.add(0, pathCurrent.vertex);
                    // If this vertex has incoming route info, it means it's the second-to-last vertex (since
                    // which incoming route we use depends on which initial "next" vertex was chosen), so store
                    // it and break to avoid adding the final node twice
                    if (pathCurrent.incomingRoute != null && pathCurrent.incomingRoute.length > 0) {
                        incomingRoute = pathCurrent.incomingRoute;
                        break;
                    }
                    // Continue along the chain of previous nodes
                    pathCurrent = allNodes.get(pathCurrent.previousVertex);
                }

                // If we have incoming route data, add that to the complete route
                if (incomingRoute != null)
                    route.addAll(0, Arrays.asList(incomingRoute));

                // Create an object that contains the pathfinder node and complete route
                return new Path(current, route.toArray(new Vertex[0]));
            }

            // Build the list of possible vertices that can be reached from this one (this will be all
            // vertices within 1 square horizontally or vertically).  Then, filter the list of vertices for validity
            // and strip out the previous vertex so we don't double back on it.  Then, calculate costs for that
            // vertex and check for a new cheapest path.
            Set.of(
                    currentVertex.withOffset(1, 0),
                    currentVertex.withOffset(-1, 0),
                    currentVertex.withOffset(0, 1),
                    currentVertex.withOffset(0, -1)
            ).stream().filter(vertex -> isValidVertex(design, vertex)).forEach(nextVertex -> {
                // If we had incoming paths passed to this method, we need to patch them in here if the previous vertex
                // is null (indicating we're at the start)
                double currentCost = current.currentCost;
                Vertex previousVertex = current.previousVertex;
                Vertex[] incomingPreviousPath = null;
                if (previousVertex == null && incomingPaths != null && incomingPaths.length > 0) {
                    // Calculate cost of the first path, while filtering out any paths that contain the "next"
                    // vertex as the "previous" vertex so we don't double back
                    Path cheapestIncoming = Arrays.stream(incomingPaths)
                            .filter(path ->
                                    !path.node.previousVertex.equals(nextVertex)
                            ).min((path1, path2) -> {
                                // Calculate cost of the first path (we don't care about "preferred" directions here
                                // as these are waypoint paths, not start or end vertices)
                                double path1Cost = path1.node.currentCost + calculateActualCost(path1.node.previousVertex, currentVertex, nextVertex, null, null, true);
                                double path2Cost = path2.node.currentCost + calculateActualCost(path2.node.previousVertex, currentVertex, nextVertex, null, null, true);
                                return Double.compare(path1Cost, path2Cost);
                            }).orElse(null);

                    // If we found a valid cheapest incoming path, set the current cost and previous vertex values
                    // If we didn't find a valid cheapest incoming path, then this is not a valid "next" vertex,
                    // so return early
                    if (cheapestIncoming != null) {
                        currentCost = cheapestIncoming.node.currentCost;
                        previousVertex = cheapestIncoming.node.previousVertex;
                        incomingPreviousPath = cheapestIncoming.route;
                    } else {
                        return;
                    }
                }

                // Find the actual cost of traversal to the next node
                double cost = currentCost + calculateActualCost(
                        previousVertex,
                        currentVertex,
                        nextVertex,
                        currentVertex.equals(startVertex) ? preferredStartDirection : null,
                        nextVertex.equals(endVertex) ? preferredEndDirection : null,
                        false
                );
                // Pull the pathfinder node from the map if it exists, or create one if not (with infinite cost)
                Node nextNode = allNodes.getOrDefault(nextVertex, new Node(nextVertex));
                // Insert the pathfinder node back into the map, in case it wasn't there already
                allNodes.put(nextVertex, nextNode);

                // If we've found a cheaper path, note the new cost and re-add it to the queue
                if (cost < nextNode.currentCost) {
                    nextNode.previousVertex = currentVertex;
                    nextNode.currentCost = cost;
                    // Calculate the new heuristic value (now that we know the cost to this node)
                    nextNode.totalCostHeuristic = cost + calculateTotalHeuristicCost(nextVertex, endVertex);
                    // If the incoming previous path array has data, we need to set that data in the vertex
                    nextNode.incomingRoute = incomingPreviousPath;
                    // Append the node back to the processing queue
                    nodesToSearch.add(nextNode);
                }
            });
        }

        return null;
    }

    private static boolean isValidVertex(Design design, Vertex vertex) {
        return (vertex.getX() >= 0 && vertex.getX() <= design.getWidth()) &&
                (vertex.getY() >= 0 && vertex.getY() <= design.getHeight());
         // TODO
    }

    private static double calculateTotalHeuristicCost(Vertex startVertex, Vertex endVertex) {
        // Base cost of moving between grid squares is always 1, so we can just use the Manhattan distance here
        int xDiff = endVertex.getX() - startVertex.getX();
        int yDiff = endVertex.getY() - startVertex.getY();
        double baseCost = Math.abs(xDiff) + Math.abs(yDiff);
        // If the path involves at least one corner, add the penalty
        if (xDiff != 0 && yDiff != 0)
            baseCost += 0.5;
        return baseCost;
    }

    private static double calculateActualCost(Vertex previousVertex, Vertex currentVertex, Vertex nextVertex, Direction preferredStartDirection, Direction preferredEndDirection, boolean isCurrentWaypoint) {
        // Base cost of moving between grid squares is always 1, so we can just use the Manhattan distance here
        int xDiff = nextVertex.getX() - currentVertex.getX();
        int yDiff = nextVertex.getY() - currentVertex.getY();
        double baseCost = Math.abs(xDiff) + Math.abs(yDiff);
        // If we change direction with this movement, add a penalty
        // If we're changing direction at a waypoint, use a lower penalty to encourage
        // cornering at waypoints (instead of next to them)
        if (previousVertex != null &&
                ((currentVertex.getX() - previousVertex.getX()) != xDiff ||
                        (currentVertex.getY() - previousVertex.getY()) != yDiff))
            baseCost += isCurrentWaypoint ? 0.4 : 0.5;
        // If we're not going the preferred direction, add a penalty
        if (preferredStartDirection != null &&
                (xDiff != preferredStartDirection.getXDiff() || yDiff != preferredStartDirection.getYDiff()))
            baseCost += 5;
        // We invert xDiff and yDiff here to reverse the direction to that seen by the end vertex (since we do the
        // initial calculations from the standpoint of the previous vertex, not the end vertex, which provides
        // the preferred direction)
        if (preferredEndDirection != null &&
                (-xDiff != preferredEndDirection.getXDiff() || -yDiff != preferredEndDirection.getYDiff()))
            baseCost += 5;

        return baseCost;
    }
}
