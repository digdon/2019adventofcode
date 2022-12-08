package day_19;

import java.util.LinkedList;

import aoc2019.IntCodeComputer;

public class Day19 {

    private static final int GRID_SIZE = 50;
    
    public static void main(String[] args) {
        Long[] program = IntCodeComputer.loadProgramFromInput(System.in);
        IntCodeComputer computer = new IntCodeComputer(program, new LinkedList<>(), new LinkedList<>());
        char[][] grid = new char[GRID_SIZE][GRID_SIZE];
        int pointCount = 0;
        
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                long value = getGridValue(computer, x, y);
                grid[y][x] = (value == 0 ? '.' : '#');
                
                if (value == 1) {
                    pointCount++;
                }
            }
        }

        for (int y = 0; y < grid.length; y++) {
            System.out.print(String.format("%4d ", y));
            for (int x = 0; x < grid[y].length; x++) {
                System.out.print(grid[y][x]);
            }
            
            System.out.println();
        }
        
        System.out.println("Part 1: " + pointCount);
        
        int x = 0, y = 100;
        
        // Start by finding the closest edge
        while (true) {
            long value = getGridValue(computer, x, y);
            
            if (value == 0) {
                x++;
            } else {
                break;
            }
        }

        // Now find the farthest edge
        while (true) {
            long value = getGridValue(computer, x + 1, y);
            
            if (value == 1) {
                x++;
            } else {
                break;
            }
        }
        
        System.out.println(String.format("x = %d, y = %d", x, y));
        
        // Now look for a block, following the edge if current values don't contain the block
        while (true) {
            // Look for bottom-left corner
            long value = getGridValue(computer, x - 99, y + 99);
            
            if (value == 1) {
                break;
            } else {
                // Does not contain block, go down a row and find the edge
                y++;
                while (true) {
                    value = getGridValue(computer, x + 1, y);
                    
                    if (value == 1) {
                        x++;
                    } else {
                        break;
                    }
                }
                
                System.out.println(String.format("x = %d, y = %d", x, y));
            }
        }

        x -= 99;
        System.out.println(String.format("Found block at x = %d, y = %d", x, y));
        System.out.println("Part 2: " + ((x * 10000) + y));
    }
    
    private static long getGridValue(IntCodeComputer computer, int x, int y) {
        computer.reset();
        computer.inputValue(x);
        computer.inputValue(y);
        computer.execute();
        
        return computer.outputValue();
    }
}
