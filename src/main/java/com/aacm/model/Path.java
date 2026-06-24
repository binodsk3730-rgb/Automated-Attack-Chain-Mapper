package com.aacm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Path class - Represents a sequence of nodes forming a path through the attack graph.
 */
public class Path {
    private List<String> nodes;  // Sequence of node IDs
    private double totalWeight;  // Total cost/weight of the path

    public Path() {
        this.nodes = new ArrayList<>();
        this.totalWeight = 0.0;
    }

    public Path(List<String> nodes) {
        this.nodes = new ArrayList<>(nodes);
        this.totalWeight = 0.0;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getLength() {
        return nodes.size();
    }

    public void addNode(String nodeId) {
        nodes.add(nodeId);
    }

    public String getStartNode() {
        return nodes.isEmpty() ? "" : nodes.get(0);
    }

    public String getEndNode() {
        return nodes.isEmpty() ? "" : nodes.get(nodes.size() - 1);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public String toString() {
        return "Path{" + String.join(" -> ", nodes) +
                ", weight=" + String.format("%.2f", totalWeight) + '}';
    }

    /**
     * Format path as readable string
     */
    public String formatPath() {
        return String.join(" -> ", nodes);
    }
}
