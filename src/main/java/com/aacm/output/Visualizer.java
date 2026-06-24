package com.aacm.output;

import com.aacm.graph.AttackGraph;
import com.aacm.model.Edge;
import com.aacm.model.Node;
import com.aacm.model.Path;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Visualizer class - Generates graph visualization files (DOT format)
 */
public class Visualizer {

    /**
     * Generate a Graphviz DOT file for visualization
     */
    public static void generateDotFile(AttackGraph graph, List<Path> paths, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("digraph AttackGraph {\n");
            writer.write("  rankdir=LR;\n");
            writer.write("  node [shape=box, style=rounded];\n\n");

            // Color map for different node types
            writeNodeDefinitions(writer, graph, paths);
            writeEdgeDefinitions(writer, graph, paths);

            writer.write("}\n");
            System.out.println("[INFO] Generated DOT visualization: " + filename);
        } catch (IOException e) {
            System.err.println("[ERROR] Could not write DOT file: " + filename);
            e.printStackTrace();
        }
    }

    private static void writeNodeDefinitions(FileWriter writer, AttackGraph graph, List<Path> paths) throws IOException {
        // Track which nodes are in critical paths
        for (Node node : graph.getAllNodes()) {
            String nodeId = node.getId();
            String color = getNodeColor(node);
            boolean inPath = isNodeInPaths(nodeId, paths);
            String style = inPath ? "filled" : "rounded";

            writer.write(String.format("  \"%s\" [label=\"%s\", shape=%s, style=%s, fillcolor=%s, color=black];\n",
                    nodeId, node.getLabel(), getNodeShape(node), style, color));
        }
        writer.write("\n");
    }

    private static void writeEdgeDefinitions(FileWriter writer, AttackGraph graph, List<Path> paths) throws IOException {
        for (Node source : graph.getAllNodes()) {
            for (Edge edge : graph.getOutgoingEdges(source.getId())) {
                String color = "black";
                String style = "solid";
                String label = edge.getLabel() + " (w=" + String.format("%.2f", edge.getWeight()) + ")";

                // Highlight edges in critical paths
                if (isEdgeInPaths(edge, paths)) {
                    color = "red";
                    style = "bold";
                }

                writer.write(String.format("  \"%s\" -> \"%s\" [label=\"%s\", color=%s, style=%s];\n",
                        edge.getSourceId(), edge.getDestinationId(), label, color, style));
            }
        }
    }

    private static String getNodeColor(Node node) {
        switch (node.getType()) {
            case "attacker":
                return "red";
            case "host":
                return "lightblue";
            case "service":
                return "lightgreen";
            case "vulnerability":
                return "orange";
            case "privilege":
                return "yellow";
            case "target":
                return "red";
            default:
                return "white";
        }
    }

    private static String getNodeShape(Node node) {
        switch (node.getType()) {
            case "attacker":
                return "ellipse";
            case "target":
                return "diamond";
            default:
                return "box";
        }
    }

    private static boolean isNodeInPaths(String nodeId, List<Path> paths) {
        return paths.stream()
                .anyMatch(p -> p.getNodes().contains(nodeId));
    }

    private static boolean isEdgeInPaths(Edge edge, List<Path> paths) {
        for (Path path : paths) {
            List<String> nodes = path.getNodes();
            for (int i = 0; i + 1 < nodes.size(); i++) {
                if (nodes.get(i).equals(edge.getSourceId()) &&
                    nodes.get(i + 1).equals(edge.getDestinationId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
