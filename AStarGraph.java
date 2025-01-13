// Implementation of a coordinate-based graph class using an adjacency matrix with the A* shortest path algorithm
// Joshua Tiffany Fall 2024

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AStarGraph {

    // Helper class to store the coordinates of a vertex
    private class Coordinate {
        private int x;
        private int y;

        // Constructor
        private Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Helper class to store edges for use in the A* algorithm
    private class Edge implements Comparable<Edge> {
        private int fromNode;
        private int toNode;
        private double curCost;
        private double estimatedCost;

        // Constructor
        private Edge(int fromNode, int toNode, double curCost, double heuristic) {
            this.fromNode = fromNode;
            this.toNode = toNode;
            this.curCost = curCost;
            estimatedCost = curCost + heuristic;
        }

        // Custom comparison function for use in priority queue
        public int compareTo(Edge edge) {
            int returnVal = 0;
            if (this.estimatedCost < edge.estimatedCost)
                returnVal = -1;
            else if (this.estimatedCost > edge.estimatedCost)
                returnVal = 1;
            return returnVal;
        }
    }

    double[][] adjacencyMatrix;
    Coordinate[] vertexCoordinates;
    String[] indexToNameMap;
    HashMap<String, Integer> nameToIndexMap;
    int numVertices;
    int numEdges;

    // Constructor
    public AStarGraph() {
        adjacencyMatrix = new double[0][0];
        vertexCoordinates = new Coordinate[0];
        indexToNameMap = new String[0];
        nameToIndexMap = new HashMap<String, Integer>();
        numVertices = 0;
        numEdges = 0;
    }

    // Read a graph in from a file
    // Return true if file is opened successfully and false otherwise
    public boolean readGraph(String fileName) {
        boolean readSuccessful = false;
        File inFile = new File(fileName);

        try {
        if (inFile.exists()) {
            nameToIndexMap.clear(); // Empty the map to remove previous graph data
        
            Scanner fileReader = new Scanner(inFile);
            numVertices = Integer.parseInt(fileReader.nextLine());
            
            adjacencyMatrix = new double[numVertices][numVertices];
            vertexCoordinates = new Coordinate[numVertices];
            indexToNameMap = new String[numVertices];

            String vertexName;
            int x;
            int y;

            // Add each vertex's information to the maps and store its coordinates
            for (int i = 0; i < numVertices; i++) {
                vertexName = fileReader.nextLine();
                x = Integer.parseInt(fileReader.nextLine());
                y = Integer.parseInt(fileReader.nextLine());

                indexToNameMap[i] = vertexName;
                nameToIndexMap.put(vertexName, i);
                vertexCoordinates[i] = (new Coordinate(x, y));
            }

            numEdges = Integer.parseInt(fileReader.nextLine());
            int fromNode;
            int toNode;
            double cost;

            // Store each edge in the adjacency matrix
            for (int i = 0; i < numEdges; i++) {
                fromNode = nameToIndexMap.get(fileReader.nextLine());
                toNode = nameToIndexMap.get(fileReader.nextLine());
                cost = Double.parseDouble(fileReader.nextLine());
                adjacencyMatrix[fromNode][toNode] = cost;
            }

            fileReader.close();
        }
        } catch (FileNotFoundException e) {
            // This should never be reached
            System.out.println("Error: file not found");
            e.printStackTrace();
        }
        
        return readSuccessful;
    }

    // Print the graph in the same format as the file it is read from
    public void printGraph() {
        System.out.println(numVertices);

        // Print vertices
        for (int i = 0; i < numVertices; i++) {
            System.out.println(indexToNameMap[i]);
            System.out.println(vertexCoordinates[i].x);
            System.out.println(vertexCoordinates[i].y);
        }

        System.out.println(numEdges);

        // Print edges
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (adjacencyMatrix[i][j] > 0) {
                    System.out.println(indexToNameMap[i]);
                    System.out.println(indexToNameMap[j]);
                    System.out.println(adjacencyMatrix[i][j]);
                }
            }
        }
    }

    // Implementation of A* algorithm to find the shortest path between two nodes
    // Returns the length of the path or -1 if there is not a path
    public double findShortestPath(String startNode, String goalNode) {
        // Convert edges to their index values
        int startIndex = nameToIndexMap.get(startNode);
        int goalIndex = nameToIndexMap.get(goalNode);

        boolean[] vertexFound = new boolean[numVertices];
        int[] prevVertex = new int[numVertices];
        double[] vertexCost = new double[numVertices];

        PriorityQueue<Edge> pQueue = new PriorityQueue<>();

        Edge curEdge = new Edge(-1, startIndex, 0, estimateFutureCost(startIndex, goalIndex));
        pQueue.offer(curEdge);

        // Main loop of A* algorithm, runs until the goal vertex is found or there are no more edges to search
        while (!vertexFound[goalIndex] && !pQueue.isEmpty()) {
            curEdge = pQueue.poll();

            if (!vertexFound[curEdge.toNode]) {
                // Update arrays
                vertexFound[curEdge.toNode] = true;
                prevVertex[curEdge.toNode] = curEdge.fromNode;
                vertexCost[curEdge.toNode] = curEdge.curCost;

                // If the current edge is not the goal, add any of its edges that have not been found to the priority queue
                if (curEdge.toNode != goalIndex)
                    for (int i = 0; i < numVertices; i++)
                        if (adjacencyMatrix[curEdge.toNode][i] != 0 && !vertexFound[i])
                            pQueue.offer(new Edge(curEdge.toNode, i, curEdge.curCost + adjacencyMatrix[curEdge.toNode][i], estimateFutureCost(curEdge.toNode, goalIndex)));
            }
        }

        double totalCost = -1;

        if (vertexFound[goalIndex]) {
            totalCost = curEdge.curCost;
            printPath(goalIndex, prevVertex);   
            System.out.println("\nTotal cost: " + totalCost);  
        } else {
            System.out.println("No path from " + startNode + " to " + goalNode + " exists");
        }

        return totalCost;
    }

    // Heuristic function that returns the straight-line distance from the current node to the goal
    // The function is admissible because the edge length between two nodes cannot be less than its straight-line distance,
    // so it will never overestimate a path length
    private double estimateFutureCost(int curNode, int toNode) {
        double xDif = vertexCoordinates[toNode].x - vertexCoordinates[curNode].x;
        double yDif = vertexCoordinates[toNode].y - vertexCoordinates[curNode].y;
        return Math.sqrt((xDif * xDif + yDif * yDif));
    }

    // Helper function to recursively print the path to a goal node
    private void printPath(int curIndex, int[] prevVertex) {
        if (prevVertex[curIndex] != -1) {
            printPath(prevVertex[curIndex], prevVertex);
            System.out.print(" --> "); 
        }
        System.out.print(indexToNameMap[curIndex]);
    }
}