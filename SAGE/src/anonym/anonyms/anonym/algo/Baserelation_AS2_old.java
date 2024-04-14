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
import anonym.anonyms.anonym.utilities.RelationEvidenceVector;
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

//import org.mp.naumann.algorithms.fd.utils.BitSetUtils;
//import org.mp.naumann.algorithms.fd.utils.ValueComparator;
import org.apache.lucene.util.OpenBitSet;

// @author Khalid Belhajjame

 

public class Baserelation_AS2_old {
	 
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
    //protected ObjectArrayList<ColumnIdentifier> columnIdentifiers;
	//int op_ID;
	
	List<Set<Integer>> att_values;
 
    protected Map<Integer,List<Integer>> tuples = new HashMap<Integer,List<Integer>>();
    List<Integer> tuple_ids = new ArrayList<Integer>();
    protected int tuple_ID =0;
	private String tableName;
	private OpenBitSet emptybitset;
	
	protected RelationEvidenceVector rev;
	
	ObjectArrayList<HashMap<Integer,AttributeValueEvidenceVector>> AVES = null;
	
	HashMap<Integer,TupleEvidenceVector_v1> TEV_v1 = null;
	HashMap<Integer,TupleEvidenceVector_v2> TEV_v2= null;
	
	ObjectArrayList<HashMap<Integer,Set<Integer>>> indices; // Used for indexing the tuples by attribute values

	Map<OpenBitSet,Integer> agreesets;
	 
	Baserelation_AS2_old(String file, String result_file){
		
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
		
		indices = new ObjectArrayList<HashMap<Integer,Set<Integer>>>();
		HashMap<Integer,Set<Integer>> tmp = new HashMap<Integer,Set<Integer>>();
		for (int i=0; i< this.numAtts;i++)
			indices.add(new HashMap<Integer,Set<Integer>>()); 
		
	}
	
	public int addTuple(List<Integer> t) {
		
		int id = this.generateID();
		this.tuples.put(id, t);
		
		for (int j=0; j< this.numAtts;j++) {
			if (indices.get(j).containsKey(t.get(j)))
				indices.get(j).get(t.get(j)).add(id); 
			else {
				Set<Integer> si = new HashSet<Integer>();
				si.add(id);
				indices.get(j).put(t.get(j), si);
			}
			
			this.att_values.get(j).add(t.get(j));
		}
		return id;
		
	}
	
    public int generateID() {
    	this.tuple_ids.add(tuple_ID);
    	return this.tuple_ID++;
    }


    
	public void initializeEvidenceSets() {
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
	
	protected AttributeValueEvidenceVector initializeAttributeValueVector(Integer attribute, Integer value) {
		
		int size = this.getTuples().size();
		OpenBitSet vector = new OpenBitSet(size);
		
		int i = 0;
		
		for (Integer key_k : this.getTuples().keySet()) {
			if (value == this.getTuples().get(key_k).get(attribute))
				vector.set(i);
			i++;
		}
				
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
						bs.set(i);
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
		
		int size = this.tuples.size();
		rev = new RelationEvidenceVector();
		rev.initializeElements(size);
		OpenBitSet bs;
		List<Integer> tuple = null;
		int row;
		
		
		List<List<OpenBitSet>> rev_value = rev.getValue();

		
		for (Integer key_k : this.getTuples().keySet()) {
			
			row = key_k;
			tuple = this.getTuples().get(key_k);
			
			for (int j = row+1; j < size; j++) {
				bs = new OpenBitSet(this.numAtts);
				for (int i= 0; i<this.numAtts; i++) {
					if (this.AVES.get(i).get(tuple.get(i)).toOpenBitSet().get(j))				
						bs.set(i);
				}
				
				//System.out.println("Tuple "+row+" with tuple "+j+" have the agree-set "+BitSetUtils.toString(bs,this.numAtts));
				
				rev_value.get(j).add(bs);
				
			}
			
		}
		
	}

	public static void main(String[] args) {

		
		long startTime, endTime;
		
		startTime= System.nanoTime();
		
		endTime = System.nanoTime();

		long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
		
		 String file = "resources/datasets/example/example.csv";
		 String result_file = "results/example.csv";
		 
		 //String file = "resources/datasets/Flight/num_flight.csv";
		 //String result_file = "results/flight.csv";
		 
		 
		
		 
		 //String file = "resources/datasets/Adult/num_adult.csv";
		 //String result_file = "results/num_adult.csv";
		
		
		 
		 Baserelation_AS2_old br = new Baserelation_AS2_old(file,result_file);
		//br.displayBaseTable(10);
		 
		 System.out.println("Number of different values for the first attribute is "+br.att_values.get(0).size());
		 System.out.println("Number of different values for the second attribute is "+br.att_values.get(1).size());
		 System.out.println("Number of different values for the third attribute is "+br.att_values.get(2).size());
		 System.out.println("Number of different values for the fourth attribute is "+br.att_values.get(3).size());
		 
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
		
		startTime= System.nanoTime();
		br.generateRelationEvidenceVector();
		endTime = System.nanoTime();
		System.out.println("Time for generating Relation Evidence Vector: "+(endTime - startTime)/1000000+"ms");
		
		RelationEvidenceVector.print(br.rev,  br.numAtts);
	
		  
		

	}

}
