package day_12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day12 {
    
    public static void main(String[] args) {
        String inputmoonsString = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                inputmoonsString += inputLine;
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

        List<Moon> moons = new ArrayList<>();
        
        // Build moon position list from input (which consists of elements that look like this: <x=1, y=2, z=-3>)
        Pattern pattern = Pattern.compile("<\\s*x\\s*=\\s*(\\-?\\d+)\\s*,\\s*y\\s*=\\s*(\\-?\\d+)\\s*,\\sz\\s*=\\s*(\\-?\\d+)\\s*>");
        Matcher matcher = pattern.matcher(inputmoonsString);

        while (matcher.find()) {
            int x = Integer.valueOf(matcher.group(1));
            int y = Integer.valueOf(matcher.group(2));
            int z = Integer.valueOf(matcher.group(3));
            moons.add(new Moon(x, y, z));
        }
        
        part1(moons);
        moons.forEach(Moon::reset);
        part2(moons);
    }
    
    private static void displayData(List<Moon> list) {
        for (Moon moon : list) {
            String posString = String.format("<x=%3d, y=%3d, z=%3d>", moon.x, moon.y, moon.z);
            String velString = String.format("<x=%3d, y=%3d, z=%3d>", moon.vel_x, moon.vel_y, moon.vel_z);
            System.out.println(String.format("pos=%s, vel=%s", posString, velString));
        }
    }
    
    private static void part1(List<Moon> moons) {
        System.out.println("PART 1:");
        System.out.println("Initial");
        displayData(moons);
        System.out.println();
        
        for (int step = 1; step <= 1000; step++) {
            moveMoons(moons);
        }
        
        displayData(moons);
        System.out.println();
        
        int totalEnergy = 0;
        
        for (Moon moon : moons) {
            totalEnergy += moon.totalEnergy();
        }
        
        System.out.println("Total energy: " + totalEnergy);
        System.out.println();
    }
    
    /**
     * The number of steps required to reach the initial parameters for the entire set all at once
     * is likely to be a gigantic number and take a significant amount of time. Instead, let's figure
     * out how long it takes for the X values to return to the initial parameters, the Y values to return,
     * and the Z values. Essentially, we're working out the period of each dimension. Once we've got the
     * period for each dimension, we'll calculate the least common multiple.
     * 
     * @param moons
     */
    private static void part2(List<Moon> moons) {
        System.out.println("PART 2:");
        System.out.println("Initial");
        displayData(moons);
        System.out.println();

        int xCount = 0;
        int yCount = 0;
        int zCount = 0;
        boolean xBackToStart = false;
        boolean yBackToStart = false;
        boolean zBackToStart = false;

        // Rather than work out X, then reset the positions, work out Y, reset, work out Z, we'll do all 3 at
        // the same time. Once the period of a particular dimension has been determined, we'll stop checking it
        // and counting steps for it. Once all 3 have been determined, we exit the loop.
        do {
            moveMoons(moons);

            if (xBackToStart == false) {
                xCount++;
                xBackToStart = moons.stream().allMatch(Moon::xMatchesInitial);
            }
            if (yBackToStart == false) {
                yCount++;
                yBackToStart = moons.stream().allMatch(Moon::yMatchesInitial);
            }
            if (zBackToStart == false) {
                zCount++;
                zBackToStart = moons.stream().allMatch(Moon::zMatchesInitial);
            }
        } while ((xBackToStart && yBackToStart && zBackToStart) == false);
        
        System.out.println("X period: " + xCount);
        System.out.println("Y period: " + yCount);
        System.out.println("Z period: " + zCount);
        System.out.println("Steps required: " + lcm(xCount, yCount, zCount));
    }
    
    private static void moveMoons(List<Moon> moons) {
        // Apply gravity (update velocities)
        for (int i = 0; i < moons.size() - 1; i++) {
            Moon a = moons.get(i);
            for (int j = i + 1; j < moons.size(); j++) {
                Moon b = moons.get(j);
                
                if (a.x > b.x) {
                    a.vel_x--;
                    b.vel_x++;
                } else if (a.x < b.x) {
                    a.vel_x++;
                    b.vel_x--;
                }
                
                if (a.y > b.y) {
                    a.vel_y--;
                    b.vel_y++;
                } else if (a.y < b.y) {
                    a.vel_y++;
                    b.vel_y--;
                }
                
                if (a.z > b.z) {
                    a.vel_z--;
                    b.vel_z++;
                } else if (a.z < b.z) {
                    a.vel_z++;
                    b.vel_z--;
                }
            }
        }

        // Apply velocity (update positions)
        for (Moon moon : moons) {
            moon.x += moon.vel_x;
            moon.y += moon.vel_y;
            moon.z += moon.vel_z;
        }
    }
    
    private static long lcm(int a, int b, int c) {
        return lcm(lcm(a, b), c);
    }

    private static long lcm(long a, long b) {
        if (b == 0) {
            return 0;
        }
        
        return (a * b) / gcd(a, b);
    }
    
    private static long gcd(long x, long y) {
        while (y != 0) {
            long temp = x;
            x = y;
            y = temp % y;
        }
        
        return Math.abs(x);
    }
}
