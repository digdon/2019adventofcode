package day_09;

import java.util.LinkedList;

import aoc2019.IntCodeComputer;

public class Day09 {

    public static void main(String[] args) {
        Long[] program = IntCodeComputer.loadProgramFromInput(System.in);
        
        IntCodeComputer computer = new IntCodeComputer(program, new LinkedList<Long>(), null);
        computer.inputValue(1);
        System.out.println(computer.execute());
        
        computer.reset();
        computer.inputValue(2);
        System.out.println(computer.execute());
    }
}
