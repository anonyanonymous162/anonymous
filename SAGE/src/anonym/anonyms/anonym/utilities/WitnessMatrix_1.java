package anonym.anonyms.anonym.utilities;

import java.util.ArrayList;

/* The first manner to encode a witness matrix
 * As a list of integers.
 * The size of the list is the number of results of a query
 * The integer in position i indicate the position of the tuple
 * in the base relation that constributed to that element
 */

public class WitnessMatrix_1 {

	String query;
	String base_relation;
	int size_results;
	int size_base_relation;
	
	ArrayList<Integer> value;
	
	public WitnessMatrix_1(String query, int size_results, String base_relation, int size_base_relation) {
		
		this.query = query;
		this.base_relation = base_relation;
		this.size_base_relation = size_base_relation;
		this.size_results = size_results;
		
		value = new ArrayList<Integer>();
		
	}
	
	public void set(int pos, int value_pos) {
		value.set(pos, value_pos);
	}
	
	public int get(int pos) {
		return value.get(pos);
	}
	
	public void set(ArrayList<Integer> value) {
		this.value = value;
	}
	
	public ArrayList<Integer> get() {
		return value;
	}
	
}
