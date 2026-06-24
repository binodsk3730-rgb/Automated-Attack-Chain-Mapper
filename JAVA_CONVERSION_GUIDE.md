# AACM Java Conversion - Quick Start Guide

## Project Conversion Summary

Your C++ AACM project has been successfully converted to Java. Here's what was done:

### ✓ Converted Components

1. **Core Data Models**
   - `Node.java` - Graph vertices with metadata
   - `Edge.java` - Directed edges with weights
   - `Path.java` - Attack chain representation
   - `HostRecord.java` - Network scan data records

2. **Graph Engine**
   - `AttackGraph.java` - Core graph data structure (HashMap/ArrayList adjacency list)
   - `GraphBuilder.java` - Constructs graphs from network records

3. **Algorithms**
   - `PathFinder.java` - DFS (all paths), BFS (shortest), Dijkstra (min-weight)
   - All with full complexity analysis preserved

4. **Data Parsing**
   - `DataParser.java` - CSV parsing with Apache Commons CSV
   - JSON parsing with Google Gson

5. **Analysis & Output**
   - `RiskAnalyzer.java` - Risk scoring and path analysis
   - `Visualizer.java` - Graphviz DOT file generation
   - `Reporter.java` - Comprehensive report generation

6. **Application**
   - `AACM.java` - Main entry point with CLI argument parsing
   - Full feature parity with C++ version

7. **Testing**
   - `AttackGraphTest.java` - Graph operations tests
   - `PathFinderTest.java` - Algorithm tests
   - `GraphBuilderTest.java` - Graph construction tests
   - JUnit 4 framework

8. **Build Configuration**
   - `pom.xml` - Maven project configuration
   - Automatic dependency management (Gson, Commons CSV, Commons Lang3)
   - Single executable JAR creation with Maven Shade Plugin

### Project Structure

```
aacm/
├── pom.xml                          # Maven configuration
├── build.bat                        # Windows build script
├── build.sh                         # Linux/macOS build script
├── README_JAVA.md                   # Java documentation
├── src/
│   ├── main/java/com/aacm/
│   │   ├── AACM.java               # Main application
│   │   ├── algorithm/PathFinder.java
│   │   ├── analysis/RiskAnalyzer.java
│   │   ├── graph/AttackGraph.java
│   │   ├── graph/GraphBuilder.java
│   │   ├── model/Node.java
│   │   ├── model/Edge.java
│   │   ├── model/Path.java
│   │   ├── model/HostRecord.java
│   │   ├── output/Visualizer.java
│   │   ├── output/Reporter.java
│   │   └── parser/DataParser.java
│   ├── test/java/com/aacm/
│   │   ├── graph/AttackGraphTest.java
│   │   ├── graph/GraphBuilderTest.java
│   │   └── algorithm/PathFinderTest.java
│   └── resources/
├── data/                            # Input data (unchanged)
├── output/                          # Generated reports/visualizations
└── original C++ files (preserved for reference)
```

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### 1. Install Maven (if not already installed)

**Windows:**
- Download from https://maven.apache.org/download.cgi
- Extract to `C:\apache-maven-3.9.0`
- Add to PATH: `setx PATH "%PATH%;C:\apache-maven-3.9.0\bin"`
- Restart terminal

**Linux/macOS:**
```bash
# Ubuntu/Debian
sudo apt-get install maven

# Fedora/RHEL
sudo yum install maven

# macOS (with Homebrew)
brew install maven
```

### 2. Build the Project

**Option A - Using build scripts:**
```bash
# Windows
build.bat

# Linux/macOS
chmod +x build.sh
./build.sh
```

**Option B - Direct Maven commands:**
```bash
# Compile and run tests
mvn clean package

# Build without running tests
mvn clean package -DskipTests

# Run tests only
mvn test
```

### 3. Run the Application

```bash
# Show help
java -jar target/aacm.jar --help

# Example: DFS on CSV data
java -jar target/aacm.jar --input data/sample_dataset.csv --algorithm dfs

# Example: Dijkstra with report and visualization
java -jar target/aacm.jar --input data/sample_dataset.json --algorithm dijkstra --report --visualize

# Example: BFS with custom start/target
java -jar target/aacm.jar --input data/sample_dataset.csv --start Attacker --target TARGET:H4 --algorithm bfs
```

## Key Improvements Over C++ Version

1. **Better Dependency Management** - Maven automatically downloads and manages all dependencies
2. **Consistent Code Style** - Java conventions and idioms throughout
3. **Enhanced Error Handling** - More comprehensive exception handling
4. **Better Testing Framework** - JUnit 4 integrated testing
5. **No Manual JSON Header** - Uses standard Gson library instead of header-only nlohmann/json
6. **Cross-Platform** - Works seamlessly on Windows, Linux, macOS

## Dependency Details

| Library | Version | Purpose |
|---------|---------|---------|
| Gson | 2.10.1 | JSON parsing |
| Commons CSV | 1.10.0 | CSV file parsing |
| Commons Lang3 | 3.13.0 | Utility functions |
| JUnit | 4.13.2 | Unit testing |

## Performance Considerations

- **Memory**: For large graphs (>100K nodes), increase JVM heap:
  ```bash
  java -Xmx4g -jar target/aacm.jar --input large_dataset.csv
  ```

- **Algorithm Selection**:
  - **DFS**: Best for complete enumeration with small graphs
  - **BFS**: Best for shortest path in unweighted graphs
  - **Dijkstra**: Best for finding easiest/most likely attack path

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AttackGraphTest

# Generate test coverage report
mvn test jacoco:report
```

## Documentation

- **Full Documentation**: See `README_JAVA.md`
- **Code Documentation**: Javadoc comments in all classes
- **Examples**: See command-line examples in AACM.java

## Next Steps

1. Ensure Maven is installed
2. Run `build.bat` (Windows) or `./build.sh` (Linux/macOS)
3. Test with sample data: `java -jar target/aacm.jar --input data/sample_dataset.csv --help`
4. Generate visualizations: `java -jar target/aacm.jar --input data/sample_dataset.csv --visualize --report`
5. Convert DOT files to images: `dot -Tpng attack_graph.dot -o attack_graph.png`

## Troubleshooting

**Issue**: "Maven is not recognized"
- **Solution**: Ensure Maven is installed and added to PATH; restart terminal

**Issue**: "Java version not supported"
- **Solution**: Update to Java 11 or higher

**Issue**: Build takes a long time on first run
- **Solution**: Normal - Maven is downloading dependencies; subsequent builds will be faster

**Issue**: Input file not found
- **Solution**: Use absolute path or relative path from where you run the command
  ```bash
  java -jar target/aacm.jar --input data/sample_dataset.csv
  ```

## C++ to Java Conversion Details

### Data Structure Mappings

| C++ | Java | Notes |
|-----|------|-------|
| `unordered_map<string, T>` | `HashMap<String, T>` | O(1) average lookup |
| `vector<T>` | `ArrayList<T>` | Dynamic array |
| `unordered_set<T>` | `HashSet<T>` | O(1) membership check |
| `stack<T>` | `Stack<T>` | LIFO for DFS |
| `queue<T>` | `Queue<T>` | FIFO for BFS |
| `priority_queue<T>` | `PriorityQueue<T>` | Min-heap for Dijkstra |

### Function Mappings

| C++ Function | Java Method | Location |
|--------------|-------------|----------|
| `attach_graph.add_node()` | `addNode()` | AttackGraph |
| `path_finder::dfs_all_paths()` | `dfsAllPaths()` | PathFinder |
| `parser::parse_csv()` | `parseCSV()` | DataParser |
| `risk_analyzer::compute_path_risk()` | `computePathRisk()` | RiskAnalyzer |

## Support for Original Data

Your original C++ source files remain in the `src/` directory for reference.
The new Java code can read the same CSV and JSON input files without modification.

---

**Happy analyzing! 🎯**
