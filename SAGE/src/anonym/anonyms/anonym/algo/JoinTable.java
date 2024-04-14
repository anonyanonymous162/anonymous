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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.util.OpenBitSet;

import au.com.bytecode.opencsv.CSVReader;

public class JoinTable {
	
	JoinEvidenceVector jev;
	List<BaseTable> base_tables;
	List<List<Integer>> witness_lists;
	List<Integer> num_attrs_list;
	String result_file;
	
	
	List<List<Integer>> removed_tuples;
	
	List<List<OpenBitSet>> list_possible_bitsets;
	
	//for sampling
	List<Integer> curr_bt_tuples;
	int curr_bt_tuples_position;
	int iteration;
	int sampling_support;
	int curr_witness_list = 0;
	

	
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
		
		/*	
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
		
		*/	
		
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
		
		/*
		System.out.println("Possible bitsets");
		for (int i=0; i<list_possible_bitsets.size();i++) {
			System.out.println("Table "+i);
			for (int j=0;j<list_possible_bitsets.get(i).size();j++)
				System.out.println(BitSetUtils.toString(list_possible_bitsets.get(i).get(j), this.num_attrs_list.get(i)));
		}
		*/
		
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
		System.out.println("Size of a sitness list: "+res_size);
		//List<Integer> empty_index_bs = new ArrayList<Integer>();
		//for (int i=0; i<this.list_possible_bitsets.size();i++)
		//	empty_index_bs.add(0);
		
		//System.out.println("Num tuples of table supplier"+this.base_tables.get(1).numTuples);
		
		
		startTime= System.nanoTime();

		Map<List<Integer>, Integer> j_index = new HashMap<List<Integer>, Integer>();
		List<Integer> key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size
		int i1, i2, it1, it2;

		//System.out.println("Results size: "+res_size);
		
		for (int i = 0; i < res_size; i++) {
		    for (int j = i + 1; j < res_size; j++) {
		    	//System.out.println("tuple "+i+" with tuple: "+ j);
		        for (int k = 0; k < this.witness_lists.size(); k++) {
		        	
		            it1 = this.witness_lists.get(k).get(i);
		            it2 = this.witness_lists.get(k).get(j);
		            if (it1<=it2) {
		            	i1 = it1;
		            	i2 = it2;
		            } else
		            {
		            	i1 = it2;
		            	i2 = it1;
		            }
		            	
		            //i1 = Math.min(it1, it2);
		            //i2 = Math.max(it1, it2);
		            if (i1 == i2) {
		                key.set(k, -1); // reuse the key list object by updating its values
		            } else {
		            	
		            	//System.out.println("Witness list: "+k);
		            	//System.out.println("row: "+i1+" and col: "+i2);
		            	//System.out.println("Number of tuples: "+this.base_tables.get(k).numTuples);
		            	
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
			
			    if (j_map.containsKey(j_bs))
			    	j_map.compute(j_bs,(k,v)-> v+1);
			    else	
			    	j_map.put(j_bs,entry.getValue());
			//System.out.println("\n j_bs "+BitSetUtils.toString(j_bs,size));
							
		}
		
		
		//Print j_map
		
		System.out.println("Printing join results");
		
		 j_map.entrySet().removeIf(entry -> entry.getValue() == 0);
		
		System.out.println("Number of agree-sets: "+j_map.entrySet().size());
		
		for (Map.Entry<OpenBitSet, Integer> entry : j_map.entrySet()) {
			//System.out.println(BitSetUtils.toString(entry.getKey(),size)+": "+entry.getValue());
			if (entry.getValue() != 0)
			 System.out.print(","+BitSetUtils.toString(entry.getKey(),size));
			
		}
		
		System.out.println("\n======");
		
		
		

		
		System.out.println("Directly computing the agree-sets from the result file");
		startTime = System.nanoTime();
		BaseTable res_table = new BaseTable(result_file);
		res_table.initializeEvidenceSets();
		res_table.generateRelationEvidenceVector();
		TableEvidenceVector.collect(res_table.rev.getValue(), res_table.possible_bitsets);
		endTime = System.nanoTime();
		System.out.println("Time for generating relation evidence vector directly from the result file: "+(endTime - startTime)/1000000+"ms");

		
		
	}
	
public void run_ASIndex_OLD() {
	
	
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
	
	//System.out.println("Num tuples of table supplier"+this.base_tables.get(1).numTuples);
	
	//Construct possible bitsets of the join
	int join_attrs = 0;
	int pos;
	List<OpenBitSet> join_bs = new ArrayList<OpenBitSet>();
	OpenBitSet j_bs;
	for (BaseTable bt : this.base_tables)
		join_attrs += bt.numAtts;
	
	for (OpenBitSet bs: this.base_tables.get(0).possible_bitsets) {
		j_bs = new OpenBitSet(join_attrs);
		pos = bs.nextSetBit(0);
		while (pos>=0) {
			j_bs.fastSet(pos);
			pos = bs.nextSetBit(pos+1);
		}
		
		join_bs.add(j_bs);
	}
	
	int off_set = 0;
	
	OpenBitSet n_bsj;
	List<OpenBitSet> n_join_bs;
	
	if (this.witness_lists.size()>1) {
		
		for (int i = 1;  i< this.witness_lists.size(); i++) {
			off_set += this.base_tables.get(i-1).numAtts;
			n_join_bs = new ArrayList<OpenBitSet>();
			for (OpenBitSet bsj: join_bs) {
				for (OpenBitSet bsi: this.base_tables.get(i).possible_bitsets) {
					n_bsj = bsj.clone();
					pos = bsi.nextSetBit(0);
					while (pos >= 0) {
						n_bsj.fastSet(pos+off_set);
						pos = bsi.nextSetBit(pos+1);
					}
					n_join_bs.add(n_bsj);
				}
			}
			join_bs = n_join_bs;
		}
	}
	/*
	// Bitsets of each base table
	for (BaseTable bt: this.base_tables) {
		System.out.println("---"+ bt.possible_bitsets.size()+" Possible bit sets with "+bt.numAtts+" attributes");
		for (OpenBitSet bs: bt.possible_bitsets) {
			System.out.println(BitSetUtils.toString(bs,bt.numAtts));
		}
	}
	*/
	
	/*
	//Print list of possible bitsets of the join
	System.out.println(join_bs.size()+" Possible bitsets of the join");
	for (OpenBitSet bs: join_bs)
		System.out.println(BitSetUtils.toString(bs,join_attrs));
	*/
	
	//State of ASIndices
	System.out.println("AS Indices");
	for (BaseTable bt: this.base_tables) {
		bt.asindex.printSize(bt.possible_bitsets,bt.numAtts);
	}
	

	

	boolean converge = false;
	int iteration = 0;
	int num_discovered_agree_sets = 0;
	Set<Integer> bt_tuples;
	BaseTable bt_i;
	Map<List<Integer>, Integer> j_index = new HashMap<List<Integer>, Integer>();
	
	while (!converge) {
		
		System.out.println("Starting iteration "+iteration+" ...");
	
    	//constructing samples using the third method
    	
    	startTime = System.nanoTime();
    	List<Integer> sample, w;
    	Set<Integer> sample_ = new HashSet<Integer>();
    	//List<Integer> w;
    	//Integer tj;
   
    		outer:
    		for (int i =0; i <  this.witness_lists.size(); i++) {
    			w = this.witness_lists.get(i);
    			bt_i = this.base_tables.get(i);
    			bt_tuples = new HashSet();
    			for (Integer key: bt_i.asindex.getKeys()) {
    				if (bt_i.asindex.get(key).size() > iteration)
    					bt_tuples.addAll(bt_i.asindex.get(key).get(iteration));
    			}
    			for (int j=0; j<w.size(); j++) {
    				if (bt_tuples.contains(w.get(j)))
    					sample_.add(j);
    				if (sample_.size() > this.sampling_support)
    					break outer;
    			}
    		}
    		
            sample = new ArrayList<Integer>(sample_);
        	endTime = System.nanoTime();
           	System.out.println("Time for generating the sample: "+(endTime - startTime)/1000000+"ms");

    	
	
	//Printing sample
	System.out.println("Sample of size "+sample.size());

	sample.forEach(num -> System.out.print(" "+num+", "));
	
	
	startTime= System.nanoTime();
	

	List<Integer> key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size

	
	int it1, it2, i1, i2;

	for (int i = 0; i < sample.size() ; i++) {
	    for (int j = i + 1; j < sample.size(); j++) {
	        for (int k = 0; k < this.witness_lists.size(); k++) {
	            it1 = this.witness_lists.get(k).get(sample.get(i));
	            it2 = this.witness_lists.get(k).get(sample.get(j));
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
	System.out.println("\n Time for generating join index without cardinalities: "+(endTime - startTime)/1000000+"ms");

	
	OpenBitSet t_bs;

	List<OpenBitSet> set_open_bitsets = new ArrayList<OpenBitSet>();
	for (int i=0; i< this.num_attrs_list.size();i++) {
		t_bs = new OpenBitSet(this.num_attrs_list.get(i));
		t_bs.set(0,this.num_attrs_list.get(i));
		set_open_bitsets.add(t_bs);
	}
	
	for (BaseTable bt: this.base_tables) {
		list_possible_bitsets.add(bt.possible_bitsets);
	}
	
	int size = 0;
	for (Integer attrs: this.num_attrs_list)
		size += attrs;
	List<Integer> val;
	int offset;
	int size_i;
	OpenBitSet bs;
	Map<OpenBitSet,Integer> j_map = new HashMap<OpenBitSet,Integer>();
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
		//System.out.println("j_bs "+BitSetUtils.toString(j_bs,size));
						
	}
	
	//Print j_map
	
	System.out.println("Printing join results");
	System.out.println("Number of agree-sets obtained using sampling: "+j_map.entrySet().size());
	
	if (num_discovered_agree_sets == j_map.entrySet().size())
		converge = true;
	else {
		num_discovered_agree_sets = j_map.entrySet().size();
		iteration++;
	}
	
	
	
	for (Map.Entry<OpenBitSet, Integer> entry : j_map.entrySet()) {
		//System.out.println(BitSetUtils.toString(entry.getKey(),size)+": "+entry.getValue());
		System.out.print(BitSetUtils.toString(entry.getKey(),size)+",");
	}
	System.out.println();
	}
	
	System.out.println("Did it converge: "+ converge);
	System.out.println("Number of iteration necessary before converging is: "+iteration);

	
	System.out.println("Directly computing the agree-sets from the result file");
	startTime = System.nanoTime();
	BaseTable res_table = new BaseTable(result_file);
	res_table.initializeEvidenceSets();
	res_table.generateRelationEvidenceVector();
	Map<Integer,Integer> agree_sets = TableEvidenceVector.collect(res_table.rev.getValue(), res_table.possible_bitsets);
	endTime = System.nanoTime();
	System.out.println("Time for generating relation evidence vector directly from the result file: "+(endTime - startTime)/1000000+"ms");

	System.out.println("Actual bitsets of the join");
	agree_sets.entrySet().removeIf(entry -> entry.getValue() == 0);
	System.out.println("Number of agree-sets directly from the results: "+agree_sets.entrySet().size());
	
	for (Map.Entry<Integer, Integer> entry : agree_sets.entrySet()) {
	     System.out.println("Bitset = " + BitSetUtils.toString(res_table.possible_bitsets.get(entry.getKey()),res_table.numAtts) + ", Cardinality = " + entry.getValue());
	}
	
}



public List<Integer> getSample(){
	long startTime, endTime;
	startTime = System.nanoTime();
	
	
	List<Integer> sample, w;
	Set<Integer> sample_ = new HashSet();
	BaseTable bt_i;
	
	
	w = this.witness_lists.get(this.curr_witness_list);	
	
	if ((curr_bt_tuples != null) && (curr_bt_tuples_position < (curr_bt_tuples.size() - 1))) {
		
		outer1:
			for (int j=curr_bt_tuples_position; j<w.size(); j++) {
				if (curr_bt_tuples.contains(w.get(j)))
					sample_.add(j);
				if (sample_.size() > this.sampling_support) {
					//curr_witness_list = j;
					curr_bt_tuples_position = j+1;
					break outer1;
				}
			}
	}
	
	if (sample_.size() >= this.sampling_support) {
		sample = new ArrayList<Integer>(sample_);
		return sample;
	}
	
	outer2:
	for (int i = (curr_witness_list +1); i< this.witness_lists.size(); i++) {
		w = this.witness_lists.get(i);
		bt_i = this.base_tables.get(i);
		this.curr_bt_tuples = new ArrayList<Integer>();
		for (int key: bt_i.idx_encountered_bitsets) 
			if (bt_i.asindex.get(key).size() > iteration)
				curr_bt_tuples.addAll(bt_i.asindex.get(key).get(iteration));
		
		for (int j=0; j<w.size(); j++) {
			if (curr_bt_tuples.contains(w.get(j)))
				sample_.add(j);
			if (sample_.size() > this.sampling_support) {
				curr_witness_list = i;
				curr_bt_tuples_position = j+1;
				break outer2;
			}
		}		
		
	}
	
	if (sample_.size() >= this.sampling_support) {
		sample = new ArrayList<Integer>(sample_);
		return sample;
	}
	
	iteration++;
	
	outer3:
	for (int i = 0; i< this.witness_lists.size(); i++) {
		w = this.witness_lists.get(i);
		bt_i = this.base_tables.get(i);
		this.curr_bt_tuples = new ArrayList<Integer>();
		for (int key: bt_i.idx_encountered_bitsets) 
			if (bt_i.asindex.get(key).size() > iteration)
				curr_bt_tuples.addAll(bt_i.asindex.get(key).get(iteration));
		
		for (int j=0; j<w.size(); j++) {
			if (curr_bt_tuples.contains(w.get(j)))
				sample_.add(j);
			if (sample_.size() > this.sampling_support) {
				curr_witness_list = i;
				curr_bt_tuples_position = j+1;
				break outer3;
			}
		}		
		
	}
	
	sample = new ArrayList<Integer>(sample_);
	return sample;

	
}

public void run_ASIndex() {
	
	
	long startTime, endTime;
	
	int sampling_round = 0;
	
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
	
	//System.out.println("Num tuples of table supplier"+this.base_tables.get(1).numTuples);
	
	//Construct possible bitsets of the join
	int join_attrs = 0;
	int pos;
	List<OpenBitSet> join_bs = new ArrayList<OpenBitSet>();
	OpenBitSet j_bs;
	for (BaseTable bt : this.base_tables)
		join_attrs += bt.numAtts;
	
	for (OpenBitSet bs: this.base_tables.get(0).possible_bitsets) {
		j_bs = new OpenBitSet(join_attrs);
		pos = bs.nextSetBit(0);
		while (pos>=0) {
			j_bs.fastSet(pos);
			pos = bs.nextSetBit(pos+1);
		}
		
		join_bs.add(j_bs);
	}
	
	int off_set = 0;
	
	OpenBitSet n_bsj;
	List<OpenBitSet> n_join_bs;
	
	if (this.witness_lists.size()>1) {
		
		for (int i = 1;  i< this.witness_lists.size(); i++) {
			off_set += this.base_tables.get(i-1).numAtts;
			n_join_bs = new ArrayList<OpenBitSet>();
			for (OpenBitSet bsj: join_bs) {
				for (OpenBitSet bsi: this.base_tables.get(i).possible_bitsets) {
					n_bsj = bsj.clone();
					pos = bsi.nextSetBit(0);
					while (pos >= 0) {
						n_bsj.fastSet(pos+off_set);
						pos = bsi.nextSetBit(pos+1);
					}
					n_join_bs.add(n_bsj);
				}
			}
			join_bs = n_join_bs;
		}
	}
	/*
	// Bitsets of each base table
	for (BaseTable bt: this.base_tables) {
		System.out.println("---"+ bt.possible_bitsets.size()+" Possible bit sets with "+bt.numAtts+" attributes");
		for (OpenBitSet bs: bt.possible_bitsets) {
			System.out.println(BitSetUtils.toString(bs,bt.numAtts));
		}
	}
	*/
	
	/*
	//Print list of possible bitsets of the join
	System.out.println(join_bs.size()+" Possible bitsets of the join");
	for (OpenBitSet bs: join_bs)
		System.out.println(BitSetUtils.toString(bs,join_attrs));
	*/
	
	//State of ASIndices
	System.out.println("AS Indices");
	for (BaseTable bt: this.base_tables) {
		bt.asindex.printSize(bt.possible_bitsets,bt.numAtts);
	}
	
	List<Integer> sample;
	

	boolean converge = false;
	int iteration = 0;
	int num_discovered_agree_sets = 0;

	Map<List<Integer>, Integer> j_index = new HashMap<List<Integer>, Integer>();
	
	while (!converge) {
		
		System.out.println("Starting iteration "+iteration+" ...");
	
    	//constructing samples using the third method
    
		startTime= System.nanoTime();
		sample = this.getSample();
		endTime= System.nanoTime();
		System.out.println("\n Time for generating sample: "+(endTime - startTime)/1000000+"ms");

		sampling_round++;
		
     if (sample.size() == 0)
    	 break;
    	
	
	//Printing sample
	System.out.println("Sample of size "+sample.size());

	sample.forEach(num -> System.out.print(" "+num+", "));
	
	
	startTime= System.nanoTime();
	

	List<Integer> key = new ArrayList<Integer>(Collections.nCopies(this.witness_lists.size(), 0)); // create the key list object with the correct size

	
	int it1, it2, i1, i2;

	for (int i = 0; i < sample.size() ; i++) {
	    for (int j = i + 1; j < sample.size(); j++) {
	        for (int k = 0; k < this.witness_lists.size(); k++) {
	            it1 = this.witness_lists.get(k).get(sample.get(i));
	            it2 = this.witness_lists.get(k).get(sample.get(j));
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
	System.out.println("\n Time for generating join index without cardinalities: "+(endTime - startTime)/1000000+"ms");

	
	OpenBitSet t_bs;

	List<OpenBitSet> set_open_bitsets = new ArrayList<OpenBitSet>();
	for (int i=0; i< this.num_attrs_list.size();i++) {
		t_bs = new OpenBitSet(this.num_attrs_list.get(i));
		t_bs.set(0,this.num_attrs_list.get(i));
		set_open_bitsets.add(t_bs);
	}
	
	for (BaseTable bt: this.base_tables) {
		list_possible_bitsets.add(bt.possible_bitsets);
	}
	
	int size = 0;
	for (Integer attrs: this.num_attrs_list)
		size += attrs;
	List<Integer> val;
	int offset;
	int size_i;
	OpenBitSet bs;
	Map<OpenBitSet,Integer> j_map = new HashMap<OpenBitSet,Integer>();
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
		//System.out.println("j_bs "+BitSetUtils.toString(j_bs,size));
						
	}
	
	//Print j_map
	
	System.out.println("Printing join results");
	System.out.println("Number of agree-sets obtained using sampling: "+j_map.entrySet().size());
	
	if (num_discovered_agree_sets == j_map.entrySet().size())
		converge = true;
	else {
		num_discovered_agree_sets = j_map.entrySet().size();
		iteration++;
	}
	
	
	
	for (Map.Entry<OpenBitSet, Integer> entry : j_map.entrySet()) {
		//System.out.print(BitSetUtils.toString(entry.getKey(),size)+": "+entry.getValue());
		System.out.print(BitSetUtils.toString(entry.getKey(),size)+",");
	   }
	}
	
	System.out.println("Did it converge: "+ converge);
	System.out.println("Number of sampling rounds necessary before converging is: "+sampling_round);

	
	System.out.println("Directly computing the agree-sets from the result file");
	startTime = System.nanoTime();
	BaseTable res_table = new BaseTable(result_file);
	res_table.initializeEvidenceSets();
	res_table.generateRelationEvidenceVector();
	Map<Integer,Integer> agree_sets = TableEvidenceVector.collect(res_table.rev.getValue(), res_table.possible_bitsets);
	endTime = System.nanoTime();
	System.out.println("Time for generating relation evidence vector directly from the result file: "+(endTime - startTime)/1000000+"ms");

	System.out.println("Actual bitsets of the join");
	agree_sets.entrySet().removeIf(entry -> entry.getValue() == 0);
	System.out.println("Number of agree-sets directly from the results: "+agree_sets.entrySet().size());
	
	for (Map.Entry<Integer, Integer> entry : agree_sets.entrySet()) {
	     System.out.println("Bitset = " + BitSetUtils.toString(res_table.possible_bitsets.get(entry.getKey()),res_table.numAtts) + ", Cardinality = " + entry.getValue());
	}
	
	System.out.println();
	for (Map.Entry<Integer, Integer> entry : agree_sets.entrySet()) {
	     System.out.print("," + BitSetUtils.toString(res_table.possible_bitsets.get(entry.getKey()),res_table.numAtts)); 
	}
	
	System.out.println();

}
	


	public JoinTable(List<String> files, List<String> witness_files, List _num_attrs_list, String result_file, int _sampling_support) {
		
		base_tables = new ArrayList<BaseTable>();
		
		this.sampling_support = _sampling_support;
		
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
		
		String result_file = "resources/datasets/example/results_j_1_j_2.csv";
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
	
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/PTC/queries/q_atom_molecule/atom_num.csv");
		files.add("resources/PTC/queries/q_atom_molecule/molecule_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/PTC/queries/q_atom_molecule/w_atom.csv");
		witness_files.add("resources/PTC/queries/q_atom_molecule/w_molecule.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(3);
		num_attrs_list.add(2);
		
		
		String result_file = "resources/PTC/queries/q_atom_molecule/results_num.csv";
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/PTC/queries/q_connected_bond/connected_num.csv");
		files.add("resources/PTC/queries/q_connected_bond/bond_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/PTC/queries/q_connected_bond/w_connected.csv");
		witness_files.add("resources/PTC/queries/q_connected_bond/w_bond.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(3);
		num_attrs_list.add(2);
		
		String result_file = "resources/PTC/queries/q_connected_bond/results_num.csv";
		*/
		
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/PTC/queries/q_connected_atom_molecule/connected_num.csv");
		files.add("resources/PTC/queries/q_connected_atom_molecule/atom_num.csv");
		files.add("resources/PTC/queries/q_connected_atom_molecule/molecule_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/PTC/queries/q_connected_atom_molecule/w_connected.csv");
		witness_files.add("resources/PTC/queries/q_connected_atom_molecule/w_atom.csv");
		witness_files.add("resources/PTC/queries/q_connected_atom_molecule/w_molecule.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(3);
		num_attrs_list.add(3);
		num_attrs_list.add(2);
		
		String result_file = "resources/PTC/queries/q_connected_atom_molecule/results_num.csv";
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/PTC/queries/q_connected_bond_molecule/connected_num.csv");
		files.add("resources/PTC/queries/q_connected_bond_molecule/bond_num.csv");
		files.add("resources/PTC/queries/q_connected_bond_molecule/molecule_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/PTC/queries/q_connected_bond_molecule/w_connected.csv");
		witness_files.add("resources/PTC/queries/q_connected_bond_molecule/w_bond.csv");
		witness_files.add("resources/PTC/queries/q_connected_bond_molecule/w_molecule.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(3);
		num_attrs_list.add(3);
		num_attrs_list.add(2);
		
		String result_file = "resources/PTC/queries/q_connected_bond_molecule/results_num.csv";
		*/
		
		/*
		// bond join drug join active
		
		List<String> files = new ArrayList<String>();
		files.add("resources/PTE/queries/q_bond_drug_active/pte_active_num.csv");
		files.add("resources/PTE/queries/q_bond_drug_active/pte_bond_num.csv");

		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/PTE/queries/q_bond_drug_active/w_active.csv");
		witness_files.add("resources/PTE/queries/q_bond_drug_active/w_bond.csv");

		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(2);
		num_attrs_list.add(4);
		
		String result_file = "resources/PTE/queries/q_bond_drug_active/results_num.csv";
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/PTE/queries/q_atm_bond_atm_drug/pte_atm_num.csv");
		files.add("resources/PTE/queries/q_atm_bond_atm_drug/pte_atm_num.csv");
		files.add("resources/PTE/queries/q_atm_bond_atm_drug/pte_bond_num.csv");

		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/PTE/queries/q_atm_bond_atm_drug/w_atom1.csv");
		witness_files.add("resources/PTE/queries/q_atm_bond_atm_drug/w_atom2.csv");
		witness_files.add("resources/PTE/queries/q_atm_bond_atm_drug/w_bond.csv");

		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(5);
		num_attrs_list.add(5);
		num_attrs_list.add(4);
		
		String result_file = "resources/PTE/queries/q_atm_bond_atm_drug/results_num.csv";
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/flight/queries/q_flight_coord/flight_num.csv");
		files.add("resources/flight/queries/q_flight_coord/coord_num.csv");

		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/flight/queries/q_flight_coord/w_flight.csv");
		witness_files.add("resources/flight/queries/q_flight_coord/w_coord.csv");

		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(109);
		num_attrs_list.add(3);
		
		String result_file = "resources/flight/queries/q_flight_weather/results_num.csv";
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/uniprot/queries/uniprot_seq/uniprot_num.csv");
		files.add("resources/uniprot/queries/uniprot_seq/seq_num.csv");

		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/uniprot/queries/uniprot_seq/w_uniprot.csv");
		witness_files.add("resources/uniprot/queries/uniprot_seq/w_seq.csv");

		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(222);
		num_attrs_list.add(2);
		
		String result_file = "resources/uniprot/queries/uniprot_seq/results_num.csv";
		
		*/
		
		/*
		// Q2
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q2/part_num.csv");
		files.add("resources/tpch_001/queries/q2/supplier_num.csv");
		files.add("resources/tpch_001/queries/q2/partsupp_num.csv");
		files.add("resources/tpch_001/queries/q2/nation_num.csv");
		files.add("resources/tpch_001/queries/q2/region_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q2/w_part.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_supplier.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_partsupp.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_nation.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_region.csv");


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(9);
		num_attrs_list.add(8);
		num_attrs_list.add(7);
		num_attrs_list.add(4);
		num_attrs_list.add(3);
		
		String result_file = "resources/tpch_001/queries/q2/results_num.csv";
		*/
		
		// Q2 sampling
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q2/sample/part_num.csv");
		files.add("resources/tpch_001/queries/q2/sample/supplier_num.csv");
		files.add("resources/tpch_001/queries/q2/sample/partsupp_num.csv");
		files.add("resources/tpch_001/queries/q2/sample/nation_num.csv");
		files.add("resources/tpch_001/queries/q2/sample/region_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q2/sample/w_part.csv");
		witness_files.add("resources/tpch_001/queries/q2/sample/w_supplier.csv");
		witness_files.add("resources/tpch_001/queries/q2/sample/w_partsupp.csv");
		witness_files.add("resources/tpch_001/queries/q2/sample/w_nation.csv");
		witness_files.add("resources/tpch_001/queries/q2/sample/w_region.csv");


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(9);
		num_attrs_list.add(8);
		num_attrs_list.add(7);
		num_attrs_list.add(4);
		num_attrs_list.add(3);
		
		String result_file = "resources/tpch_001/queries/q2/sample/results_num.csv";
		*/
		
		/*
		// Q2 new
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q2/part_num.csv");
		files.add("resources/tpch_001/queries/q2/supplier_num.csv");
		files.add("resources/tpch_001/queries/q2/partsupp_num.csv");
		files.add("resources/tpch_001/queries/q2/nation_num.csv");
		files.add("resources/tpch_001/queries/q2/region_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q2/w_part.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_supplier.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_partsupp.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_nation.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_region.csv");


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(2);
		num_attrs_list.add(7);
		num_attrs_list.add(2);
		num_attrs_list.add(3);
		num_attrs_list.add(1);
		
		String result_file = "resources/tpch_001/queries/q2/results_num.csv";
		*/
		
		/*
		// Q2 with projection
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q2/part_num.csv");
		files.add("resources/tpch_001/queries/q2/supplier_num.csv");
		//files.add("resources/tpch_001/queries/q2/partsupp_num.csv");
		files.add("resources/tpch_001/queries/q2/nation_num.csv");
		//files.add("resources/tpch_001/queries/q2/region_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q2/w_part.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_supplier.csv");
		//witness_files.add("resources/tpch_001/queries/q2/w_partsupp.csv");
		witness_files.add("resources/tpch_001/queries/q2/w_nation.csv");
		//witness_files.add("resources/tpch_001/queries/q2/w_region.csv");


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(9);
		num_attrs_list.add(8);
		//num_attrs_list.add(7);
		num_attrs_list.add(4);
		//num_attrs_list.add(3);
		
		String result_file = "resources/tpch_001/queries/q2/results_with_projection_num.csv";
		*/
		
		/*
		 //q3 with sampling
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q3/sample/customer_num.csv");
		files.add("resources/tpch_001/queries/q3/sample/orders_num.csv");
		files.add("resources/tpch_001/queries/q3/sample/v_lineitem_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q3/sample/w_customer.csv");
		witness_files.add("resources/tpch_001/queries/q3/sample/w_orders.csv");
		witness_files.add("resources/tpch_001/queries/q3/sample/w_lineitem.csv");


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(12);
		num_attrs_list.add(9);
		num_attrs_list.add(16);

		String result_file = "resources/tpch_001/queries/q3/results_num.csv";
		*/
		
		/*
		 //q3 
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q3/customer_num.csv");
		files.add("resources/tpch_001/queries/q3/orders_num.csv");
		files.add("resources/tpch_001/queries/q3/v_lineitem_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q3/w_customer.csv");
		witness_files.add("resources/tpch_001/queries/q3/w_orders.csv");
		witness_files.add("resources/tpch_001/queries/q3/w_lineitem.csv");


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(12);
		num_attrs_list.add(9);
		num_attrs_list.add(16);

		String result_file = "resources/tpch_001/queries/q3/results_num.csv";
		*/
		
		/*
		//Q3 with projection
		List<String> files = new ArrayList<String>();
		//files.add("resources/tpch_001/queries/q3/customer_num.csv");
		files.add("resources/tpch_001/queries/q3/orders_num.csv");
		files.add("resources/tpch_001/queries/q3/v_lineitem_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		//witness_files.add("resources/tpch_001/queries/q3/w_customer.csv");
		witness_files.add("resources/tpch_001/queries/q3/w_orders.csv");
		witness_files.add("resources/tpch_001/queries/q3/w_lineitem.csv");


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		//num_attrs_list.add(12);
		num_attrs_list.add(9);
		num_attrs_list.add(16);

		String result_file = "resources/tpch_001/queries/q3/results_with_projection_num.csv";
		*/
		
		/*
		//Q9
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q9/part_num.csv");
		files.add("resources/tpch_001/queries/q9/partsupp_num.csv");
		files.add("resources/tpch_001/queries/q9/supplier_num.csv");
		files.add("resources/tpch_001/queries/q9/v_lineitem_num.csv");
		files.add("resources/tpch_001/queries/q9/orders_num.csv");
		files.add("resources/tpch_001/queries/q9/nation_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q9/w_part.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_partsupp.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_supplier.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_lineitem.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_orders.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_nation.csv");
		


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(9);
		num_attrs_list.add(5);
		num_attrs_list.add(7);
		num_attrs_list.add(16);
		num_attrs_list.add(9);
		num_attrs_list.add(4);

		
		String result_file = "resources/tpch_001/queries/q9/results_num.csv";
		*/
		
		/*
		//Q9 with sampling
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q9/sample/part_num.csv");
		files.add("resources/tpch_001/queries/q9/sample/partsupp_num.csv");
		files.add("resources/tpch_001/queries/q9/sample/supplier_num.csv");
		files.add("resources/tpch_001/queries/q9/sample/view_lineitem_num.csv");
		files.add("resources/tpch_001/queries/q9/sample/orders_num.csv");
		files.add("resources/tpch_001/queries/q9/sample/nation_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q9/sample/w_part.csv");
		witness_files.add("resources/tpch_001/queries/q9/sample/w_partsupp.csv");
		witness_files.add("resources/tpch_001/queries/q9/sample/w_supplier.csv");
		witness_files.add("resources/tpch_001/queries/q9/sample/w_lineitem.csv");
		witness_files.add("resources/tpch_001/queries/q9/sample/w_orders.csv");
		witness_files.add("resources/tpch_001/queries/q9/sample/w_nation.csv");
		


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(9);
		num_attrs_list.add(5);
		num_attrs_list.add(7);
		num_attrs_list.add(16);
		num_attrs_list.add(9);
		num_attrs_list.add(4);

		
		String result_file = "resources/tpch_001/queries/q9/results_num.csv";
		*/
		
		/*
		//Q9 with projection
		List<String> files = new ArrayList<String>();
		//files.add("resources/tpch_001/queries/q9/part_num.csv");
		files.add("resources/tpch_001/queries/q9/partsupp_num.csv");
		//files.add("resources/tpch_001/queries/q9/supplier_num.csv");
		files.add("resources/tpch_001/queries/q9/v_lineitem_num.csv");
		files.add("resources/tpch_001/queries/q9/orders_num.csv");
		files.add("resources/tpch_001/queries/q9/nation_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		//witness_files.add("resources/tpch_001/queries/q9/w_part.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_partsupp.csv");
		//witness_files.add("resources/tpch_001/queries/q9/w_supplier.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_lineitem.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_orders.csv");
		witness_files.add("resources/tpch_001/queries/q9/w_nation.csv");
		


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		//num_attrs_list.add(9);
		num_attrs_list.add(5);
		//num_attrs_list.add(7);
		num_attrs_list.add(16);
		num_attrs_list.add(9);
		num_attrs_list.add(4);

		
		String result_file = "resources/tpch_001/queries/q9/results_with_projection_num.csv";
		*/
		
		
		/*
		//Q10
		
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q10/customer_num.csv");
		files.add("resources/tpch_001/queries/q10/orders_num.csv");
		files.add("resources/tpch_001/queries/q10/v_lineitem_num.csv");
		files.add("resources/tpch_001/queries/q10/nation_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q10/w_customer.csv");
		witness_files.add("resources/tpch_001/queries/q10/w_orders.csv");
		witness_files.add("resources/tpch_001/queries/q10/w_lineitem.csv");
		witness_files.add("resources/tpch_001/queries/q10/w_nation.csv");
		


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(8);
		num_attrs_list.add(9);
		num_attrs_list.add(16);
		num_attrs_list.add(4);

		
		String result_file = "resources/tpch_001/queries/q10/results_num.csv";
		*/
		
		//Q10 sampling
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q10/sample/customer_num.csv");
		files.add("resources/tpch_001/queries/q10/sample/orders_num.csv");
		files.add("resources/tpch_001/queries/q10/sample/view_lineitem_num.csv");
		files.add("resources/tpch_001/queries/q10/sample/nation_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q10/sample/w_customer.csv");
		witness_files.add("resources/tpch_001/queries/q10/sample/w_orders.csv");
		witness_files.add("resources/tpch_001/queries/q10/sample/w_lineitem.csv");
		witness_files.add("resources/tpch_001/queries/q10/sample/w_nation.csv");
		


		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(8);
		num_attrs_list.add(9);
		num_attrs_list.add(16);
		num_attrs_list.add(4);

		
		String result_file = "resources/tpch_001/queries/q10/sample/results_num.csv";
		*/
		/*
		//Q10 with projection
		
				List<String> files = new ArrayList<String>();
				files.add("resources/tpch_001/queries/q10/customer_num.csv");
				//files.add("resources/tpch_001/queries/q10/orders_num.csv");
				files.add("resources/tpch_001/queries/q10/v_lineitem_num.csv");
				files.add("resources/tpch_001/queries/q10/nation_num.csv");


				
				List<String> witness_files = new ArrayList<String>();
				witness_files.add("resources/tpch_001/queries/q10/w_customer.csv");
				//witness_files.add("resources/tpch_001/queries/q10/w_orders.csv");
				witness_files.add("resources/tpch_001/queries/q10/w_lineitem.csv");
				witness_files.add("resources/tpch_001/queries/q10/w_nation.csv");
				


				
				List<Integer> num_attrs_list = new ArrayList<Integer>();
				num_attrs_list.add(8);
				//num_attrs_list.add(9);
				num_attrs_list.add(16);
				num_attrs_list.add(4);

				
				String result_file = "resources/tpch_001/queries/q10/results_with_projection_num.csv";
		*/
		
		/*
		//Q11
		List<String> files = new ArrayList<String>();
		files.add("resources/tpch_001/queries/q11/supplier_num.csv");
		files.add("resources/tpch_001/queries/q11/partsupp_num.csv");
		files.add("resources/tpch_001/queries/q11/nation_num.csv");

		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/tpch_001/queries/q11/w_supplier.csv");
		witness_files.add("resources/tpch_001/queries/q11/w_partsupp.csv");
		witness_files.add("resources/tpch_001/queries/q11/w_nation.csv");	

		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(7);
		num_attrs_list.add(5);
		num_attrs_list.add(4);

		String result_file = "resources/tpch_001/queries/q11/results_num.csv";
		*/
		/*
		//Q11 with projection
		List<String> files = new ArrayList<String>();
		//files.add("resources/tpch_001/queries/q11/supplier_num.csv");
		files.add("resources/tpch_001/queries/q11/partsupp_num.csv");
		//files.add("resources/tpch_001/queries/q11/nation_num.csv");

		List<String> witness_files = new ArrayList<String>();
		//witness_files.add("resources/tpch_001/queries/q11/w_supplier.csv");
		witness_files.add("resources/tpch_001/queries/q11/w_partsupp.csv");
		//witness_files.add("resources/tpch_001/queries/q11/w_nation.csv");	

		List<Integer> num_attrs_list = new ArrayList<Integer>();
		//num_attrs_list.add(7);
		num_attrs_list.add(5);
		//num_attrs_list.add(4);

		String result_file = "resources/tpch_001/queries/q11/results_with_projection_num.csv";
		*/
		
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
		
		String result_file = "resources/PTC/queries/q_connected_bond/results_num.csv";
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/flight/data/flight_info_num.csv");
		files.add("resources/flight/data/state_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/flight/data/w_flight_info.csv");
		witness_files.add("resources/flight/data/w_state.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(56);
		num_attrs_list.add(13);
		
		String result_file = "resources/flight/data/results_num.csv";
		*/
		
		// Flight join delay
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/flight/data/flight_Delay/flight_info_num.csv");
		files.add("resources/flight/data/flight_Delay/delayGroupInfo_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/flight/data/flight_Delay/w_flight_info.csv");
		witness_files.add("resources/flight/data/flight_Delay/w_delay_info.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(56);
		num_attrs_list.add(8);
		
		String result_file = "resources/flight/data/results_num.csv";
		*/
		
		/*
		// Flight join State join Delay
		
		List<String> files = new ArrayList<String>();
		files.add("resources/flight/data/flight_State_Delay/flight_info_num.csv");
		files.add("resources/flight/data/flight_State_Delay/state_num.csv");
		files.add("resources/flight/data/flight_State_Delay/delayGroupInfo_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/flight/data/flight_State_Delay/w_flight_info.csv");
		witness_files.add("resources/flight/data/flight_State_Delay/w_state.csv");
		witness_files.add("resources/flight/data/flight_State_Delay/w_delay_info.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(56);
		num_attrs_list.add(13);
		num_attrs_list.add(8);
		
		String result_file = "resources/flight/data/flight_State_Delay/results_num.csv";
		*/
		
		
		//join proteins_1 proteins_2
		
		List<String> files = new ArrayList<String>();
		files.add("resources/protein/protein_chemical/proteins_num.csv");
		files.add("resources//protein/protein_chemical/chemical_num.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/protein/protein_chemical/w_proteins.csv");
		witness_files.add("resources/protein/protein_chemical/w_proteins_2.csv");
		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(31);
		num_attrs_list.add(26);
		
		String result_file = "resources/protein/protein_chemical/results_num.csv";
		
		
		int sampling_support = 20;
		JoinTable jt = new JoinTable(files,witness_files, num_attrs_list,result_file, sampling_support);
		
		jt.run();
		
		//jt.run_variant(); // The one I used for the experiments this far
		
		//jt.run_ASIndex_OLD();
		
		//jt.run_ASIndex();
		
		//jt.run_variant_test();
		
		
		
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
