package com.aethersim.simulation.mesh.mesh.generation;

import com.aethersim.designs.ChipPin;
import com.aethersim.designs.Design;
import com.aethersim.designs.wires.WireNode;
import com.aethersim.designs.wires.WireSegment;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MeshWireSet {

    private final Set<WireNode> wireNodes;
    private final Set<WireSegment> wireSegments;
    private final Set<ChipPin> connectedPins;

    private MeshWireSet(Set<WireNode> wireNodes, Set<WireSegment> wireSegments) {
        this.wireNodes = wireNodes;
        this.wireSegments = wireSegments;

        // Generate the set of connected pins from the endpoints of the wire segments
        this.connectedPins = wireSegments.stream()
                .map(WireSegment::getEndpoints)
                .flatMap(List::stream)
                .filter(endpoint -> endpoint instanceof ChipPin)
                .map(endpoint -> (ChipPin) endpoint)
                .collect(Collectors.toSet());
    }

    public Set<WireNode> getWireNodes() {
        return wireNodes;
    }

    public Set<WireSegment> getWireSegments() {
        return wireSegments;
    }

    public Set<ChipPin> getConnectedPins() {
        return connectedPins;
    }

    @Override
    public String toString() {
        return "MeshWireSet{\n" +
                "\twireNodes=" + wireNodes +
                ",\n\twireSegments=" + wireSegments +
                ",\n\tconnectedPins=" + connectedPins +
                "\n}";
    }

    public static Set<MeshWireSet> generate(Design design) {
        Set<MeshWireSet> results = new LinkedHashSet<>();

        Set<WireNode> allNodes = new LinkedHashSet<>(design.getWireNodes());
        Set<WireSegment> allSegments = new LinkedHashSet<>(design.getWireSegments());

        // Iterate over all nodes in the design, and generate a set of all segments and other nodes connected to
        // that node.  For each set of unique nodes/segments, create a new wire set
        while (!allNodes.isEmpty()) {
            Set<WireNode> nodes = new LinkedHashSet<>();
            Set<WireSegment> segments = new LinkedHashSet<>();

            // Pick a random starting node of the nodes remaining
            WireNode startNode = allNodes.iterator().next();
            generate(allNodes, allSegments, nodes, segments, startNode);

            // Generate a new wire set and add it to the set of results
            results.add(new MeshWireSet(nodes, segments));
        }

        // If we're all out of nodes, check if we have segments remaining.  These will be standalone
        // mesh wire sets that connect pin->pin
        for (WireSegment wireSegment : allSegments) {
            results.add(new MeshWireSet(Set.of(), Set.of(wireSegment)));
        }

        return results;
    }

    private static void generate(final Set<WireNode> remainingNodes, final Set<WireSegment> remainingSegments,
                                 final Set<WireNode> resultNodes, final Set<WireSegment> resultSegments,
                                 WireNode startNode) {
        // Remove the wire node from the set of remaining nodes (so we don't iterate through it again)
        remainingNodes.remove(startNode);
        // Add the node to the results, and generate a list of segments connected to this node
        resultNodes.add(startNode);
        Set<WireSegment> connectedSegments = remainingSegments.stream()
                .filter(wireSegment -> wireSegment.getEndpoints().contains(startNode))
                .collect(Collectors.toSet());

        // Add the found sets to the result set, and then remove them from the set of remaining segments
        resultSegments.addAll(connectedSegments);
        remainingSegments.removeAll(connectedSegments);

        // Now, iterate over all other node endpoints that are still in the remaining set of nodes
        connectedSegments.stream()
                .map(WireSegment::getEndpoints)
                .flatMap(List::stream)
                .filter(endpoint -> endpoint instanceof WireNode)
                .map(endpoint -> (WireNode) endpoint)
                .filter(remainingNodes::contains)
                .forEach(wireNode -> generate(
                        remainingNodes, remainingSegments,
                        resultNodes, resultSegments,
                        wireNode
                ));
    }
}
