package com.aacm.output;

import com.aacm.analysis.RiskAnalyzer;
import com.aacm.graph.AttackGraph;
import com.aacm.model.Path;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Reporter class - Generates detailed analysis reports
 */
public class Reporter {

    /**
     * Generate a comprehensive analysis report
     */
    public static void generateReport(AttackGraph graph, List<Path> paths, 
                                    String startNode, String targetNode, String algorithm,
                                    String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("================================================================================\n");
            writer.write("AACM - AUTOMATED ATTACK CHAIN MAPPER\n");
            writer.write("Analysis Report\n");
            writer.write("================================================================================\n\n");

            // Report metadata
            writer.write("Report Generated: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");

            // Analysis parameters
            writer.write("ANALYSIS PARAMETERS\n");
            writer.write("-------------------\n");
            writer.write("Start Node: " + startNode + "\n");
            writer.write("Target Node: " + targetNode + "\n");
            writer.write("Algorithm: " + algorithm.toUpperCase() + "\n");
            writer.write("Graph Size: " + graph.getNodeCount() + " nodes, " + graph.getEdgeCount() + " edges\n\n");

            // Results summary
            writer.write("ANALYSIS RESULTS\n");
            writer.write("----------------\n");
            writer.write("Paths Found: " + paths.size() + "\n");

            if (!paths.isEmpty()) {
                writer.write("Average Path Length: " + String.format("%.2f",
                        paths.stream().mapToInt(Path::getLength).average().orElse(0.0)) + "\n");
                writer.write("Average Risk Score: " + String.format("%.2f",
                        RiskAnalyzer.computeAverageRisk(graph, paths)) + "\n\n");

                // Critical path analysis
                Path criticalPath = RiskAnalyzer.findCriticalPath(graph, paths);
                if (!criticalPath.isEmpty()) {
                    writer.write("CRITICAL PATH (Highest Risk)\n");
                    writer.write("----------------------------\n");
                    writer.write("Path: " + criticalPath.formatPath() + "\n");
                    writer.write("Length: " + criticalPath.getLength() + " nodes\n");
                    writer.write("Total Weight: " + String.format("%.2f", criticalPath.getTotalWeight()) + "\n");
                    writer.write("Risk Score: " + String.format("%.2f", 
                            RiskAnalyzer.computePathRisk(graph, criticalPath)) + "\n\n");
                }

                // Detailed path listing
                writer.write("DETAILED ATTACK PATHS\n");
                writer.write("---------------------\n");
                for (int i = 0; i < Math.min(paths.size(), 20); i++) {
                    Path path = paths.get(i);
                    writer.write(String.format("Path %d: %s%n", i + 1, path.formatPath()));
                    writer.write(String.format("  Length: %d, Weight: %.2f%n", 
                            path.getLength(), path.getTotalWeight()));
                }

                if (paths.size() > 20) {
                    writer.write(String.format("... and %d more paths\n\n", paths.size() - 20));
                }

                // Node centrality analysis
                writer.write("\nNODE CENTRALITY ANALYSIS\n");
                writer.write("------------------------\n");
                Map<String, Integer> centrality = RiskAnalyzer.computeNodeCentrality(paths);
                centrality.entrySet().stream()
                        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                        .limit(10)
                        .forEach(e -> {
                            try {
                                writer.write(String.format("  %s: appears in %d paths%n", 
                                        e.getKey(), e.getValue()));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });

                // Statistics
                writer.write("\nSTATISTICS\n");
                writer.write("----------\n");
                writer.write(RiskAnalyzer.getStatistics(paths));
            } else {
                writer.write("No attack paths found from " + startNode + " to " + targetNode + "\n");
            }

            writer.write("\n================================================================================\n");
            System.out.println("[INFO] Generated analysis report: " + filename);
        } catch (IOException e) {
            System.err.println("[ERROR] Could not write report file: " + filename);
            e.printStackTrace();
        }
    }
}
