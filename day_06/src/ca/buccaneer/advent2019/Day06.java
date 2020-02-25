package ca.buccaneer.advent2019;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Day06 {

    public static void main(String[] args) {
        HashMap<String, Orbit> orbitMap = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                String[] orbitPath = inputLine.split("\\s*\\)\\s*");
                Orbit parent = null;
                for (int i = 0; i < orbitPath.length; i++) {
                    String name = orbitPath[i];
                    Orbit orbit = orbitMap.get(name);
                    
                    if (orbit == null) {
                        orbit = new Orbit(name);
                        orbitMap.put(name, orbit);
                    }
                    
                    if (parent != null) {
                        parent.addChild(orbit);
                    }

                    if (orbit.getParent() == null) {
                        orbit.setParent(parent);
                    }
                    
                    parent = orbit;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

//        displayMap(orbitMap);
        int count = countOrbits(orbitMap);
        System.out.println("Part 1: " + count);
        
        System.out.println("Part 2: " + calculateTransfer("YOU", "SAN", orbitMap));
    }

    private static int countOrbits(Map<String, Orbit> map) {
        int total = 0;

        for (Orbit orbit : map.values()) {
            if (orbit.getParent() == null) {
                // A top-level node
                if (orbit.getChildren() != null) {
                    for (Orbit child : orbit.getChildren()) {
                        total += countNode(child, 1);
                    }
                }
            }
        }
        
        return total;
    }

    private static int countNode(Orbit orbit, int depth) {
        int total = depth;

        if (orbit.getChildren() != null) {
            for (Orbit child : orbit.getChildren()) {
                total += countNode(child, depth + 1);
            }
        }
        
        return total;
    }

    private static int calculateTransfer(String firstName, String secondName, Map<String, Orbit> map) {
        Orbit firstNode = map.get(firstName);
        Orbit secondNode = map.get(secondName);
        
        if (firstNode == null || secondNode == null) {
            return -1;
        }
        
        Stack<Orbit> firstPath = nodePath(firstNode);
        Stack<Orbit> secondPath = nodePath(secondNode);

        while (firstPath.isEmpty() == false && secondPath.isEmpty() == false) {
            Orbit first = firstPath.peek();
            Orbit second = secondPath.peek();
            
            if (first.getName().equals(second.getName()) == false) {
                break;
            }
            
            firstPath.pop();
            secondPath.pop();
        }

        return (firstPath.size() + secondPath.size());
    }
    
    private static Stack<Orbit> nodePath(Orbit element) {
        Stack<Orbit> path = new Stack<>();
        
        for (element = element.getParent(); element != null; element = element.getParent()) {
            path.push(element);
        }
        
        return path;
    }
    
    private static void displayMap(Map<String, Orbit> map) {
        for (Orbit orbit : map.values()) {
            if (orbit.getParent() == null) {
                // Top-level - display map from here
                displayNode(orbit);
            }
        }
    }

    private static void displayNode(Orbit orbit) {
        displayNode(orbit, "");
    }
    
    private static void displayNode(Orbit orbit, String padding) {
        System.out.println(padding + orbit.getName());
        
        if (orbit.getChildren() != null) {
            for (Orbit child : orbit.getChildren()) {
                displayNode(child, padding + "    ");
            }
        }
    }
    
    static class Orbit {
        private String name;
        private Orbit parent;
        private List<Orbit> children;

        public Orbit(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }

        public void setParent(Orbit parent) {
            this.parent = parent;
        }
        
        public Orbit getParent() {
            return parent;
        }
        
        public void addChild(Orbit child) {
            if (children == null) {
                children = new ArrayList<>();
            } else if (children.contains(child)) {
                children.remove(child);
            }
            
            children.add(child);
        }
        
        public List<Orbit> getChildren() {
            return children;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            
            if (o == null) {
                return false;
            }
            
            if ((o instanceof Orbit) == false) {
                return false;
            }
            
            Orbit oOrbit = (Orbit)o;
            
            return this.name.equals(oOrbit.name);
        }
    }
}
