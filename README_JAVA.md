# AACM - Automated Attack Chain Mapper (Java)

A graph-based cybersecurity analysis tool that maps attack paths through networks using **DFS**, **BFS**, and **Dijkstra** algorithms.

This is a Java port of the original C++ AACM project, maintaining full feature parity with the C++ version.

## Architecture

```
src/main/java/com/aacm/
├── AACM.java              # Main entry point and CLI
├── algorithm/
│   └── PathFinder.java    # DFS, BFS, Dijkstra implementations
├── analysis/
│   └── RiskAnalyzer.java  # Risk metrics and path analysis
├── graph/
│   ├── AttackGraph.java   # Core graph data structure
│   └── GraphBuilder.java  # Graph construction from network data
├── model/
│   ├── Node.java          # Graph vertices
│   ├── Edge.java          # Graph edges
│   ├── Path.java          # Attack chain representation
│   └── HostRecord.java    # Network scan data
├── output/
│   ├── Reporter.java      # Report generation
│   └── Visualizer.java    # Graphviz DOT visualization
└── parser/
    └── DataParser.java    # CSV and JSON parsing
```

## Building

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Compile
```bash
mvn clean package
```

This creates an executable JAR file: `target/aacm.jar`

## Usage

### Basic Usage
```bash
java -jar target/aacm.jar --input data/sample_dataset.csv --start Attacker --target "TARGET:H4" --algorithm dfs
```

### Command-line Options
- `--input <file>` - Input data file (CSV or JSON) **[REQUIRED]**
- `--start <node>` - Starting node ID (default: `Attacker`)
- `--target <node>` - Target node ID (default: `TARGET:H4`)
- `--algorithm <alg>` - Algorithm: `dfs`, `bfs`, `dijkstra` (default: `dfs`)
- `--max-depth <N>` - Max DFS path depth (default: `10`)
- `--output <dir>` - Output directory (default: `output/`)
- `--visualize` - Generate DOT visualization file
- `--report` - Generate detailed analysis report
- `--help` - Show help message

### Examples

**Find all attack paths using DFS:**
```bash
java -jar target/aacm.jar --input data/sample_dataset.csv --algorithm dfs --max-depth 12
```

**Find shortest unweighted path using BFS:**
```bash
java -jar target/aacm.jar --input data/sample_dataset.csv --algorithm bfs
```

**Find minimum-weight path using Dijkstra:**
```bash
java -jar target/aacm.jar --input data/sample_dataset.json --algorithm dijkstra --report --visualize
```

**Generate report and visualization:**
```bash
java -jar target/aacm.jar --input data/sample_dataset.csv --report --visualize --output results/
```

## Input Data Formats

### CSV Format
Expected columns (in order):
```
HostID,IP,Hostname,Port,Service,Version,VulnID,Severity,ExploitabilityScore,AssetCriticality,Reachability
```

Example:
```
H1,192.168.1.10,webserver,80,HTTP,2.4.49,CVE-2021-41773,High,0.8,Critical,Attacker->H1
H2,192.168.1.11,database,3306,MySQL,5.7.32,CVE-2020-14556,High,0.7,Critical,H1->H2
```

### JSON Format
```json
{
  "hosts": [
    {
      "host_id": "H1",
      "ip": "192.168.1.10",
      "hostname": "webserver",
      "asset_criticality": "Critical",
      "reachability": ["Attacker->H1"],
      "services": [
        {
          "port": 80,
          "service": "HTTP",
          "version": "2.4.49",
          "vulnerabilities": [
            {
              "vuln_id": "CVE-2021-41773",
              "severity": "High",
              "exploitability_score": 0.8
            }
          ]
        }
      ]
    }
  ]
}
```

## Output

### Report (--report)
Generates a `report.txt` file containing:
- Analysis parameters
- All discovered attack paths
- Critical path (highest risk)
- Node centrality analysis
- Risk metrics

### Visualization (--visualize)
Generates an `attack_graph.dot` file in Graphviz format that can be rendered with:
```bash
dot -Tpng attack_graph.dot -o attack_graph.png
```

## Key Classes

### Core Components

**AttackGraph**
- Adjacency list representation of the attack graph
- O(1) average-case node and edge lookups
- Methods: `addNode()`, `addEdge()`, `getNeighbors()`, `getEdgeWeight()`

**PathFinder**
- DFS: Enumerates all possible attack chains
- BFS: Finds shortest unweighted path
- Dijkstra: Finds minimum-weight path
- Supports cycle detection and depth limiting

**GraphBuilder**
- Converts network scan data (HostRecords) into graph nodes and edges
- Automatically creates:
  - Host nodes with metadata (IP, criticality)
  - Service nodes with port/version info
  - Vulnerability nodes with severity
  - Privilege nodes (USER, ADMIN)
  - Target nodes (critical assets)

**RiskAnalyzer**
- Computes risk scores for paths
- Identifies critical paths
- Analyzes node centrality
- Generates statistics

## Algorithms

### Depth-First Search (DFS)
- **Purpose:** Find all possible attack chains
- **Time Complexity:** O(V + E) per traversal; exponential for enumeration
- **Space Complexity:** O(V) for stack and visited tracking
- **Best for:** Complete attack path enumeration with depth limiting

### Breadth-First Search (BFS)
- **Purpose:** Find shortest unweighted path
- **Time Complexity:** O(V + E)
- **Space Complexity:** O(V)
- **Best for:** Fewest hops attack path

### Dijkstra's Algorithm
- **Purpose:** Find minimum-weight (lowest-resistance) path
- **Time Complexity:** O((V + E) log V)
- **Space Complexity:** O(V)
- **Best for:** Fastest/easiest attack path considering edge weights

## Testing

Run unit tests:
```bash
mvn test
```

Tests cover:
- Graph operations (add nodes/edges, queries)
- Pathfinding algorithms (DFS, BFS, Dijkstra)
- Graph building from host records
- Risk analysis metrics

## Dependencies

- **gson** (2.10.1) - JSON parsing
- **commons-csv** (1.10.0) - CSV parsing
- **commons-lang3** (3.13.0) - Utility functions
- **junit** (4.13.2) - Testing framework

## Comparison with C++ Version

| Feature | Java | C++ |
|---------|------|-----|
| Core Algorithms | ✓ | ✓ |
| CSV Parsing | ✓ | ✓ |
| JSON Parsing | ✓ | ✓ |
| Report Generation | ✓ | ✓ |
| DOT Visualization | ✓ | ✓ |
| Risk Analysis | ✓ | ✓ |
| **Enhancements** | **✓** | **-** |
| Maven build system | ✓ | - |
| Standard library dependencies | ✓ | Header-only json |
| Cross-platform compatibility | ✓ | ✓ |

## Performance Notes

- Java version uses standard library collections (HashMap, ArrayList)
- Garbage collection may impact real-time applications with very large graphs
- For graphs > 100K nodes, consider increasing JVM heap: `java -Xmx4g -jar aacm.jar ...`

## Future Improvements

- Add support for weighted BFS
- Implement parallel pathfinding for large graphs
- Add interactive UI/web interface
- Support for graph filtering and queries
- Integration with vulnerability databases (NVD)

## License

[Specify your license here - GPL, MIT, etc.]

## Contributing

Contributions welcome! Please ensure:
- All tests pass: `mvn test`
- Code compiles without warnings: `mvn clean compile`
- New features include unit tests

## Authors

Original C++ version: [Original Author]
Java port: [Your Name/Organization]
