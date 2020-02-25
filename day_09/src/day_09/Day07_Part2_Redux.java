package day_09;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class Day07_Part2_Redux {

    private static List<Integer[]> combinations = new ArrayList<>();
    
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
        
        System.out.println(codeArray);

        // Set up computers
        int maxComputers = 5;
        List<Queue<Long>> queueList = new ArrayList<>(maxComputers);
        
        for (int i = 0; i < maxComputers; i++) {
            queueList.add(new LinkedList<>());
        }

        Long[] program = new Long[codeArray.size()];
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
        long maxSignal = 0;
        Integer[] maxPhase = null;

        for (Integer[] phaseList : combinations) {
            System.out.print("Combination list:");
            for (int i = 0; i < phaseList.length; i++) {
                computers[i].reset();
                computers[i].inputValue(phaseList[i]);
                System.out.print(" " + phaseList[i]);
            }
            
            System.out.println();

            // Input starting signal value
            computers[0].inputValue(0);

            Status status = Status.FAILURE;
            
            do {
                for (int i = 0; i < computers.length; i++) {
                    status = computers[i].execute();
                    
                    if (status == Status.FAILURE) {
                        System.out.println("Program failure on computer " + i);
                        System.exit(-1);
                    }
                }
            } while (status != Status.SUCCESS);

            Long poll = queueList.get(0).poll();
            
            if (poll > maxSignal) {
                maxSignal = poll;
                maxPhase = phaseList;
                System.out.println("NEW MAX: " + poll);
            }
        }
        
        System.out.println("Max signal: " + maxSignal);
        for (int i = 0; i < maxPhase.length; i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            
            System.out.print(maxPhase[i]);
        }
        System.out.println();
    }

    private static void generatePhaseSettings() {
        // Generate phase setting values
        Integer[] values = new Integer[] { 5, 6, 7, 8, 9 };
        Queue<Integer> valueQueue = new LinkedList<>(Arrays.asList(values));
        processList(valueQueue, "", "");
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
            processList(queue, padding + "  ", path + item);
            queue.add(item);
        }
    }
}
