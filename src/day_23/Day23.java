package day_23;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class Day23 {

    private static final int COMPUTER_COUNT = 50;
    
    record Packet(long x, long y) {}
    
    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("src/day_23/puzzle_input.txt");
        Long[] program = IntCodeComputer.loadProgramFromInput(is);
        is.close();
        
        IntCodeComputer[] computers = new IntCodeComputer[COMPUTER_COUNT];
        
        System.out.println("Setting up computers...");
        
        for (int i = 0; i < computers.length; i++) {
            IntCodeComputer computer = new IntCodeComputer(program, new LinkedList<>(), new LinkedList<>());
            computer.inputValue(i);
            computer.execute();
            computers[i] = computer;
        }
        
        Map<Integer, List<Packet>> packetMap = new HashMap<>();
        Packet natData = null;
        Packet natPreviousSent = null;
        
        boolean part1Found = false;
        long part1Value = 0l;
        
        boolean part2Found = false;
        long part2Value = 0l;

        while (part1Found == false || part2Found == false) {
            for (int i = 0; i < computers.length; i++) {
                IntCodeComputer computer = computers[i];
                
                // Look for packets for this computer
                List<Packet> packetList = packetMap.remove(i);
                
                if (packetList == null) {
                    // No packets, send -1
                    computer.inputValue(-1);
                } else {
                    // Packets found, send each x/y pair to input queue
                    packetList.forEach(p -> { computer.inputValue(p.x()); computer.inputValue(p.y()); } );
                }
                
                Status status = computer.execute();
                
                if (status == Status.FAILURE) {
                    System.out.println("Computer " + i + " failed - exiting");
                    System.exit(i);
                }
                
                Queue<Long> queue = computer.getOutputQueue();
                
                while (queue.isEmpty() == false) {
                    int dest = queue.remove().intValue();
                    long x = queue.remove();
                    long y = queue.remove();
                    Packet packet = new Packet(x, y);
                    
                    if (dest == 255) {
                        if (part1Found == false) {
                            part1Found = true;
                            part1Value = y;
                        }
                        
                        natData = packet;
                        System.out.println("New NAT packet: " + packet);
                    } else {
                        List<Packet> list = packetMap.get(dest);
                        
                        if (list == null) {
                            list = new ArrayList<>();
                            packetMap.put(dest, list);
                        }
                        
                        list.add(packet);
                    }                        
                }
                
                if (part1Found && part2Found) {
                    break;
                }
            }
            
            if (packetMap.isEmpty()) {
                // System is idle, queue up NAT packet for computer 0
                System.out.println("Sending " + natData + " to computer 0");
                if (natPreviousSent != null && natData.y() == natPreviousSent.y()) {
                    System.out.println("Conditions for part 2 triggered");
                    part2Found = true;
                    part2Value = natData.y();
                    break;
                }

                List<Packet> list = new ArrayList<>();
                list.add(natData);
                packetMap.put(0, list);
                natPreviousSent = natData;
            }
        }
        
        System.out.println("Part 1: " + part1Value);
        System.out.println("Part 2: " + part2Value);
    }
}
