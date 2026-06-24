package com.aacm.gui;

import com.aacm.algorithm.PathFinder;
import com.aacm.analysis.RiskAnalyzer;
import com.aacm.graph.AttackGraph;
import com.aacm.graph.GraphBuilder;
import com.aacm.model.HostRecord;
import com.aacm.model.Node;
import com.aacm.model.Edge;
import com.aacm.model.Path;
import com.aacm.parser.DataParser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WebGuiServer {

    public static void start() {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.http.maxRequestSize = 50_000_000L; // 50MB
        }).start(8080);

        System.out.println("[INFO] Web GUI started! Open http://localhost:8080 in your browser.");

        app.post("/api/analyze", WebGuiServer::handleAnalyze);
    }

    private static void handleAnalyze(Context ctx) {
        try {
            UploadedFile uploadedFile = ctx.uploadedFile("dataset");
            String algorithm = ctx.formParam("algorithm");
            String startNode = ctx.formParam("startNode");
            String targetNode = ctx.formParam("targetNode");

            if (uploadedFile == null) {
                ctx.status(400).json(Map.of("error", "No dataset uploaded"));
                return;
            }

            if (startNode == null || startNode.isBlank()) startNode = "Attacker";
            if (targetNode == null || targetNode.isBlank()) targetNode = "TARGET:H4";
            if (algorithm == null || algorithm.isBlank()) algorithm = "dfs";

            // Save uploaded file temporarily
            String tempFileName = "data/temp_upload_" + System.currentTimeMillis() + "_" + uploadedFile.filename();
            File tempDir = new File("data");
            if (!tempDir.exists()) tempDir.mkdirs();
            
            File tempFile = new File(tempFileName);
            try (java.io.InputStream is = uploadedFile.content();
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(is.readAllBytes());
            }

            // Parse Data
            List<HostRecord> records;
            if (uploadedFile.filename().toLowerCase().endsWith(".json")) {
                records = DataParser.parseJSON(tempFileName);
            } else {
                records = DataParser.parseCSV(tempFileName);
            }

            // Cleanup temp file
            tempFile.delete();

            if (records.isEmpty()) {
                ctx.status(400).json(Map.of("error", "Failed to parse records or file is empty"));
                return;
            }

            // Build Graph
            AttackGraph graph = GraphBuilder.buildGraph(records);

            // Find Paths
            List<Path> paths = new ArrayList<>();
            switch (algorithm.toLowerCase()) {
                case "dfs":
                    paths = PathFinder.dfsAllPaths(graph, startNode, targetNode, 10);
                    break;
                case "bfs":
                    Path pBfs = PathFinder.bfsShortestPath(graph, startNode, targetNode);
                    if (!pBfs.isEmpty()) paths.add(pBfs);
                    break;
                case "dijkstra":
                    Path pDijk = PathFinder.dijkstraShortestPath(graph, startNode, targetNode);
                    if (!pDijk.isEmpty()) paths.add(pDijk);
                    break;
                default:
                    ctx.status(400).json(Map.of("error", "Unknown algorithm: " + algorithm));
                    return;
            }

            // Compute statistics
            double avgRisk = 0.0;
            if (!paths.isEmpty()) {
                avgRisk = RiskAnalyzer.computeAverageRisk(graph, paths);
            }

            // Build JSON Response
            Map<String, Object> response = new HashMap<>();
            
            // Format nodes for Vis.js
            List<Map<String, Object>> nodesList = new ArrayList<>();
            for (Node n : graph.getAllNodes()) {
                Map<String, Object> nodeMap = new HashMap<>();
                nodeMap.put("id", n.getId());
                nodeMap.put("label", n.getId());
                nodeMap.put("group", n.getType().toString());
                nodeMap.put("title", "Type: " + n.getType() + "<br>ID: " + n.getId());
                nodesList.add(nodeMap);
            }
            response.put("nodes", nodesList);

            // Format edges for Vis.js
            List<Map<String, Object>> edgesList = new ArrayList<>();
            for (Node n : graph.getAllNodes()) {
                for (Edge e : graph.getOutgoingEdges(n.getId())) {
                    Map<String, Object> edgeMap = new HashMap<>();
                    edgeMap.put("from", n.getId());
                    edgeMap.put("to", e.getDestinationId());
                    edgeMap.put("label", String.format("%.2f", e.getWeight()));
                    edgeMap.put("arrows", "to");
                    edgesList.add(edgeMap);
                }
            }
            response.put("edges", edgesList);

            // Format paths
            List<Map<String, Object>> pathsList = new ArrayList<>();
            for (Path p : paths) {
                Map<String, Object> pathMap = new HashMap<>();
                pathMap.put("formatted", p.formatPath());
                pathMap.put("weight", p.getTotalWeight());
                pathMap.put("length", p.getNodes().size());
                pathMap.put("nodes", p.getNodes());
                pathsList.add(pathMap);
            }
            
            // Sort paths by weight descending to match CLI behavior
            pathsList.sort((a, b) -> Double.compare((Double) b.get("weight"), (Double) a.get("weight")));
            
            response.put("paths", pathsList);
            response.put("avgRisk", avgRisk);
            response.put("graphSize", Map.of("nodes", graph.getAllNodes().size(), "edges", edgesList.size()));

            ctx.json(response);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}
