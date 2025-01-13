// Implementation of a class that represents the 8-puzzle in an array and finds the shortest solution with the A* algorithm
// Joshua Tiffany Fall 2024

import java.util.PriorityQueue;
import java.util.HashMap;

public class EightPuzzle {
    
    // Helper class that stores state of boards for use in A* algorithm
    private class BoardState implements Comparable<BoardState> {
        private int[][] boardData;
        private int zeroRow;
        private int zeroColumn;
        private int depth;
        private int cost;
        private BoardState parent;

        // Constructor
        private BoardState(int[][] boardData, int zeroRow, int zeroColumn, int depth, int heuristic, BoardState parent) {
            this.boardData = boardData;
            this.zeroRow = zeroRow;
            this.zeroColumn = zeroColumn;
            this.depth = depth;
            cost = depth + heuristic;
            this.parent = parent;
        }  
        
        // Custom comparator for priority queue
        public int compareTo(BoardState state) {
            return this.cost - state.cost;
        }
    }

    private int[][] board;

    // Constructor
    public EightPuzzle() {
        board = new int[3][3];
    }

    // Set the board state using a two dimensional array as input
    // Return true if the input creates a valid board, return false otherwise
    // Precondition: the array is size 3x3 and contains each integer from 0 to 8 once
    public void setBoardState(int[][] boardVals) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = boardVals[i][j];
    }

    // Implementation of A* algorithm to find solution in least possible moves
    public int findShortestPath() {
        int cost;
        
        int zeroRow = 0;
        int zeroColumn = 0;
    
        // Find the location of the empty space in the initial board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    zeroRow = i;
                    zeroColumn = j;
                }
            }
        }

        PriorityQueue<BoardState> pQueue = new PriorityQueue<>();
        BoardState curState;

        curState = new BoardState(board, zeroRow, zeroColumn, 0, estimateFutureCost(board), null);
        pQueue.offer(curState);

        HashMap<String, Boolean> boardFound = new HashMap<String, Boolean>(); // Map to store if a board layout has been found
        String key;

        // Values to make all possible moves (up down left right) in a for loop without rewriting code
        int[] rowSwap = {-1, 1, 0, 0};
        int[] columnSwap = {0, 0, -1, 1};

        int[][] newBoard;
        int newZeroRow;
        int newZeroColumn;

        boolean solutionFound = false;
        
        // Main loop for A* algorithm, runs until a solution is found or there are no more options to search
        while (!solutionFound && !pQueue.isEmpty()) {
            curState = pQueue.poll();

            if (estimateFutureCost(curState.boardData) == 0) {
                solutionFound = true;
            } else {
                key = generateKey(curState.boardData);
                if (boardFound.get(key) == null){
                    boardFound.put(key, true);
                    
                    // Check all possible moves and add them to the priority queue if they have not been searched already
                    for (int i = 0; i < 4; i++) {
                        newZeroRow = curState.zeroRow + rowSwap[i];
                        newZeroColumn = curState.zeroColumn + columnSwap[i];
                        if (newZeroRow >= 0 && newZeroRow < 3 && newZeroColumn >= 0 && newZeroColumn < 3) {
                            newBoard = copyBoard(curState.boardData);
                            newBoard[curState.zeroRow][curState.zeroColumn] = newBoard[newZeroRow][newZeroColumn];
                            newBoard[newZeroRow][newZeroColumn] = 0;
                            if (boardFound.get(generateKey(newBoard)) == null) 
                            pQueue.offer(new BoardState(newBoard, newZeroRow, newZeroColumn, curState.depth + 1, estimateFutureCost(newBoard), curState));
                        }
                    }
                }
            }
        }
        
        if (solutionFound) {
            cost = curState.depth;
            System.out.println("Total number of moves: " + cost);
            printPath(curState);
        } else {
            System.out.println("No solution found!");
            cost = -1;
        }
        return cost;
    }

    // Returns the contents of the board as a string
    public static String displayBoard(int[][] board) {
        String boardDisplay = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardDisplay += board[i][j] + " ";
            }
            boardDisplay += "\n";
        }
        return boardDisplay;
    }

    // Helper function to recursively print the steps to a solution
    private void printPath(BoardState state) {
        if (state.parent != null) {
            printPath(state.parent);
        }
        System.out.println(displayBoard(state.boardData)); 
    }

    // Helper function to create a copy of a board
    private static int[][] copyBoard(int[][] origArray) {
        int[][] newArray = new int[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                newArray[i][j] = origArray[i][j];

        return newArray;
    }

    // Heuristic function that finds the minimum number of moves for a solution
    // For each node, it finds the x and y distances from the goal coordinate and adds them together
    // This function is admissible because a solution can never take more moves than the combined x and y distance of
    // each number to its correct position, so it will not overestimate the real cost of a solution 
    private static int estimateFutureCost(int[][] board) {
        int futureCost = 0;
        int goalRow;
        int goalColumn;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != 0) {
                    goalRow = (board[i][j] - 1) / 3;
                    goalColumn = (board[i][j] - 1) % 3;
                    futureCost += (Math.abs(goalRow - i) + Math.abs(goalColumn - j));
                }
            }
        }

        return futureCost;
    }
    
    // Helper function to generate a string key for a hash table
    private static String generateKey(int[][] board) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                str.append(board[i][j]);
        return str.toString();
    }
}
