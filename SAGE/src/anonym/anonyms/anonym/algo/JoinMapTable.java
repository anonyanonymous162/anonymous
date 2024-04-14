package anonym.anonyms.anonym.algo;

import anonym.anonyms.anonym.utilities.BitSetUtils;
import anonym.anonyms.anonym.utilities.JoinEvidenceVector;
import anonym.anonyms.anonym.utilities.RelationEvidenceVector;
import anonym.anonyms.anonym.utilities.TableEvidenceVector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.util.OpenBitSet;

import au.com.bytecode.opencsv.CSVReader;

public class JoinMapTable {
	
	JoinEvidenceVector jev;
	List<BaseTable> base_tables;
	List<List<Integer>> witness_lists;
	List<Integer> num_attrs_list;
	String result_file;
	
	List<List<Integer>> removed_tuples;
	
	List<List<OpenBitSet>> list_possible_bitsets;
	

	
	List<TableEvidenceVector> select_table_vectors;
	
	
public void run_variant_test() {
		
		
		long startTime,endTime;
		
		// Generate the table evidence vector for each base relation
		for (BaseTable bt: this.base_tables) {
			startTime= System.nanoTime();
			bt.initializeEvidenceSets();
			bt.generateRelationEvidenceVector();
			endTime = System.nanoTime();
			System.out.println("Time for generating Table Evidence Vector: "+(endTime - startTime)/1000000+"ms");
			//TableEvidenceVector.print(bt.rev,  bt.numAtts, bt.possible_bitsets);
		}
		
		
		removed_tuples = new ArrayList<List<Integer>>();
		List<Integer> temp;
		for (int j = 0; j <this.witness_lists.size(); j++) {
			temp = new ArrayList<Integer>();
			for (int i=0; i< this.base_tables.get(j).numTuples; i++)
				if (!this.witness_lists.get(j).contains(i))
					temp.add(i);
			removed_tuples.add(temp);
		}
		
				
		
		
		
		
		//this.base_tables.get(0).print_table_evidence_vector();
		startTime= System.nanoTime();
		System.out.println("Time to remove uncessary cells in the key-based selection");
		List<List<Integer>> val = this.base_tables.get(0).rev.getValue();
		List<Integer> indices = this.removed_tuples.get(0);
		for (int i=this.base_tables.get(0).numTuples-1;i>=0; i--) {
			//System.out.println("i: "+i);
			
			if (indices.contains(i)) {
				//System.out.println("Remove row "+i);
				val.remove(i);
			}
				else {
					//System.out.println("indices.size(): "+indices.size());
					for (int j =indices.get(indices.size()-1) ; j>i; j--) {
						//System.out.println("i: "+i+", j: "+j);
						val.get(i).remove(j-i-1);
					}
				}
		}
		endTime = System.nanoTime();
		System.out.println("Time for removing unnecessary cells: "+(endTime - startTime)/1000000+"ms");
		
			
		//this.base_tables.get(0).print_table_evidence_vector();
		startTime= System.nanoTime();
		System.out.println("Time to remove uncessary cells in the key-based selection");
		List<Map<Integer,Integer>> val_map = new ArrayList<Map<Integer,Integer>>();
		
				
				this.base_tables.get(0).rev.getValue();
		List<Integer> indices = this.removed_tuples.get(0);
		for (int i=this.base_tables.get(0).numTuples-1;i>=0; i--) {
			//System.out.println("i: "+i);
			
			if (indices.contains(i)) {
				//System.out.println("Remove row "+i);
				val.remove(i);
			}
				else {
					//System.out.println("indices.size(): "+indices.size());
					for (int j =indices.get(indices.size()-1) ; j>i; j--) {
						//System.out.println("i: "+i+", j: "+j);
						val.get(i).remove(j-i-1);
					}
				}
		}
		endTime = System.nanoTime();
		System.out.println("Time for removing unnecessary cells: "+(endTime - startTime)/1000000+"ms");
		
			
		
		//this.base_tables.get(0).print_table_evidence_vector();
		
		
	/**
		int res_size = this.witness_lists.get(0).size();
		
		
		
		//List<Integer> empty_index_bs = new ArrayList<Integer>();
		//for (int i=0; i<this.list_possible_bitsets.size();i++)
		//	empty_index_bs.add(0);
		
		startTime= System.nanoTime();

		Map<List<Integer>, Integer> j_index = new HashMap<List<Integer>, Integer>();
		List<Integer> key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size
		int i1, i2, it1, it2;

		for (int i = 0; i < res_size; i++) {
		    for (int j = i + 1; j < res_size; j++) {
		        for (int k = 0; k < this.witness_lists.size(); k++) {
		            it1 = this.witness_lists.get(k).get(i);
		            it2 = this.witness_lists.get(k).get(j);
		            i1 = Math.min(it1, it2);
		            i2 = Math.max(it1, it2);
		            if (i1 == i2) {
		                key.set(k, -1); // reuse the key list object by updating its values
		            } else {
		                key.set(k, this.base_tables.get(k).rev.getValue().get(i1).get(i2-i1-1)); // reuse the key list object by updating its values
		            }
		        }

		        if (j_index.containsKey(key)) {
		            j_index.compute(key, (k, v) -> v + 1);
		        } else {
		            j_index.put(new ArrayList<>(key), 1); // create a new ArrayList to avoid modifying the key
		        }
		    }
		}

		endTime = System.nanoTime();
		System.out.println("Time for generating join index: "+(endTime - startTime)/1000000+"ms");
		
		
		startTime= System.nanoTime();
		j_index = new HashMap<List<Integer>, Integer>();

		key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size


		for (int i = 0; i < res_size; i++) {
		    for (int j = i + 1; j < res_size; j++) {
		        for (int k = 0; k < this.witness_lists.size(); k++) {
		            it1 = this.witness_lists.get(k).get(i);
		            it2 = this.witness_lists.get(k).get(j);
		            i1 = Math.min(it1, it2);
		            i2 = Math.max(it1, it2);
		            if (i1 == i2) {
		                key.set(k, -1); // reuse the key list object by updating its values
		            } else {
		                key.set(k, this.base_tables.get(k).rev.getValue().get(i1).get(i2-i1-1)); // reuse the key list object by updating its values
		            }
		        }

		        if (!j_index.containsKey(key)) {
		            j_index.put(new ArrayList<>(key), 1); // create a new ArrayList to avoid modifying the key
		        }
		    }
		}
		endTime = System.nanoTime();
		System.out.println("Time for generating join index without cardinalities: "+(endTime - startTime)/1000000+"ms");
		
		for (BaseTable bt: this.base_tables) {
			list_possible_bitsets.add(bt.possible_bitsets);
		}
		
		/*
		System.out.println("Possible bitsets");
		for (int i=0; i<list_possible_bitsets.size();i++) {
			System.out.println("Table "+i);
			for (int j=0;j<list_possible_bitsets.get(i).size();j++)
				System.out.println(BitSetUtils.toString(list_possible_bitsets.get(i).get(j), this.num_attrs_list.get(i)));
		}
		*/
		
	   //print the j_index map
		/*
		System.out.println("Printing index results");
		for (Map.Entry<List<Integer>, Integer> entry : j_index.entrySet()) {
			System.out.println(entry.getKey().toString()+": "+entry.getValue());
			
		}
		*/
		
		/**
		//print collect the join results.
		List<Integer> val;
		int size=0;
		for (Integer attrs: this.num_attrs_list)
			size += attrs;
		OpenBitSet j_bs;
		int pos;
		int offset=0;
		OpenBitSet bs;
		
		Map<OpenBitSet,Integer> j_map = new HashMap<OpenBitSet,Integer>();
		OpenBitSet t_bs;

		List<OpenBitSet> set_open_bitsets = new ArrayList<OpenBitSet>();
		for (int i=0; i< this.num_attrs_list.size();i++) {
			t_bs = new OpenBitSet(this.num_attrs_list.get(i));
			t_bs.set(0,this.num_attrs_list.get(i));
			set_open_bitsets.add(t_bs);
		}
		
		
		int size_i;
		
		
		System.out.println("Printing resulting bitsets");
		for (Map.Entry<List<Integer>, Integer> entry : j_index.entrySet()) {
			val = entry.getKey();
			j_bs = new OpenBitSet(size);
			offset = 0;
			
			for (int i=0; i< val.size();i++) {
				
				
				
				size_i = this.num_attrs_list.get(i);
				
				
				
				if (val.get(i)==-1) {
					bs = set_open_bitsets.get(i);
				}
				else {
					bs = this.list_possible_bitsets.get(i).get(val.get(i));
				}
				
				//System.out.print(i+" "+BitSetUtils.toString(bs,this.num_attrs_list.get(i))+"   ");
				
				pos = bs.nextSetBit(0);
				while(pos !=-1) {
					j_bs.fastSet(offset+pos);
					pos = bs.nextSetBit(pos+1);
				}
				offset = offset + this.num_attrs_list.get(i);
			}
			
				j_map.put(j_bs,entry.getValue());
			//System.out.println("\n j_bs "+BitSetUtils.toString(j_bs,size));
							
		}
		
		
		//Print j_map
		System.out.println("Printing join results");
		for (Map.Entry<OpenBitSet, Integer> entry : j_map.entrySet()) {
			System.out.println(BitSetUtils.toString(entry.getKey(),size)+": "+entry.getValue());
			
		}
		
		System.out.println("Directly computing the agree-sets from the result file");
		startTime = System.nanoTime();
		BaseTable res_table = new BaseTable(result_file);
		res_table.initializeEvidenceSets();
		res_table.generateRelationEvidenceVector();
		TableEvidenceVector.collect(res_table.rev.getValue(), res_table.possible_bitsets);
		endTime = System.nanoTime();
		System.out.println("Time for generating relation evidence vector directly from the result file: "+(endTime - startTime)/1000000+"ms");

		
		**/
	}
	
	
	public void run() {
		
		long startTime,endTime;
		
		// Generate the table evidence vector for each base relation
		for (BaseTable bt: this.base_tables) {
			startTime= System.nanoTime();
			bt.initializeEvidenceSets();
			bt.generateRelationEvidenceVector();
			endTime = System.nanoTime();
			System.out.println("Time for generating Table Evidence Vector: "+(endTime - startTime)/1000000+"ms");
			//TableEvidenceVector.print(bt.rev,  bt.numAtts, bt.possible_bitsets);
		}
		
		// Generate the selections necessary for each base table given the join
		
		for (int i=0; i< this.base_tables.size(); i++) {
			startTime= System.nanoTime();
			//System.out.println("witness list: "+this.witness_lists.get(i).toString());
			select_table_vectors.add(this.base_tables.get(i).select_for_join(this.witness_lists.get(i)));
			endTime = System.nanoTime();
			System.out.println("Time for generating the selection Relation Evidence Vector: "+(endTime - startTime)/1000000+"ms");
			//TableEvidenceVector.print(select_table_vectors.get(i).getValue(), num_attrs_list.get(i), this.base_tables.get(i).possible_bitsets);
		}
		
		for (BaseTable bt: this.base_tables) {
			list_possible_bitsets.add(bt.possible_bitsets);
		}
		
		
		System.out.println("Possible bitsets");
		for (int i=0; i<list_possible_bitsets.size();i++) {
			System.out.println("Table "+i);
			for (int j=0;j<list_possible_bitsets.get(i).size();i++)
				System.out.println(BitSetUtils.toString(list_possible_bitsets.get(i).get(j), this.num_attrs_list.get(i)));
		}
		
		//Construct the Join evidence vector
		startTime= System.nanoTime();
		jev = new JoinEvidenceVector(this.select_table_vectors,list_possible_bitsets,num_attrs_list);
		endTime = System.nanoTime();
		System.out.println("Time for generating join evidence vector: "+(endTime - startTime)/1000000+"ms");

		startTime= System.nanoTime();
		BaseTable res_table = new BaseTable(result_file);
		res_table.initializeEvidenceSets();
		res_table.generateRelationEvidenceVector();
		endTime = System.nanoTime();
		System.out.println("Time for generating result evidence vector: "+(endTime - startTime)/1000000+"ms");
		
		
		/*
		System.out.println("Base Tables");
		for (BaseTable bt: this.base_tables)
			bt.print_table_evidence_vector();

		
		jev.print();
		
		System.out.println("Print bitsets of the join");
		jev.print_bitsets();
		*/
		
		startTime= System.nanoTime();
		jev.collect();
		endTime = System.nanoTime();
		System.out.println("Time for collecting join evidence vector: "+(endTime - startTime)/1000000+"ms");
		
		
		jev.print_map_bitsets();
		
		
		
	}
	
	
public void run_variant_old_old() {
		
		
		long startTime,endTime;
		
		// Generate the table evidence vector for each base relation
		for (BaseTable bt: this.base_tables) {
			startTime= System.nanoTime();
			bt.initializeEvidenceSets();
			bt.generateRelationEvidenceVector();
			endTime = System.nanoTime();
			System.out.println("Time for generating Table Evidence Vector: "+(endTime - startTime)/1000000+"ms");
			//TableEvidenceVector.print(bt.rev,  bt.numAtts, bt.possible_bitsets);
		}
		
		
		int res_size = this.witness_lists.get(0).size();
		//List<Integer> empty_index_bs = new ArrayList<Integer>();
		//for (int i=0; i<this.list_possible_bitsets.size();i++)
		//	empty_index_bs.add(0);
		
		startTime= System.nanoTime();
		/*
		Map<List<Integer>, Integer> j_index = new HashMap<List<Integer>, Integer>();
		List<Integer> key;
		int i1, i2, it1, it2;
		
		for (int i=0; i<res_size; i++) {
			for (int j=i+1; j<res_size; j++) {
				key = new ArrayList<Integer>();
				for (int k=0;k<this.witness_lists.size();k++) {
					it1 = this.witness_lists.get(k).get(i);
					it2 = this.witness_lists.get(k).get(j);
					i1 = Math.min(it1,it2);
					i2 = Math.max(it1,it2);
					if (i1==i2)
						key.add(-1);
					else
						key.add(this.base_tables.get(k).rev.getValue().get(i1).get(i2-i1-1));
				}
				
				if (j_index.containsKey(key)) {
				    j_index.compute(key, (k, v) -> v + 1);
				} else {
				    j_index.put(key, 1);
				}

			}
		}
		*/
		Map<List<Integer>, Integer> j_index = new HashMap<List<Integer>, Integer>();
		List<Integer> key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size
		int i1, i2, it1, it2;

		for (int i = 0; i < res_size; i++) {
		    for (int j = i + 1; j < res_size; j++) {
		        for (int k = 0; k < this.witness_lists.size(); k++) {
		            it1 = this.witness_lists.get(k).get(i);
		            it2 = this.witness_lists.get(k).get(j);
		            i1 = Math.min(it1, it2);
		            i2 = Math.max(it1, it2);
		            if (i1 == i2) {
		                key.set(k, -1); // reuse the key list object by updating its values
		            } else {
		                key.set(k, this.base_tables.get(k).rev.getValue().get(i1).get(i2-i1-1)); // reuse the key list object by updating its values
		            }
		        }

		        if (j_index.containsKey(key)) {
		            j_index.compute(key, (k, v) -> v + 1);
		        } else {
		            j_index.put(new ArrayList<>(key), 1); // create a new ArrayList to avoid modifying the key
		        }
		    }
		}

		endTime = System.nanoTime();
		System.out.println("Time for generating join index: "+(endTime - startTime)/1000000+"ms");
		
		
		startTime= System.nanoTime();
		j_index = new HashMap<List<Integer>, Integer>();

		key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size


		for (int i = 0; i < res_size; i++) {
		    for (int j = i + 1; j < res_size; j++) {
		        for (int k = 0; k < this.witness_lists.size(); k++) {
		            i1 = this.witness_lists.get(k).get(i);
		            i2 = this.witness_lists.get(k).get(j);
		            key.set(k, this.base_tables.get(k).rev.getValue().get(i1).get(i2-i1-1)); // reuse the key list object by updating its values

		        }

		        if (!j_index.containsKey(key)) {
		            j_index.put(new ArrayList<>(key), 1); // create a new ArrayList to avoid modifying the key
		        }
		    }
		}
		endTime = System.nanoTime();
		System.out.println("Time for generating join index without cardinalities: "+(endTime - startTime)/1000000+"ms");
		
		for (BaseTable bt: this.base_tables) {
			list_possible_bitsets.add(bt.possible_bitsets);
		}
		
		/*
		System.out.println("Possible bitsets");
		for (int i=0; i<list_possible_bitsets.size();i++) {
			System.out.println("Table "+i);
			for (int j=0;j<list_possible_bitsets.get(i).size();j++)
				System.out.println(BitSetUtils.toString(list_possible_bitsets.get(i).get(j), this.num_attrs_list.get(i)));
		}
		*/
		
	   //print the j_index map
		/*
		System.out.println("Printing index results");
		for (Map.Entry<List<Integer>, Integer> entry : j_index.entrySet()) {
			System.out.println(entry.getKey().toString()+": "+entry.getValue());
			
		}
		*/
		
		//print collect the join results.
		List<Integer> val;
		int size=0;
		for (Integer attrs: this.num_attrs_list)
			size += attrs;
		OpenBitSet j_bs;
		int pos;
		int offset=0;
		OpenBitSet bs;
		
		Map<OpenBitSet,Integer> j_map = new HashMap<OpenBitSet,Integer>();
		OpenBitSet t_bs;

		List<OpenBitSet> set_open_bitsets = new ArrayList<OpenBitSet>();
		for (int i=0; i< this.num_attrs_list.size();i++) {
			t_bs = new OpenBitSet(this.num_attrs_list.get(i));
			t_bs.set(0,this.num_attrs_list.get(i));
			set_open_bitsets.add(t_bs);
		}
		
		
		int size_i;
		
		
		System.out.println("Printing resulting bitsets");
		for (Map.Entry<List<Integer>, Integer> entry : j_index.entrySet()) {
			val = entry.getKey();
			j_bs = new OpenBitSet(size);
			offset = 0;
			
			for (int i=0; i< val.size();i++) {
				
				
				
				size_i = this.num_attrs_list.get(i);
				
				
				
				if (val.get(i)==-1) {
					bs = set_open_bitsets.get(i);
				}
				else {
					bs = this.list_possible_bitsets.get(i).get(val.get(i));
				}
				
				//System.out.print(i+" "+BitSetUtils.toString(bs,this.num_attrs_list.get(i))+"   ");
				
				pos = bs.nextSetBit(0);
				while(pos !=-1) {
					j_bs.fastSet(offset+pos);
					pos = bs.nextSetBit(pos+1);
				}
				offset = offset + this.num_attrs_list.get(i);
			}
			
				j_map.put(j_bs,entry.getValue());
			//System.out.println("\n j_bs "+BitSetUtils.toString(j_bs,size));
							
		}
		
		
		//Print j_map
		System.out.println("Printing join results");
		for (Map.Entry<OpenBitSet, Integer> entry : j_map.entrySet()) {
			System.out.println(BitSetUtils.toString(entry.getKey(),size)+": "+entry.getValue());
			
		}
		
		System.out.println("Directly computing the agree-sets from the result file");
		startTime = System.nanoTime();
		BaseTable res_table = new BaseTable(result_file);
		res_table.initializeEvidenceSets();
		res_table.generateRelationEvidenceVector();
		TableEvidenceVector.collect(res_table.rev.getValue(), res_table.possible_bitsets);
		endTime = System.nanoTime();
		System.out.println("Time for generating relation evidence vector directly from the result file: "+(endTime - startTime)/1000000+"ms");

		
		
	}
		
	
public void run_variant() {
		
		
		long startTime,endTime;
		
		// Generate the table evidence vector for each base relation
		for (BaseTable bt: this.base_tables) {
			startTime= System.nanoTime();
			bt.initializeEvidenceSets();
			bt.generateRelationEvidenceVector();
			endTime = System.nanoTime();
			System.out.println("Time for generating Table Evidence Vector: "+(endTime - startTime)/1000000+"ms");
			//TableEvidenceVector.print(bt.rev,  bt.numAtts, bt.possible_bitsets);
		}
		
		
		int res_size = this.witness_lists.get(0).size();
		//List<Integer> empty_index_bs = new ArrayList<Integer>();
		//for (int i=0; i<this.list_possible_bitsets.size();i++)
		//	empty_index_bs.add(0);
		
		startTime= System.nanoTime();

		Map<List<Integer>, Integer> j_index = new HashMap<List<Integer>, Integer>();
		List<Integer> key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size
		int i1, i2, it1, it2;

		for (int i = 0; i < res_size; i++) {
		    for (int j = i + 1; j < res_size; j++) {
		        for (int k = 0; k < this.witness_lists.size(); k++) {
		            it1 = this.witness_lists.get(k).get(i);
		            it2 = this.witness_lists.get(k).get(j);
		            i1 = Math.min(it1, it2);
		            i2 = Math.max(it1, it2);
		            if (i1 == i2) {
		                key.set(k, -1); // reuse the key list object by updating its values
		            } else {
		                key.set(k, this.base_tables.get(k).rev.getValue().get(i1).get(i2-i1-1)); // reuse the key list object by updating its values
		            }
		        }

		        if (j_index.containsKey(key)) {
		            j_index.compute(key, (k, v) -> v + 1);
		        } else {
		            j_index.put(new ArrayList<>(key), 1); // create a new ArrayList to avoid modifying the key
		        }
		    }
		}

		endTime = System.nanoTime();
		System.out.println("Time for generating join index: "+(endTime - startTime)/1000000+"ms");
		
		
		startTime= System.nanoTime();
		j_index = new HashMap<List<Integer>, Integer>();

		key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size


		for (int i = 0; i < res_size; i++) {
		    for (int j = i + 1; j < res_size; j++) {
		        for (int k = 0; k < this.witness_lists.size(); k++) {
		            it1 = this.witness_lists.get(k).get(i);
		            it2 = this.witness_lists.get(k).get(j);
		            i1 = Math.min(it1, it2);
		            i2 = Math.max(it1, it2);
		            if (i1 == i2) {
		                key.set(k, -1); // reuse the key list object by updating its values
		            } else {
		                key.set(k, this.base_tables.get(k).rev.getValue().get(i1).get(i2-i1-1)); // reuse the key list object by updating its values
		            }
		        }

		        if (!j_index.containsKey(key)) {
		            j_index.put(new ArrayList<>(key), 1); // create a new ArrayList to avoid modifying the key
		        }
		    }
		}
		endTime = System.nanoTime();
		System.out.println("Time for generating join index without cardinalities: "+(endTime - startTime)/1000000+"ms");
		
		for (BaseTable bt: this.base_tables) {
			list_possible_bitsets.add(bt.possible_bitsets);
		}
		
		/*
		System.out.println("Possible bitsets");
		for (int i=0; i<list_possible_bitsets.size();i++) {
			System.out.println("Table "+i);
			for (int j=0;j<list_possible_bitsets.get(i).size();j++)
				System.out.println(BitSetUtils.toString(list_possible_bitsets.get(i).get(j), this.num_attrs_list.get(i)));
		}
		*/
		
	   //print the j_index map
		/*
		System.out.println("Printing index results");
		for (Map.Entry<List<Integer>, Integer> entry : j_index.entrySet()) {
			System.out.println(entry.getKey().toString()+": "+entry.getValue());
			
		}
		*/
		
		//print collect the join results.
		List<Integer> val;
		int size=0;
		for (Integer attrs: this.num_attrs_list)
			size += attrs;
		OpenBitSet j_bs;
		int pos;
		int offset=0;
		OpenBitSet bs;
		
		Map<OpenBitSet,Integer> j_map = new HashMap<OpenBitSet,Integer>();
		OpenBitSet t_bs;

		List<OpenBitSet> set_open_bitsets = new ArrayList<OpenBitSet>();
		for (int i=0; i< this.num_attrs_list.size();i++) {
			t_bs = new OpenBitSet(this.num_attrs_list.get(i));
			t_bs.set(0,this.num_attrs_list.get(i));
			set_open_bitsets.add(t_bs);
		}
		
		
		int size_i;
		
		
		System.out.println("Printing resulting bitsets");
		for (Map.Entry<List<Integer>, Integer> entry : j_index.entrySet()) {
			val = entry.getKey();
			j_bs = new OpenBitSet(size);
			offset = 0;
			
			for (int i=0; i< val.size();i++) {
				
				
				
				size_i = this.num_attrs_list.get(i);
				
				
				
				if (val.get(i)==-1) {
					bs = set_open_bitsets.get(i);
				}
				else {
					bs = this.list_possible_bitsets.get(i).get(val.get(i));
				}
				
				//System.out.print(i+" "+BitSetUtils.toString(bs,this.num_attrs_list.get(i))+"   ");
				
				pos = bs.nextSetBit(0);
				while(pos !=-1) {
					j_bs.fastSet(offset+pos);
					pos = bs.nextSetBit(pos+1);
				}
				offset = offset + this.num_attrs_list.get(i);
			}
			
				j_map.put(j_bs,entry.getValue());
			//System.out.println("\n j_bs "+BitSetUtils.toString(j_bs,size));
							
		}
		
		
		//Print j_map
		System.out.println("Printing join results");
		for (Map.Entry<OpenBitSet, Integer> entry : j_map.entrySet()) {
			System.out.println(BitSetUtils.toString(entry.getKey(),size)+": "+entry.getValue());
			
		}
		
		System.out.println("Directly computing the agree-sets from the result file");
		startTime = System.nanoTime();
		BaseTable res_table = new BaseTable(result_file);
		res_table.initializeEvidenceSets();
		res_table.generateRelationEvidenceVector();
		TableEvidenceVector.collect(res_table.rev.getValue(), res_table.possible_bitsets);
		endTime = System.nanoTime();
		System.out.println("Time for generating relation evidence vector directly from the result file: "+(endTime - startTime)/1000000+"ms");

		
		
	}
	
	

	
	public JoinMapTable(List<String> files, List<String> witness_files, List _num_attrs_list, String result_file) {
		
		base_tables = new ArrayList<BaseTable>();
		
		list_possible_bitsets = new ArrayList<List<OpenBitSet>>();
		
		for (String f: files) {
			base_tables.add(new BaseTable(f));
		}
		
		witness_lists = new ArrayList<List<Integer>>();
		
		for (String f: witness_files) {
			witness_lists.add(this.getWitnessList(f));
		}
		
		this.num_attrs_list =_num_attrs_list;
		
		select_table_vectors = new ArrayList<TableEvidenceVector>();
		
		this.result_file = result_file;
		
	}
	
	
	public static List<Integer> getWitnessList(String witness_file) {
		
		CSVReader csvReader;
		List<Integer> list = new ArrayList<Integer>();
		String[] values = null;	
		

			
			try {
				list = new ArrayList<Integer>();
				csvReader = new CSVReader(new FileReader(witness_file));
			
				while ((values = csvReader.readNext()) != null) {
					list.add(new Integer(values[0]));
				}

			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	
	return list;
		
		
	}
	
	
	
	
	
	public static List<List<OpenBitSet>> Run_old(RelationEvidenceVector rev, List<Integer> witness_list) {
		
		List<List<OpenBitSet>> result_evidence_vector = new ArrayList<List<OpenBitSet>>();
		
		for (int item: witness_list) {
			
			result_evidence_vector.add(rev.getValue().get(item));
			
		}
		
		return result_evidence_vector;
		
	}
	
	public static List<List<OpenBitSet>> Run_clone(RelationEvidenceVector rev, List<Integer> witness_list) {
		
		List<List<OpenBitSet>> result_evidence_vector = new ArrayList<List<OpenBitSet>>();
		List<OpenBitSet> list, list_clone;
		
		for (int item: witness_list) {
			list_clone = new ArrayList<OpenBitSet>();
			list = rev.getValue().get(item);
			
			for (OpenBitSet element : list) {
				  list_clone.add(element.clone());
				}
			
			result_evidence_vector.add(list_clone);
			
		}
		
		return result_evidence_vector;
		
	}

	public static void main(String[] args) {
		
		long startTime, endTime;
		
		/*
		String relation_example = "resources/datasets/example/example.csv";
		String witness_example = "resources/datasets/example/q_e_1.csv";
		String result_example = "results/example.csv";
		String query_results = "resources/datasets/example/results.csv";
		*/
		
		/*
		String relation_1 = "resources/PTE/single_tables/pte_active_num.csv";
		String witness_1 = "resources/PTE/queries/q_active_drug/w.csv";
		//String result_example = "results/flight.csv";
		//String query_results = "resources/datasets/Flight/q_f_2_results.csv";
		String query_results_wh = "resources/PTE/queries/q_active_drug/results_num.csv";
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/datasets/example/example_j_1.csv");
		files.add("resources/datasets/example/example_j_2.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/datasets/example/w_j_1.csv");
		witness_files.add("resources/datasets/example/w_j_2.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(4);
		num_attrs_list.add(3);
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/datasets/example/example_j_3.csv");
		files.add("resources/datasets/example/example_j_4.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/datasets/example/w_j_3.csv");
		witness_files.add("resources/datasets/example/w_j_4.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(4);
		num_attrs_list.add(3);
		*/
	
		
		List<String> files = new ArrayList<String>();
		files.add("resources/PTC/queries/q_atom_molecule/atom_num.csv");
		files.add("resources/PTC/queries/q_atom_molecule/molecule_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/PTC/queries/q_atom_molecule/w_atom.csv");
		witness_files.add("resources/PTC/queries/q_atom_molecule/w_molecule.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(3);
		num_attrs_list.add(2);
		
		
		/*
		
		List<String> files = new ArrayList<String>();
		files.add("resources/PTC/queries/q_connected_bond/connected_num.csv");
		files.add("resources/PTC/queries/q_connected_bond/bond_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/PTC/queries/q_connected_bond/w_connected.csv");
		witness_files.add("resources/PTC/queries/q_connected_bond/w_bond.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(3);
		num_attrs_list.add(3);
		*/
		
		
		String result_file = "resources/PTC/queries/q_atom_molecule/results_num.csv";
		
		
		JoinMapTable jt = new JoinMapTable(files,witness_files, num_attrs_list,result_file);
		
		//jt.run();
		
		jt.run_variant_test();
		
		
		
		/*
		Baserelation_AS_byRef br1 = new Baserelation_AS_byRef(relation_1, null);
		
		startTime= System.nanoTime();
		br1.initializeEvidenceSets();
		endTime = System.nanoTime();
		System.out.println("Time for constructing attribute value evidence vectors: "+(endTime - startTime)/1000000+"ms");

		startTime= System.nanoTime();
		br1.generateRelationEvidenceVector();
		endTime = System.nanoTime();
		System.out.println("Time for generating Relation Evidence Vector: "+(endTime - startTime)/1000000+"ms");
		
		//RelationEvidenceVector.print(br.rev,  br.numAtts);
		
		startTime= System.nanoTime();
		List<Integer> witness_list = JoinTable.getWitnessList(witness_1);
		endTime = System.nanoTime();
		System.out.println("Getting witness list: "+(endTime - startTime)/1000000+"ms");

		
		startTime= System.nanoTime();
		List<List<OpenBitSet>> q_r_v_c = JoinTable.Run_clone(br1.rev, witness_list);
		endTime = System.nanoTime();
		System.out.println("Generating the Query (clone) Result Evidence Vector: "+(endTime - startTime)/1000000+"ms");

		
		startTime= System.nanoTime();
		List<List<OpenBitSet>> q_r_v = JoinTable.Run(br1.rev, witness_list);
		endTime = System.nanoTime();
		System.out.println("Generating the Query Result Evidence Vector: "+(endTime - startTime)/1000000+"ms");
		
		
		Baserelation_AS_byRef br_q = new Baserelation_AS_byRef(query_results_wh, null);
		
		startTime= System.nanoTime();
		br_q.initializeEvidenceSets();
		endTime = System.nanoTime();
		System.out.println("Time for constructing attribute value evidence vectors for the query results directly: "+(endTime - startTime)/1000000+"ms");

		startTime= System.nanoTime();
		br_q.generateRelationEvidenceVector();
		endTime = System.nanoTime();
		System.out.println("Time for generating Relation Evidence Vector for the query results directly: "+(endTime - startTime)/1000000+"ms");
	
		*/
		
		//RelationEvidenceVector.print(q_r_v, br.numAtts);
		
		

	}

}
