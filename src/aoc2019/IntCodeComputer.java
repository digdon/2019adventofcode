package aoc2019;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class IntCodeComputer {

    public enum Status {
        SUCCESS, FAILURE, BLOCKED, READY
    }
    
    private static final int MEMORY_IMMEDIATE = 1;
    private static final int MEMORY_RELATIVE = 2;
    
    private Long[] origProgram;
    private Queue<Long> inputQueue;
    private Queue<Long> outputQueue;
    private Long[] memory;
    private int pc = 0;
    private int relativeBase = 0;
    private Status lastStatus = Status.READY;

    public IntCodeComputer(Long[] program, Queue<Long> inputQueue, Queue<Long> outputQueue) {
        this.origProgram = program;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        memory = Arrays.copyOf(origProgram, origProgram.length);
    }

    public void reset() {
        memory = Arrays.copyOf(origProgram, origProgram.length);
        pc = 0;
        relativeBase = 0;
        lastStatus = Status.READY;
        
        if (inputQueue != null) {
            inputQueue.clear();
        }
        
        if (outputQueue != null) {
            outputQueue.clear();
        }
    }

    public void displayMemory() {
        for (int i = 0; i < memory.length; i++) {
            if ((i > 0) && (i % 20 == 0)) {
                System.out.println();
            }
            
            System.out.print(String.format("%4d ", memory[i]));
        }
        
        if (memory.length % 20 != 0) {
            System.out.println();
        }
    }
    
    public void inputValue(long value) {
        inputQueue.add(value);
    }
    
    public Long outputValue() {
        return outputQueue.poll();
    }
    
    public Queue<Long> getOutputQueue() {
        return outputQueue;
    }

    public Status execute() {
        while (true) {
            long instruction = memory[pc];

            // Break down instruction into opCode and parameter modes
            int opCode = (int)(instruction % 100);
            LinkedList<Integer> paramModes = new LinkedList<>();
            int value = (int)(instruction / 100);
            
            while (value > 0) {
                paramModes.addLast(value % 10);
                value /= 10;
            }
            
            Status status = Status.FAILURE;
            
            switch (opCode) {
                case 1:     // Add two values and store
                    status = opCode1(paramModes);
                    break;

                case 2:     // Multiply two values and store
                    status = opCode2(paramModes);
                    break;
                    
                case 3:     // Read value from input and store
                    status = opCode3(paramModes);
                    break;

                case 4:     // Output a value from memory
                    status = opCode4(paramModes);
                    break;
                    
                case 5:     // Jump if true
                    status = opCode5(paramModes);
                    break;
                    
                case 6:     // Jump if false
                    status = opCode6(paramModes);
                    break;
                    
                case 7:     // Less than
                    status = opCode7(paramModes);
                    break;
                    
                case 8:     // Equals
                    status = opCode8(paramModes);
                    break;
                    
                case 9:     // Alter relative base
                    status = opCode9(paramModes);
                    break;
                    
                case 99:    // End of program
                    pc = 0;
                    lastStatus = Status.SUCCESS;
                    return lastStatus;
                    
                default:
                    System.out.println("System error - unknown opcode " + opCode);
                    lastStatus = Status.FAILURE;
                    return lastStatus;
            }

            lastStatus = status;
            
            if (status != Status.SUCCESS) {
                return lastStatus;
            }
        }
    }

    /**
     * Add two values and store the result
     * 
     * @param paramModes
     * @return
     */
    private Status opCode1(List<Integer> paramModes) {
        long value1 = getMemoryValue(paramModes, 1, pc + 1);
        long value2 = getMemoryValue(paramModes, 2, pc + 2);

        int addr = getMemoryAddress(paramModes, 3, pc + 3);
        setMemoryValue(addr, (value1 + value2));
        
        pc += 4;
        
        return Status.SUCCESS;
    }

    /**
     * Multiply two values and store the result
     * 
     * @param paramModes
     * @return
     */
    private Status opCode2(List<Integer> paramModes) {
        long value1 = getMemoryValue(paramModes, 1, pc + 1);
        long value2 = getMemoryValue(paramModes, 2, pc + 2);
        
        int addr = getMemoryAddress(paramModes, 3, pc + 3);
        setMemoryValue(addr, (value1 * value2));
        
        pc += 4;
        
        return Status.SUCCESS;
    }

    /**
     * Read in a value and store it
     * 
     * @return
     */
    private Status opCode3(List<Integer> paramModes) {
        if (inputQueue == null) {
            System.out.println("input queue not defined");
            return Status.FAILURE;
        }
        
        Long value = inputQueue.poll();
        
        if (value == null) {
            // Nothing on the input queue - need to block
            return Status.BLOCKED;
        }
        
        int address = getMemoryAddress(paramModes, 1, pc + 1);
        setMemoryValue(address, value);
        
        pc += 2;
        
        return Status.SUCCESS;
    }

    /**
     * Output memory location
     * 
     * @param paramModes
     * @return
     */
    private Status opCode4(List<Integer> paramModes) {
        long value = getMemoryValue(paramModes, 1, pc + 1);

        if (outputQueue != null) {
            outputQueue.add(value);
        } else {
            System.out.println("Output value: " + value);
        }

        pc += 2;
        
        return Status.SUCCESS;
    }

    /**
     * Jump if true (test value != 0)
     * 
     * @param paramModes
     * @return
     */
    private Status opCode5(List<Integer> paramModes) {
        long testValue = getMemoryValue(paramModes, 1, pc + 1);
        long jumpPc = getMemoryValue(paramModes, 2, pc + 2);

        if (testValue != 0) {
            pc = (int)jumpPc;
        } else {
            pc += 3;
        }
        
        return Status.SUCCESS;
    }
    
    /**
     * Jump if false (test value = 0)
     * 
     * @param paramModes
     * @return
     */
    private Status opCode6(List<Integer> paramModes) {
        long testValue = getMemoryValue(paramModes, 1, pc + 1);
        long jumpPc = getMemoryValue(paramModes, 2, pc + 2);

        if (testValue == 0) {
            pc = (int)jumpPc;
        } else {
            pc += 3;
        }
        
        return Status.SUCCESS;
    }

    /**
     * Less than (value1 < value2)
     * 
     * @param paramModes
     * @return
     */
    private Status opCode7(List<Integer> paramModes) {
        long value1 = getMemoryValue(paramModes, 1, pc + 1);
        long value2 = getMemoryValue(paramModes, 2, pc + 2);
        
        int addr = getMemoryAddress(paramModes, 3, pc + 3);
        
        if (value1 < value2) {
            setMemoryValue(addr, 1);
        } else {
            setMemoryValue(addr, 0);
        }
        
        pc += 4;
        
        return Status.SUCCESS;
    }

    /**
     * Equals (value1 = value2)
     * 
     * @param paramModes
     * @return
     */
    private Status opCode8(List<Integer> paramModes) {
        long value1 = getMemoryValue(paramModes, 1, pc + 1);
        long value2 = getMemoryValue(paramModes, 2, pc + 2);
        
        int addr = getMemoryAddress(paramModes, 3, pc + 3);
        
        if (value1 == value2) {
            setMemoryValue(addr, 1);
        } else {
            setMemoryValue(addr, 0);
        }
        
        pc += 4;
        
        return Status.SUCCESS;
    }
    
    /**
     * Modify relative base
     * 
     * @param paramModes
     * @return
     */
    private Status opCode9(List<Integer> paramModes) {
        long value = getMemoryValue(paramModes, 1, pc + 1);
        relativeBase += value;

        pc += 2;
        
        return Status.SUCCESS;
    }

    /**
     * Fetch a value from memory, in either positional, immediate, or relative mode.
     * For positional and relative modes, if the target memory location is beyond the current
     * size of the program (and active memory), memory will be automatically expanded.
     * 
     * @param paramModes
     * @param paramNum
     * @param tempPc
     * @return
     */
    private long getMemoryValue(List<Integer> paramModes, int paramNum, int tempPc) {
        Long value = null;
        
        if (paramModes.size() >= paramNum && paramModes.get(paramNum - 1) == MEMORY_IMMEDIATE) {
            // Immediate
            value = memory[tempPc];
        } else {
            int address = 0;
            
            if (paramModes.size() >= paramNum && paramModes.get(paramNum - 1) == MEMORY_RELATIVE) {
                // Relative
                int relAddr = memory[tempPc].intValue();
                address = (relativeBase + relAddr);
            } else {
                // Positional
                address = memory[tempPc].intValue();
            }
            
            if (address >= memory.length) {
                // Need to extend memory
                Long[] temp = Arrays.copyOf(memory, address + 1);
                memory = temp;
            }
            
            value = memory[address];
        }

        if (value == null) {
            value = 0L;
        }
        
        return value;
    }

    /**
     * Set the specified memory location to the given value. If the target memory location
     * is beyond the current size of the program (and active memory), memory will be
     * automatically expanded.
     * 
     * @param address
     * @param value
     */
    public void setMemoryValue(int address, long value) {
        if (address >= memory.length) {
            // Need to extend memory
            Long[] temp = Arrays.copyOf(memory, address + 1);
            memory = temp;
        }
        
        memory[address] = value;
    }

    /**
     * Get a memory address. Memory can be addressed in either relative or positional mode.
     * 
     * @param paramModes
     * @param paramNum
     * @param tempPc
     * @return
     */
    private int getMemoryAddress(List<Integer> paramModes, int paramNum, int tempPc) {
        int address = 0;
        
        if (paramModes.size() >= paramNum && paramModes.get(paramNum - 1) == MEMORY_RELATIVE) {
            // Relative
            int relAddr = memory[tempPc].intValue();
            address = (relativeBase + relAddr);
        } else {
            // Positional
            address = memory[tempPc].intValue();
        }

        return address;
    }
    
    public static Long[] loadProgramFromInput(InputStream is) {
        // Load the program from input
        List<Long> codeArray = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                String[] split = inputLine.split("\\s*,\\s*");
                
                for (int i = 0; i < split.length; i++) {
                    codeArray.add(Long.valueOf(split[i]));
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
        
        return program;
    }
}
