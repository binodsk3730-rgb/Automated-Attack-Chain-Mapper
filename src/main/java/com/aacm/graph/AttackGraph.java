package com.aacm.graph;

import com.aacm.model.Edge;
import com.aacm.model.Node;
import com.aacm.model.Path;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AttackGraph class - The core graph data structure for the AACM system.
 *
 * Data Structures Used:
 *   - HashMap<String, Node> : O(1) average-case node lookup by ID
 *   - HashMap<String, List<Edge>> : Adjacency list for O(1) edge list access
 *   - HashSet<String> : For O(1) membership checks
 *
 * Complexity:
 *   - addNode: O(1) average
 *   - addEdge: O(1) average
 *   - getNeighbors: O(degree(v)) where degree(v) is the number of outgoing edges
 *   - Space: O(V + E) where V = number of nodes, E = number of edges
 */
public class AttackGraph {
    private Map<String, Node> nodes;
    private Map<String, List<Edge>> adjacencyList;

    public AttackGraph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Add a node to the graph. Returns false if node already exists.
     */
    public boolean addNode(Node node) {
        if (nodes.containsKey(node.getId())) {
            return false; // Node already exists
        }
        nodes.put(node.getId(), node);
        // Ensure adjacency list entry exists
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
        return true;
    }

    /**
     * Get a node by ID, or null if not found
     */
    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    /**
     * Check if node exists
     */
    public boolean hasNode(String nodeId) {
        return nodes.containsKey(nodeId);
    }

    /**
     * Get all nodes
     */
    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    /**
     * Get count of nodes
     */
    public int getNodeCount() {
        return nodes.size();
    }

    /**
     * Add an edge to the graph
     */
    public boolean addEdge(Edge edge) {
        String source = edge.getSourceId();
        String dest = edge.getDestinationId();

        if (!hasNode(source) || !hasNode(dest)) {
            return false; // Source or destination node doesn't exist
        }

        // Check if edge already exists between these nodes
        for (Edge e : adjacencyList.get(source)) {
            if (e.getDestinationId().equals(dest)) {
                return false; // Edge already exists
            }
        }

        adjacencyList.get(source).add(edge);
        return true;
    }

    /**
     * Get all neighbors of a node
     */
    public List<String> getNeighbors(String nodeId) {
        if (!adjacencyList.containsKey(nodeId)) {
            return new ArrayList<>();
        }
        return adjacencyList.get(nodeId).stream()
                .map(Edge::getDestinationId)
                .collect(Collectors.toList());
    }

    /**
     * Get all edges from a node
     */
    public List<Edge> getOutgoingEdges(String nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }

    /**
     * Get weight of edge between two nodes
     */
    public double getEdgeWeight(String source, String destination) {
        if (!adjacencyList.containsKey(source)) {
            return 0.0;
        }
        for (Edge edge : adjacencyList.get(source)) {
            if (edge.getDestinationId().equals(destination)) {
                return edge.getWeight();
            }
        }
        return 0.0;
    }

    /**
     * Get edge between two nodes
     */
    public Edge getEdge(String source, String destination) {
        if (!adjacencyList.containsKey(source)) {
            return null;
        }
        for (Edge edge : adjacencyList.get(source)) {
            if (edge.getDestinationId().equals(destination)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Get total edge count
     */
    public int getEdgeCount() {
        return adjacencyList.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    /**
     * Clear the graph
     */
    public void clear() {
        nodes.clear();
        adjacencyList.clear();
    }
}
