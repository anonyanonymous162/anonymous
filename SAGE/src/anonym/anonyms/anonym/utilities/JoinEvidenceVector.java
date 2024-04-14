package anonym.anonyms.anonym.utilities;

import java.util.List;
import java.util.Map;

import org.apache.lucene.util.OpenBitSet;

import anonym.anonyms.anonym.algo.BaseTable;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinEvidenceVector {
	
	List<List<List<Integer>>> value_j;
	List<List<OpenBitSet>> list_possible_bitsets;
	List<Integer> num_attrs_list;
	Map<OpenBitSet,Integer> j_map;
	List<OpenBitSet> j_possible_bitsets;
	Map<List<Integer>,Integer> j_index_map;
	int j_size;
	
	public JoinEvidenceVector(List<TableEvidenceVector> table_evidence_vectors, List<List<OpenBitSet>> _list_possible_bitsets, List<Integer> _num_attrs_list){
		
		list_possible_bitsets = _list_possible_bitsets;
		num_attrs_list = _num_attrs_list;
		
		List<List<Integer>> value;
		

		j_index_map = new HashMap<List<Integer>,Integer>();
		
		TableEvidenceVector tev_0 = table_evidence_vectors.get(0);
		value = tev_0.value;
		
		int row_num = value.size();
		int col_num = value.get(0).size();
		
		List<Integer> cell;
		
		for (int row = 0; row < row_num; row++) {
			
			for (int col = 0; col < col_num; col++) {
				cell = new ArrayList<Integer>();
				for(TableEvidenceVector tev: table_evidence_vectors) {
					cell.add(tev.value.get(row).get(col));
				}				
				if (j_index_map.containsKey(cell))
					j_index_map.put(cell, j_index_map.get(cell)+1);
				else
					j_index_map.put(cell,1);
			}
			col_num--;

			
			
		}
		
		
		
		/*
		for (List<Integer> row: value) {
			row_j = new ArrayList<List<Integer>>();
			for(Integer col: row) {
				row_j.add(new ArrayList<Integer>(col));
			}
			value_j.add(row_j);
		}
		
		if (table_evidence_vectors.size()>1) {
			for (TableEvidenceVector tev: table_evidence_vectors) {
				value = tev.value;
				for (int i=0; i<value.size(); i++) {
					for(int j=0; j<value.get(i).size(); j++) {
						value_j.get(i).get(j).add(value.get(i).get(j));
					}
				}
			}
		}	
		*/	
		
	}
	
	/*
	public JoinEvidenceVector(List<TableEvidenceVector> table_evidence_vectors, List<List<OpenBitSet>> _list_possible_bitsets, List<Integer> _num_attrs_list){
		value_j = new ArrayList<List<List<Integer>>>();
		
		list_possible_bitsets = _list_possible_bitsets;
		num_attrs_list = _num_attrs_list;
		
		List<List<Integer>> value;
		

		
		TableEvidenceVector tev_0 = table_evidence_vectors.get(0);
		value = tev_0.value;
		
		int row_num = value.size();
		int col_num = value.get(0).size();
		
		List<List<Integer>> row_j;
		List<Integer> cell;
		
		for (int row = 0; row < row_num; row++) {
			row_j = new ArrayList<List<Integer>>();
			for (int col = 0; col < col_num; col++) {
				cell = new ArrayList<Integer>();
				for(TableEvidenceVector tev: table_evidence_vectors) {
					cell.add(tev.value.get(row).get(col));
				}
				row_j.add(cell);
			}
			col_num--;
			value_j.add(row_j);
		}
		
		
		
		/*
		for (List<Integer> row: value) {
			row_j = new ArrayList<List<Integer>>();
			for(Integer col: row) {
				row_j.add(new ArrayList<Integer>(col));
			}
			value_j.add(row_j);
		}
		
		if (table_evidence_vectors.size()>1) {
			for (TableEvidenceVector tev: table_evidence_vectors) {
				value = tev.value;
				for (int i=0; i<value.size(); i++) {
					for(int j=0; j<value.get(i).size(); j++) {
						value_j.get(i).get(j).add(value.get(i).get(j));
					}
				}
			}
		}	
		*/	
		
	//}

	
	/*
	public  JoinEvidenceVector_old(List<TableEvidenceVector> table_evidence_vectors, List<List<OpenBitSet>> _list_possible_bitsets, List<Integer> _num_attrs_list){
		value_j = new ArrayList<List<List<Integer>>>();
		
		list_possible_bitsets = _list_possible_bitsets;
		num_attrs_list = _num_attrs_list;
		
		List<List<Integer>> value;
		
		if (table_evidence_vectors.size() == 0) {
			System.out.println("There seem to be no tables to join");
		}
		
		TableEvidenceVector tev_0 = table_evidence_vectors.get(0);
		value = tev_0.value;
		
		List<List<Integer>> row_j;
		
		for (List<Integer> row: value) {
			row_j = new ArrayList<List<Integer>>();
			for(Integer col: row) {
				row_j.add(new ArrayList<Integer>(col));
			}
			value_j.add(row_j);
		}
		
		if (table_evidence_vectors.size()>1) {
			for (TableEvidenceVector tev: table_evidence_vectors) {
				value = tev.value;
				for (int i=0; i<value.size(); i++) {
					for(int j=0; j<value.get(i).size(); j++) {
						value_j.get(i).get(j).add(value.get(i).get(j));
					}
				}
			}
		}		
		
	}
	*/
	
	public void print() {
		System.out.println("Join evidence vector");
		int r =0;
		for (List<List<Integer>> row_j: this.value_j) {
			System.out.println("Row "+r);
			for (List<Integer> col_j: row_j)
				System.out.print(col_j.toString()+"	");
			System.out.println();
			r++;
		}
	}
	

	
	public List<List<List<Integer>>> getValue(){
		return this.value_j;
	}
	

	
	public void print_bitsets() {
		
		
		for (int i =0; i<this.value_j.size(); i++) {
			System.out.println("row "+i);
			for (int j = 0; j< this.value_j.get(i).size(); j++) {
				System.out.println();
				for (int k =0; k < this.value_j.get(i).get(j).size(); k++)
					System.out.println("column "+j+": "+BitSetUtils.toString(list_possible_bitsets.get(k).get(this.value_j.get(i).get(j).get(k)),num_attrs_list.get(k)));
			}
			}
		}
	
	public  Map<OpenBitSet,Integer> collect(){
		
		j_size = 0;
		for (int s: num_attrs_list)
			j_size += s;
		
		j_map = new HashMap<OpenBitSet,Integer>();
		j_possible_bitsets = new ArrayList<OpenBitSet>();
		int curr = 0;
		
		OpenBitSet j_bs;
		OpenBitSet bs;
		List<Integer> key;
		int pos;
		
		//System.out.println("Concatenating bitsets");
		
		for (Map.Entry<List<Integer>, Integer> entry : j_index_map.entrySet()) {
		
			
			j_bs= new OpenBitSet(j_size);
			int offset = 0;
			
			for (int i=0; i < entry.getKey().size(); i++) {
				bs = list_possible_bitsets.get(i).get(entry.getKey().get(i));
				
				//System.out.println(BitSetUtils.toString(j_bs,j_size)+"  ");
				
				pos = bs.nextSetBit(0);
				//System.out.println(BitSetUtils.toString(bs,this.num_attrs_list.get(i)));
				
				while(pos != -1) {
					j_bs.set(offset+pos);
					pos = bs.nextSetBit(pos+1);
					
				}
				offset += num_attrs_list.get(i);
				
			}
			
			//System.out.println(" -> "+BitSetUtils.toString(j_bs,this.j_size));
			
			j_possible_bitsets.add(j_bs);
			j_map.put(j_bs, entry.getValue());
			curr++;
			
		}
		
		
		return j_map;
		
	}
	
	public  Map<OpenBitSet,Integer> collect_old(){
		
		
		Map<List<Integer>,Integer> map = new HashMap<List<Integer>,Integer>();
		
		for (List<List<Integer>> row: this.value_j) {
				for (List<Integer> col: row) {
					if (map.containsKey(col))
						map.put(col, map.get(col)+1);
					else
						map.put(col, 1);
				}
		}
		
		j_size = 0;
		for (int s: num_attrs_list)
			j_size += s;
		
		j_map = new HashMap<OpenBitSet,Integer>();
		j_possible_bitsets = new ArrayList<OpenBitSet>();
		int curr = 0;
		
		OpenBitSet j_bs;
		OpenBitSet bs;
		List<Integer> key;
		int pos;
		
		//System.out.println("Concatenating bitsets");
		
		for (Map.Entry<List<Integer>, Integer> entry : map.entrySet()) {
		
			
			j_bs= new OpenBitSet(j_size);
			int offset = 0;
			
			for (int i=0; i < entry.getKey().size(); i++) {
				bs = list_possible_bitsets.get(i).get(entry.getKey().get(i));
				
				//System.out.println(BitSetUtils.toString(j_bs,j_size)+"  ");
				
				pos = bs.nextSetBit(0);
				//System.out.println(BitSetUtils.toString(bs,this.num_attrs_list.get(i)));
				
				while(pos != -1) {
					j_bs.set(offset+pos);
					pos = bs.nextSetBit(pos+1);
					
				}
				offset += num_attrs_list.get(i);
				
			}
			
			//System.out.println(" -> "+BitSetUtils.toString(j_bs,this.j_size));
			
			j_possible_bitsets.add(j_bs);
			j_map.put(j_bs, entry.getValue());
			curr++;
			
		}
		
		
		return j_map;
	}
	
	public void print_map_bitsets() {
		//System.out.println("j_size: "+j_size);
		//System.out.println("num_attrs_list: "+this.num_attrs_list.toString());
		
		System.out.println("Join bitsets");
		for (Map.Entry<OpenBitSet, Integer> entry : j_map.entrySet()) {
			System.out.println(BitSetUtils.toString(entry.getKey(),j_size)+": "+entry.getValue());
		}
		
	}


}
