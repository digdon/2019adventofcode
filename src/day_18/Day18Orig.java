package day_18;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18Orig {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> inputLines = reader.lines().collect(Collectors.toList());
        reader.close();
        
        // Convert to grid and look for start position
        int startRow = 0;
        int startCol = 0;
        int targetKeySet = 0;
        
        char[][] grid = new char[inputLines.size()][];
        
        for (int row = 0; row < inputLines.size(); row++) {
            String line = inputLines.get(row);
            grid[row] = new char[line.length()];
            
            for (int col = 0; col < line.length(); col++) {
                char ch = line.charAt(col);
                grid[row][col] = ch;

                if (ch >= 'a' && ch <= 'z') {
                    // Found a key
                    targetKeySet |= (1 << (ch - 'a'));
                } else if (ch == '@') {
                    startRow = row;
                    startCol = col;
                }
            }
        }
        
        long start = System.currentTimeMillis();
        System.out.println("Part 1:" + partOne(grid, startRow, startCol, targetKeySet));
        System.out.println(System.currentTimeMillis() - start);
    }
    
    private static int partOne(char[][]grid, int startRow, int startCol, int targetKeySet) {
        // BFS to find all of the keys
        PointData startPoint = new PointData(startRow, startCol, 0);
        Set<PointData> visited = new HashSet<>();
        visited.add(startPoint);
        Deque<Entry> queue = new LinkedList<>();
        queue.add(new Entry(startPoint, 0));
        int counter = 0;
        
        while (queue.isEmpty() == false) {
            counter++;
            Entry entry = queue.remove();
            PointData point = entry.pointData();
            int currSteps = entry.steps();
            int currKeySet = point.keySet();

            if (point.keySet() == targetKeySet) {
                System.out.println(counter);
                return currSteps;
            }
            
            for (int i = 0; i < DIRECTIONS.length; i++) {
                int nextRow = point.row() + DIRECTIONS[i][0];
                int nextCol = point.col() + DIRECTIONS[i][1];
                int nextKeySet = currKeySet;
                char ch = grid[nextRow][nextCol];
                
                if (ch == '#') {
                    // A wall
                    continue;
                } else if (ch >= 'A' && ch <= 'Z') {
                    // A door. Do we have a key?
                    if ((currKeySet & (1 << (ch - 'A'))) == 0) {
                        // We do not
                        continue;
                    }
                }
                
                if (ch >= 'a' && ch <= 'z') {
                    // A key - pick it up
                    nextKeySet |= (1 << (ch - 'a'));
                }
                
                PointData nextPoint = new PointData(nextRow, nextCol, nextKeySet);
                
                if (visited.contains(nextPoint)) {
                    continue;
                }
                
                visited.add(nextPoint);
                queue.add(new Entry(nextPoint, currSteps + 1));
            }
        }

        return -1;
    }
    
    private record PointData(int row, int col, int keySet) {}
    private record Entry(PointData pointData, int steps) {}
    
    private static final int[][] DIRECTIONS = {
            { -1, 0 },  // up
            { 1, 0 },   // down
            { 0, -1 },  // left
            { 0, 1 }    // right
    };
}
