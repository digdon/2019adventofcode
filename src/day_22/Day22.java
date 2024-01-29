package day_22;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day22 {

    public static void main(String []args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> inputLines = reader.lines().collect(Collectors.toList());
        reader.close();

        // Part 1 stuff
        
        // Let's do it by building the deck and shuffling via that deck, rebuilding as necessary
        int deckSize = 10007;
        List<Integer> deck = new ArrayList<>(deckSize);
        
        for (int i = 0; i < deckSize; i++) {
            deck.add(i);
        }
        
        deck = processRulesViaDeck(deck, inputLines);
        
        for (int i = 0; i < deckSize; i++) {
            if (deck.get(i) == 2019) {
                System.out.println("Part 1: " + i);
                break;
            }
        }

        // Now let's do it using simple match to follow the one card we're interested in
        part1FollowCard(inputLines);
        
        // Part 2 foolishness
        part2(inputLines);
    }

    private static List<Integer> processRulesViaDeck(List<Integer> deck, List<String> rules) {
        for (String rule : rules) {
            if (rule.contains("increment")) {
                int inc = Integer.parseInt(rule.substring(rule.lastIndexOf(' ') + 1));
                deck = dealIncrement(deck, inc);
            } else if (rule.contains("stack")) {
                deck = dealStack(deck);
            } else {
                int cutSize = Integer.parseInt(rule.substring(rule.lastIndexOf(' ') + 1));
                deck = cutN(deck, cutSize);
            }
        }
        
        return deck;
    }
    
    private static List<Integer> dealStack(List<Integer> sourceDeck) {
        List<Integer> newDeck = new ArrayList<>(sourceDeck.size());

        for (int i = sourceDeck.size() - 1; i >= 0; i--) {
            newDeck.add(sourceDeck.get(i));
        }
        
        return newDeck;
    }
    
    private static List<Integer> dealIncrement(List<Integer> sourceDeck, int increment) {
        int deckSize = sourceDeck.size();
        int[] tempDeck = new int[deckSize];
        int pos = 0;
        
        for (Integer card : sourceDeck) {
            tempDeck[pos % deckSize] = card;
            pos += increment;
        }

        List<Integer> newDeck = new ArrayList<>(deckSize);
        for (int i = 0; i < tempDeck.length; i++) {
            newDeck.add(tempDeck[i]);
        }
        
        return newDeck;
    }
    
    private static List<Integer> cutN(List<Integer> sourceDeck, int n) {
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                sourceDeck.add(sourceDeck.remove(0));
            }
        } else {
            for (int i = n; i < 0; i++) {
                sourceDeck.add(0, sourceDeck.remove(sourceDeck.size() - 1));
            }
        }
        
        return sourceDeck;
    }
    
    private static void part1FollowCard(List<String> inputLines) {
        int deckSize = 10007;
        int cardPos = 2019; // This is the card we're following. At the beginning, it's in position 2019, but as the rules are applied, this card changes location
        
        for (String line : inputLines) {
            if (line.contains("increment")) {
                int inc = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
                cardPos = (cardPos * inc) % deckSize;
            } else if (line.contains("stack")) {
                cardPos = deckSize - cardPos - 1;
            } else {
                int cutPos = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
                cardPos = (cardPos - cutPos) % deckSize;
            }
        }
        
        System.out.println("Part 1: " + cardPos);
    }
    
    private static void part2(List<String> rules) {
        // Set up a test first, using part 1 deck solution
        int deckSize = 10007;
        List<Integer> deck = new ArrayList<>(deckSize);
        
        for (int i = 0; i < deckSize; i++) {
            deck.add(i);
        }
        
        for (int i = 0; i < 10; i++) {
            deck = processRulesViaDeck(deck, rules);
        }
        
        System.out.println(deck.get(2020));
        
        // Now with new code, to compare
        BigInteger value = shuffle2(BigInteger.valueOf(deckSize), BigInteger.valueOf(10), 2020, rules);
        System.out.println(value);
        
        // Again, but with the real values
        value = shuffle2(BigInteger.valueOf(119315717514047l),
                         BigInteger.valueOf(101741582076661l),
                         2020,
                         rules);
        System.out.println("Part 2: " + value);
    }
    
    private static BigInteger shuffle2(BigInteger L, BigInteger N, int targetPos, List<String> rules) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = BigInteger.ZERO;
        
        for (int i = rules.size() - 1; i >= 0; i--) {
            String rule = rules.get(i);
            if (rule.contains("stack")) {
                a = a.negate();
                b = L.subtract(b).subtract(BigInteger.ONE);
            } else if (rule.startsWith("cut")) {
                BigInteger n = new BigInteger(rule.substring(rule.lastIndexOf(' ') + 1));
                b = b.add(n).mod(L);
            } else {
                BigInteger n = new BigInteger(rule.substring(rule.lastIndexOf(' ') + 1));
                BigInteger z = n.modPow(L.subtract(BigInteger.TWO), L);
                a = a.multiply(z).mod(L);
                b = b.multiply(z).mod(L);
            }
        }
        
        System.out.println(a + " " + b);

        PolyValues values = polypow(new PolyValues(a, b), N, L);
        System.out.println(values);
        return BigInteger.valueOf(targetPos).multiply(values.a()).add(values.b()).mod(L);
    }

    private static PolyValues polypow(PolyValues values, BigInteger m, BigInteger n) {
        if (m.equals(BigInteger.ZERO)) {
            return new PolyValues(BigInteger.ONE, BigInteger.ZERO);
        }
        
        if (m.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            BigInteger a = values.a().multiply(values.a()).mod(n);
            BigInteger b = values.a().multiply(values.b()).add(values.b()).mod(n);
            return polypow(new PolyValues(a, b), m.divide(BigInteger.TWO), n);
        }
        
        PolyValues tempValues = polypow(values, m.subtract(BigInteger.ONE), n);
        BigInteger a = values.a().multiply(tempValues.a()).mod(n);
        BigInteger b = values.a().multiply(tempValues.b()).add(values.b()).mod(n);
        
        return new PolyValues(a, b);
    }
    
    private record PolyValues(BigInteger a, BigInteger b) {}
}
