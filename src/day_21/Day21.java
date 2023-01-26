package day_21;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class Day21 {

    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("src/day_21/puzzle_input.txt");
        Long[] program = IntCodeComputer.loadProgramFromInput(is);
        is.close();
        
        IntCodeComputer computer = new IntCodeComputer(program, new LinkedList<>(), new LinkedList<>());
        Status status = computer.execute();
        System.out.println(status);
        
        Queue<Long> outputQueue = computer.getOutputQueue();
        
        while (outputQueue.isEmpty() == false) {
            long value = outputQueue.remove();
            System.out.print((char)value);
        }

        String commandList = 
                "NOT C J\n"
                + "AND D J\n"
                + "NOT A T\n"
                + "OR T J\n"
                + "WALK\n";
        
        long result = issueCommands(computer, commandList);
        System.out.println("Part 1: " + result);
        
        computer.reset();
        status = computer.execute();
        System.out.println(status);
        
        commandList = 
                "NOT C J\n"
                + "AND D J\n"
                + "AND H J\n"
                + "NOT B T\n"
                + "AND D T\n"
                + "OR T J\n"
                + "NOT A T \n"
                + "OR T J\n"
                + "RUN\n";
        
        result = issueCommands(computer, commandList);
        System.out.println("Part 2: " + result);
    }
    
    private static long issueCommands(IntCodeComputer computer, String commandList) {
        commandList.chars().forEach(c -> computer.inputValue(c));
        
        Status status = computer.execute();
        System.out.println(status);
        Queue<Long> outputQueue = computer.getOutputQueue();

        long result = 0;
        
        while (outputQueue.isEmpty() == false) {
            long value = outputQueue.remove();
            
            if (value < 256) {
                System.out.print((char)value);
            } else {
                if (result == 0) {
                    result = value;
                }
            }
        }
        
        return result;
    }
}
