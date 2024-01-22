package day_18;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Day18 {
    
    private record Point(int row, int col) {}
    private record KeyToKey(char key, int neededKeys, int dist) {}
    private record Item(Point point, int neededKeys) {}
    private record Reachable(int mover, char key, int distance) {}
    private record CacheKey(List<Character> keyList, int currentKeys) {}
    
    private static final int[][] DIRECTIONS = {
            { -1, 0 },  // up
            { 1, 0 },   // down
            { 0, -1 },  // left
            { 0, 1 }    // right
    };
    
    private static final char[][] PART2_ALTER = {
            { '@', '#', '@' },
            { '#', '#', '#' },
            { '@', '#', '@' }
    };

    static int cacheHits = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> inputLines = reader.lines().collect(Collectors.toList());
        reader.close();
        
        // Parse the input into a maze, looking for the single start position (this becomes important for part 2 later)
        Point startPoint = null;
        char[][] grid = new char[inputLines.size()][];
        
        for (int row = 0; row < inputLines.size(); row++) {
            String line = inputLines.get(row);
            grid[row] = new char[line.length()];
            
            for (int col = 0; col < line.length(); col++) {
                char ch = line.charAt(col);
                grid[row][col] = ch;

                if (ch == '@') {
                    startPoint = new Point(row, col);
                }
            }
        }

        // Part 1
        cacheHits = 0;
        System.out.println(String.format("Part 1: %d (%d)", calculateSteps(grid), cacheHits));
        
        // Alter grid for part 2
        for (int y = 0; y < PART2_ALTER.length; y++) {
            for (int x = 0; x < PART2_ALTER[y].length; x++) {
                int nextRow = startPoint.row() - 1 + y;
                int nextCol = startPoint.col() - 1 + x;
                grid[nextRow][nextCol] = PART2_ALTER[y][x];
            }
        }
        
        cacheHits = 0;
        System.out.println(String.format("Part 2: %d (%d)", calculateSteps(grid), cacheHits));
    }
    
    private static int calculateSteps(char[][] grid) {
        Map<Character, Point> positionMap = new HashMap<>();
        List<Character> startPositions = new ArrayList<>();
        
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                char ch = grid[row][col];
                
                if (ch >= 'a' && ch <= 'z') {
                    // Found a key
                    positionMap.put(ch, new Point(row, col));
                } else if (ch == '@') {
                    char value = (char)('0' + startPositions.size());
                    startPositions.add(value);
                    positionMap.put(value, new Point(row, col));
                }
            }
        }
        
        Map<Character, List<KeyToKey>> keyToKeyMap = new HashMap<>();

        // For each key, find the distances to every other key, keeping track of doors in the way
        for (Entry<Character, Point> entry : positionMap.entrySet()) {
            List<KeyToKey> keyToKeyData = generateKeyToKeyData(grid, entry.getKey(), entry.getValue());
            keyToKeyMap.put(entry.getKey(), keyToKeyData);
        }
        
        return minSteps(startPositions, 0, new HashMap<>(), keyToKeyMap);
    }
    
    private static int minSteps(List<Character> positions,
                                int currentKeys,
                                Map<CacheKey, Integer> cache,
                                Map<Character, List<KeyToKey>> keyToKeyMap) {
        CacheKey cacheKey = new CacheKey(positions, currentKeys);
        Integer value = cache.get(cacheKey);

        if (value != null) {
            cacheHits++;
            return value;
        }
        
        List<Reachable> reachableKeys = reachableKeys(positions, currentKeys, keyToKeyMap);
        
        if (reachableKeys.size() == 0) {
            value = 0;
        } else {
            value = Integer.MAX_VALUE;
            
            for (Reachable reach : reachableKeys) {
                int mover = reach.mover();
                char origMoverKey = positions.get(mover);
                positions.set(mover, reach.key());
                int steps = reach.distance() + minSteps(positions, currentKeys | (1 << (reach.key() - 'a')), cache, keyToKeyMap);
                
                if (steps < value) {
                    value = steps;
                }
                
                positions.set(mover, origMoverKey);
            }
        }
        
        cache.put(cacheKey, value);

        return value;
    }
    
    private static List<Reachable> reachableKeys(List<Character> positions,
                                                 int collectedKeys,
                                                 Map<Character, List<KeyToKey>> keyToKeyMap) {
        List<Reachable> keys = new ArrayList<>();
        
        for (int i = 0; i < positions.size(); i++) {
            Character pos = positions.get(i);
            List<KeyToKey> list = keyToKeyMap.get(pos);
            
            for (KeyToKey entry : list) {
                int temp = (1 << (entry.key() - 'a'));
                if ((collectedKeys & temp) == temp) {
                    // Already got this one
                    continue;
                } else if ((collectedKeys & entry.neededKeys()) != entry.neededKeys()) {
                    // Don't have all of the required keys, so not reachable
                    continue;
                }
                
                keys.add(new Reachable(i, entry.key(), entry.dist()));
            }
        }
        
        return keys;
    }
    
    private static List<KeyToKey> generateKeyToKeyData(char[][] grid, int key, Point keyPoint) {
        Map<Point, Integer> pointDistanceMap = new HashMap<>();
        pointDistanceMap.put(keyPoint, 0);
        List<KeyToKey> keyList = new ArrayList<>();
        Deque<Item> queue = new LinkedList<>();
        queue.add(new Item(keyPoint, 0));

        // We're using a flood fill to work out distances from the initial key to every other point.
        // Along the way, we're keeping track of what keys we'll find and the keys for doors that might be in the way.
        while (queue.isEmpty() == false) {
            Item item = queue.remove();
            Point currPoint = item.point();
            int neededKeys = item.neededKeys();
            
            for (int i = 0; i < DIRECTIONS.length; i++) {
                int nextRow = currPoint.row() + DIRECTIONS[i][0];
                int nextCol = currPoint.col() + DIRECTIONS[i][1];
                char ch = grid[nextRow][nextCol];
                
                if (ch == '#') {
                    // A wall - skip
                    continue;
                }
                
                Point nextPoint = new Point(nextRow, nextCol);
                
                if (pointDistanceMap.containsKey(nextPoint)) {
                    // We've already been to this point
                    continue;
                }

                pointDistanceMap.put(nextPoint, pointDistanceMap.get(currPoint) + 1);
                
                if (ch >= 'a' && ch <= 'z') {
                    // Found a key - add it to the key list
                    keyList.add(new KeyToKey(ch, neededKeys, pointDistanceMap.get(nextPoint)));
                    queue.add(new Item(nextPoint, neededKeys));
                } else if (ch >= 'A' && ch <= 'Z') {
                    // Found a door - add its key to the needed key set
                    queue.add(new Item(nextPoint, neededKeys | (1 << (ch - 'A'))));
                } else {
                    queue.add(new Item(nextPoint, neededKeys));
                }
            }
        }
        
        return keyList;
    }
}
