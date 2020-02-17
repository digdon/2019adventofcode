package day_08;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day08 {

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                partOne(inputLine);
                partTwo(inputLine);
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
    }

    private static final int IMAGE_WIDTH = 25;
    private static final int IMAGE_HEIGHT = 6;
    private static final int LAYER_LENGTH = (IMAGE_WIDTH * IMAGE_HEIGHT);
    
    private static void partOne(String imageData) {
        int fewestZeros = Integer.MAX_VALUE;
        int onesCount = 0;
        int twosCount = 0;
        
        for (int pos = 0; pos < imageData.length(); pos += LAYER_LENGTH) {
            String layer = imageData.substring(pos, pos + LAYER_LENGTH);
            int zeroCount = 0;
            int tempOnesCount = 0;
            int tempTwosCount = 0;
            
            for (int i = 0; i < layer.length(); i++) {
                if (layer.charAt(i) == '0') {
                    zeroCount++;
                } else if (layer.charAt(i) == '1') {
                    tempOnesCount++;
                } else if (layer.charAt(i) == '2') {
                    tempTwosCount++;
                }
            }
            
            if (zeroCount < fewestZeros) {
                fewestZeros = zeroCount;
                onesCount = tempOnesCount;
                twosCount = tempTwosCount;
            }
        }
        
        System.out.println("Part 1: " + (onesCount * twosCount));
    }
    
    private static void partTwo(String imageData) {
        byte[] baseImage = imageData.substring(0, LAYER_LENGTH).getBytes();
        
        for (int pos = LAYER_LENGTH; pos < imageData.length(); pos += LAYER_LENGTH) {
            byte[] layerData = imageData.substring(pos, pos + LAYER_LENGTH).getBytes();
            
            for (int i = 0; i < layerData.length; i++) {
                if (baseImage[i] == '2') {
                    baseImage[i] = layerData[i];
                }
            }
        }
        
        String newImage = new String(baseImage);
        newImage = newImage.replaceAll("0", " ");
        newImage = newImage.replaceAll("1", "\\*");
        
        for (int i = 0; i < newImage.length(); i += IMAGE_WIDTH) {
            System.out.println(newImage.substring(i, i + IMAGE_WIDTH));
        }
    }
}
