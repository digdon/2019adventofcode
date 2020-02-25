package day_11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class Day11 {
    
    private static final int BLACK = 0;
    private static final int WHITE = 1;

    private static final DirectionInfo[] directionData = new DirectionInfo[] { new DirectionInfo(0, 1),
                                                                               new DirectionInfo(1, 0),
                                                                               new DirectionInfo(0, -1),
                                                                               new DirectionInfo(-1, 0) };
    
    public static void main(String[] args) {
        // Load the program from input
        List<Long> codeArray = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                String[] split = inputLine.split("\\s*,\\s*");
                
                for (int i = 0; i < split.length; i++) {
                    codeArray.add(new Long(split[i]));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        Long[] program = new Long[codeArray.size()];
        program = codeArray.toArray(program);
        
        IntCodeComputer computer = new IntCodeComputer(program, new LinkedList<Long>(), new LinkedList<Long>());
        Map<String, Integer> panelMap = runPaintJob(computer, BLACK);
        System.out.println("PART 1 - Total panels painted: " + panelMap.size());
        System.out.println();
        
        computer.reset();
        panelMap = runPaintJob(computer, WHITE);
        System.out.println("PART 2 - Total panels painted: " + panelMap.size());

        // Go through the keys and find the min/max X/Y positions
        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        
        for (String key : panelMap.keySet()) {
            String[] split = key.split(",");
            int x = Integer.valueOf(split[0]);
            int y = Integer.valueOf(split[1]);
            
            if (x < minX) {
                minX = x;
            } else if (x > maxX) {
                maxX = x;
            }
            
            if (y < minY) {
                minY = y;
            } else if (y > maxY) {
                maxY = y;
            }
        }

        // Using min/max values, construct an array to hold the data
        int[][] painting = new int[maxX - minX + 1][maxY - minY + 1];

        // Paint the panels
        for (String key : panelMap.keySet()) {
            String[] split = key.split(",");
            int x = Integer.valueOf(split[0]) - minX;
            int y = Integer.valueOf(split[1]) - minY;
            painting[x][y] = panelMap.get(key);
        }

        // Print out the array - it's rotated and backwards, so orient accordingly
        for (int x = painting.length - 1; x >= 0; x--) {
            for (int y = painting[x].length - 1; y >= 0; y--) {
                System.out.print(painting[x][y] == BLACK ? " " : "#");
            }
            System.out.println();
        }
    }
    
    private static Map<String, Integer> runPaintJob(IntCodeComputer computer, int initialColour) {
        Status status = Status.READY;
        int direction = 0;
        int posX = 0;
        int posY = 0;
        Map<String, Integer> panelMap = new HashMap<>();
        panelMap.put(positionLabel(posX, posY), initialColour);

        int count = 0;
        
        while (true) {
            // Determine colour at current position
            Integer colour = panelMap.get(positionLabel(posX, posY));
            
            if (colour == null) {
                colour = BLACK;
            }

            System.out.println(String.format("Step %d: %d,%d", ++count, posX, posY));
            System.out.println("    Current colour " + colour);
            
            // Enter current colour into computer and execute
            computer.inputValue(colour);
            status = computer.execute();
            
            if (status == Status.FAILURE) {
                System.out.println("Program failure");
                System.exit(-1);
            } else if (status == Status.SUCCESS) {
                break;
            }

            // Fetch the new paint colour and paint the current position
            Integer newColour = computer.outputValue().intValue();
            System.out.println("    Painting it " + newColour);
            panelMap.put(positionLabel(posX, posY), newColour);
            
            // Fetch the new direction and move one space
            Integer newDirection = computer.outputValue().intValue();
            
            if (newDirection == 0) {
                // Turning left
                direction -= 1;
                
                if (direction < 0) {
                    direction = 3;
                }
                
                System.out.println("    Turning left to direction " + direction);
            } else {
                // Turning right
                direction += 1;
                
                if (direction > 3) {
                    direction = 0;
                }
                
                System.out.println("    Turning right to direction " + direction);
            }

            DirectionInfo info = directionData[direction];
            posX += info.dirX;
            posY += info.dirY;
            System.out.println(String.format("    Moved to %d,%d", posX, posY));
        }

        return panelMap;
    }
    
    private static String positionLabel(int x, int y) {
        StringBuilder sb = new StringBuilder();
        sb.append(x).append(",").append(y);
        
        return sb.toString();
    }

    static class DirectionInfo {
        public int dirX;
        public int dirY;
        
        public DirectionInfo(int x, int y) {
            this.dirX = x;
            this.dirY = y;
        }
    }
}
