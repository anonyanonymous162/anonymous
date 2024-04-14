package anonym.anonyms.anonym.utilities;

import java.util.List;
import java.util.Map;

import org.apache.lucene.util.OpenBitSet;

import java.util.ArrayList;
import java.util.HashMap;

public class TableEvidenceVector {
	
	List<List<Integer>> value;
	
	public TableEvidenceVector(){
		value = new ArrayList<List<Integer>>();
	}
	
	public void initializeElements(int size) {
		
		for (int i=0; i< size; i++)
			value.add(new ArrayList<Integer>());
		
	}
	
	public List<List<Integer>> getValue(){
		return this.value;
	}
	
	public static void print(TableEvidenceVector rev, int num_attrs, List<OpenBitSet> possible_bitsets) {
		
		List<List<Integer>> value = rev.value;
		
		for (int i =0; i<value.size(); i++) {
			System.out.println("row "+i);
			for (int j = 0; j< value.get(i).size(); j++)
				System.out.println("column "+j+": "+BitSetUtils.toString(possible_bitsets.get(value.get(i).get(j)),num_attrs));
			}
		}
	
	public static void print(List<List<Integer>> value, int num_attrs, List<OpenBitSet> possible_bitsets) {
		
		
		for (int i =0; i<value.size(); i++) {
			System.out.println("row "+i);
			for (int j = 0; j< value.get(i).size(); j++)
				System.out.println("column "+j+": "+BitSetUtils.toString(possible_bitsets.get(value.get(i).get(j)),num_attrs));
			}
		}
	
	public static Map<Integer,Integer> collect(List<List<Integer>> value, List<OpenBitSet> possible_bitsets){
		
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for (int i=0; i<possible_bitsets.size(); i++)
			map.put(i, 0);
		
			for (List<Integer> row: value) {
				for (Integer col: row) {
					map.merge(col, 1, Integer::sum);
				}
			}
		
		return map;
	}
		


}
