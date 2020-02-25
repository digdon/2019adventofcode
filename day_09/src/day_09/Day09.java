package day_09;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import aoc2019.IntCodeComputer;

public class Day09 {

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
        
        IntCodeComputer computer = new IntCodeComputer(program, new LinkedList<Long>(), null);
        computer.inputValue(1);
        System.out.println(computer.execute());
        
        computer.reset();
        computer.inputValue(2);
        System.out.println(computer.execute());
    }
}
