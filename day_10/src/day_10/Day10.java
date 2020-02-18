package day_10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Day10 {

    public static void main(String[] args) {
        List<String> grid = new ArrayList<>();
        
        // Load input
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                grid.add(inputLine);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int maxAsteroids = 0;
        int maxX = 0;
        int maxY = 0;
        
        // Scan the grid, looking for asteroids to calculate from
        for (int y = 0; y < grid.size(); y++) {
            String row = grid.get(y);
            
            for (int x = 0; x < row.length(); x++) {
                if (row.charAt(x) == '#') {
                    int count = calculateSightMap(grid, x, y);
                    
                    if (count > maxAsteroids) {
                        maxAsteroids = count;
                        maxX = x;
                        maxY = y;
                    }
                }
            }
        }
        
        System.out.println("Part 1");
        System.out.println("    Max asteroids " + maxAsteroids + " at " + maxX + ", " + maxY);
        
        calculateDistances(grid, maxX, maxY);
        System.out.println();
        System.out.println();
        Map<Double, TreeSet<PositionInfo>> angleMap = calculateAngles(grid, maxX, maxY);

        int killCount = 0;
        PositionInfo pos200 = null;

        while (angleMap.size() > 0) {
            List<Double> keys = angleMap.keySet().stream().sorted().collect(Collectors.toList());
            
            for (double angle : keys) {
                TreeSet<PositionInfo> list = angleMap.get(angle);
                PositionInfo info = list.pollFirst();
                System.out.println(String.format("%3d %7.3f %s", ++killCount, angle, info));
                
                if (killCount == 200) {
                    pos200 = info;
                }                
                
                if (list.isEmpty()) {
                    angleMap.remove(angle);
                }
            }
        }

        System.out.println("Part 2");
        System.out.println("    " + ((pos200.getX() * 100) + pos200.getY()));
    }
    
    private static int calculateSightMap(List<String> grid, int posX, int posY) {
        Set<String> angles = new HashSet<>();
        
        for (int y = 0; y < grid.size(); y++) {
            String row = grid.get(y);
            
            for (int x = 0; x < row.length(); x++) {
                if (x == posX && y == posY) {
                    continue;
                }

                if (row.charAt(x) == '#') {
                    int diffX = x - posX;
                    int diffY = y - posY;
                    int gcd = gcd(diffX, diffY);
                    diffX /= gcd;
                    diffY /= gcd;
                    angles.add(diffX + "/" + diffY);
                }
            }
        }
        
        return angles.size();
    }
    
    private static int gcd(int x, int y) {
        while (y != 0) {
            int temp = x;
            x = y;
            y = temp % y;
        }
        
        return Math.abs(x);
    }
    
    private static void calculateDistances(List<String> grid, int posX, int posY) {
        for (int y = 0; y < grid.size(); y++) {
            String row = grid.get(y);
            
            for (int x = 0; x < row.length(); x++) {
                if (x == posX && y == posY) {
                    System.out.print("   X   ");
                    continue;
                }
                
                if (row.charAt(x) == '#') {
                    int distance = Math.abs(x - posX) + Math.abs(y - posY);
                    System.out.print(String.format("%4d   ", distance));
                } else {
                    System.out.print("   .   ");
                }
            }
            
            System.out.println();
        }
    }
    
    private static Map<Double, TreeSet<PositionInfo>> calculateAngles(List<String> grid, int posX, int posY) {
        Map<Double, TreeSet<PositionInfo>> angleMap = new HashMap<>();

        for (int y = 0; y < grid.size(); y++) {
            String row = grid.get(y);
            
            for (int x = 0; x < row.length(); x++) {
                if (x == posX && y == posY) {
                    System.out.print("    X    ");
                    continue;
                }
                
                if (row.charAt(x) == '#') {
                    int diffX = posX - x;
                    int diffY = posY - y;
                    int distance = Math.abs(diffX) + Math.abs(diffY);
                    double degrees = Math.toDegrees(Math.atan2(-diffX, diffY));
                    
                    if (degrees < 0.0) {
                        degrees += 360.0;
                    }

                    System.out.print(String.format(" %7.2f ", degrees));
                    PositionInfo posInfo = new PositionInfo(x, y, distance);
                    TreeSet<PositionInfo> angleList = angleMap.get(degrees);
                    
                    if (angleList == null) {
                        angleList = new TreeSet<>(Comparator.comparing(PositionInfo::getDistance));
                        angleMap.put(degrees, angleList);
                    }
                    
                    angleList.add(posInfo);
                } else {
                    System.out.print("    .    ");
                }
            }
            
            System.out.println();
        }

        return angleMap;
    }
    
    static class PositionInfo {
        private int x;
        private int y;
        private int distance;
        
        public PositionInfo(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public int getDistance() {
            return distance;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{ ");
            sb.append("x=").append(x);
            sb.append(", y=").append(y);
            sb.append(", distance=").append(distance);
            sb.append(" }");
            
            return sb.toString();
        }
    }
}
