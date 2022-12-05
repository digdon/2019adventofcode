package day_18;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day18 {

    public static void main(String[] args) {
        List<String> grid = new ArrayList<>();
        
        // Load input
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                grid.add(inputLine);
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
        
        // Scan the grid, counting up the total number of keys and looking for start point
        int totalKeys = 0;
        int startRow = 0;
        int startCol = 0;
        
        for (int row = 0; row < grid.size(); row++) {
            String rowData = grid.get(row);
            
            for (int col = 0; col < rowData.length(); col++) {
                char charAt = rowData.charAt(col);
                
                if (charAt >= 'a' && charAt <= 'z') {
                    // Found a key
                    totalKeys++;
                } else if (charAt == '@') {
                    // Found the start
                    startRow = row;
                    startCol = col;
                }
            }
        }
        
        System.out.println(String.format("Start position: %d,%d", startCol, startRow));
        System.out.println("Keys to find: " + totalKeys);
    }
}
