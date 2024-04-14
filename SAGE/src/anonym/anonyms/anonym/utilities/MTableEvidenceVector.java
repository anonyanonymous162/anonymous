package anonym.anonyms.anonym.utilities;

import java.util.List;
import java.util.Map;

import org.apache.lucene.util.OpenBitSet;

import java.util.ArrayList;
import java.util.HashMap;

public class MTableEvidenceVector {
	
	Map<Integer,List<Integer>> value;
	
	public MTableEvidenceVector(){
		value = new HashMap<Integer,List<Integer>>();
	}

	
	public Map<Integer,List<Integer>> getValue(){
		return this.value;
	}
	
	public static void print(MTableEvidenceVector rev, int num_attrs, List<OpenBitSet> possible_bitsets) {
		
		Map<Integer,List<Integer>> value = rev.value;
		
		for (Map.Entry<Integer,List<Integer>> entry : value.entrySet()) {
		    System.out.println("Row:"+entry.getKey());
		   entry.getValue().forEach(num -> System.out.print(BitSetUtils.toString(possible_bitsets.get(num),num_attrs)+ "   "));
		   System.out.println();
		}
		

	}
	
	public static void print(Map<Integer,List<Integer>> value, int num_attrs, List<OpenBitSet> possible_bitsets) {
		
		
		for (Map.Entry<Integer,List<Integer>> entry : value.entrySet()) {
		    System.out.println("Row:"+entry.getKey());
		    entry.getValue().forEach(num -> System.out.print(BitSetUtils.toString(possible_bitsets.get(num),num_attrs)+ "   "));
		    System.out.println();
		}
		

	}
	
	public static Map<Integer,Integer> collect(Map<Integer,List<Integer>> value, List<OpenBitSet> possible_bitsets){
		
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for (int i=0; i<possible_bitsets.size(); i++)
			map.put(i, 0);
		
			for (Map.Entry<Integer,List<Integer>> entry : value.entrySet()) {
				for (Integer col: entry.getValue()) {
					map.merge(col, 1, Integer::sum);
				}
			}
		
		return map;
	}
		


}
