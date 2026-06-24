package com.aacm.analysis;

import com.aacm.graph.AttackGraph;
import com.aacm.model.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RiskAnalyzer class - Analyzes paths and computes risk metrics.
 */
public class RiskAnalyzer {

    /**
     * Compute risk score for a path based on various factors
     */
    public static double computePathRisk(AttackGraph graph, Path path) {
        if (path.isEmpty()) {
            return 0.0;
        }

        // Risk = Total weight of path (lower = easier/higher risk)
        // Normalized to 0-100 scale
        double riskScore = path.getTotalWeight();
        return Math.min(100.0, riskScore * 10.0); // Scale to 0-100
    }

    /**
     * Compute average risk across multiple paths
     */
    public static double computeAverageRisk(AttackGraph graph, List<Path> paths) {
        if (paths.isEmpty()) {
            return 0.0;
        }
        
        return paths.stream()
                .mapToDouble(p -> computePathRisk(graph, p))
                .average()
                .orElse(0.0);
    }

    /**
     * Find the most critical path (highest risk)
     */
    public static Path findCriticalPath(AttackGraph graph, List<Path> paths) {
        return paths.stream()
                .max((p1, p2) -> Double.compare(computePathRisk(graph, p1), computePathRisk(graph, p2)))
                .orElse(new Path());
    }

    /**
     * Compute node centrality (how many critical paths pass through this node)
     */
    public static Map<String, Integer> computeNodeCentrality(List<Path> paths) {
        Map<String, Integer> centrality = new HashMap<>();
        
        for (Path path : paths) {
            for (String node : path.getNodes()) {
                centrality.put(node, centrality.getOrDefault(node, 0) + 1);
            }
        }
        
        return centrality;
    }

    /**
     * Get attack chain statistics
     */
    public static String getStatistics(List<Path> paths) {
        if (paths.isEmpty()) {
            return "No paths found";
        }
        
        double avgLength = paths.stream()
                .mapToInt(Path::getLength)
                .average()
                .orElse(0.0);
        
        double avgWeight = paths.stream()
                .mapToDouble(Path::getTotalWeight)
                .average()
                .orElse(0.0);
        
        int minLength = paths.stream()
                .mapToInt(Path::getLength)
                .min()
                .orElse(0);
        
        int maxLength = paths.stream()
                .mapToInt(Path::getLength)
                .max()
                .orElse(0);
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Total Paths: %d%n", paths.size()));
        sb.append(String.format("Average Path Length: %.2f%n", avgLength));
        sb.append(String.format("Min Path Length: %d%n", minLength));
        sb.append(String.format("Max Path Length: %d%n", maxLength));
        sb.append(String.format("Average Path Weight: %.2f%n", avgWeight));
        
        return sb.toString();
    }
}
