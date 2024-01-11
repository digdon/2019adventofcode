package day_24;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PartTwo {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> inputLines = reader.lines().collect(Collectors.toList());
        reader.close();
        
        // Build the initial grid
        boolean[][] grid = new boolean[inputLines.size()][inputLines.get(0).length()];
        
        for (int row = 0; row < inputLines.size(); row++) {
            String line = inputLines.get(row);
            
            for  (int col = 0; col < line.length(); col++) {
                if (line.charAt(col) == '#') {
                    grid[row][col] = true;
                }
            }
        }
        
        Map<Integer, boolean[][]> gridMap = new TreeMap<>();
        gridMap.put(0, grid);
        
        for (int i = 0; i < 200; i++) {
            gridMap = processBugs(gridMap);
        }

        int totalBugs = 0;
        
        for (Entry<Integer, boolean[][]> entry : gridMap.entrySet()) {
            System.out.println("Depth " + entry.getKey());
            boolean[][] tempGrid = entry.getValue();
            for (int row = 0; row < tempGrid.length; row++) {
                for (int col = 0; col < tempGrid[row].length; col++) {
                    if (tempGrid[row][col]) {
                        totalBugs++;
                    }
                    
                    if (row == 2 && col == 2) {
                        System.out.print('?');
                    } else {
                        System.out.print(tempGrid[row][col] ? '#' : '.');
                    }
                }
                
                System.out.println();
            }
            
            System.out.println();
        }
        
        System.out.println(totalBugs);
    }
    
    private static final int[][] DIRECTIONS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
   
    private static Map<Integer, boolean[][]> processBugs(Map<Integer, boolean[][]> gridMap) {
        boolean[][] baseGrid = gridMap.get(0);
        int centerRow = (baseGrid.length) / 2;
        int centerCol = (baseGrid[0].length) / 2;
        int maxRow = baseGrid.length - 1;
        int maxCol = baseGrid[0].length - 1;

        Map<Integer, boolean[][]> newGridMap = new TreeMap<>();

        // Queue up the known dimensions
        Deque<Entry<Integer, boolean[][]>> dimensionQueue = new LinkedList<>(gridMap.entrySet());

        while (dimensionQueue.isEmpty() == false) {
            Entry<Integer,boolean[][]> entry = dimensionQueue.remove();
            int depth = entry.getKey();
            boolean[][] origGrid = entry.getValue();
            boolean[][] newGrid = new boolean[maxRow + 1][maxCol + 1];
            boolean processChild = false, processParent = false;
            
            for (int row = 0; row <= maxRow; row++) {
                for (int col = 0; col <= maxCol; col++) {
                    if (row == centerRow && col == centerCol) {
                        // center tile is actually a sub grid - skip over this
                        continue;
                    }
                    
                    int bugCount = 0;
                    
                    for (int i = 0; i < DIRECTIONS.length; i++) {
                        int newRow = row + DIRECTIONS[i][0];
                        int newCol = col + DIRECTIONS[i][1];
                        
                        if (newRow == centerRow && newCol == centerCol) {
                            // This is the child grid
                            int childDepth = depth + 1;
                            boolean[][] childGrid = gridMap.get(childDepth);
                            
                            if (childGrid == null) {
                                // Child grid doesn't exist - no neighbour bugs to count
                                if (origGrid[row][col]) {
                                    // Current tile has a bug, which might affect child content, so let's be sure to create and process a child grid
                                    processChild = true;
                                }
                            } else {
                                // Count appropriate child bugs
                                if (DIRECTIONS[i][0] != 0) {
                                    bugCount += countBugsByRow(childGrid, DIRECTIONS[i][0] == 1 ? 0 : maxRow);
                                } else {
                                    bugCount += countBugsByCol(childGrid, DIRECTIONS[i][1] == 1 ? 0 : maxCol);
                                }
                            }
                        } else if (newRow < 0 || newRow > maxRow || newCol < 0 || newCol > maxCol) {
                            // This is the parent grid
                            int parentDepth = depth - 1;
                            boolean[][] parentGrid = gridMap.get(parentDepth);
                            
                            if (parentGrid == null) {
                                // Parent grid doesn't exist - no neighbour bugs to count
                                if (origGrid[row][col]) {
                                    // Current tile has a bug, which might affect parent content, so let's be sure to create and process a parent grid
                                    processParent = true;
                                }
                            } else {
                                // Count appropriate parent bugs
                                int parentRow = centerRow + DIRECTIONS[i][0];
                                int parentCol = centerCol + DIRECTIONS[i][1];
                                
                                if (parentGrid[parentRow][parentCol]) {
                                    bugCount++;
                                }
                            }
                        } else {
                            if (origGrid[newRow][newCol]) {
                                bugCount++;
                            }
                        }
                    }
                    
                    if (origGrid[row][col]) {
                        newGrid[row][col] = (bugCount == 1);
                    } else {
                        newGrid[row][col] = (bugCount == 1 || bugCount == 2);
                    }
                }
            }

            newGridMap.put(depth, newGrid);
            
            if (processChild) {
                dimensionQueue.add(new AbstractMap.SimpleEntry<>(depth + 1, new boolean[maxRow + 1][maxCol +1]));
            }
            
            if (processParent) {
                dimensionQueue.add(new AbstractMap.SimpleEntry<>(depth - 1, new boolean[maxRow + 1][maxCol +1]));
            }
        }
        
        return newGridMap;
    }
    
    private static int countBugsByRow(boolean[][] grid, int row) {
        int bugCount = 0;
        
        for (int col = 0; col < grid[row].length; col++) {
            if (grid[row][col]) {
                bugCount++;
            }
        }
        
        return bugCount;
    }
    
    private static int countBugsByCol(boolean[][] grid, int col) {
        int bugCount = 0;
        
        for (int row = 0; row < grid.length; row++) {
            if (grid[row][col]) {
                bugCount++;
            }
        }
        
        return bugCount;
    }
}
