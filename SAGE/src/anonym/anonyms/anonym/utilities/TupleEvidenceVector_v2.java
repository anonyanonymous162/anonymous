package anonym.anonyms.anonym.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.util.OpenBitSet;

public class TupleEvidenceVector_v2 {
	
	int num_atts;
	int num_tuples;
	ArrayList<OpenBitSet> value;
	
	
	public TupleEvidenceVector_v2(int num_atts, int num_tuples) {
		this.num_atts = num_atts;
		this.num_tuples = num_tuples;
		value = new ArrayList<OpenBitSet>();
	};
	

	public void add(int pos, OpenBitSet ops) {
		this.value.add(pos, ops);
	}
	
	
	public OpenBitSet get(int pos) {
		return this.value.get(pos);
	}
	
	public ArrayList<OpenBitSet> get(){
		return this.value;
	}
	
	public static Map<OpenBitSet,Integer> collect(TupleEvidenceVector_v2 tev) {
		
		/* This method may be efficient when the number
		 * of set bits in bitsets is small
		 */
		
		Map<OpenBitSet,Integer> agreesets = new HashMap<OpenBitSet,Integer>();
		int val;
		
		for (OpenBitSet agreeset: tev.value)
			if (agreesets.containsKey(agreeset)) {
				val = agreesets.get(agreeset);
				val++;
				agreesets.put(agreeset, val);
				
			}
			else 
				agreesets.put(agreeset,1);
		
		return agreesets;
		
	}
	
	

}
