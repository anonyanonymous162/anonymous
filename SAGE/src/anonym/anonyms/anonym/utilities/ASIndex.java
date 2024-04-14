package anonym.anonyms.anonym.utilities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.lucene.util.OpenBitSet;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.*;




public class ASIndex {
    public Map<Integer, List<Set<Integer>>> dictionary;
   // public Set<Integer> tuples;
    
    public ASIndex() {
        dictionary = new Int2ObjectOpenHashMap<>();
      //  tuples = new HashSet<Integer>();
    }
    
    public Set<Integer> getKeys(){
    	return dictionary.keySet();
    }
    
    public void add(int key, int ti, int tj) {
        //if (!dictionary.containsKey(key)) {
        //    dictionary.put(key, new HashSet<>());
       // }

    	Set set = new HashSet();
    	set.add(ti);
    	set.add(tj);
    	
        dictionary.get(key).add(set);
        
       // tuples.add(ti);
       // tuples.add(tj);
    }
    
    
    public List<Set<Integer>> get(int key) {
        return dictionary.get(key);
    }
    
    public void addKey(int key) {
    	dictionary.put(key, new ArrayList<Set<Integer>>());
    }
    
    public void printDictionary() {
        for (Map.Entry<Integer, List<Set<Integer>>> entry : dictionary.entrySet()) {
            int key = entry.getKey();
            List<Set<Integer>> list_values = entry.getValue();
            System.out.println(key);
            for (Set<Integer> values: list_values) {
            	for (Integer value : values) {
            		System.out.print("value: "+value+"; ");
            	}
            	System.out.println();
            }
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
            System.out.println("Number of tuples for bitset " + BitSetUtils.toString(possible_bs.get(key),attrs) + ": " + dictionary.get(key).size());
        }
    }
    
 
    
}