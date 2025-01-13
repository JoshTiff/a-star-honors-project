// Tester class for A* algorithm with EightPuzzle and [] classes
// Joshua Tiffany Fall 2024

public class AStarTester {
    public static void main(String[] args) {
        boolean testsSuccessful = true;
        
        if (graphSearches()) {
            System.out.println("A* graph tests passed!");
        } else {
            testsSuccessful = false;
            System.out.println("At least one graph search failed to return the correct path length");
        }

        if (possibleEightPuzzleSearches()) {
            System.out.println("Eight puzzle with solution tests passed!");
        } else {
            testsSuccessful = false;
            System.out.println("At least one eight puzzle search with a solution failed to return the shortest path");
        }

        if (impossibleEightPuzzleSearches()) {
            System.out.println("Eight puzzle without solution tests passed!");
        } else {
            testsSuccessful = false;
            System.out.println("At least one eight puzzle search without a solution failed");
        }

        if (testsSuccessful)
            System.out.println("All tests passed!");
        else
            System.out.println("At least one test failed");
    }
    // Tester function for A* graphs searches
    public static boolean graphSearches() {
        boolean testsSuccessful = true;
        double maxDifference = 0.00001; // Value used to compare two doubles

        AStarGraph graph = new AStarGraph();
        graph.readGraph("graph.txt");
        graph.printGraph();

        if (Math.abs(graph.findShortestPath("C", "E") - 13.55) > maxDifference)
            testsSuccessful = false;

        if (Math.abs(graph.findShortestPath("A", "F") - 2.9) > maxDifference)
            testsSuccessful = false;

        if (Math.abs(graph.findShortestPath("A", "C") - 21.25) > maxDifference)
            testsSuccessful = false;

        if (Math.abs(graph.findShortestPath("D", "F") - 8.7) > maxDifference)
            testsSuccessful = false;

        if (Math.abs(graph.findShortestPath("F", "C") + 1) > maxDifference)
            testsSuccessful = false;

        return testsSuccessful;
    }

    // Tester function for eight puzzles that have a solution
    public static boolean possibleEightPuzzleSearches() {
        boolean testsSuccessful = true;

        int[][] board = {{1, 8, 2}, {0, 4, 3}, {7, 6, 5}};
        int[][] board2 = {{4, 1, 5}, {2, 7, 6}, {0, 3, 8}};
        int[][] board3 = {{7, 3, 8}, {4, 0, 1}, {5, 2, 6}};

        EightPuzzle myPuzzle = new EightPuzzle();
        myPuzzle.setBoardState(board);

        if (myPuzzle.findShortestPath() != 9)
            testsSuccessful = false;
        
        myPuzzle.setBoardState(board2);
        
        if (myPuzzle.findShortestPath() != 16)
            testsSuccessful = false;

        myPuzzle.setBoardState(board3);
            
        if (myPuzzle.findShortestPath() != 24)
            testsSuccessful = false;
            
        return testsSuccessful;
    }
    
    // Tester function for eight puzzles that do not have a solution
    public static boolean impossibleEightPuzzleSearches() {
        boolean testsSuccessful = true;

        int[][] board = {{8, 1, 2}, {0, 4, 3}, {7, 6, 5}};
        int[][] board2 = {{1, 0, 3}, {2, 4, 5}, {6, 7, 8}};
        int[][] board3 = {{7, 0, 2}, {8, 5, 3}, {6, 4, 1}};

        EightPuzzle myPuzzle = new EightPuzzle();
        myPuzzle.setBoardState(board);

        if (myPuzzle.findShortestPath() != -1)
            testsSuccessful = false;
        
        myPuzzle.setBoardState(board2);
        
        if (myPuzzle.findShortestPath() != -1)
            testsSuccessful = false;

        myPuzzle.setBoardState(board3);
            
        if (myPuzzle.findShortestPath() != -1)
            testsSuccessful = false;

        return testsSuccessful;
    }
}
