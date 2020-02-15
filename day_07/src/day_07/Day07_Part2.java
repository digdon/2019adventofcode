package day_07;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Day07_Part2 {

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
        }
        
        try {
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println(codeArray);

        // Set up computers
        int maxComputers = 5;
        List<Queue<Integer>> queueList = new ArrayList<>(maxComputers);
        
        for (int i = 0; i < maxComputers; i++) {
            queueList.add(new LinkedList<>());
        }

        Integer[] program = new Integer[codeArray.size()];
        program = codeArray.toArray(program);

        IntCodeComputer[] computers = new IntCodeComputer[maxComputers];
        
        for (int i = 0; i< computers.length; i++) {
            System.out.println(i + " " + ((i + 1) % computers.length));
            computers[i] = new IntCodeComputer(program, queueList.get(i), queueList.get((i + 1) % computers.length));
        }

        // Build the list of phase settings to be attempted
        generatePhaseSettings();
//        combinations = new ArrayList<>();
//        combinations.add(new Integer[] {9, 8, 7, 6, 5});
        int maxSignal = 0;

        for (Integer[] phaseList : combinations) {
            System.out.print("Combination list:");
            for (int i = 0; i < phaseList.length; i++) {
                computers[i].initialize();
                computers[i].inputValue(phaseList[i]);
                System.out.print(" " + phaseList[i]);
            }
            
            System.out.println();

            // Input starting signal value
            computers[0].inputValue(0);
            
            ProgramStatus status = ProgramStatus.FAILURE;
            
            do {
                for (int i = 0; i < computers.length; i++) {
                    status = computers[i].executeProgram();
                    
                    if (status == ProgramStatus.FAILURE) {
                        System.out.println("Program failure on computer " + i);
                        System.exit(-1);
                    }
                }
            } while (status != ProgramStatus.SUCCESS);

            Integer poll = queueList.get(0).poll();
            
            if (poll > maxSignal) {
                maxSignal = poll;
                System.out.println("NEW MAX: " + poll);
            }
        }
        
        System.out.println("Max signal: " + maxSignal);
        /*
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
        */
    }

    private static void generatePhaseSettings() {
        // Generate phase setting values
        Integer[] values = new Integer[] { 5, 6, 7, 8, 9 };
        Queue<Integer> valueQueue = new LinkedList<>(Arrays.asList(values));
        processList(valueQueue, "", "");
//        System.out.println(combinations);
//        for (Integer[] list : combinations) {
//            for (int i = 0; i < list.length; i++) {
//                System.out.print(list[i] + " ");
//            }
//            System.out.println();
//        }
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
//            System.out.println(padding + item);
            processList(queue, padding + "  ", path + item);
            queue.add(item);
        }
    }
}
