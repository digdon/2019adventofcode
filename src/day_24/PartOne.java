package day_24;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartOne {

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
                } else {
                    grid[row][col] = false;
                }
            }
        }
        
        displayGrid(grid);
        System.out.println();
        List<Integer> seenGridRatings = new ArrayList<>();
        seenGridRatings.add(calculateBioRating(grid));
        int minute = 0;
        int bioRating = 0;
        
        while (true) {
            grid = processBugs(grid);
            bioRating = calculateBioRating(grid);
            System.out.println("After " + ++minute + " minutes:");
            displayGrid(grid);
            System.out.println();
            
            // Check for a repeat
            if (seenGridRatings.contains(bioRating)) {
                break;
            } else {
                seenGridRatings.add(bioRating);
            }
        }
        
        System.out.println(bioRating);
    }

    private static final int[][] DIRECTIONS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    
    private static boolean[][] processBugs(boolean[][] origGrid) {
        boolean[][] newGrid = new boolean[origGrid.length][origGrid[0].length];

        for (int row = 0; row < origGrid.length; row++) {
            for (int col = 0; col < origGrid[row].length; col++) {
                // Start by counting the number of bugs surrounding the point
                int bugCount = 0;

                for (int i = 0; i < DIRECTIONS.length; i++) {
                    int newRow = row + DIRECTIONS[i][0];
                    int newCol = col + DIRECTIONS[i][1];
                    
                    if (newRow >= 0 && newRow < origGrid.length && newCol >= 0 && newCol < origGrid[newRow].length) {
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
        
        return newGrid;
    }
    
    private static int calculateBioRating(boolean[][] grid) {
        int rating = 0;
        int counter = 0;
        
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col]) {
                    rating += Math.pow(2, counter);
                }
                
                counter++;
            }
        }

        return rating;
    }
    
    private static void displayGrid(boolean[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                System.out.print(grid[row][col] ? '#' : '.');
            }
            
            System.out.println();
        }
    }
}
