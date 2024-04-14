package anonym.anonyms.anonym.utilities;

import java.util.ArrayList;

/* The first manner to encode a witness matrix
 * As a list of list of integers.
 * The size of the list is the size of the base relation.
 * The list of integer in position i indicate the position of the tuple in the results 
 * of the query to which the tuple in poistion i in the base relation contributes to.
 */
public class WitnessMatrix_2 {

	String query;
	String base_relation;
	int size_results;
	int size_base_relation;
	
	ArrayList<ArrayList<Integer>> value;
	
	public WitnessMatrix_2(String query, int size_results, String base_relation, int size_base_relation) {
		
		this.query = query;
		this.base_relation = base_relation;
		this.size_base_relation = size_base_relation;
		this.size_results = size_results;
		
		value = new ArrayList<ArrayList<Integer>>();
		
	}
	
	public void set(int pos, ArrayList<Integer> value_pos) {
		value.set(pos, value_pos);
	}
	
	public ArrayList<Integer> get(int pos) {
		return value.get(pos);
	}
	
	public void set(ArrayList<ArrayList<Integer>> value) {
		this.value = value;
	}
	
	public ArrayList<ArrayList<Integer>> get() {
		return value;
	}

}
