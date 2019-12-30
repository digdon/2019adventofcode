package ca.buccaneer.advent2019.day02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day02 {

    public static void main(String[] args) {
        // Load the program from input
        List<Integer> codeArray = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                String[] split = inputLine.split(",");
                
                for (int i = 0; i < split.length; i++) {
                    codeArray.add(new Integer(split[i]));
                }
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
        
        System.out.println(codeArray);
        Integer[] program = new Integer[codeArray.size()];
        program = codeArray.toArray(program);

        // Part 1
        // Manual memory setup
        program[1] = 12;
        program[2] = 2;
        
        // Execute the program
        execute(program);

        // Display memory
        displayMemory(program);

        // Part 2
        for (int noun = 0; noun < 100; noun++) {
            for (int verb = 0; verb < 100; verb++) {
                // Reset program
                program = codeArray.toArray(program);

                // memory setup
                program[1] = noun;
                program[2] = verb;
                
                // execute program
                execute(program);
                
                if (program[0] == 19690720) {
                    // Found the answer we're looking for - done
                    displayMemory(program);
                    System.out.println("noun = " + noun + ", verb = " + verb);
                    System.out.println("Answer: " + ((100 * noun) + verb));
                    System.exit(0);
                }
            }
        }
    }

    /*
     * Execute the provide program
     */
    private static boolean execute(Integer[] program) {
        for (int pc = 0; pc < program.length; pc += 4) {
            int opcode = program[pc];
            int ml1 = Integer.MAX_VALUE;
            int ml2 = Integer.MAX_VALUE;
            int ml3 = Integer.MAX_VALUE;

            if ((pc + 1) < program.length) {
                ml1 = program[pc + 1];
            }

            if ((pc + 2) < program.length) {
                ml2 = program[pc + 2];
            }

            if ((pc + 3) < program.length) {
                ml3 = program[pc + 3];
            }
            
            if (ml1 == Integer.MAX_VALUE || ml2 == Integer.MAX_VALUE || ml3 == Integer.MAX_VALUE) {
                System.out.println("System error - Missing a memory location operand");
                return false;
            }
            
            switch (opcode) {
                case 1:     // Addition
                    program[ml3] = program[ml1] + program[ml2];
                    break;
                    
                case 2:     // Multiplication
                    program[ml3] = program[ml1] * program[ml2];
                    break;

                case 99:    // Halt
                    return true;
                    
                default:
                    System.out.println("System error - unknown opcode " + opcode);
                    return false;
            }
        }
        
        return true;
    }
    
    private static void displayMemory(Integer[] program) {
        for (int i = 0; i < program.length; i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            
            System.out.print(program[i]);
        }
        
        System.out.println();
    }
}
