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
import java.util.stream.Collectors;

public class PartTwo {

    record Point(int row, int col) {}
    record Portal(String id, Point point, boolean outside) {}
    record Coordinate(int level, int row, int col) {}
    record Entry(Coordinate coord, int dist) {}

    static Map<String, List<Portal>> portalToPointMap = new HashMap<>();
    static Map<Point, Portal> pointToPortalMap = new HashMap<>();

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
        
        System.out.println(pointToPortalMap);
        
        // BFS to find shortest path
        Point aaPoint = portalToPointMap.get("AA").get(0).point();
        Point zzPoint = portalToPointMap.get("ZZ").get(0).point();
        Coordinate start = new Coordinate(0, aaPoint.row(), aaPoint.col());
        Coordinate end = new Coordinate(0, zzPoint.row(), zzPoint.col());
        Deque<Entry> queue = new LinkedList<>();
        queue.add(new Entry(start, 0));
        Set<Coordinate> visited = new HashSet<>();
        int distance = 0;
        
        while (queue.isEmpty() == false) {
            Entry entry = queue.remove();
            Coordinate coord = entry.coord();
            int currDist = entry.dist();

            if (coord.level() > pointToPortalMap.size()) {
                // If we've gone down this far, we're looping through portals, so we know there's no exit on this path
                continue;
            }
            
            if (visited.contains(coord)) {
                // Already been here
                continue;
            }
            
            if (coord.equals(end)) {
                distance = currDist;
                break;
            }
            
            visited.add(coord);
            
            for (int i = 0; i < DIRECTIONS.length; i++) {
                int nextRow = coord.row() + DIRECTIONS[i][0];
                int nextCol = coord.col() + DIRECTIONS[i][1];
                int nextLevel = coord.level();
                
                if (grid[nextRow][nextCol] != '.') {
                    // Not an open tile
                    continue;
                }
                
                Point nextPoint = new Point(nextRow, nextCol);
                int dist = entry.dist();
                
                // Check to see if next point is a portal
                Portal portal = pointToPortalMap.get(nextPoint);

//                if (portal == null || portal.id().equals("AA") || portal.id().equals("ZZ")) {
//                    // Just a regular tile (or is portal AA or ZZ, which are special case) - queue it up
//                    queue.add(new Entry(new Coordinate(nextLevel, nextRow, nextCol), dist + 1));
//                    continue;
//                }
//
//                if (coord.level() == 0 && portal.outside() == true) {
//                    // At the top level, only inside portals work, so treat outside as regular tile
//                    queue.add(new Entry(new Coordinate(nextLevel, nextRow, nextCol), dist + 1));
//                    continue;
//                }

                // If the current point is a portal, we're going to go to the other end. Sometimes.
                // AA and ZZ are always special case and we always treat them as a wall, so no applying as a portal.
                // At the top level, we only process a portal if it's an inside portal. If we're at any level
                // other than the top (0), process the portal.
                if (portal != null && portal.id().equals("AA") == false && portal.id().equals("ZZ") == false
                        && ((coord.level() == 0 && portal.outside() == false) || coord.level() > 0)) {
                    List<Portal> list = portalToPointMap.get(portal.id());
                    
                    for (Portal p : list) {
                        if (p.point().equals(nextPoint) == false) {
                            // this is the other end of the portal - use it
                            nextRow = p.point().row();
                            nextCol = p.point().col();
                            nextLevel += portal.outside() ? -1 : 1;
                            dist++;
                            break;
                        }
                    }
                }
                
                queue.add(new Entry(new Coordinate(nextLevel, nextRow, nextCol), dist + 1));
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

                            boolean outside = (col == 0 || col == grid[row].length - 2);
                            addPortalDataToMaps(row, portalCol, ch, next, outside);
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

                            boolean outside = (row == 0 || row == grid.length - 2);
                            addPortalDataToMaps(portalRow, col, ch, next, outside);
                        }
                    }
                }
            }
        }
    }
    
    private static void addPortalDataToMaps(int row, int col, char first, char second, boolean outside) {
        String id = String.format("%c%c", first, second);
        Point point = new Point(row, col);
        Portal portal = new Portal(id, point, outside);
        pointToPortalMap.put(point, portal);

        List<Portal> portalList = portalToPointMap.get(id);

        if (portalList == null) {
            portalList = new ArrayList<>();
            portalToPointMap.put(id, portalList);
        }

        portalList.add(portal);
    }
}
