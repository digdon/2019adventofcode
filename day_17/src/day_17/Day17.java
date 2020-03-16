package day_17;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class Day17 {

    private static Long[] programFromInput = null;
    private static IntCodeComputer computer = null;
    
    public static void main(String[] args) {
        InputStream is = null;
        
        try {
            is = new FileInputStream("input.txt");
            programFromInput = IntCodeComputer.loadProgramFromInput(is);
            computer = new IntCodeComputer(programFromInput, new LinkedList<>(), new LinkedList<>());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        Status status = computer.execute();
        System.out.println(status.toString());
        StringBuilder sb = new StringBuilder();
        Queue<Long> outputQueue = computer.getOutputQueue();
        
        while (outputQueue.isEmpty() == false) {
            int value = outputQueue.remove().intValue();
            sb.append((char)value);
        }
        
        String[] lines = sb.toString().split("\n");
        
        partOne(lines);
        partTwo(lines);
    }
    
    private static void partOne(String[] lines) {
        int[][] grid = new int[lines.length][lines[0].length()];
        
        for (int row = 0; row < lines.length; row++) {
            for (int col = 0; col < lines[row].length(); col++) {
                grid[row][col] = lines[row].charAt(col);
            }
        }

        int alignment = 0;
        
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] != '#') {
                    continue;
                }

                // Look for intersection
                int directionCount = 0;

                // Anything above?
                if (row - 1 >= 0 && grid[row - 1][col] == '#') {
                    directionCount++;
                }
                
                // Anything below?
                if (row + 1 < grid.length && grid[row + 1][col] == '#') {
                    directionCount++;
                }

                // Anything left?
                if (col - 1 >= 0 && grid[row][col - 1] == '#') {
                    directionCount++;
                }
                
                // Anything right?
                if (col + 1 < grid[row].length && grid[row][col + 1] == '#') {
                    directionCount++;
                }
                
                if (directionCount >= 3) {
                    grid[row][col] = 'O';
                    alignment += (row * col);
                }
            }
        }
        
        for (int row = 0; row < lines.length; row++) {
            for (int col = 0; col < lines[row].length(); col++) {
                System.out.print((char)grid[row][col]);
            }
            System.out.println();
        }
        
        System.out.println("PART 1: " + alignment);
    }

    public static final int[][] DIRECTIONS = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };
    public static final char[] DIRECTION_CHARS = { '^', '>', 'v', '<' };
    public static final int[][] DIRECTION_MAP = {
            { 0, 1, 2, -3 },
            { 0, 1, 2, 3 },
            { 0, -1, 2, 3 },
            { 0, -1, -2, 3 }
    };
    
    private static void partTwo(String[] lines) {
        int[][] grid = new int[lines.length][lines[0].length()];
        int startCol = 0;
        int startRow = 0;
        int robotDir = 0;
        
        for (int row = 0; row < lines.length; row++) {
            for (int col = 0; col < lines[row].length(); col++) {
                char tile = lines[row].charAt(col);
                grid[row][col] = tile;

                if (tile == '^' || tile == '<' || tile == '>' || tile == 'v') {
                    startCol = col;
                    startRow = row;
                    
                    switch (tile) {
                        case '^':
                            robotDir = 0;
                            break;
                            
                        case '>':
                            robotDir = 1;
                            break;
                            
                        case 'v':
                            robotDir = 2;
                            break;
                            
                        case '<':
                            robotDir = 3;
                            break;
                    }
                }
            }
        }

        // Find first piece of scaffolding in relation to robot and determine initial direction
        int row = startRow;
        int col = startCol;
        int newRobotDir = 0;
        
        if (row - 1 >= 0 && grid[row - 1][col] == '#') {                        // Up above?
            newRobotDir = DIRECTION_MAP[robotDir][0];
        } else if (col + 1 < grid[row].length && grid[row][col + 1] == '#') {   // To the right?
            newRobotDir = DIRECTION_MAP[robotDir][1];
        } else if (row + 1 < grid.length && grid[row + 1][col] == '#') {        // Down below?
            newRobotDir = DIRECTION_MAP[robotDir][2];
        } else {                                                                // To the left?
            newRobotDir = DIRECTION_MAP[robotDir][3];
        }

        StringBuilder sb = new StringBuilder();
        
        if (robotDir != Math.abs(newRobotDir)) {
            // Orient robot to the correct direction
            if (newRobotDir < 0) {
                System.out.println("Turning left");
                sb.append("L,");
            } else if (newRobotDir > 0) {
                System.out.println("Turning right");
                sb.append("R");
            }
    
            robotDir = Math.abs(newRobotDir);
        }
        
        System.out.println(DIRECTION_CHARS[robotDir]);

        boolean finished = false;
        int counter = 0;
        
        while (!finished) {
            int tempRow = row + DIRECTIONS[robotDir][1];
            int tempCol = col + DIRECTIONS[robotDir][0];
            
            if (tempRow >= 0 && tempRow < grid.length && tempCol >= 0 && tempCol < grid[row].length) {
                if (grid[tempRow][tempCol] == '#') {
                    counter++;
                    row = tempRow;
                    col = tempCol;
                    continue;
                }
            }
            
            // Ugly - but if we get here, that means we need to turn. Or we've reached the end
            sb.append(counter).append(',');
            counter = 0;
            
            if (robotDir == 0 || robotDir == 2) {
                // Currently moving up or down - only need to look left and/or right
                if (col - 1 >= 0 && grid[row][col - 1] == '#') {
                    sb.append(robotDir == 0 ? 'L' : 'R').append(',');
                    robotDir = 3;
                } else if (col + 1 < grid[row].length && grid[row][col + 1] == '#') {
                    sb.append(robotDir == 0 ? 'R' : 'L').append(',');
                    robotDir = 1;
                } else {
                    // Nowhere else to go
                    finished = true;
                }
            } else {
                // Currently moving left or right - only need to look up and/or down
                if (row - 1 >= 0 && grid[row - 1][col] == '#') {
                    sb.append(robotDir == 1 ? 'L' : 'R').append(',');
                    robotDir = 0;
                } else if (row + 1 < grid.length && grid[row + 1][col] == '#') {
                    sb.append(robotDir == 1 ? 'R' : 'L').append(',');
                    robotDir = 2;
                } else {
                    finished = true;
                }
            }
        }
        
        String pathString = sb.toString();
        
        Pattern pattern = Pattern.compile("^(.{1,21})\\1*(.{1,21})(?:\\1|\\2)*(.{1,21})(?:\\1|\\2|\\3)*");
        Matcher matcher = pattern.matcher(pathString);
        
        if (matcher.matches() == false) {
            System.out.println("Something wrong with path string");
            System.exit(-1);
        }

        List<String> functions = new ArrayList<>();
        functions.add(matcher.group(1));
        functions.add(matcher.group(2));
        functions.add(matcher.group(3));
        List<Integer> movementRoutine = new ArrayList<>();

        for (int pos = 0; pos < pathString.length(); ) {
            for (int i = 0; i < functions.size(); i++) {
                String steps = functions.get(i);
                
                if (pathString.regionMatches(pos, steps, 0, steps.length())) {
                    movementRoutine.add(i + 'A');
                    pos += steps.length();
                    break;
                }
            }
        }
        
        System.out.println(functions);
        System.out.println(movementRoutine);

        // Submit movement routine to computer
        for (int i = 0; i < movementRoutine.size(); i++) {
            if (i > 0) {
                computer.inputValue(',');
            }
            
            computer.inputValue(movementRoutine.get(i));
        }
        
        computer.inputValue('\n');
        
        // Submit movement functions
        for (String function : functions) {
            for (int i = 0; i < function.length() - 1; i++) {
                computer.inputValue(function.charAt(i));
            }
            
            computer.inputValue('\n');
        }
        
        computer.inputValue('n');
        computer.inputValue('\n');

        computer.reset();
        computer.setMemoryValue(0, 2);
        Status status = computer.execute();
        System.out.println(status);
        
        Queue<Long> outputQueue = computer.getOutputQueue();

        while (outputQueue.size() > 0) {
            long value = outputQueue.remove();
            
            if (value > 255) {
                System.out.println(value);
            } else {
                System.out.print((char)value);
            }
        }                
    }
}
