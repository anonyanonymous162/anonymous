package anonym.anonyms.anonym.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.lucene.util.OpenBitSet;

import anonym.anonyms.anonym.utilities.BitSetUtils;

public class VariousSimpleTests {
	
	List<OpenBitSet> possible_bitsets;
	
	
	public void createPossibleBitest(int num_atts) {
		
		this.possible_bitsets = new ArrayList<OpenBitSet>();
		
		OpenBitSet bs = new OpenBitSet(num_atts);
		this.possible_bitsets.add(bs);
		for (int next_att = 0; next_att<num_atts; next_att++)
			this.createpbs(bs, next_att, num_atts);
		
	}
	
	public void createpbs(OpenBitSet current_bs, int curr_att, int num_atts) {
		
		OpenBitSet bs = current_bs.clone();
		bs.fastSet(curr_att);
		this.possible_bitsets.add(bs);
		
		if (curr_att < (num_atts - 1))
			for (int next_att = curr_att+1; next_att < num_atts; next_att++) {
				this.createpbs(bs, next_att, num_atts);
			}
		
	}
	
	public void displayPossibleBitSet(int num_atts) {
		for (OpenBitSet bs: this.possible_bitsets)
			System.out.println(BitSetUtils.toString(bs, num_atts));
	}
	
	public List<List<OpenBitSet>> createRelationEvidenceVector(int num, int num_atts) {

		
		this.createPossibleBitSet();
		
		Random random = new Random();
		
		OpenBitSet bs;
		
		List<List<OpenBitSet>> rev = new ArrayList<List<OpenBitSet>>();
		
		for (int i =0; i< num; i++) {
			
			bs = new OpenBitSet(num_atts);
			bs.fastSet(random.nextInt(3));
			
			List<OpenBitSet> list = new ArrayList<OpenBitSet>();
			for (int j =0; j<= i; j++) {
				int idx = this.possible_bitsets.indexOf(bs);
				list.add(this.possible_bitsets.get(idx));
			}
			System.out.println("Added list "+i);
			rev.add(list);
			
		}
		
		return rev;
		
	}
	
	public void createPossibleBitSet() {
		
		OpenBitSet bs0 = new OpenBitSet(3);
		OpenBitSet bs1 = new OpenBitSet(3);
		bs1.set(0);
		OpenBitSet bs2 = new OpenBitSet(3);
		bs2.set(1);
		OpenBitSet bs3 = new OpenBitSet(3);
		bs3.set(2);
		OpenBitSet bs4 = new OpenBitSet(3);
		bs4.set(0);
		bs4.set(1);
		OpenBitSet bs5 = new OpenBitSet(3);
		bs5.set(0);
		bs5.set(2);
		OpenBitSet bs6 = new OpenBitSet(3);
		bs6.set(1);
		bs6.set(2);
		OpenBitSet bs7 = new OpenBitSet(3);
		bs7.set(0);
		bs7.set(1);
		bs7.set(2);
		
		this.possible_bitsets = new ArrayList<OpenBitSet>();
		this.possible_bitsets.add(bs0);
		this.possible_bitsets.add(bs1);
		this.possible_bitsets.add(bs2);
		this.possible_bitsets.add(bs3);
		this.possible_bitsets.add(bs4);
		this.possible_bitsets.add(bs5);
		this.possible_bitsets.add(bs6);
		this.possible_bitsets.add(bs7);
		
	}

	public static void main(String[] args) {
		
		/*
		VariousSimpleTests test = new VariousSimpleTests();
		
		int relation_size = 25000;
		int num_atts = 3;
		
		test.createRelationEvidenceVector(relation_size, num_atts);
		System.out.println("Done");
		*/
		
		VariousSimpleTests test = new VariousSimpleTests();
		int num_atts = 4;
		test.createPossibleBitest(num_atts);
		test.displayPossibleBitSet(num_atts);
		

	}

}
