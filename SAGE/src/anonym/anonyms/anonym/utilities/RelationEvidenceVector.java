package anonym.anonyms.anonym.utilities;

import java.util.List;

import org.apache.lucene.util.OpenBitSet;

import java.util.ArrayList;

public class RelationEvidenceVector {
	
	List<List<OpenBitSet>> value;
	
	public RelationEvidenceVector(){
		value = new ArrayList<List<OpenBitSet>>();
	}
	
	public void initializeElements(int size) {
		
		for (int i=0; i< size; i++)
			value.add(new ArrayList<OpenBitSet>());
		
	}
	
	public List<List<OpenBitSet>> getValue(){
		return this.value;
	}
	
	public static void print(RelationEvidenceVector rev, int num_attrs) {
		
		List<List<OpenBitSet>> value = rev.value;
		
		for (int i =0; i<value.size(); i++) {
			System.out.println("row "+i);
			for (int j = 0; j< value.get(i).size(); j++)
				System.out.println("column "+j+": "+BitSetUtils.toString(value.get(i).get(j),num_attrs));
			}
		}
	
	public static void print(List<List<OpenBitSet>> value, int num_attrs) {
		
		
		for (int i =0; i<value.size(); i++) {
			System.out.println("row "+i);
			for (int j = 0; j< value.get(i).size(); j++)
				System.out.println("column "+j+": "+BitSetUtils.toString(value.get(i).get(j),num_attrs));
			}
		}
		


}
