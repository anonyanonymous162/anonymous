package anonym.anonyms.anonym.algo;

import anonym.anonyms.anonym.utilities.BitSetUtils;
import anonym.anonyms.anonym.utilities.JoinEvidenceVector;
import anonym.anonyms.anonym.utilities.MTableEvidenceVector;
import anonym.anonyms.anonym.utilities.RelationEvidenceVector;
import anonym.anonyms.anonym.utilities.TableEvidenceVector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.lucene.util.OpenBitSet;

import au.com.bytecode.opencsv.CSVReader;

public class MJoinTable {
	
	JoinEvidenceVector jev;
	List<MBaseTable> base_tables;
	List<List<Integer>> witness_lists;
	List<Integer> num_attrs_list;
	String result_file;
	
	List<List<Integer>> removed_tuples;
	
	List<List<OpenBitSet>> list_possible_bitsets;
	

	
	List<MTableEvidenceVector> select_table_vectors;
	
	
public void run_variant_test() {
		
		
		long startTime,endTime;
		
		// Generate the table evidence vector for each base relation
		for (MBaseTable bt: this.base_tables) {
			startTime= System.nanoTime();
			bt.initializeEvidenceSets();
			bt.generateRelationEvidenceVector();
			endTime = System.nanoTime();
			System.out.println("Time for generating Table Evidence Vector: "+(endTime - startTime)/1000000+"ms");
			//MTableEvidenceVector.print(bt.rev,  bt.numAtts, bt.possible_bitsets);
		}
		
		System.out.println("Number of tuples in the first table: "+this.base_tables.get(0).numTuples);
		
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
		
		System.out.println("Time to remove uncessary cells in the key-based selection");
		startTime= System.nanoTime();
		Map<Integer,List<Integer>> val = this.base_tables.get(0).rev.getValue();
		
		
		List<Integer> indices = this.removed_tuples.get(0);
		
		//System.out.println("Number of tuples to be removed: "+indices.size());
		//System.out.println("Tuples that needs to be removed "+indices.toString());
		
		val.entrySet().removeIf(entry -> indices.contains(entry.getKey()));
		
		//Scanner scanner = new Scanner(System.in);
		
		/*
		for (Map.Entry<Integer, List<Integer>> entry : val.entrySet()) {
			
			p= indices.size()-1;
			jp =indices.get(p);
			

			while (jp> entry.getKey()) {
			//for (int j =indices.get(indices.size()-1) ; j> entry.getKey(); j--) {
				//System.out.println("i: "+i+", j: "+j);
			
				//System.out.println("Remove item in entry "+entry.getKey()+" the element in position "+(jp-entry.getKey()-1));
				entry.getValue().remove(jp-entry.getKey()-1);
				//scanner.nextLine();
				p--;
				if (p>=0)
					jp =indices.get(p);
				else 
					break;
			}
			
		}
		*/
		
		int p= indices.size()-1;

		
		while (p>0) {
			final int jp =indices.get(p);
			val.forEach((k,v) -> {if (jp > k) v.remove(jp-k-1);});
			p--;
			
		}
		

		
		
		endTime = System.nanoTime();
		System.out.println("Time for removing unnecessary cells: "+(endTime - startTime)/1000000+"ms");
		
		//this.base_tables.get(0).print_table_evidence_vector();
		
		
		int pos = 0;
		int i1;
		int i2;
		int r,c;
		//List<Integer> w2 = this.witness_lists.get(1);
		int ll = this.base_tables.get(1).possible_bitsets.size();//length of the list
		OpenBitSet full_bs = new OpenBitSet(this.num_attrs_list.get(1));
		full_bs.set(0,this.num_attrs_list.get(1));
		
		//System.out.println("full_bs: "+BitSetUtils.toString(full_bs));
		
		int pos_full_bs = this.base_tables.get(1).possible_bitsets.indexOf(full_bs);
		
		val = this.base_tables.get(0).rev.getValue();
		

		//this.base_tables.get(0).print_table_evidence_vector();
		
		startTime= System.nanoTime();
		Map<Integer,List<Integer>> val2 = this.base_tables.get(1).rev.getValue();
		
		val2.forEach((k,v) -> {v.stream().map(num -> num * ll);});
		endTime = System.nanoTime();
		System.out.println("Time for multiplying the elements of the second table with ll"+(endTime - startTime)/1000000+"ms");
		
		
		//Constructing a map that associate the id of Table 1 to Id of table 2
		Map<Integer,Integer> map_t1_2_t2 = new HashMap<Integer,Integer>();
		for (int i=0; i <this.witness_lists.get(0).size(); i++)
			map_t1_2_t2.put(this.witness_lists.get(0).get(i),this.witness_lists.get(1).get(i));
		
		startTime= System.nanoTime();
		
		//val.forEach((k,v) -> {v.stream().map((num,index) -> (num + 1);  )});
		
		/*
		for (Map.Entry<Integer, List<Integer>> entry : val.entrySet()) {
			i1 = w2.get(pos);
			for (int j = pos+1; j<w2.size(); j++) {
				i2 = w2.get(j);
				r = Math.min(i1, i2);
				c = Math.max(i1,i2);
				//System.out.println("r: "+r+", c: "+c);
				
				if (c == r) {
					//System.out.println("pos: "+pos+", j: "+j+ ", j-pos-1: "+(j-pos-1));
					//System.out.println("entry.getValue().size(): "+entry.getValue().size());
					//int i = entry.getValue().get(j-pos-1);
					//entry.getValue().set(j-pos-1,   entry.getValue().get(j-pos-1) + (ll * pos_full_bs));
					entry.getValue().set(j-pos-1,   entry.getValue().get(j-pos-1) + pos_full_bs);
				}
				else	{
					//int val_to_add = val2.get(r).get(c-r-1);
					//entry.getValue().set(j-pos-1,   entry.getValue().get(j-pos-1) + (ll * val2.get(r).get(c-r-1)));
					entry.getValue().set(j-pos-1,   entry.getValue().get(j-pos-1) + (val2.get(r).get(c-r-1)));
				}
			}
			pos++;
			
		}*/
		
		List<Integer> w2 = this.witness_lists.get(1);
		int[] w2Array = new int[w2.size()];
		for (int i = 0; i < w2.size(); i++) {
		    w2Array[i] = w2.get(i);
		}
		int valToAdd;
		pos = 0;
		
		startTime= System.nanoTime();
		
		for (Map.Entry<Integer, List<Integer>> entry : val.entrySet()) {
		    i1 = w2Array[pos];
		    int[] values = entry.getValue().stream().mapToInt(Integer::intValue).toArray(); // convert List to array for faster access
		    for (int j = pos+1; j < w2Array.length; j++) {
		        i2 = w2Array[j];
		        //r, c, valToAdd;
		        if (i1 < i2) {
		            r = i1;
		            c = i2;
		            valToAdd = val2.get(r).get(c-r-1);
		        } else if (i1 > i2) {
		            r = i2;
		            c = i1;
		            valToAdd = val2.get(r).get(c-r-1);
		        } else {
		            r = i1;
		            c = i2;
		            valToAdd = pos_full_bs;
		        }
		        values[j-pos-1] += valToAdd;
		    }
		    entry.setValue(Arrays.stream(values).boxed().collect(Collectors.toList())); // convert back to List
		    pos++;
		}
		
		endTime = System.nanoTime();
		System.out.println("Table evidence vector after perfoming the join");
		//this.base_tables.get(0).print_table_evidence_vector();
		//MTableEvidenceVector.print(this.base_tables.get(0).rev,  this.base_tables.get(0).numAtts, this.base_tables.get(0).possible_bitsets);

		System.out.println("Time for getting the end results: "+(endTime - startTime)/1000000+"ms");
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
	


	
	public MJoinTable(List<String> files, List<String> witness_files, List _num_attrs_list, String result_file) {
		
		base_tables = new ArrayList<MBaseTable>();
		
		list_possible_bitsets = new ArrayList<List<OpenBitSet>>();
		
		for (String f: files) {
			base_tables.add(new MBaseTable(f));
		}
		
		witness_lists = new ArrayList<List<Integer>>();
		
		for (String f: witness_files) {
			witness_lists.add(this.getWitnessList(f));
		}
		
		this.num_attrs_list =_num_attrs_list;
		
		select_table_vectors = new ArrayList<MTableEvidenceVector>();
		
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
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/datasets/example/example_j_5.csv");
		files.add("resources/datasets/example/example_j_6.csv");
		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/datasets/example/w_j_5.csv");
		witness_files.add("resources/datasets/example/w_j_6.csv");
		
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
		files.add("resources/flight/queries/q_flight_weather/flight_num.csv");
		files.add("resources/flight/queries/q_flight_weather/weather_num.csv");

		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/flight/queries/q_flight_weather/w_flight.csv");
		witness_files.add("resources/flight/queries/q_flight_weather/w_weather.csv");

		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(109);
		num_attrs_list.add(8);
		
		String result_file = "resources/flight/queries/q_flight_weather/results_num.csv";
		*/
		
		/*
		List<String> files = new ArrayList<String>();
		files.add("resources/flight/queries/q_flight_airport/flight_num.csv");
		//files.add("resources/flight/queries/q_flight_airport/airport_num.csv");

		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/flight/queries/q_flight_airport/w_flight.csv");
		//witness_files.add("resources/flight/queries/q_flight_airport/w_coord.csv");

		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(109);
		//num_attrs_list.add(1);
		
		String result_file = "resources/flight/queries/q_flight_weather/results_num.csv";
		*/
		
		
		List<String> files = new ArrayList<String>();
		files.add("resources/uniprot/queries/uniprot_drosophilia/uniprot_num.csv");


		
		List<String> witness_files = new ArrayList<String>();
		witness_files.add("resources/uniprot/queries/uniprot_drosophilia/w_uniprot.csv");

		
		List<Integer> num_attrs_list = new ArrayList<Integer>();
		num_attrs_list.add(222);
		//num_attrs_list.add(2);
		
		String result_file = "resources/uniprot/queries/uniprot_drosophilia/results_num.csv";
		
		
		
		
		
		
		
		
		MJoinTable jt = new MJoinTable(files,witness_files, num_attrs_list,result_file);
		
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
