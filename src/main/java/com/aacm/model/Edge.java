package com.aacm.model;

/**
 * Edge class - Represents a directed relationship between two nodes in the attack graph.
 *
 * Edge Types:
 *   "reachability"        - Network connectivity between nodes
 *   "exposure"            - A service is exposed on a host
 *   "vulnerability"       - A service/host has a known weakness
 *   "privilege_escalation" - Exploiting a vulnerability grants a privilege level
 *   "lateral_movement"    - Access on one host enables movement to another
 *   "target_access"       - A path reaches a sensitive/high-value asset
 *
 * Weighting Strategy:
 *   Weights are numeric costs where LOWER values represent EASIER/LIKELIER steps.
 *   This allows Dijkstra to find the "path of least resistance."
 */
public class Edge {
    private String sourceId;       // Source node ID
    private String destinationId;  // Destination node ID
    private String type;           // Edge type classification
    private double weight;         // Numeric cost (lower = easier/more likely)
    private String label;          // Human-readable label for visualization

    public Edge() {
        this("", "", "", 1.0, "");
    }

    public Edge(String sourceId, String destinationId, String type, double weight) {
        this(sourceId, destinationId, type, weight, "");
    }

    public Edge(String sourceId, String destinationId, String type, double weight, String label) {
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.type = type;
        this.weight = weight;
        this.label = label;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Edge{" +
                sourceId + " -> " + destinationId +
                ", type='" + type + '\'' +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;
        if (!sourceId.equals(edge.sourceId)) return false;
        return destinationId.equals(edge.destinationId);
    }

    @Override
    public int hashCode() {
        int result = sourceId.hashCode();
        result = 31 * result + destinationId.hashCode();
        return result;
    }
}
