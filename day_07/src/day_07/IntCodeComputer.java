package day_07;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class IntCodeComputer {
    
    private Integer[] origProgram;
    private Queue<Integer> inputQueue;
    private Queue<Integer> outputQueue;
    private Integer[] program;
    private int pc = 0;
    
    public IntCodeComputer(Integer[] program, Queue<Integer> inputQueue, Queue<Integer> outputQueue) {
        this.origProgram = program;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void initialize() {
        program = Arrays.copyOf(origProgram, origProgram.length);
        pc = 0;
    }
    
    public void inputValue(int value) {
        inputQueue.add(value);
    }
    
    public ProgramStatus executeProgram() {
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
                    int origPc = pc;
                    int newPc = opCode3(program, paramModes, pc);
                    
                    if (newPc == origPc) {
                        pc = origPc - 1;
                        return ProgramStatus.BLOCKED;
                    } else {
                        pc = newPc;
                    }
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
                    pc = 0;
                    return ProgramStatus.SUCCESS;
                    
                default:
                    System.out.println("System error - unknown opcode " + opCode);
                    return ProgramStatus.FAILURE;
            }
            
            if (pc == -1) {
                return ProgramStatus.FAILURE;
            }
        }
        
        return ProgramStatus.FAILURE;
    }
    
    /**
     * Add two values and store
     * @param program
     * @param paramModes
     * @param pc
     * @return
     */
    private int opCode1(Integer[] program, List<String> paramModes, int pc) {
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

    /**
     * Multiple two values and store
     * @param program
     * @param paramModes
     * @param pc
     * @return
     */
    private int opCode2(Integer[] program, List<String> paramModes, int pc) {
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

    /**
     * Read in a value and store it
     * @param program
     * @param paramModes
     * @param pc
     * @return
     */
    private int opCode3(Integer[] program, List<String> paramModes, int pc) {
        if (pc + 1 > program.length) {
            System.out.println("Ran past specified program");
            return -1;
        }
        
        int origPc = pc;
        
        int addr1 = program[pc++];
        
        if (addr1 > program.length) {
            System.out.println("Memory location does not exist");
            return -1;
        }

        Integer input = inputQueue.poll();
        
        if (input == null) {
            // Nothing on the input queue - need to block
            return origPc;
        }
        
//        System.out.println("Input: " + input);
        program[addr1] = input;
        
        return pc;
    }

    /**
     * Output a value
     * @param program
     * @param paramModes
     * @param pc
     * @return
     */
    private int opCode4(Integer[] program, List<String> paramModes, int pc) {
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

//        System.out.println("Output: " + value1);
        outputQueue.add(value1);
        
        return pc;
    }
    
    /**
     * Jump if true
     * @param program
     * @param paramModes
     * @param pc
     * @return
     */
    private int opCode5(Integer[] program, List<String> paramModes, int pc) {
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

    /**
     * Jump if false
     * @param program
     * @param paramModes
     * @param pc
     * @return
     */
    private int opCode6(Integer[] program, List<String> paramModes, int pc) {
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

    /**
     * Less than
     * @param program
     * @param paramModes
     * @param pc
     * @return
     */
    private int opCode7(Integer[] program, List<String> paramModes, int pc) {
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

    /**
     * Equals
     * @param program
     * @param paramModes
     * @param pc
     * @return
     */
    private int opCode8(Integer[] program, List<String> paramModes, int pc) {
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
