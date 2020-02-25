package day_13;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class Day13 {

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
        
        part1(program);
        System.out.println();
        System.out.println("PART 2 - playing the game");
        PlayTheGame game = new PlayTheGame(program);
        game.play();
    }
    
    private static void part1(Long[] program) {
        System.out.println("PART 1 - calculating number of block tiles");
        IntCodeComputer computer = new IntCodeComputer(program, null, new LinkedList<>());
        Status status = computer.execute();
        System.out.println("Computer status: " + status);
        
        if (status != Status.SUCCESS) {
            System.out.println("Nothing more we can do");
            System.exit(0);
        }

        Queue<Long> outputQueue = computer.getOutputQueue();
        
        if (outputQueue.size() %3 != 0) {
            System.out.println(outputQueue.size() + " elements on the queue is not a multiple of 3 - something went wrong");
            System.exit(0);
        }

        Set<String> tileSet = new HashSet<>();
        
        while (outputQueue.isEmpty() == false) {
            // Data values are in groups of 3
            long x = outputQueue.remove();
            long y = outputQueue.remove();
            long tile = outputQueue.remove();
            
            if (tile == 2) {
                // Block tile
                tileSet.add(String.format("%d,%d", x, y));
            } else {
                tileSet.remove(String.format("%d,%d", x, y));
            }
        }
        
        System.out.println("Number of block tiles: " + tileSet.size());
    }
}
