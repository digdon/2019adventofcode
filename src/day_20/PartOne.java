package day_20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PartOne {

    private record Point(int row, int col) {}
    private record Entry(Point point, int dist) {}

    private static Map<String, List<Point>> portalToPointMap = new TreeMap<>();
    private static Map<Point, String> pointToPortalMap = new HashMap<>();

    private static final int[][] DIRECTIONS = {
            { -1, 0 },  // up
            { 1, 0 },   // down
            { 0, -1 },  // left
            { 0, 1 }    // right
    };

    public static void main(String[] args) throws IOException {
        // Load in the puzzle input
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> inputLines = reader.lines().collect(Collectors.toList());
        reader.close();

        char[][] grid = new char[inputLines.size()][];

        for (int row = 0; row < inputLines.size(); row++) {
            grid[row] = inputLines.get(row).toCharArray();
        }

        generatePortalData(grid);
        
        // BFS to find shortest path
        Point start = portalToPointMap.get("AA").get(0);
        Point end = portalToPointMap.get("ZZ").get(0);
        
        Set<Point> visited = new HashSet<>();
        visited.add(start);
        Deque<Entry> queue = new LinkedList<>();
        queue.add(new Entry(start, 0));
        int distance = 0;
        
        while (queue.isEmpty() == false) {
            Entry entry = queue.remove();
            Point point = entry.point();
            
            // Are we at the end?
            if (point.equals(end)) {
                distance = entry.dist();
                break;
            }
            
            for (int i = 0; i < DIRECTIONS.length; i++) {
                int nextRow = point.row() + DIRECTIONS[i][0];
                int nextCol = point.col() + DIRECTIONS[i][1];
                
                if (grid[nextRow][nextCol] != '.') {
                    // Not an open tile
                    continue;
                }

                Point nextPoint = new Point(nextRow, nextCol);
                int dist = entry.dist();

                if (visited.contains(nextPoint)) {
                    // Already been here
                    continue;
                }

                // Check to see if next point is a portal
                String id = pointToPortalMap.get(nextPoint);

                if (id != null && id.equals("ZZ") == false) {
                    List<Point> list = portalToPointMap.get(id);
                    
                    for (Point p : list) {
                        if (p.equals(nextPoint) == false) {
                            // The other end
                            nextPoint = p;
                            dist++;
                            break;
                        }
                    }
                }
                
                queue.add(new Entry(nextPoint, dist + 1));
                visited.add(nextPoint);
            }
        }
        
        System.out.println(distance);
    }

    private static void generatePortalData(char[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            int lineLength = grid[row].length;

            for (int col = 0; col < lineLength; col++) {
                char ch = grid[row][col];

                if (Character.isAlphabetic(ch)) {
                    // Possible start of a portal ID
                    if (col + 1 < lineLength) {
                        char next = grid[row][col + 1];

                        if (Character.isAlphabetic(next)) {
                            // A portal ID, but now we need to know which column is the portal point
                            int portalCol = col;

                            if (col - 1 >= 0) {
                                if (grid[row][col - 1] == '.') {
                                    portalCol = col - 1;
                                } else {
                                    portalCol = col + 2;
                                }
                            } else {
                                portalCol = col + 2;
                            }

                            addPortalDataToMaps(row, portalCol, ch, next);
                        }
                    }

                    if (row + 1 < grid.length) {
                        char next = grid[row + 1][col];

                        if (Character.isAlphabetic(next)) {
                            // A portal ID, but now we need to know which row is the portal point
                            int portalRow = row;

                            if (row - 1 >= 0) {
                                if (grid[row - 1][col] == '.') {
                                    portalRow = row - 1;
                                } else {
                                    portalRow = row + 2;
                                }
                            } else {
                                portalRow = row + 2;
                            }

                            addPortalDataToMaps(portalRow, col, ch, next);
                        }
                    }
                }
            }
        }
    }

    private static void addPortalDataToMaps(int row, int col, char first, char second) {
        String id = String.format("%c%c", first, second);
        Point point = new Point(row, col);
        pointToPortalMap.put(point, id);

        List<Point> pointList = portalToPointMap.get(id);

        if (pointList == null) {
            pointList = new ArrayList<>();
            portalToPointMap.put(id, pointList);
        }

        pointList.add(point);
    }
}
