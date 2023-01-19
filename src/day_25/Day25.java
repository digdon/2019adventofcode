package day_25;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class Day25 {

    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("src/day_25/puzzle_input.txt");
        Long[] program = IntCodeComputer.loadProgramFromInput(is);
        is.close();
        IntCodeComputer computer = new IntCodeComputer(program, new LinkedList<>(), new LinkedList<>());
        Queue<Long> outputQueue = computer.getOutputQueue();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("executing...");
            Status status = computer.execute();
            
            System.out.println(status);

            while (outputQueue.isEmpty() == false) {
                long value = outputQueue.remove();
                System.out.print((char)value);
            }

            if (status != Status.BLOCKED) {
                break;
            }

            String line = scanner.nextLine();
            String command = null;
            
            if (line.startsWith("n")) {
                command = "north";
            } else if (line.startsWith("s")) {
                command = "south";
            } else if (line.startsWith("e")) {
                command = "east";
            } else if (line.startsWith("w")) {
                command = "west";
            } else if (line.startsWith("i")) {
                command = "inv";
            } else {
                command = line;
            }
            
            command.chars().forEach(c -> computer.inputValue(c));
            computer.inputValue('\n');
        }
        
        scanner.close();
    }
}
