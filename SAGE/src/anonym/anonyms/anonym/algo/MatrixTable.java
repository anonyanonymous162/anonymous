package anonym.anonyms.anonym.algo;



import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import au.com.bytecode.opencsv.CSVReader;
//import de.metanome.algorithm_integration.ColumnIdentifier;
//import de.metanome.algorithm_integration.ColumnIdentifier;
//import fr.dauphine.lamsade.khalid.dynast.util.*;
//import fr.dauphine.lamsade.khalid.dynast.util.DiffVector;
import anonym.anonyms.anonym.utilities.AttributeValueEvidenceVector;
import anonym.anonyms.anonym.utilities.BitSetUtils;
import anonym.anonyms.anonym.utilities.MatrixEvidenceVector;
import anonym.anonyms.anonym.utilities.TableEvidenceVector;
import anonym.anonyms.anonym.utilities.TupleEvidenceVector_v1;
import anonym.anonyms.anonym.utilities.TupleEvidenceVector_v2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;

import org.apache.commons.math4.legacy.linear.OpenMapRealMatrix;
import org.apache.commons.math4.legacy.linear.RealMatrix;
//import org.mp.naumann.algorithms.fd.utils.BitSetUtils;
//import org.mp.naumann.algorithms.fd.utils.ValueComparator;
import org.apache.lucene.util.OpenBitSet;



// @author Khalid Belhajjame

 

public class MatrixTable {
	
	OpenMapRealMatrix w1_matrix, w2_matrix;
	String result_file;
	public int numAtts;
	int numberOfBatches = 0;
	String insert_header = "Load, Evidence sets update, BitSetMap update, Negative cover update, Positive cover update, Merge BitSet map, Total, Total (without data loading)";
	String delete_header = "Load, Sort, Deleted bitset computation, New bitset computation, Negative cover update, Positive cover update, Total, Total (without data loading)";
	/* file refers to the csv of the initial batch (without the extension ".csv"). For each iteration we may have 
	 * an inser and/or delete batch denoted by file_insert_i and file_delete_i, 
	 * where i refers to the iteration number. 
	 */
	String file = ""; 
	protected List<String> columnNames;
	protected List<OpenBitSet> bitsets;
    //protected ObjectArrayList<ColumnIdentifier> columnIdentifiers;
	//int op_ID;
	
	AttributeValueEvidenceVector  empty_av_vector;
	
	List<Set<Integer>> att_values;
 
    protected Map<Integer,List<Integer>> tuples = new HashMap<Integer,List<Integer>>();
    List<Integer> tuple_ids = new ArrayList<Integer>();
    protected int tuple_ID =0;
	private String tableName;
	private OpenBitSet emptybitset;
	
	public MatrixEvidenceVector rev;
	
	ObjectArrayList<HashMap<Integer,AttributeValueEvidenceVector>> AVES = null;
	
	HashMap<Integer,TupleEvidenceVector_v1> TEV_v1 = null;
	HashMap<Integer,TupleEvidenceVector_v2> TEV_v2= null;
	
	//ObjectArrayList<HashMap<Integer,Set<Integer>>> indices; // Used for indexing the tuples by attribute values
	ObjectArrayList<HashMap<Integer,List<Integer>>> indices; // Used for indexing the tuples by attribute values

	
	Map<OpenBitSet,Integer> agreesets;
	
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
	 
	public OpenMapRealMatrix createFirstWitnessMatrix(List<Integer> w_list) {
		
		this.w1_matrix = new OpenMapRealMatrix(this.rev.matrix.getRowDimension(),w_list.size());
		
		for (int i=0; i<w_list.size();i++)
			w1_matrix.setEntry(w_list.get(i), i, 1);
		
		
		return w1_matrix;
		
	}
	
	
	public OpenMapRealMatrix createSecondWitnessMatrix(List<Integer> w_list) {
		
		this.w2_matrix = new OpenMapRealMatrix(w_list.size(),this.rev.matrix.getRowDimension());
		
		for (int i=0; i<w_list.size();i++)
			w2_matrix.setEntry(i, w_list.get(i), 1);
		
		return w2_matrix;
		
	}
	
	
	public List<Integer> getWitnessList(String witness_file) {
		
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
	
	MatrixTable(String file){
		
		this.tableName = file.substring(0, file.length() - 4);
		this.file = file;
		String[] values = null;	
		
		att_values = new ArrayList<Set<Integer>>();
		
		CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader(file));
			if ((values = csvReader.readNext()) != null) {
		    	this.columnNames = Arrays.asList(values);
		    	this.numAtts = this.columnNames.size();
		    	for (int i = 0; i< this.numAtts; i++)
		    		att_values.add(new HashSet<Integer>());
		    	this.initializeIndexStructure();    	
		    }
		    while ((values = csvReader.readNext()) != null) {  	
		    	this.addTuple(Arrays.asList(values).stream().map(Integer::parseInt).collect(Collectors.toList()));
		    			//.stream().map(Integer::parseInt).collect(Collectors.toList()));
		    			//.stream().map(Integer::valueOf).collect(Collectors.toList()));
		    }
		    this.empty_av_vector = new AttributeValueEvidenceVector(new OpenBitSet(this.tuples.size()));
		    
		    for (int i = 0; i< this.numAtts; i++) {
		    	if ((att_values.get(i).size() == this.tuples.size()) || (att_values.get(i).size() == 1)) {
		    		System.out.println("Attribute: "+i+" should be ignored since all of its values are distincts");
		    		this.ignoreAttribute(i);
		    	}
		    }
		    
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		
		///this.numberOfBatches = _numberOfBatches;
		///this.algo = new FDEP(this.numberAttributes ,new ValueComparator(true));
		///this.algo.op_ID = 0;
		
		
		this.result_file = result_file;
 
	}
	
	public void ignoreAttribute(Integer att) {
		
		this.indices.remove(att);
		this.columnNames.remove(att);
		
		for (Integer tuple_id: this.tuples.keySet()) {
			this.tuples.get(tuple_id).remove(att);
		}
		
		this.numAtts--;
		
		System.out.println("Attribute "+att+" removed from processing");
		
	}
	
	public void printAVES() {
		
		System.out.println("AVES");
		int size = this.getTuples().size();
		
		for (int i=0; i<this.AVES.size(); i++) {
			
			System.out.println("* AVES for Attribute "+i);
		 	
			
			for (int key: this.AVES.get(i).keySet()) {		
				System.out.print("--- attribute value "+key+" ");
					System.out.println(BitSetUtils.toString(this.AVES.get(i).get(key).toOpenBitSet(),size));
			
			}	
		}
		
	}
	
	public void displayBaseTable(int num) {
		
		System.out.println("Table name: "+this.tableName);
		System.out.println("Columns: "+Arrays.toString(this.columnNames.toArray()));
		
		for (int i = 0; i< num; i++) {
			List<Integer> tuple = this.getTuples().get(i);
			for (int j=0 ; j< this.numAtts; j++)
				System.out.print(tuple.get(j));
			System.out.println();
		}
		
	}
	
	public Map<Integer, List<Integer>> getTuples() {
		return tuples;
	}
	
	public void initializeIndexStructure() {
		
		indices = new ObjectArrayList<HashMap<Integer,List<Integer>>>();
		HashMap<Integer,List<Integer>> tmp = new HashMap<Integer,List<Integer>>();
		for (int i=0; i< this.numAtts;i++)
			indices.add(new HashMap<Integer,List<Integer>>()); 
		
	}
	
	public int addTuple(List<Integer> t) {
		
		int id = this.generateID();
		this.tuples.put(id, t);
		

		return id;
		
	}
	
    public int generateID() {
    	this.tuple_ids.add(tuple_ID);
    	return this.tuple_ID++;
    }


    /*
    public void print_table_evidence_vector() {
    	System.out.println("Table Evidence Vector");
    	int r = 0;
    	for(List<Integer> row: this.rev.getValue()) {
    		System.out.println("Row: "+r);
    		for(Integer col: row) {
    			System.out.print(col+"   ");
    		}
    		System.out.println();
    		r++;
    		
    	}
    }
    */
    
	public void initializeEvidenceSets_old() {
		AVES = new ObjectArrayList<HashMap<Integer,AttributeValueEvidenceVector>>();
		for (int i=0; i<this.numAtts; i++) 
			AVES.add(new HashMap<Integer,AttributeValueEvidenceVector>());
		
		
		for (Integer key_j : this.getTuples().keySet()) {
			//System.out.println("Handling tuple: "+key_j);
			for (int i=0; i<this.numAtts; i++) {
				if (!AVES.get(i).containsKey(this.getTuples().get(key_j).get(i))) {
					AVES.get(i).put(this.getTuples().get(key_j).get(i), this.initializeAttributeValueVector(i, this.getTuples().get(key_j).get(i)));
				}
			}

		}
		
		
		//System.out.println("Number of attribute-value vectors created at initialization using the new method: "+num_att_value_vectors);
		//this.printAVES();
		//this.printEvidenceSets();
		
	}
	
	public void initializeEvidenceSets() {
		
		this.createIndex();
		
		List<Integer> tuples;
		int size = this.tuples.size();
		
		AVES = new ObjectArrayList<HashMap<Integer,AttributeValueEvidenceVector>>();
		for (int i=0; i<this.numAtts; i++) 
			AVES.add(new HashMap<Integer,AttributeValueEvidenceVector>());
		
		for (int i=0; i<this.numAtts; i++) {
			for (Integer att_val : this.indices.get(i).keySet()) {
			    
				tuples =  this.indices.get(i).get(att_val);
				
				if (tuples.size() == 1) {
					AVES.get(i).put(att_val,this.empty_av_vector);
				}
				else {
					OpenBitSet val = new OpenBitSet(size);
					for (Integer pos: tuples)
						val.fastSet(pos);
					AVES.get(i).put(att_val,new AttributeValueEvidenceVector(val));
				}
					
				
			}
		}
		

		
		
		//System.out.println("Number of attribute-value vectors created at initialization using the new method: "+num_att_value_vectors);
		//this.printAVES();
		//this.printEvidenceSets();
		
	}
	
	protected AttributeValueEvidenceVector initializeAttributeValueVector(Integer attribute, Integer value) {
		
		int size = this.getTuples().size();
		OpenBitSet vector = new OpenBitSet(size);
		
		int i = 0;
		
		for (Integer key_k : this.getTuples().keySet()) {
			if (value == this.getTuples().get(key_k).get(attribute))
				vector.fastSet(i);
			i++;
		}
		
		if (vector.cardinality() == 1)
			return this.empty_av_vector;
		AttributeValueEvidenceVector  av_vector = new AttributeValueEvidenceVector(vector);

		return av_vector;
	}
	
	
	protected void InitializeTupleEvidenceVector_v1() {
		
		
		
		int size = this.tuples.size();
		TEV_v1 = new HashMap<Integer,TupleEvidenceVector_v1> ();
		
		TupleEvidenceVector_v1 tev = null;
		List<Integer> tuple = null;
		OpenBitSet bs;
		
		for (Integer key_k : this.getTuples().keySet()) {
			
			tev = new TupleEvidenceVector_v1(this.numAtts,size);
			tuple = this.getTuples().get(key_k);
			for (int i= 0; i<this.numAtts; i++) {
				
				bs = this.AVES.get(i).get(tuple.get(i)).toOpenBitSet().clone();
				tev.add(i, bs);
				
			}
			
			this.TEV_v1.put(key_k, tev);

		}
			
	}
	
	
	protected void displayTEV_v1(Integer tuple_id) {
		
		TupleEvidenceVector_v1 tev = this.TEV_v1.get(tuple_id);
		
		for (int i = 0; i< this.numAtts; i++)
			
			System.out.println("att: "+i+ ", vector: "+BitSetUtils.toString(tev.get(i)));
		
	}
	
	protected void createIndex() {
		
		for (Integer id: this.tuples.keySet()) {
			List<Integer> t = this.tuples.get(id);
			for (int j=0; j< this.numAtts;j++) {
				if (indices.get(j).containsKey(t.get(j)))
					indices.get(j).get(t.get(j)).add(id); 
				else {
					List<Integer> si = new ArrayList<Integer>();
					si.add(id);
					indices.get(j).put(t.get(j), si);
				}
				
				this.att_values.get(j).add(t.get(j));
			}
		}	
			
		
	}
	
	protected void displayTEV_v1() {
		
		System.out.println("Display TEV_v1");
		int size = this.getTuples().size();
		
		for( Map.Entry<Integer, TupleEvidenceVector_v1> entry : this.TEV_v1.entrySet() ){
			System.out.println("Tuple: "+entry.getKey());
			for (int i = 0; i< this.numAtts; i++)
				System.out.println("att: "+i+ ", vector: "+BitSetUtils.toString(entry.getValue().get(i),size));
		}
		
	}
	
	protected void InitializeTupleEvidenceVector_v2() {
		
		int size = this.tuples.size();
		TEV_v2 = new HashMap<Integer,TupleEvidenceVector_v2> ();
		
		TupleEvidenceVector_v2 tev = null;
		List<Integer> tuple = null;
		OpenBitSet bs;
		
		for (Integer key_k : this.getTuples().keySet()) {
			
			tev = new TupleEvidenceVector_v2(this.numAtts,size);
			tuple = this.getTuples().get(key_k);
			for (int j = 0; j< size; j++) {
				bs = new OpenBitSet(this.numAtts);
				for (int i= 0; i<this.numAtts; i++) {
					if (this.AVES.get(i).get(tuple.get(i)).toOpenBitSet().get(j))				
						bs.fastSet(i);
				}
				tev.add(j, bs);
			}
			
			this.TEV_v2.put(key_k, tev);

		}
			
	}
	
	
	protected void collect_v1() {
		agreesets = new HashMap<OpenBitSet,Integer>();
		
		TupleEvidenceVector_v1 tev;
		int num_tuples = this.getTuples().size();
		
		for (Integer key_k : this.TEV_v1.keySet()) {
			//agreeSets.addAll(TupleEvidenceVector_v1.collect(tev,key_k,num_tuples));
			//agreesets.put(bitset, val);
			//tev = this.TEV_v1.get(key_k);
			TupleEvidenceVector_v1.collect(this.TEV_v1.get(key_k),key_k,num_tuples).forEach((k, v) -> agreesets.merge(k, v, (v1, v2) -> v1 + v2));
		}
		
		System.out.println("Number of agree sets is "+this.agreesets.size());
		
	}
	
	
	protected void collect_v2() {
		agreesets = new HashMap<OpenBitSet,Integer>();
		
		TupleEvidenceVector_v2 tev;
		int num_tuples = this.getTuples().size();
		
		for (Integer key_k : this.TEV_v2.keySet()) {
			//tev = this.TEV_v2.get(key_k);
			//agreeSets.addAll(TupleEvidenceVector_v2.collect(tev));
			TupleEvidenceVector_v2.collect(this.TEV_v2.get(key_k)).forEach((k, v) -> agreesets.merge(k, v, (v1, v2) -> v1 + v2));
			
			
		}
		
		System.out.println("Number of agree sets is "+this.agreesets.size());
		
	}
	
	
	protected void displayAgreeSet() {
		
		//System.out.println("AgreeSet "+i);
		//System.out.println(BitSetUtils.toString(this.agreesets.get(i),this.numAtts));
		for (OpenBitSet key : this.agreesets.keySet()) {
			System.out.println("agreeset");
			System.out.println(BitSetUtils.toString(key,this.numAtts));
			System.out.println("cardinality: "+this.agreesets.get(key));
		}
		
	}
	
	
	public void processRelation() {
		
		String exec_time = "";
		long startTime, endTime;
		
			
			startTime = System.nanoTime();
			this.initializeEvidenceSets();
			endTime = System.nanoTime();
			exec_time += (endTime - startTime)/1000000 +"," ;
			System.out.println("Time for constructing attribute value evidence vectorsfor base relation: "+(endTime - startTime)/1000000+"ms");
			
			//br.printAVES();
			
			
			startTime= System.nanoTime();
			this.InitializeTupleEvidenceVector_v1();	
			endTime = System.nanoTime();
			exec_time += (endTime - startTime)/1000000 +"," ;
			System.out.println("Time for constructing tuple evidence vectors: "+(endTime - startTime)/1000000+"ms");

			//br.displayTEV_v1();
			
			startTime= System.nanoTime();
			this.collect_v1();
			endTime = System.nanoTime();
			exec_time += (endTime - startTime)/1000000 ;
			System.out.println("Time for collecting agree-sets: "+(endTime - startTime)/1000000+"ms");
			
			
			//br.displayAgreeSet();
			
		
		
	}
	

	
	public void generateRelationEvidenceVector() {
		
		this.createPossibleBitest(this.numAtts);
		
		int size = this.tuples.size();
		
		
		

		
		List<Integer> tuple = null;
		int r;
		
		this.bitsets = new ArrayList<OpenBitSet>();
		
		
		int[][] rev_value = new int[this.getTuples().size()][this.getTuples().size()];
		int index;
		
		
		//System.out.println("Generate Matrix");
		
		
		for (Integer key_k : this.getTuples().keySet()) {
			
			r = key_k;
			
			tuple = this.getTuples().get(key_k);
			
			//System.out.println("key_k: "+key_k);
			
			for (int j = key_k; j < size; j++) {
				
				
				
				//System.out.print(", col:"+j);
				
				if (r==j) {
					//System.out.println(", cell: "+0);
					rev_value[r][j] = 0;
					continue;
				}
					
				
				OpenBitSet bs = new OpenBitSet(this.numAtts);
				
				for (int i= 0; i<this.numAtts; i++) {
					if (this.AVES.get(i).get(tuple.get(i)).toOpenBitSet().get(j))				
						bs.fastSet(i);					
					
				}
				
				//System.out.println("Tuple "+row+" with tuple "+j+" have the agree-set "+BitSetUtils.toString(bs,this.numAtts));
				
				
				
				index = this.possible_bitsets.indexOf(bs);

				//System.out.println(", cell: "+index);
				
				rev_value[r][j] = index;
				rev_value[j][r] = index;
				
				
			}
			
			
		}

		
		/*
		System.out.println("Bitset with id 0: "+BitSetUtils.toString(this.possible_bitsets.get(0),this.numAtts));
		System.out.println("Bitset with id 8: "+BitSetUtils.toString(this.possible_bitsets.get(8),this.numAtts));
		System.out.println("Bitset with id 10: "+BitSetUtils.toString(this.possible_bitsets.get(10),this.numAtts));
		*/
		
		rev = new MatrixEvidenceVector(rev_value);
		
	}
	
	/*
	public TableEvidenceVector select_for_join(List<Integer> w) {
		
		//System.out.println("Processing select for join");
		
		TableEvidenceVector rev_s = new TableEvidenceVector();
		rev_s.initializeElements(w.size());
		List<List<Integer>> value_s = rev_s.getValue();
		List<List<Integer>> value = this.rev.getValue();
		
		int index_fullbitset;
		OpenBitSet fullbitset = new OpenBitSet(this.numAtts);
		fullbitset.set(0,this.numAtts);
		
		
		if (!this.possible_bitsets.contains(fullbitset))
			this.possible_bitsets.add(fullbitset);
			
		index_fullbitset = this.possible_bitsets.indexOf(fullbitset);

		
		int row, col, val_i, val_j;
		
		for (int i =0; i< w.size(); i++) {
			val_i = w.get(i);
			for (int j = i+1; j< w.size() ; j++) {
				val_j = w.get(j);
				//System.out.println("val_i: "+val_i+", val_j: "+val_j);
				if (val_i == val_j)
					value_s.get(i).add(index_fullbitset);
				else {
					if (val_i < val_j) {
						row = val_i;
						col = val_j;
					} else
					{
						row = val_j;
						col = val_i;
					}
					value_s.get(i).add(value.get(row).get(col-row-1));
				}
			}
		}
		
		return rev_s;
	}
	
	*/

	/*
	public TableEvidenceVector select(List<Integer> w) {
		
		TableEvidenceVector rev_s = new TableEvidenceVector();
		rev_s.initializeElements(w.size());
		List<List<Integer>> value_s = rev_s.getValue();
		List<List<Integer>> value = this.rev.getValue();
		int index = 0;
		
		for (int row : w) {
			System.out.println("Treating row: "+row+" with index value of "+index);	
			
			
			for (int col : w.subList(1 + index, w.size())) {
				System.out.println("col - row - 1: "+(col-row-1));
				System.out.println("value.get(row).size() "+value.get(row).size());
				System.out.println("Still in row "+row);
				//value_s.get(index).add(value.get(row).get(col-1));	
				value_s.get(index).add(value.get(row).get(col-row-1));			

			}
			index++;
		}
		
		
		return rev_s;
		
	}
	*/

	 
	public void toy(List<Integer> w1, List<Integer> w2) {
		
		List<String> w = new ArrayList<String>();
		for (int i=0; i<w1.size(); i++)
			w.add(w1.get(i)+"-"+w2.get(i));
		
		System.out.println("First element of the cocatenated list is "+w.get(0));
		
		Map<String,Integer> res = new HashMap<String,Integer>();
		
		String key;
		for (int i=0; i<w1.size(); i++) {
			for (int j=i+1; j < w1.size(); j++) {
				key = w.get(i)+"-"+w.get(j);
				if (res.containsKey(key))
					res.compute(key, (k,v) -> v+1);
				else
					res.put(key,1);
			}
		}
		
		
	}
	
	public static void main(String[] args) {

		String file = "resources/PTC/queries/q_atom_molecule/atom_num.csv";
		String w_file1 = "resources/PTC/queries/q_atom_molecule/w_atom.csv";
		String w_file2 = "resources/PTC/queries/q_atom_molecule/w_molecule.csv";
		
		MatrixTable br = new MatrixTable(file);
		
		List<Integer> w1 = br.getWitnessList(w_file1);
		List<Integer> w2 = br.getWitnessList(w_file2);
		
		br.toy(w1, w2);
		
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
		String file = "resources/PTC/queries/q_atom_molecule/atom_num.csv";
		String w_file = "resources/PTC/queries/q_atom_molecule/w_atom.csv";
		int num_attrs = 4;
		
		long startTime, endTime;
		
		startTime= System.nanoTime();
		
		endTime = System.nanoTime();

		long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
		
		 //String file = "resources/datasets/example/example.csv";
		 //String file = "resources/datasets/example/example1.csv";
		 //String file = "resources/datasets/example/example_j_2.csv";
		 
		 //String file = "resources/datasets/Flight/num_flight.csv";
		 //String result_file = "results/flight.csv";
		 
		 
		
		 
		 //String file = "resources/datasets/Adult/num_adult.csv";
		 //String result_file = "results/num_adult.csv";
		
		 
		 //String file = "resources/tpch/orders_num.csv";
		
		 //String file = "resources/tpch/orders_num.csv";
		 
		 //String file = "resources/PTE/single_tables/pte_drug_num.csv";
		 
		 //String file = "resources/datasets/example/example2.csv";
		
		 
		 MatrixTable br = new MatrixTable(file);
		//br.displayBaseTable(10);
		 
		
		 
		startTime= System.nanoTime();
		br.initializeEvidenceSets();
		endTime = System.nanoTime();
		System.out.println("Time for constructing attribute value evidence vectors: "+(endTime - startTime)/1000000+"ms");
		
		//br.printAVES();
		
		/*
		startTime= System.nanoTime();
		br.InitializeTupleEvidenceVector_v1();	
		endTime = System.nanoTime();
		System.out.println("Time for constructing tuple evidence vectors: "+(endTime - startTime)/1000000+"ms");

		//br.displayTEV_v1();
		
		startTime= System.nanoTime();
		br.collect_v1();
		endTime = System.nanoTime();
		System.out.println("Time for collecting agree-sets: "+(endTime - startTime)/1000000+"ms");
		
		
		//br.displayAgreeSet();
		  	
		*/
		
		
		/*
		
		startTime= System.nanoTime();
		br.InitializeTupleEvidenceVector_v2();
		endTime = System.nanoTime();
		System.out.println("Time for constructing tuple evidence vectors: "+(endTime - startTime)/1000000+"ms");
		
		
		startTime= System.nanoTime();
		br.collect_v2();
		endTime = System.nanoTime();
		System.out.println("Time for collecting agree-sets: "+(endTime - startTime)/1000000+"ms");
		
		//br.displayAgreeS
		 */
		/*
		startTime= System.nanoTime();
		br.generateRelationEvidenceVector();
		endTime = System.nanoTime();
		System.out.println("Time for generating Relation Evidence Vector: "+(endTime - startTime)/1000000+"ms");
		
	
		System.out.println("Matrix");
		//br.rev.matrix.print(0,0);
		
		
		/*
		startTime= System.nanoTime();
		TableEvidenceVector.collect(br.rev.getValue(), br.possible_bitsets);
		endTime = System.nanoTime();
		System.out.println("Time for collection Relation Evidence Vector: "+(endTime - startTime)/1000000+"ms");

		*/
		List<Integer> w = new ArrayList<Integer>();
		//w.add(1);
		//w.add(1);
		//w.add(2);
		//w.add(3);
		/*		
		w.add(0);
		w.add(2);
		w.add(0);
		w.add(2);
		*/
		
		/*
		w = br.getWitnessList(w_file);
		
		OpenMapRealMatrix w1_matrix = br.createFirstWitnessMatrix(w);
		System.out.println("First Witness matrix");
		//w1_matrix.print(0,0);
		
		
		OpenMapRealMatrix w2_matrix = br.createSecondWitnessMatrix(w);
		System.out.println("Second Witness matrix");
		//w2_matrix.print(0,0);
		
		
		RealMatrix M = Array2DRowRealMatrix(w.size(),w.size());
		
		
		/*
		
		RealMatrix A = br.rev.matrix.multiply(w1_matrix);
		
		
		System.out.println("A matrix");
		//A.print(0,0);
		
		
		RealMatrix B = w2_matrix.multiply(A);
		System.out.println("B matrix");
		//B.print(0,0);
		
		/*
		TableEvidenceVector rev_s = br.select_for_join(w);

		System.out.println("The selection");
		TableEvidenceVector.print(rev_s,  br.numAtts, br.possible_bitsets);
		
		
		System.out.println("Collect");
		//Map<Integer,Integer> map = TableEvidenceVector.collect(br.rev.getValue(), br.possible_bitsets);
		Map<Integer,Integer> map = TableEvidenceVector.collect(rev_s.getValue(), br.possible_bitsets);
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
		
		/*
		System.out.println("Possible bit sets, element 0 "+BitSetUtils.toString(br.possible_bitsets.get(0),4));
		System.out.println("Possible bit sets, element 8 "+BitSetUtils.toString(br.possible_bitsets.get(8),4));
		System.out.println("Possible bit sets, element 9 "+BitSetUtils.toString(br.possible_bitsets.get(9),4));
		System.out.println("Possible bit sets, element 10 "+BitSetUtils.toString(br.possible_bitsets.get(10),4));
		System.out.println("Possible bit sets, element 12 "+BitSetUtils.toString(br.possible_bitsets.get(12),4));

		*/

	}

}
