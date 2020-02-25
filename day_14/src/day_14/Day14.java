package day_14;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {

    private static final Map<String, Reaction> reactionMap = new HashMap<>();
    
    public static void main(String[] args) {
        // Read in and parse reactions
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = null;

        try {
            while ((inputLine = reader.readLine()) != null) {
                parseReaction(inputLine);
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

        System.out.println(reactionMap);

        long oreRequired = makeFuel();
        System.out.println("PART 1: " + oreRequired);

        long startOre = 1000000000000L - oreRequired;
        int count = 1;

        while (true) {
            if (count % 100000 == 0) {
                System.out.println(count);
            }
            
            oreRequired = makeFuel();
            startOre -= oreRequired;
            
            if (startOre < 0) {
                break;
            }
            
            count++;
        }
        
        System.out.println("PART 2: " + count);
    }

    private static final Map<String, Integer> supplies = new HashMap<>();
    private static final Queue<Reaction> orders = new LinkedList<>();
    
    private static long makeFuel() {
        long oreNeeded = 0;
        
        Reaction item = reactionMap.get("FUEL");
        orders.add(new Reaction(item.getName(), item.getCount()));

        while (orders.isEmpty() == false) {
            Reaction order = orders.remove();
            
            if (order.getName().equals("ORE")) {
                oreNeeded += order.getCount();
            } else {
                int orderCount = order.getCount();
                Integer supplyCount = supplies.get(order.getName());
                
                if (supplyCount == null) {
                    supplyCount = new Integer(0);
                }
                
                if (supplyCount < orderCount) {
                    // Not enough supplies - order more
                    // But how many more do we actually need?
                    int needed = orderCount - supplyCount;
                    Reaction reaction = reactionMap.get(order.getName());
                    int batchesRequired = (int)(Math.ceil((double)needed / (double)reaction.count));
                    
                    for (Reaction piece : reaction.getComponents()) {
                        orders.add(new Reaction(piece.getName(), piece.getCount() * batchesRequired));
                    }
                    
                    int leftovers = (batchesRequired * reaction.getCount()) - needed;
                    supplies.put(order.getName(), leftovers);
                } else {
                    // Take what we need
                    supplies.replace(order.getName(), supplyCount - orderCount);
                }
            }
        }
        
        return oreNeeded;
    }
    
    private static final Pattern PATTERN = Pattern.compile("\\s*(\\d+)\\s+(\\w+),?");

    private static void parseReaction(String input) {
        String[] sides = input.split("=>");
        Reaction component = null;
        Matcher matcher = PATTERN.matcher(sides[1]);

        if (matcher.find()) {
            int count = Integer.valueOf(matcher.group(1));
            String name = matcher.group(2);
            component = new Reaction(name, count);
        }

        matcher = PATTERN.matcher(sides[0]);
        
        while (matcher.find()) {
            int count = Integer.valueOf(matcher.group(1));
            String name = matcher.group(2);
            component.addComponent(new Reaction(name, count));
        }
        
        if (reactionMap.containsKey(component.getName())) {
            System.out.println("Weird - reaction mapping for " + component.getName() + " already exists");
            System.exit(-1);
        } else {
            reactionMap.put(component.getName(), component);
        }
    }
    
    static class Reaction {
        private String name;
        private int count;
        private List<Reaction> components = null;
        
        public Reaction(String name, int count) {
            this.name = name;
            this.count = count;
        }
        
        public void addComponent(Reaction item) {
            if (components == null) {
                components = new ArrayList<>();
            }
            
            components.add(item);
        }

        public String getName() {
            return name;
        }
        
        public int getCount() {
            return count;
        }
        
        public List<Reaction> getComponents() {
            return components;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("name=").append(name);
            sb.append(", count=").append(count);
            
            if (components != null) {
                sb.append(", componentList=").append(components);
            }
            
            sb.append("}");
            
            return sb.toString();
        }
    }
}
