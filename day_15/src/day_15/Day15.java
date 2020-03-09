package day_15;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class Day15 {

    private static final Direction[] DIRECTIONS = {
            new Direction(1, 2, 0, 1),
            new Direction(2, 1, 0, -1),
            new Direction(3, 4, -1, 0),
            new Direction(4, 3, 1, 0)
    };
    
    private static int minX = 0, maxX = 0;
    private static int minY = 0, maxY = 0;
    private static Map<String, Integer> map = new HashMap<>();
    private static Set<String> visitedSet = new HashSet<>();
    private static int oxygenX = 0;
    private static int oxygenY = 0;
    
    private static IntCodeComputer computer;
    
    public static void main(String[] args) {
        Long[] program = IntCodeComputer.loadProgramFromInput(System.in);
        computer = new IntCodeComputer(program, new LinkedList<>(), new LinkedList<>());
        computer.execute();
        Map<String, List<String>> graph = new HashMap<>();
        String coord = generateKey(0, 0);
        graph.put(coord, new ArrayList<>());
        map.put(coord, 3);
        dfs(graph, coord);
        
        System.out.println("PART 1");
        System.out.println("Oxygen sensor found at " + generateKey(oxygenX, oxygenY));
        System.out.println("Discovered maze:");
        displayMap(null);

        Deque<String> path = bfs(graph, coord, generateKey(oxygenX, oxygenY));
        System.out.println("Path to oxygen sensor:");
        displayMap(path);
        System.out.println("Number of steps: " + path.size());
        
        System.out.println();
        System.out.println("PART 2");
        int minutes = oxygenFill();
        System.out.println(minutes);
    }

    private static void dfs(Map<String, List<String>> graph, String vertex) {
//        System.out.println("Processing node " + vertex);
        String[] parts = vertex.split(",");
        int x = Integer.valueOf(parts[0]);
        int y = Integer.valueOf(parts[1]);
        visitedSet.add(vertex);
    
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int tempX = x + DIRECTIONS[i].getX();
            int tempY = y + DIRECTIONS[i].getY();
            String coord = generateKey(tempX, tempY);
            
            if (visitedSet.contains(coord)) {
//                System.out.println(coord + " already visited - skipping");
                continue;
            }
            
            if (tempX > maxX) {
                maxX = tempX;
            } else if (tempX < minX) {
                minX = tempX;
            }
            
            if (tempY > maxY) {
                maxY = tempY;
            } else if (tempY < minY) {
                minY = tempY;
            }
            
//            System.out.println("Examining " + coord);

            // Move robot in a direction
            computer.inputValue(DIRECTIONS[i].getCommand());
            Status status = computer.execute();
            
            if (status != Status.BLOCKED) {
                System.out.println("Excepted BLOCKED status, but got " + status.toString());
                System.exit(0);
            }
            
            Long result = computer.outputValue();
            
            if (result == 0) {
                // Hit a wall - mark it and then do nothing
//                System.out.println("Found wall at " + coord);
                map.put(coord, result.intValue());
            } else {
                if (result == 2) {
                    // Found the oxygen spot
                    oxygenX = tempX;
                    oxygenY = tempY;
                }
                
                map.put(coord, result.intValue());
                addVertexToNode(graph, vertex, coord);
                dfs(graph, coord);
                
                // Move robot back to original spot
//                System.out.println("Moving back from " + coord + " to " + vertex);
                computer.inputValue(DIRECTIONS[i].getOpposite());
                status = computer.execute();
                
                if (status != Status.BLOCKED) {
                    System.out.println("Excepted BLOCKED status, but got " + status.toString());
                    System.exit(0);
                }
                
                computer.outputValue();
            }
        }
    }

    private static Deque<String> bfs(Map<String, List<String>> map, String start, String target) {
        Queue<String> nodeQueue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        visited.add(start);
        nodeQueue.add(start);
        Map<String, String> parents = new HashMap<>();
        
        while (nodeQueue.isEmpty() == false) {
            String node = nodeQueue.remove();
            
            if (node.equals(target)) {
                // Target node found - return the path
                Deque<String> path = new LinkedList<>();
                
                while (true) {
                    path.addFirst(node);
                    String parent = parents.get(node);
                    
                    if (parent.equals(start)) {
                        // We're done - return the path
                        return path;
                    } else {
                        node = parent;
                    }
                }
            }
            
            // Find node in map
            List<String> vertices = map.get(node);
            
            if (vertices == null) {
                continue;
            }
            
            for (String vertex : vertices) {
                if (visited.contains(vertex) == false) {
                    visited.add(vertex);
                    parents.put(vertex, node);
                    nodeQueue.add(vertex);
                }
            }
        }
        
        return null;
    }
    
    private static void addVertexToNode(Map<String, List<String>> graph, String node, String vertex) {
        if (graph.containsKey(node) == false) {
            graph.put(node, new ArrayList<>());
        }
        
        List<String> vertexList = graph.get(node);
        vertexList.add(vertex);
    }

    private static int oxygenFill() {
        int[][] maze = buildMaze();
        Queue<Holder> nodeQueue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        int minutes = 0;
        Holder holder = new Holder(0, oxygenX - minX, oxygenY - minY);
        nodeQueue.add(holder);
        visited.add(generateKey(holder.x, holder.y));

        while (nodeQueue.isEmpty() == false) {
            holder = nodeQueue.remove();
            
            if (holder.depth > minutes) {
                minutes = holder.depth;
            }
            
            if (maze[holder.y - 1][holder.x] != 0
                    && visited.contains(generateKey(holder.x, holder.y - 1)) == false) {
                nodeQueue.add(new Holder(holder.depth + 1, holder.x, holder.y - 1));
                visited.add(generateKey(holder.x, holder.y - 1));
            }
            
            if (maze[holder.y + 1][holder.x] != 0
                    && visited.contains(generateKey(holder.x, holder.y + 1)) == false) {
                nodeQueue.add(new Holder(holder.depth + 1, holder.x, holder.y + 1));
                visited.add(generateKey(holder.x, holder.y + 1));
            }
            
            if (maze[holder.y][holder.x - 1] != 0
                    && visited.contains(generateKey(holder.x - 1, holder.y)) == false) {
                nodeQueue.add(new Holder(holder.depth + 1, holder.x - 1, holder.y));
                visited.add(generateKey(holder.x - 1, holder.y));
            }
            
            if (maze[holder.y][holder.x + 1] != 0
                    && visited.contains(generateKey(holder.x + 1, holder.y)) == false) {
                nodeQueue.add(new Holder(holder.depth + 1, holder.x + 1, holder.y));
                visited.add(generateKey(holder.x + 1, holder.y));
            }
        }
        
        return minutes;
    }

    private static int[][] buildMaze() {
        // Using min/max values, construct an array to hold the data
        int[][] maze = new int[maxY - minY + 1][maxX - minX + 1];
    
        for (String key : map.keySet()) {
            String[] split = key.split(",");
            int x = Integer.valueOf(split[0]) - minX;
            int y = Integer.valueOf(split[1]) - minY;
            maze[y][x] = map.get(key);
        }
        
        return maze;
    }
    
    private static void displayMap(Deque<String> path) {
        int[][] maze = buildMaze();

        if (path != null) {
            String oxygenCoord = generateKey(oxygenX, oxygenY);
            
            for (String node : path) {
                if (node.equals(oxygenCoord) == false) {
                    String[] split = node.split(",");
                    int x = Integer.valueOf(split[0]) - minX;
                    int y = Integer.valueOf(split[1]) - minY;
                    maze[y][x] = 4;
                }
            }
        }
        
        // Print out the array - it's partially backwards, so orient accordingly
        for (int y = maze.length - 1; y >= 0; y--) {
            for (int x = 0; x < maze[y].length; x++) {
                switch (maze[y][x]) {
                    case 0:
                        System.out.print('#');
                        break;
                        
                    case 1:
                        System.out.print(' ');
                        break;
                        
                    case 2:
                        System.out.print('$');
                        break;
                        
                    case 3:
                        System.out.print('s');
                        break;
                        
                    case 4:
                        System.out.print('.');
                        break;
                }
            }
            
            System.out.println();
        }
    }
    
    private static String generateKey(int x, int y) {
        return String.format("%d,%d", x, y);
    }
    
    static class Direction {
        private int command;
        private int opposite;
        private int x;
        private int y;
        
        public Direction(int command, int opposite, int x, int y) {
            this.command = command;
            this.opposite = opposite;
            this.x = x;
            this.y = y;
        }
        
        public int getCommand() {
            return command;
        }

        public int getOpposite() {
            return opposite;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
    }
    
    static class Holder {
        int depth;
        int x;
        int y;
        
        public Holder(int depth, int x, int y) {
            this.depth = depth;
            this.x = x;
            this.y = y;
        }
    }
}
