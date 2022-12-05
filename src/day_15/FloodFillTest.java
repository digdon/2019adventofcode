package day_15;

import java.util.Deque;
import java.util.LinkedList;

public class FloodFillTest {

    private static int[][] map = null;

    public static void main(String[] args) {
        map = new int[10][10];
        
        for (int i = 0; i < 4; i++) {
            map[3][i] = -1;
            map[i][3] = -1;
            map[6][i] = -1;
            map[i + 6][3] = -1;
            map[6][i + 6] = -1;
            map[i+ 6][6] = -1;
        }
        
        displayMap();
        System.out.println();
        floodFill(5, 6, 0, 1);
        displayMap();
    }

    private static void displayMap() {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                System.out.print(map[y][x] == 0 ? "  . " : String.format("%3d ", map[y][x]));
            }
            System.out.println();
        }
    }
    
    public static void floodFill(int startX, int startY, int targetValue, int newValue) {
        int count = 0;
        Deque<Integer> xQueue = new LinkedList<>();
        Deque<Integer> yQueue = new LinkedList<>();
        map[startY][startX] = ++count;
        xQueue.add(startX);
        yQueue.add(startY);
        
        while (xQueue.isEmpty() == false) {
            int x = xQueue.remove();
            int y = yQueue.remove();
            
            if ((y - 1 >= 0) && map[y - 1][x] == targetValue) {     // node to the north
                map[y - 1][x] = ++count;
                xQueue.add(x);
                yQueue.add(y - 1);
            }

            if ((y + 1 < map.length) && map[y + 1][x] == targetValue) {     // node to the south
                map[y + 1][x] = ++count;
                xQueue.add(x);
                yQueue.add(y + 1);
            }
            
            if ((x - 1 >= 0) && map[y][x - 1] == targetValue) {     // node to the west
                map[y][x - 1] = ++count;
                xQueue.add(x - 1);
                yQueue.add(y);
            }
            
            if ((x + 1 < map[y].length) && map[y][x + 1] == targetValue) {   // node to the east
                map[y][x + 1] = ++count;
                xQueue.add(x + 1);
                yQueue.add(y);
            }
        }
    }
}
