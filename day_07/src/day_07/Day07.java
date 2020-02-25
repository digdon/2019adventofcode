package day_07;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Day07 {

    private static List<Integer[]> combinations = new ArrayList<>();
    
    public static void main(String[] args) {
        // Load the program from input
        List<Integer> codeArray = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                String[] split = inputLine.split("\\s*,\\s*");
                
                for (int i = 0; i < split.length; i++) {
                    codeArray.add(new Integer(split[i]));
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
        
        System.out.println(codeArray);
        
        // Build the list of phase settings to be attempted
        generatePhaseSettings();
        
        int maxSignal = 0;
        
        // Process each group of phase settings
        for (Integer[] list : combinations) {
            System.out.println("Starting new list of phase settings");
            outputQueue.add(0);
            for (int i = 0; i < list.length; i++) {
                // Set up the program
                Integer[] program = new Integer[codeArray.size()];
                program = codeArray.toArray(program);
                
                inputQueue.add(list[i]);
                inputQueue.add(outputQueue.remove());
                
                executeProgram(program);
            }
            
            int temp = outputQueue.remove();
            
            if (temp > maxSignal) {
                maxSignal = temp;
                System.out.println("NEW MAX: " + temp);
            }
        }
        
        System.out.println("Max signal: " + maxSignal);
    }

    private static void generatePhaseSettings() {
        // Generate phase setting values
        Integer[] values = new Integer[] { 0, 1, 2, 3, 4 };
        Queue<Integer> valueQueue = new LinkedList<>(Arrays.asList(values));
        processList(valueQueue, "", "");
        System.out.println(combinations);
        for (Integer[] list : combinations) {
            for (int i = 0; i < list.length; i++) {
                System.out.print(list[i] + " ");
            }
            System.out.println();
        }
    }
    
    private static void processList(Queue<Integer> queue, String padding, String path) {
        if (queue.size() == 0) {
            Integer[] result = Arrays.stream(path.split(""))
                    .map(Integer::valueOf)
                    .toArray(Integer[]::new);
            combinations.add(result);
            return;
        }
        
        for (int i = 0; i < queue.size(); i++) {
            Integer item = queue.remove();
            System.out.println(padding + item);
            processList(queue, padding + "  ", path + item);
            queue.add(item);
        }
    }
    
    private static Queue<Integer> inputQueue = new LinkedList<>();
    private static Queue<Integer> outputQueue = new LinkedList<>();
    
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

        Integer input = inputQueue.remove();
        System.out.println("Input: " + input);
        program[addr1] = input;
        
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
        outputQueue.add(value1);
        
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
