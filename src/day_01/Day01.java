package day_01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day01 {

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;
        int moduleFuelSum = 0;
        
        try {
            while ((inputLine = reader.readLine()) != null) {
                int moduleMass = Integer.parseInt(inputLine);
                int moduleFuel = requiredFuel(moduleMass);
                int fuelFuelSum = 0;
                int fuelFuel = moduleFuel;
                
                do {
                    fuelFuel = requiredFuel(fuelFuel);
                    fuelFuelSum += fuelFuel;
                } while (fuelFuel > 0);
                
                moduleFuelSum += (moduleFuel + fuelFuelSum);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        System.out.println(moduleFuelSum);
    }
    
    private static int requiredFuel(int weight) {
        int fuel = (weight / 3) - 2;
        
        return (fuel > 0 ? fuel : 0);
    }
}
