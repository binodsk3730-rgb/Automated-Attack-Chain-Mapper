package com.aacm.algorithm;

import com.aacm.graph.AttackGraph;
import com.aacm.model.Path;

import java.util.*;

/**
 * PathFinder class - Implements graph traversal algorithms for attack path discovery.
 *
 * Algorithms Implemented:
 *   1. DFS (Depth-First Search) - Enumerates all possible attack chains
 *   2. BFS (Breadth-First Search) - Finds shortest unweighted path
 *   3. Dijkstra - Finds lowest-cost weighted path using priority queue
 */
public class PathFinder {

    /**
     * DFS - Enumerate all possible attack chains from start to target.
     *
     * Uses a stack (LIFO) for depth-first exploration with backtracking.
     * A depth limit prevents exponential path explosion in dense graphs.
     *
     * Time Complexity: O(V + E) per traversal, but path enumeration can grow exponentially
     * Space Complexity: O(V) for the stack and visited tracking
     */
    public static List<Path> dfsAllPaths(AttackGraph graph, String start, String target, int maxDepth) {
        List<Path> paths = new ArrayList<>();

        if (!graph.hasNode(start)) {
            System.err.println("[ERROR] DFS: Start node '" + start + "' not found.");
            return paths;
        }
        if (!graph.hasNode(target)) {
            System.err.println("[ERROR] DFS: Target node '" + target + "' not found.");
            return paths;
        }

        // Stack stores pairs of (current_node, path_list)
        Stack<Pair<String, List<String>>> stack = new Stack<>();
        stack.push(new Pair<>(start, new ArrayList<>(Collections.singletonList(start))));

        while (!stack.isEmpty()) {
            Pair<String, List<String>> current = stack.pop();
            String currentNode = current.getKey();
            List<String> currentPath = current.getValue();

            // Check if we've reached the target
            if (currentNode.equals(target)) {
                Path foundPath = new Path(currentPath);
                // Calculate total weight for this path
                double totalWeight = 0.0;
                for (int i = 0; i + 1 < currentPath.size(); i++) {
                    double w = graph.getEdgeWeight(currentPath.get(i), currentPath.get(i + 1));
                    if (w > 0) totalWeight += w;
                }
                foundPath.setTotalWeight(totalWeight);
                paths.add(foundPath);
                continue;
            }

            // Depth limit check
            if (currentPath.size() > maxDepth) {
                continue;
            }

            // Get neighbors and add to stack in reverse order to maintain natural order
            List<String> neighbors = graph.getNeighbors(currentNode);
            Set<String> pathSet = new HashSet<>(currentPath);

            // Process neighbors in reverse order
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                String neighbor = neighbors.get(i);
                // Skip if node already in path (prevents cycles)
                if (!pathSet.contains(neighbor)) {
                    List<String> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    stack.push(new Pair<>(neighbor, newPath));
                }
            }
        }

        System.out.println("[INFO] DFS found " + paths.size() + " paths from '" +
                start + "' to '" + target + "' (max_depth=" + maxDepth + ").");
        return paths;
    }

    /**
     * BFS - Find the shortest unweighted path from start to target.
     *
     * Uses a queue (FIFO) for level-by-level traversal.
     * The first time we reach the target, we've found the shortest path.
     *
     * Time Complexity: O(V + E)
     * Space Complexity: O(V)
     */
    public static Path bfsShortestPath(AttackGraph graph, String start, String target) {
        if (!graph.hasNode(start)) {
            System.err.println("[ERROR] BFS: Start node '" + start + "' not found.");
            return new Path();
        }
        if (!graph.hasNode(target)) {
            System.err.println("[ERROR] BFS: Target node '" + target + "' not found.");
            return new Path();
        }

        Queue<Pair<String, List<String>>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(new Pair<>(start, new ArrayList<>(Collections.singletonList(start))));
        visited.add(start);

        while (!queue.isEmpty()) {
            Pair<String, List<String>> current = queue.poll();
            String currentNode = current.getKey();
            List<String> currentPath = current.getValue();

            if (currentNode.equals(target)) {
                Path result = new Path(currentPath);
                // Calculate weight
                double totalWeight = 0.0;
                for (int i = 0; i + 1 < currentPath.size(); i++) {
                    double w = graph.getEdgeWeight(currentPath.get(i), currentPath.get(i + 1));
                    if (w > 0) totalWeight += w;
                }
                result.setTotalWeight(totalWeight);
                System.out.println("[INFO] BFS found path from '" + start + "' to '" + target + "'.");
                return result;
            }

            List<String> neighbors = graph.getNeighbors(currentNode);
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    List<String> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    queue.add(new Pair<>(neighbor, newPath));
                }
            }
        }

        System.out.println("[INFO] BFS: No path found from '" + start + "' to '" + target + "'.");
        return new Path();
    }

    /**
     * Dijkstra - Find the lowest-cost weighted path from start to target.
     *
     * Uses a priority queue (min-heap) to always select the lowest-cost node.
     *
     * Time Complexity: O((V + E) log V) with a standard binary heap
     * Space Complexity: O(V)
     */
    public static Path dijkstraShortestPath(AttackGraph graph, String start, String target) {
        if (!graph.hasNode(start)) {
            System.err.println("[ERROR] Dijkstra: Start node '" + start + "' not found.");
            return new Path();
        }
        if (!graph.hasNode(target)) {
            System.err.println("[ERROR] Dijkstra: Target node '" + target + "' not found.");
            return new Path();
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<Pair<Double, String>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(Pair::getKey)
        );

        // Initialize distances
        for (String nodeId : graph.getAllNodes().stream()
                .map(n -> n.getId()).toArray(String[]::new)) {
            distances.put(nodeId, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.add(new Pair<>(0.0, start));

        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            Pair<Double, String> current = pq.poll();
            String currentNode = current.getValue();
            double currentDistance = current.getKey();

            if (visited.contains(currentNode)) {
                continue;
            }
            visited.add(currentNode);

            if (currentNode.equals(target)) {
                // Reconstruct path
                List<String> path = new ArrayList<>();
                String node = target;
                while (node != null) {
                    path.add(0, node);
                    node = predecessors.get(node);
                }

                Path result = new Path(path);
                result.setTotalWeight(distances.get(target));
                System.out.println("[INFO] Dijkstra found path from '" + start + "' to '" + target +
                        "' with cost " + String.format("%.2f", distances.get(target)) + ".");
                return result;
            }

            for (String neighbor : graph.getNeighbors(currentNode)) {
                if (visited.contains(neighbor)) continue;

                double edgeWeight = graph.getEdgeWeight(currentNode, neighbor);
                double newDistance = currentDistance + edgeWeight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, currentNode);
                    pq.add(new Pair<>(newDistance, neighbor));
                }
            }
        }

        System.out.println("[INFO] Dijkstra: No path found from '" + start + "' to '" + target + "'.");
        return new Path();
    }

    /**
     * Wrapper class for key-value pairs used in algorithms
     */
    private static class Pair<K, V> {
        private K key;
        private V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K getKey() {
            return key;
        }

        V getValue() {
            return value;
        }
    }
}
