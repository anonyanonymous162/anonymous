package anonym.anonyms.anonym.utilities;

import java.util.*;

import org.apache.lucene.util.OpenBitSet;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.*;

public class ASIndex_Old {
    private Map<Integer, Set<Pair<Integer, Integer>>> dictionary;
    
    public ASIndex_Old() {
        dictionary = new Int2ObjectOpenHashMap<>();
    }
    
    public void add(int key, int ti, int tj) {
        //if (!dictionary.containsKey(key)) {
        //    dictionary.put(key, new HashSet<>());
       // }
        dictionary.get(key).add(Pair.of(ti, tj));
    }
    
    
    public Set<Pair<Integer, Integer>> get(int key) {
        return dictionary.get(key);
    }
    
    public void addKey(int key) {
    	dictionary.put(key, new HashSet<>());
    }
    
    public void printDictionary() {
        for (Map.Entry<Integer, Set<Pair<Integer, Integer>>> entry : dictionary.entrySet()) {
            int key = entry.getKey();
            Set<Pair<Integer, Integer>> values = entry.getValue();
            System.out.print(key + ": ");
            for (Pair<Integer, Integer> value : values) {
                System.out.print("(" + value.left() + ", " + value.right() + ") ");
            }
            System.out.println();
        }
    }
    
    public void printSize() {
        System.out.println("Number of entries in the dictionary: " + dictionary.size());
        for (int key : dictionary.keySet()) {
            System.out.println("Number of pairs for key " + key + ": " + dictionary.get(key).size());
        }
    }
    
    public void printSize(List<OpenBitSet> possible_bs, int attrs) {
        System.out.println("Number of entries in the dictionary: " + dictionary.size());
        for (int key : dictionary.keySet()) {
            System.out.println("Number of pairs for bitset " + BitSetUtils.toString(possible_bs.get(key),attrs) + ": " + dictionary.get(key).size());
        }
    }
    
}