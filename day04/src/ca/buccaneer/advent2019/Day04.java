package ca.buccaneer.advent2019;

public class Day04 {

    public static void main(String[] args) {
        int start = 256310;
        int end = 732736;

        // Part one
        int value = 111111;
        System.out.println(value + " - " + testValuePartOne(value));
        
        value = 223450;
        System.out.println(value + " - " + testValuePartOne(value));
        
        value = 123789;
        System.out.println(value + " - " + testValuePartOne(value));
        
        int pwCount = 0;
        
        for (int i = start; i <= end; i++) {
            if (testValuePartOne(i)) {
                System.out.println(i);
                pwCount++;
            }
        }
        
        System.out.println("Total possibilities: " + pwCount);

        // Part two
        value = 112233;
        System.out.println(value + " - " + testValuePartTwo(value));

        value = 123444;
        System.out.println(value + " - " + testValuePartTwo(value));

        value = 111122;
        System.out.println(value + " - " + testValuePartTwo(value));
        
        pwCount = 0;
        
        for (int i = start; i <= end; i++) {
            if (testValuePartTwo(i)) {
                System.out.println(i);
                pwCount++;
            }
        }
        
        System.out.println("Total possibilities: " + pwCount);
    }
    
    private static boolean testValuePartOne(int value) {
        String digits = String.valueOf(value);
        
        boolean doubleFound = false;
        
        for (int i = 0; i < digits.length() - 1; i++) {
            if (digits.charAt(i + 1) < digits.charAt(i)) {
                // Digits must never decrease
                return false;
            }
            
            if (digits.charAt(i + 1) == digits.charAt(i)) {
                doubleFound = true;
            }
        }
        
        return doubleFound;
    }
    
    private static boolean testValuePartTwo(int value) {
        String digits = String.valueOf(value);
        
        boolean doubleFound = false;
        boolean largeGroupFound = false;
        int runCount = 1;
        
        for (int i = 0; i < digits.length() - 1; i++) {
            if (digits.charAt(i + 1) < digits.charAt(i)) {
                // Digits must never decrease
                return false;
            }
            
            if (digits.charAt(i + 1) == digits.charAt(i)) {
                runCount++;
            } else {
                if (runCount == 2) {
                    doubleFound = true;
                } else if (runCount > 2) {
                    largeGroupFound = true;
                }
                
                runCount = 1;
            }
        }
        
        if (runCount == 2) {
            doubleFound = true;
        } else if (runCount > 2) {
            largeGroupFound = true;
        }
        
        if (largeGroupFound == true && doubleFound == false) {
            return false;
        } else {
            return doubleFound;
        }
    }
}
