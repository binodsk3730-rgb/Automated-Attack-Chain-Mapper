package com.aacm.graph;

import com.aacm.model.Edge;
import com.aacm.model.HostRecord;
import com.aacm.model.Node;

import java.util.*;

/**
 * GraphBuilder class - Constructs an AttackGraph from network scan data (HostRecords).
 * 
 * Algorithms:
 * 1. Create nodes for entities: hosts, services, vulnerabilities
 * 2. Create edges representing relationships: reachability, exposure, vulnerability, privilege escalation
 * 3. Weight edges based on exploitability score and asset criticality
 */
public class GraphBuilder {

    /**
     * Build a complete attack graph from host records
     */
    public static AttackGraph buildGraph(List<HostRecord> records) {
        AttackGraph graph = new AttackGraph();
        
        if (records.isEmpty()) {
            System.out.println("[WARNING] No records provided to build graph");
            return graph;
        }

        // Track which entities we've created to avoid duplicates
        Set<String> createdNodes = new HashSet<>();

        // Phase 1: Create nodes
        // Create an "Attacker" node as the entry point
        Node attackerNode = new Node("Attacker", "External Attacker", "attacker");
        graph.addNode(attackerNode);
        createdNodes.add("Attacker");

        // Create nodes for each host, service, and vulnerability
        Map<String, Node> hostNodes = new HashMap<>();
        Map<String, Node> serviceNodes = new HashMap<>();
        Map<String, Node> vulnNodes = new HashMap<>();

        for (HostRecord record : records) {
            // Host node
            String hostId = "HOST:" + record.getHostId();
            if (!createdNodes.contains(hostId)) {
                Node hostNode = new Node(hostId, "Host: " + record.getHostname(), "host");
                hostNode.putMetadata("ip", record.getIpAddress());
                hostNode.putMetadata("criticality", record.getAssetCriticality());
                graph.addNode(hostNode);
                hostNodes.put(hostId, hostNode);
                createdNodes.add(hostId);
            }

            // Service node
            String serviceId = "SERVICE:" + record.getHostId() + ":" + record.getPort() + ":" + record.getService();
            if (!createdNodes.contains(serviceId)) {
                Node serviceNode = new Node(serviceId, 
                        record.getService() + " v" + record.getVersion(), "service");
                serviceNode.putMetadata("port", String.valueOf(record.getPort()));
                serviceNode.putMetadata("version", record.getVersion());
                graph.addNode(serviceNode);
                serviceNodes.put(serviceId, serviceNode);
                createdNodes.add(serviceId);
            }

            // Vulnerability node (if exists)
            String vulnId = record.getVulnId();
            if (!vulnId.equals("NONE") && !createdNodes.contains(vulnId)) {
                Node vulnNode = new Node(vulnId, "Vuln: " + vulnId, "vulnerability");
                vulnNode.putMetadata("severity", record.getSeverity());
                vulnNode.putMetadata("exploitability", String.format("%.2f", record.getExploitabilityScore()));
                graph.addNode(vulnNode);
                vulnNodes.put(vulnId, vulnNode);
                createdNodes.add(vulnId);
            }
        }

        // Create target nodes
        // Try to find a critical asset as target
        boolean hasTarget = false;
        for (HostRecord record : records) {
            if ("Critical".equals(record.getAssetCriticality())) {
                String targetId = "TARGET:" + record.getHostId();
                if (!createdNodes.contains(targetId)) {
                    Node targetNode = new Node(targetId, "Target: " + record.getHostname(), "target");
                    graph.addNode(targetNode);
                    createdNodes.add(targetId);
                    hasTarget = true;
                }
            }
        }

        // If no critical asset, create a default target
        if (!hasTarget) {
            Node defaultTarget = new Node("TARGET:H4", "Default Target", "target");
            graph.addNode(defaultTarget);
        }

        // Phase 2: Create edges

        // Attacker -> Host reachability edges based on reachability data
        for (HostRecord record : records) {
            String hostId = "HOST:" + record.getHostId();
            
            for (String reachPair : record.getReachability()) {
                // Parse "Source->Dest" format
                if (reachPair.contains("->")) {
                    String source = reachPair.substring(0, reachPair.indexOf("->")).trim();
                    if ("Attacker".equals(source)) {
                        double weight = 1.0; // Reachable from attacker
                        Edge edge = new Edge("Attacker", hostId, "reachability", weight, "Can reach");
                        graph.addEdge(edge);
                    }
                }
            }

            // Service exposed on host
            String serviceId = "SERVICE:" + record.getHostId() + ":" + record.getPort() + ":" + record.getService();
            if (graph.hasNode(serviceId)) {
                Edge edge = new Edge(hostId, serviceId, "exposure", 1.0, "Service exposed");
                graph.addEdge(edge);
            }

            // Vulnerability on service
            if (!record.getVulnId().equals("NONE")) {
                String vulnId = record.getVulnId();
                if (graph.hasNode(vulnId)) {
                    double weight = 1.0 + record.getExploitabilityScore(); // Higher exploitability = lower weight (easier to exploit)
                    Edge edge = new Edge(serviceId, vulnId, "vulnerability", weight, "Has vulnerability");
                    graph.addEdge(edge);
                }
            }
        }

        // Privilege escalation: vuln -> privilege nodes
        Node userPriv = new Node("PRIVILEGE:USER", "User Access", "privilege");
        Node adminPriv = new Node("PRIVILEGE:ADMIN", "Admin Access", "privilege");
        graph.addNode(userPriv);
        graph.addNode(adminPriv);

        for (HostRecord record : records) {
            if (!record.getVulnId().equals("NONE")) {
                String vulnId = record.getVulnId();
                
                // Vulnerability -> User privilege (easier)
                Edge userEdge = new Edge(vulnId, "PRIVILEGE:USER", "privilege_escalation", 1.0, "Grant user access");
                graph.addEdge(userEdge);

                // User privilege -> Admin privilege (harder, uses criticality)
                double adminWeight = 2.0;
                if ("Critical".equals(record.getAssetCriticality())) {
                    adminWeight = 1.5; // Easier to escalate on critical assets
                }
                Edge adminEdge = new Edge("PRIVILEGE:USER", "PRIVILEGE:ADMIN", "privilege_escalation", 
                        adminWeight, "Escalate to admin");
                graph.addEdge(adminEdge);
            }
        }

        // Privilege -> Target (lateral movement to critical assets)
        for (HostRecord record : records) {
            if ("Critical".equals(record.getAssetCriticality())) {
                String targetId = "TARGET:" + record.getHostId();
                Edge edge = new Edge("PRIVILEGE:ADMIN", targetId, "target_access", 1.0, "Reach target");
                graph.addEdge(edge);
            }
        }

        System.out.println("[INFO] Built graph with " + graph.getNodeCount() + " nodes and " 
                + graph.getEdgeCount() + " edges");
        return graph;
    }
}
