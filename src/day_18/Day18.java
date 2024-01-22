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
    
    private static final Map<Character, List<KeyToKey>> keyToKeyMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> inputLines = reader.lines().collect(Collectors.toList());
        reader.close();
        
        // Parse the maze, looking for start position(s) and key locations
        Map<Character, Point> startPosMap = new HashMap<>();
        Map<Character, Point> keyPositionMap = new HashMap<>();
        char[][] grid = new char[inputLines.size()][];
        
        for (int row = 0; row < inputLines.size(); row++) {
            String line = inputLines.get(row);
            grid[row] = new char[line.length()];
            
            for (int col = 0; col < line.length(); col++) {
                char ch = line.charAt(col);
                grid[row][col] = ch;

                if (ch >= 'a' && ch <= 'z') {
                    // Found a key
                    keyPositionMap.put(ch, new Point(row, col));
                } else if (ch == '@') {
                    startPosMap.put((char)('0' + startPosMap.size()), new Point(row, col));
                }
            }
        }
        
        keyPositionMap.putAll(startPosMap);
        System.out.println(keyPositionMap);

        // For each key, find the distances to every other key, keeping track of doors in the way
        for (Entry<Character, Point> entry : keyPositionMap.entrySet()) {
            List<KeyToKey> keyToKeyData = generateKeyToKeyData(grid, entry.getKey(), entry.getValue());
//            System.out.println(entry.getKey() + " -> " + keyToKeyData);
            keyToKeyMap.put(entry.getKey(), keyToKeyData);
        }

        // Part 1
        System.out.println("Part 1: " + minSteps('0', 0, new HashMap<>()));
        System.out.println(cacheHits);
        
        // Part 2
        cacheHits = 0;
        System.out.println("Part 2: " + minSteps('0', 0, new HashMap<>()));
        System.out.println(cacheHits);
    }
    
    private record Point(int row, int col) {}
    private record KeyToKey(char key, int neededKeys, int dist) {}
    private record Item(Point point, int neededKeys) {}
    private record Reachable(char key, int distance) {}
    private record CacheKey(char key, int currentKeys) {}
    
    private static final int[][] DIRECTIONS = {
            { -1, 0 },  // up
            { 1, 0 },   // down
            { 0, -1 },  // left
            { 0, 1 }    // right
    };

    static int cacheHits = 0;
    
    private static int minSteps(char sourceKey, int currentKeys, Map<CacheKey, Integer> cache) {
        CacheKey cacheKey = new CacheKey(sourceKey, currentKeys);
        Integer value = cache.get(cacheKey);

        if (value == null) {
            List<Reachable> reachableKeys = reachableKeys(sourceKey, currentKeys);
            
            if (reachableKeys.size() == 0) {
                value = 0;
            } else {
                value = Integer.MAX_VALUE;
                
                for (Reachable reach : reachableKeys) {
                    int steps = reach.distance() + minSteps(reach.key(), currentKeys | (1 << (reach.key() - 1)), cache);
                    
                    if (steps < value) {
                        value = steps;
                    }
                }
            }
            
            cache.put(cacheKey, value);
        } else {
            cacheHits++;
        }
        
        return cache.get(cacheKey);
    }
    
    private static List<Reachable> reachableKeys(char sourceKey, int collectedKeys) {
        List<Reachable> keys = new ArrayList<>();
        List<KeyToKey> list = keyToKeyMap.get(sourceKey);
        
        for (KeyToKey entry : list) {
            int temp = (1 << (entry.key() - 'a'));
            if ((collectedKeys & temp) == temp) {
                // Already got this one
                continue;
            } else if ((collectedKeys & entry.neededKeys()) != entry.neededKeys()) {
                // Got all of the needed keys
                continue;
            }
            
            keys.add(new Reachable(entry.key(), entry.dist()));
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
