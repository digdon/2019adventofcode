package ca.buccaneer.advent2019;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Day05 {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need to specify name of a program file");
            System.exit(-1);
        }
        
        // Load the program from input
        BufferedReader reader = null;
        List<Integer> codeArray = new ArrayList<>();
        String inputLine = null;

        try {
            reader = new BufferedReader(new FileReader(args[0]));
            
            while ((inputLine = reader.readLine()) != null) {
                String[] split = inputLine.split("\\s*,\\s*");
                
                for (int i = 0; i < split.length; i++) {
                    codeArray.add(new Integer(split[i]));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        Integer[] program = new Integer[codeArray.size()];
        program = codeArray.toArray(program);
        
        for (int i = 0; i < program.length; i++) {
            if (i % 10 == 0) {
                if (i > 0) {
                    System.out.println();
                }
                
                System.out.print(String.format("%-5d: ", i));
            }
            
            System.out.print(String.format("%7d", program[i]));
        }

        System.out.println();
                
        System.out.println(executeProgram(program));
    }
    
    private static boolean executeProgram(Integer[] program) {
        int pc = 0;
        
        while (pc < program.length) {
            
            int instruction = program[pc++];
            
            // Break down instruction into opCode and parameter modes
            int opCode = instruction % 100;
            List<String> paramModes = Arrays.asList(String.valueOf(instruction / 100).split(""));
            Collections.reverse(paramModes);
            
            switch (opCode) {
                case 1:
                    pc = opCode1(program, paramModes, pc);
                    break;
                    
                case 2:
                    pc = opCode2(program, paramModes, pc);
                    break;
                    
                case 3:
                    pc = opCode3(program, pc);
                    break;
                    
                case 4:
                    pc = opCode4(program, paramModes, pc);
                    break;

                case 5:
                    pc = opCode5(program, paramModes, pc);
                    break;
                    
                case 6:
                    pc = opCode6(program, paramModes, pc);
                    break;
                    
                case 7:
                    pc = opCode7(program, paramModes, pc);
                    break;
                    
                case 8:
                    pc = opCode8(program, paramModes, pc);
                    break;
                    
                case 99:
                    return true;
                    
                default:
                    System.out.println("System error - unknown opcode " + opCode);
                    return false;
            }
            
            if (pc == -1) {
                return false;
            }
        }
        
        return false;
    }
    
    private static int opCode1(Integer[] program, List<String> paramModes, int pc) {
        if (pc + 3 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }
        
        int value1 = 0;
        int value2 = 0;

        if (paramModes.size() >= 1 && paramModes.get(0).charAt(0) == '1') {
            // Immediate
            value1 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value1 = program[addr];
        }
        
        if (paramModes.size() >= 2 && paramModes.get(1).charAt(0) == '1') {
            // Immediate
            value2 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value2 = program[addr];
        }

        int storeAddr = program[pc++];
        program[storeAddr] = value1 + value2;
        
        return pc;
    }
    
    private static int opCode2(Integer[] program, List<String> paramModes, int pc) {
        if (pc + 3 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }
        
        int value1 = 0;
        int value2 = 0;

        if (paramModes.size() >= 1 && paramModes.get(0).charAt(0) == '1') {
            // Immediate
            value1 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value1 = program[addr];
        }
        
        if (paramModes.size() >= 2 && paramModes.get(1).charAt(0) == '1') {
            // Immediate
            value2 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value2 = program[addr];
        }

        int storeAddr = program[pc++];
        program[storeAddr] = value1 * value2;
        
        return pc;
    }
    
    private static int opCode3(Integer[] program, int pc) {
        if (pc + 1 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }
        
        int addr1 = program[pc++];
        
        if (addr1 > program.length) {
            System.out.println("Memory location does not exist");
            return -1;
        }

        System.out.print("Input: ");
        System.out.flush();
        Scanner scanner = new Scanner(System.in);
        int input = 0;

        try {
            input = scanner.nextInt();
            program[addr1] = input;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        } finally {
            scanner.close();
        }
        
        return pc;
    }
    
    private static int opCode4(Integer[] program, List<String> paramModes, int pc) {
        if (pc + 1 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }

        int value1 = 0;
        
        if (paramModes.size() >= 1 && paramModes.get(0).charAt(0) == '1') {
            // Immediate
            value1 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value1 = program[addr];
        }

        System.out.println("Output: " + value1);
        
        return pc;
    }
    
    private static int opCode5(Integer[] program, List<String> paramModes, int pc) {
        if (pc + 2 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }
        
        int value1 = 0;
        int jumpPc = 0;

        if (paramModes.size() >= 1 && paramModes.get(0).charAt(0) == '1') {
            // Immediate
            value1 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value1 = program[addr];
        }
        
        if (paramModes.size() >= 2 && paramModes.get(1).charAt(0) == '1') {
            // Immediate
            jumpPc = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            jumpPc = program[addr];
        }
        
        if (value1 != 0) {
            return jumpPc;
        } else {
            return pc;
        }
    }
    
    private static int opCode6(Integer[] program, List<String> paramModes, int pc) {
        if (pc + 2 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }
        
        int value1 = 0;
        int jumpPc = 0;

        if (paramModes.size() >= 1 && paramModes.get(0).charAt(0) == '1') {
            // Immediate
            value1 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value1 = program[addr];
        }
        
        if (paramModes.size() >= 2 && paramModes.get(1).charAt(0) == '1') {
            // Immediate
            jumpPc = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            jumpPc = program[addr];
        }
        
        if (value1 == 0) {
            return jumpPc;
        } else {
            return pc;
        }
    }
    
    private static int opCode7(Integer[] program, List<String> paramModes, int pc) {
        if (pc + 3 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }
        
        int value1 = 0;
        int value2 = 0;

        if (paramModes.size() >= 1 && paramModes.get(0).charAt(0) == '1') {
            // Immediate
            value1 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value1 = program[addr];
        }
        
        if (paramModes.size() >= 2 && paramModes.get(1).charAt(0) == '1') {
            // Immediate
            value2 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value2 = program[addr];
        }
        
        int addr1 = program[pc++];

        if (value1 < value2) {
            program[addr1] = 1;
        } else {
            program[addr1] = 0;
        }
        
        return pc;
    }
    
    private static int opCode8(Integer[] program, List<String> paramModes, int pc) {
        if (pc + 3 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }
        
        int value1 = 0;
        int value2 = 0;

        if (paramModes.size() >= 1 && paramModes.get(0).charAt(0) == '1') {
            // Immediate
            value1 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value1 = program[addr];
        }
        
        if (paramModes.size() >= 2 && paramModes.get(1).charAt(0) == '1') {
            // Immediate
            value2 = program[pc++];
        } else {
            // Position
            int addr = program[pc++];
            value2 = program[addr];
        }
        
        int addr1 = program[pc++];

        if (value1 == value2) {
            program[addr1] = 1;
        } else {
            program[addr1] = 0;
        }
        
        return pc;
    }
}
