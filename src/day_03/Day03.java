package day_03;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day03 {

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String wire1Input = reader.readLine();
            String wire2Input = reader.readLine();
            reader.close();
            
            List<Point> wire1 = generateWireMap(wire1Input.split("\\s*,\\s*"));
            System.out.println(wire1);
            List<Point> wire2 = generateWireMap(wire2Input.split("\\s*,\\s*"));
            System.out.println(wire2);
            
            List<CrossPoint> crossPoints = calculateCrossPoints(wire1, wire2);
            System.out.println(crossPoints);
            
            // Part 1 - shortest Manhattan distance to origin
            int distance = Integer.MAX_VALUE;
            
            for (Point point : crossPoints) {
                int temp = Math.abs(point.getX()) + Math.abs(point.getY());
                
                if (temp < distance) {
                    distance = temp;
                    System.out.println("New short distance: " + distance + " " + point);
                }
            }
            
            System.out.println("Part 1: " + distance);
            
            // Part 2 - shortest distance by total wire length
            distance = Integer.MAX_VALUE;
            
            for (CrossPoint point : crossPoints) {
                int temp = point.getWire1Length() + point.getWire2Length();
                
                if (temp < distance) {
                    distance = temp;
                    System.out.println("New short distance: " + distance + " " + point);
                }
            }
            
            System.out.println("Part 2: " + distance);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static List<Point> generateWireMap(String[] input) {
        List<Point> wireMap = new ArrayList<>();
        int x = 0; int y = 0;
        wireMap.add(new Point(x, y));
        
        for (int i = 0; i < input.length; i++) {
            char direction = input[i].charAt(0);
            int value = Integer.parseInt(input[i].substring(1));
            
            switch (direction) {
                case 'U':
                    y += value;
                    break;
                    
                case 'D':
                    y -= value;
                    break;
                    
                case 'L':
                    x -= value;
                    break;
                    
                case 'R':
                    x += value;
                    break;
                    
                default:
                    System.out.println("Bad direction value: " + direction);
                    System.exit(-1);
            }
                
            wireMap.add(new Point(x, y));
        }
        
        return wireMap;
    }
    
    private static List<CrossPoint> calculateCrossPoints(List<Point> wire1, List<Point> wire2) {
        List<CrossPoint> crossPoints = new ArrayList<>();
        
        int w1RunningLength = 0;
        
        for (int i = 1; i < wire1.size(); i++) {
            // Wire 1 segment
            Point w1p1 = wire1.get(i - 1);
            Point w1p2 = wire1.get(i);
            
            // horizontal or vertical?
            boolean w1Vert = w1p1.getY() != w1p2.getY();
            
            int w2RunningLength = 0;
            
            for (int j = 1; j < wire2.size(); j++) {
                // Wire 2 segment
                Point w2p1 = wire2.get(j - 1);
                Point w2p2 = wire2.get(j);
                
                // horizontal or vertical?
                boolean w2Vert = w2p1.getY() != w2p2.getY();
                
//                if (w1Vert == w2Vert) {
//                    // segments are in the same direction, so they can't cross
//                    continue;
//                }
                
                if (w1Vert != w2Vert) {
                    if (w1Vert) {   // w1 segment is vertical, w2 segment is horizontal
                        // Work out w1 segment Y changes - X stays the same
                        int minY = Math.min(w1p1.getY(), w1p2.getY());
                        int maxY = Math.max(w1p1.getY(), w1p2.getY());
                        
                        // Work out w2 segment X changes - Y stays the same
                        int minX = Math.min(w2p1.getX(), w2p2.getX());
                        int maxX = Math.max(w2p1.getX(), w2p2.getX());
                        
                        // Does w2 Y fall within w1 Y AND w1 X fall within w2 X
                        if ((w2p1.getY() > minY && w2p1.getY() < maxY)
                                && (w1p1.getX() > minX && w1p1.getX() < maxX)) {
                            CrossPoint cross = new CrossPoint(w1p1.getX(), w2p1.getY());
                            cross.setWire1Length(w1RunningLength + segmentLength(w1p1, cross));
                            cross.setWire2Length(w2RunningLength + segmentLength(w2p1, cross));
                            crossPoints.add(cross);
                            System.out.println("cross point - " + cross);
                        }
                    } else {        // w1 segment is horizontal, w2 segment is vertical
                        // Work out w1 segment X changes - Y stays the same
                        int minX = Math.min(w1p1.getX(), w1p2.getX());
                        int maxX = Math.max(w1p1.getX(), w1p2.getX());

                        // Work out w2 segment Y changes - X stays the same
                        int minY = Math.min(w2p1.getY(), w2p2.getY());
                        int maxY = Math.max(w2p1.getY(), w2p2.getY());
                        
                        // Does w2 X fall within w1 X AND w1 Y fall within w2 Y
                        if ((w2p1.getX() > minX && w2p1.getX() < maxX)
                                && (w1p1.getY() > minY && w1p1.getY() < maxY)) {
                            CrossPoint cross = new CrossPoint(w2p1.getX(), w1p1.getY());
                            cross.setWire1Length(w1RunningLength + segmentLength(w1p1, cross));
                            cross.setWire2Length(w2RunningLength + segmentLength(w2p1, cross));
                            crossPoints.add(cross);
                            System.out.println("cross point - " + cross);
                        }
                    }
                }
                
                int w2SegLen = segmentLength(w2p1, w2p2);
                w2RunningLength += w2SegLen;
            }
            
            int w1SegLen = segmentLength(w1p1, w1p2);
            w1RunningLength += w1SegLen;
        }
        
        return crossPoints;
    }

    private static int segmentLength(Point a, Point b) {
        int xLen = Math.abs(b.getX() - a.getX());
        int yLen = Math.abs(b.getY() - a.getY());
        
        return (xLen + yLen);
    }
    
    static class Point {
        private int x;
        private int y;
        
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        @Override
        public String toString() {
            return "[x=" + x + ", y=" + y + "]";
        }
    }
    
    static class CrossPoint extends Point {
        private int wire1Length;
        private int wire2Length;
        
        public CrossPoint(int x, int y) {
            super(x, y);
        }
        
        public CrossPoint(int x, int y, int w1Length, int w2Length) {
            super(x, y);
            this.wire1Length = w1Length;
            this.wire2Length = w2Length;
        }

        public void setWire1Length(int length) {
            this.wire1Length = length;
        }
        
        public int getWire1Length() {
            return wire1Length;
        }

        public void setWire2Length(int length) {
            this.wire2Length = length;
        }
        
        public int getWire2Length() {
            return wire2Length;
        }
        
        @Override
        public String toString() {
            return "[x=" + getX() + ", y=" + getY() + ", w1Length=" + wire1Length + ", w2Length=" + wire2Length + "]";
        }
    }
}
