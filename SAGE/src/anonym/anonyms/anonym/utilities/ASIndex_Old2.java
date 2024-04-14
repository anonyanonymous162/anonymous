package anonym.anonyms.anonym.utilities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.lucene.util.OpenBitSet;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.*;




public class ASIndex_Old2 {
    public Map<Integer, Set<Integer>> dictionary;
    public Set<Integer> tuples;
    
    public ASIndex_Old2() {
        dictionary = new Int2ObjectOpenHashMap<>();
        tuples = new HashSet<Integer>();
    }
    
    public void add(int key, int ti, int tj) {
        //if (!dictionary.containsKey(key)) {
        //    dictionary.put(key, new HashSet<>());
       // }
        dictionary.get(key).add(ti);
        dictionary.get(key).add(tj);
        tuples.add(ti);
        tuples.add(tj);
    }
    
    
    public Set<Integer> get(int key) {
        return dictionary.get(key);
    }
    
    public void addKey(int key) {
    	dictionary.put(key, new HashSet<>());
    }
    
    public void printDictionary() {
        for (Map.Entry<Integer, Set<Integer>> entry : dictionary.entrySet()) {
            int key = entry.getKey();
            Set<Integer> values = entry.getValue();
            System.out.print(key + ": ");
            for (Integer value : values) {
                System.out.print("value: "+value+"; ");
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
            System.out.println("Number of tuples for bitset " + BitSetUtils.toString(possible_bs.get(key),attrs) + ": " + dictionary.get(key).size());
        }
    }
    
    public Set<Integer> get_union(){
  
    	return this.dictionary.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    	
    }
    
    public Set<Integer> getResPositions_old(List<Integer> witnessList){
    	
    	Set<Integer> unionSet = this.dictionary.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    	Set<Integer> positions = IntStream.range(0, witnessList.size())
                .filter(i -> unionSet.contains(witnessList.get(i)))
                .boxed()
                .collect(Collectors.toSet());
    	
    	return positions;
    	
    }
    
    public int[] getResPositions(List<Integer> witnessList){
    	
    	int[] unionArray = dictionary.values()
    		    .stream()
    		    .flatMapToInt(set -> set.stream().mapToInt(Integer::intValue))
    		    .distinct()
    		    .toArray();

    		int[] positionsArray = IntStream.range(0, witnessList.size())
    		    .parallel()
    		    .filter(i -> IntStream.of(unionArray).anyMatch(j -> j == witnessList.get(i)))
    		    .toArray();

    	return positionsArray;
    	
    }
    
}