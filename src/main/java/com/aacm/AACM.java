package com.aacm;

import com.aacm.algorithm.PathFinder;
import com.aacm.analysis.RiskAnalyzer;
import com.aacm.graph.AttackGraph;
import com.aacm.graph.GraphBuilder;
import com.aacm.model.HostRecord;
import com.aacm.model.Path;
import com.aacm.output.Reporter;
import com.aacm.output.Visualizer;
import com.aacm.parser.DataParser;

import java.io.File;
import java.util.List;

/**
 * AACM - Automated Attack Chain Mapper
 *
 * A graph-based cybersecurity analysis tool that maps attack paths through
 * networks using DFS, BFS, and Dijkstra algorithms.
 *
 * Usage:
 *   java -jar aacm.jar --input data/sample_dataset.csv --start Attacker --target "TARGET:H4" --algorithm dfs
 *   java -jar aacm.jar --input data/sample_dataset.json --start Attacker --target "TARGET:H4" --algorithm dijkstra --report
 *   java -jar aacm.jar --input data/sample_dataset.csv --algorithm bfs --visualize
 */
public class AACM {

    private static class Config {
        String inputFile = "";
        String startNode = "Attacker";
        String targetNode = "TARGET:H4";
        String algorithm = "dfs";
        int maxDepth = 10;
        String outputDir = "output";
        boolean generateReport = false;
        boolean generateVisualization = false;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("[INFO] No CLI arguments provided. Starting Web GUI...");
            com.aacm.gui.WebGuiServer.start();
            return;
        }

        Config config = parseArguments(args);

        if (config.inputFile.isEmpty()) {
            printUsage();
            System.exit(1);
        }

        try {
            // Ensure output directory exists
            ensureDirectory(config.outputDir);

            // Parse input data
            System.out.println("[INFO] Reading input file: " + config.inputFile);
            List<HostRecord> records;

            if (config.inputFile.endsWith(".json")) {
                records = DataParser.parseJSON(config.inputFile);
            } else {
                records = DataParser.parseCSV(config.inputFile);
            }

            if (records.isEmpty()) {
                System.err.println("[ERROR] No records parsed from input file");
                System.exit(1);
            }

            // Build attack graph
            System.out.println("[INFO] Building attack graph...");
            AttackGraph graph = GraphBuilder.buildGraph(records);

            // Find attack paths
            System.out.println("[INFO] Finding attack paths using " + config.algorithm.toUpperCase() + "...");
            List<Path> paths;

            switch (config.algorithm.toLowerCase()) {
                case "dfs":
                    paths = PathFinder.dfsAllPaths(graph, config.startNode, config.targetNode, config.maxDepth);
                    break;
                case "bfs":
                    Path singlePath = PathFinder.bfsShortestPath(graph, config.startNode, config.targetNode);
                    paths = singlePath.isEmpty() ? List.of() : List.of(singlePath);
                    break;
                case "dijkstra":
                    singlePath = PathFinder.dijkstraShortestPath(graph, config.startNode, config.targetNode);
                    paths = singlePath.isEmpty() ? List.of() : List.of(singlePath);
                    break;
                default:
                    System.err.println("[ERROR] Unknown algorithm: " + config.algorithm);
                    printUsage();
                    System.exit(1);
                    return;
            }

            // Display results
            System.out.println();
            System.out.println("========================================");
            System.out.println("ATTACK PATH ANALYSIS RESULTS");
            System.out.println("========================================");
            System.out.println("Paths found: " + paths.size());

            if (!paths.isEmpty()) {
                // Show top 5 paths
                System.out.println();
                System.out.println("Top Paths:");
                for (int i = 0; i < Math.min(paths.size(), 5); i++) {
                    Path path = paths.get(i);
                    System.out.println(String.format("  %d. %s (weight: %.2f)",
                            i + 1, path.formatPath(), path.getTotalWeight()));
                }

                if (paths.size() > 5) {
                    System.out.println("  ... and " + (paths.size() - 5) + " more");
                }

                System.out.println();
                System.out.println("Average Risk Score: " +
                        String.format("%.2f", RiskAnalyzer.computeAverageRisk(graph, paths)));
                System.out.println();
            } else {
                System.out.println("No attack paths found from " + config.startNode + " to " + config.targetNode);
            }

            // Generate optional outputs
            if (config.generateVisualization) {
                String dotFile = config.outputDir + File.separator + "attack_graph.dot";
                Visualizer.generateDotFile(graph, paths, dotFile);
            }

            if (config.generateReport) {
                String reportFile = config.outputDir + File.separator + "report.txt";
                Reporter.generateReport(graph, paths, config.startNode, config.targetNode, 
                        config.algorithm, reportFile);
            }

            System.out.println();
            System.out.println("Analysis complete!");

        } catch (Exception e) {
            System.err.println("[ERROR] Analysis failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Config parseArguments(String[] args) {
        Config config = new Config();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--input":
                    if (i + 1 < args.length) config.inputFile = args[++i];
                    break;
                case "--start":
                    if (i + 1 < args.length) config.startNode = args[++i];
                    break;
                case "--target":
                    if (i + 1 < args.length) config.targetNode = args[++i];
                    break;
                case "--algorithm":
                    if (i + 1 < args.length) config.algorithm = args[++i];
                    break;
                case "--max-depth":
                    if (i + 1 < args.length) {
                        try {
                            config.maxDepth = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException ignored) {}
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) config.outputDir = args[++i];
                    break;
                case "--visualize":
                    config.generateVisualization = true;
                    break;
                case "--report":
                    config.generateReport = true;
                    break;
                case "--help":
                case "-h":
                    printUsage();
                    System.exit(0);
                    break;
            }
        }

        return config;
    }

    private static void printUsage() {
        System.out.println("AACM - Automated Attack Chain Mapper v1.0");
        System.out.println();
        System.out.println("Usage: java -jar aacm.jar [options]");
        System.out.println();
        System.out.println("Required:");
        System.out.println("  --input <file>        Input data file (CSV or JSON)");
        System.out.println();
        System.out.println("Optional:");
        System.out.println("  --start <node>        Starting node ID (default: Attacker)");
        System.out.println("  --target <node>       Target node ID (default: TARGET:H4)");
        System.out.println("  --max-depth <N>       Max DFS path depth (default: 10)");
        System.out.println("  --algorithm <alg>     Algorithm: dfs, bfs, dijkstra (default: dfs)");
        System.out.println("  --output <dir>        Output directory (default: output/)");
        System.out.println("  --visualize           Generate DOT visualization file");
        System.out.println("  --report              Generate detailed report file");
        System.out.println("  --help                Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar aacm.jar --input data/sample_dataset.csv --start Attacker --target TARGET:H4");
        System.out.println("  java -jar aacm.jar --input data/sample_dataset.csv --algorithm dijkstra --report --visualize");
        System.out.println("  java -jar aacm.jar --input data/sample_dataset.json --algorithm bfs --max-depth 8");
        System.out.println();
    }

    private static boolean ensureDirectory(String dir) {
        File directory = new File(dir);
        if (directory.exists()) {
            if (directory.isDirectory()) {
                return true;
            }
            System.err.println("[WARNING] Path exists but is not a directory: " + dir);
            return false;
        }

        if (directory.mkdirs()) {
            System.out.println("[INFO] Created output directory: " + dir);
            return true;
        }

        System.err.println("[WARNING] Could not create directory '" + dir + "'");
        return false;
    }
}
