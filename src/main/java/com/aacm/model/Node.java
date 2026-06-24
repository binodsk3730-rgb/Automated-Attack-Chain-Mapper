package com.aacm.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Node class - Represents a vertex in the attack graph.
 * 
 * Node Types:
 *   "attacker"     - The starting point of the attack simulation
 *   "host"         - A machine on the network (server, workstation, etc.)
 *   "service"      - A network service running on a host (HTTP, SSH, etc.)
 *   "vulnerability" - A known weakness affecting a service or host
 *   "privilege"    - A simulated access level (USER, ADMIN)
 *   "target"       - A high-value asset node
 */
public class Node {
    private String id;           // Unique identifier (e.g., "H1", "CVE-2021-41773")
    private String label;        // Human-readable label (e.g., "Web Server", "Apache 2.4.49")
    private String type;         // Node type (see above)
    private Map<String, String> metadata;  // Flexible metadata storage

    public Node() {
        this("", "", "");
    }

    public Node(String id, String label, String type) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.metadata = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * Get metadata value or return default if key not found
     */
    public String getMetadata(String key, String defaultVal) {
        return metadata.getOrDefault(key, defaultVal);
    }

    /**
     * Get metadata value or empty string if key not found
     */
    public String getMetadata(String key) {
        return getMetadata(key, "");
    }

    public void putMetadata(String key, String value) {
        metadata.put(key, value);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;
        return id != null ? id.equals(node.id) : node.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
