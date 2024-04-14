package anonym.anonyms.anonym.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.util.OpenBitSet;

public class TupleEvidenceVector_v1 {
	
	int num_atts;
	int num_tuples;
	ArrayList<OpenBitSet> value;
	
	
	public TupleEvidenceVector_v1(int num_atts, int num_tuples) {
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
	
	public static Map<OpenBitSet,Integer> collect(TupleEvidenceVector_v1 tev, int tuple_id, int num_tuples) {
		
		/* This method may be efficient when the number
		 * of set bits in bitsets is small
		 */
		
		ArrayList<OpenBitSet> bitsets = new ArrayList<OpenBitSet>();
		Map<OpenBitSet,Integer> agreesets = new HashMap<OpenBitSet,Integer>();
		OpenBitSet tmp;
		int k;
		int diff = tuple_id +1;

		//System.out.println("Tuple id: "+tuple_id);
		
		for (int i=tuple_id+1;  i<num_tuples ; i++)
			bitsets.add(new OpenBitSet(tev.num_atts));
		
		//System.out.println("Collect() for tuple "+tuple_id+", created "+(num_tuples-tuple_id-1));
		
		//System.out.println("Num attributes: "+tev.num_atts);
		for (int j=0; j<tev.num_atts; j++) {
			tmp = tev.value.get(j);
			//System.out.println("j: "+j);
			k = tmp.nextSetBit(tuple_id+1);
			//System.out.println("k: "+k);
			while (k >= 0) {
				bitsets.get(k-diff).set(j);
				k= tmp.nextSetBit(k+1);
				//System.out.println("New k: "+ k);
			}
		}
		
		//bitsets.remove(tuple_id);
		int val;
		for (OpenBitSet bitset: bitsets)
			if (agreesets.containsKey(bitset)) {
				val = agreesets.get(bitset);
				val++;
				agreesets.put(bitset, val);
			}
			else
				agreesets.put(bitset, 1);
		
		
		return agreesets;

	}
	

}
