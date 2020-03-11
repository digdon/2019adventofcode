package day_16;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day16 {

    public static final int[] BASE_PATTERN = new int[] { 0, 1, 0, -1 };
    
    public static void main(String[] args) {
        // Read in input
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;

        try {
            input = reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (input != null) {
            // Part 1
            String result = partOne(input);
            System.out.println("PART 1: " + result.substring(0, 8));
            
            // Part 2
            result = partTwo(input);
            System.out.println("PART 2: " + result);
        }
    }

    private static String partOne(String input) {
        for (int phase = 0; phase < 100; phase++) {
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < input.length(); i++) {
                int total = 0;
                
                for (int j = 0; j < input.length(); j++) {
                    int multiplier = BASE_PATTERN[((j + 1) / (i + 1)) % BASE_PATTERN.length];
                    total += (input.charAt(j) - '0') * multiplier;
                }
                
                int value = Math.abs(total) % 10;
                sb.append(value);
            }
            
            input = sb.toString();
        }
        
        return input;
    }
    
    private static String partTwo(String input) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < 10000; i++) {
            sb.append(input);
        }
        
        String fullSignal = sb.toString();
        
        // Grab offset
        int offset = Integer.valueOf(fullSignal.substring(0, 7));
        
        // Build vector with only useful data - ie nothing before the offset
        String shortenedSignal = fullSignal.substring(offset);
        int[] vector = new int[shortenedSignal.length()];
        
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (shortenedSignal.charAt(i) - '0') % 10;
        }
        
        for (int phase = 0; phase < 100; phase++) {
            for (int pos = vector.length - 2; pos >= 0; pos--) {
                vector[pos] = (vector[pos] + vector[pos + 1]) % 10;
            }
        }

        sb = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            sb.append(vector[i]);
        }
        
        return sb.toString();
    }
}
