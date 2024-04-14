package anonym.anonyms.anonym.algo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.lucene.util.OpenBitSet;

import au.com.bytecode.opencsv.CSVReader;
import anonym.anonyms.anonym.utilities.AttributeValueEvidenceVector;
import anonym.anonyms.anonym.utilities.BitSetUtils;
import anonym.anonyms.anonym.utilities.TupleEvidenceVector_v1;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Query {
	
	String query_results;
	List<String> input_relations;
	List<List<Integer>> query_attrs;
	List<String> witnesses;
	HashMap<OpenBitSet,Integer> agreesets;
	
	HashMap<Integer,TupleEvidenceVector_v1> TEV_v1 = null;
	
    protected Map<Integer,List<Integer>> tuples = new HashMap<Integer,List<Integer>>();
    List<Integer> tuple_ids = new ArrayList<Integer>();
    protected int tuple_ID =0;
    ObjectArrayList<HashMap<Integer,AttributeValueEvidenceVector>> AVES = null;
	
	int num_attrs;
	int num_results;
	
	ObjectArrayList<HashMap<Integer,Set<Integer>>> indices; // Used for indexing the tuples by attribute values

	
	List<List<Integer>> witness_list;
	
	List<Baserelation_AS> bases_relations = new ArrayList<Baserelation_AS>();
	
	Query (List<String> _input_relations, List<List<Integer>> _query_attrs, List<String> _witnesses, String _query_results){
		
		this.input_relations =  _input_relations;
		this.query_attrs = _query_attrs;
		this.witnesses = _witnesses;
		this.query_results = _query_results;
		String[] values = null;	
		this.AVES = new ObjectArrayList<HashMap<Integer,AttributeValueEvidenceVector>>();
		
		
		CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader(query_results));
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
		
		
	}
	
	public void processBaseRelations() {
		
		String exec_time = "";
		long startTime, endTime;
		
		for (String relation: this.input_relations) {
			
			Baserelation_AS br = new Baserelation_AS(relation,null);
			
			this.bases_relations.add(br);
			
			startTime = System.nanoTime();
			br.initializeEvidenceSets();
			endTime = System.nanoTime();
			exec_time += (endTime - startTime)/1000000 +"," ;
			System.out.println("Time for constructing attribute value evidence vectorsfor base relation: "+(endTime - startTime)/1000000+"ms");
			
			//br.printAVES();
			
			
			startTime= System.nanoTime();
			br.InitializeTupleEvidenceVector_v1();	
			endTime = System.nanoTime();
			exec_time += (endTime - startTime)/1000000 +"," ;
			System.out.println("Time for constructing tuple evidence vectors: "+(endTime - startTime)/1000000+"ms");

			//br.displayTEV_v1();
			
			startTime= System.nanoTime();
			br.collect_v1();
			endTime = System.nanoTime();
			exec_time += (endTime - startTime)/1000000 ;
			System.out.println("Time for collecting agree-sets: "+(endTime - startTime)/1000000+"ms");
			
			
			//br.displayAgreeSet();
			
		}
		
	}
	
	public void getWitnessList() {
		
		CSVReader csvReader;
		ArrayList<Integer> list;
		String[] values = null;	
		
		
		this.witness_list = new ArrayList<List<Integer>>();
		
		for (String witness_file: this.witnesses) {
			
			try {
				list = new ArrayList<Integer>();
				csvReader = new CSVReader(new FileReader(witness_file));
			
				while ((values = csvReader.readNext()) != null) {
					list.add(new Integer(values[0]));
				}
			
				this.witness_list.add(list);
				/*
				System.out.println("Witness list: ");
				for (Integer e: list)
					System.out.print(e+" ");
				System.out.println();
				*/
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.num_results = this.witness_list.get(0).size();
			
		}
		
		
	}
	
	public void determineNumAttrs() {
		
		this.num_attrs = 0;
		int e = 0;
		for (List<Integer> rel_attrs: this.query_attrs) {
			if (rel_attrs == null)
				this.num_attrs += this.bases_relations.get(e).numAtts;
			else
				this.num_attrs += rel_attrs.size();
			e++;
		}				
		
	}
	
	

	
public void InitializeAVEvidenceVectors() {
	
	int e = 0;
	int pos;
	int off_set = 0;
	List<Integer> w;
	List<Integer> tuple;
	OpenBitSet bs; 
		
	AVES = new ObjectArrayList<HashMap<Integer,AttributeValueEvidenceVector>>();
	for (int i=0; i<this.num_attrs; i++) 
		AVES.add(new HashMap<Integer,AttributeValueEvidenceVector>());
	
	
	for (Baserelation_AS br : this.bases_relations) {
		
		w = this.witness_list.get(e);
		
		if (this.query_attrs.get(e) == null) {
			for (int i=0; i< this.num_results; i++) {
				tuple = this.getTuples().get(i);
				for (int j=0; j < br.numAtts; j++) {
					
					if (!AVES.get(j+off_set).containsKey(tuple.get(j+off_set))) {
						bs = this.computeAVE(this.num_results,br.AVES.get(j).get(tuple.get(j+off_set)),w);
						AVES.get(j+off_set).put(tuple.get(j+off_set), new AttributeValueEvidenceVector(bs));
						
					}
					
				}
				
			}
			off_set += br.numAtts;
		}
			else {
				
				for (int i=0; i< this.num_results; i++) {
					tuple = this.getTuples().get(i);
					
					for (int j=0; j < this.query_attrs.get(e).size(); j++) {
						if (!AVES.get(j+off_set).containsKey(tuple.get(j+off_set))) {
							bs = this.computeAVE(this.num_results,br.AVES.get(this.query_attrs.get(e).get(j)).get(tuple.get(j+off_set)),w);
							AVES.get(j+off_set).put(tuple.get(j+off_set), new AttributeValueEvidenceVector(bs));
							
						}
					}
					
				}
			
				off_set += this.query_attrs.get(e).size();
				
			}
	}
	
				
	}
	
/*
public void InitializeAVEvidenceVectors_v1() {
	
	int e = 0;
	int pos;
	int off_set = 0;
	List<Integer> w;
	List<Integer> tuple;
	OpenBitSet bs; 
		
	int num_results;
	AVES = new ObjectArrayList<HashMap<Integer,AttributeValueEvidenceVector>>();
	for (int i=0; i<this.num_attrs; i++) 
		AVES.add(new HashMap<Integer,AttributeValueEvidenceVector>());
	
	num_results = this.num_results;
	for (Baserelation_AS br : this.bases_relations) {
		
		w = this.witness_list.get(e);
		
		if (this.query_attrs.get(e) == null) {
			
			
			
			ExecutorService executor = Executors.newFixedThreadPool(this.num_results);
			
			for (int i=0; i< this.num_results; i++) {
				tuple = this.getTuples().get(i);
				executor.submit(new Runnable() {
					
					public void run() {
				
				for (int j=0; j < br.numAtts; j++) {
					
					if (!AVES.get(j+off_set).containsKey(tuple.get(j+off_set))) {
						OpenBitSet bs = Query.computeAV(num_results,br.AVES.get(j).get(tuple.get(j+off_set)),w);
						AVES.get(j+off_set).put(tuple.get(j+off_set), new AttributeValueEvidenceVector(bs));
						
					}
					
				}
					}
				
			}); 
			
		} off_set += br.numAtts; }
			
			else {
				
				for (int i=0; i< this.num_results; i++) {
					tuple = this.getTuples().get(i);
					
					for (int j=0; j < this.query_attrs.get(e).size(); j++) {
						if (!AVES.get(j+off_set).containsKey(tuple.get(j+off_set))) {
							bs = this.computeAVE(this.num_results,br.AVES.get(this.query_attrs.get(e).get(j)).get(tuple.get(j+off_set)),w);
							AVES.get(j+off_set).put(tuple.get(j+off_set), new AttributeValueEvidenceVector(bs));
							
						}
					}
					
				}
			
				off_set += this.query_attrs.get(e).size();
				
			}
	}
	
				
	}
*/
	public OpenBitSet computeAVE(int num_results, AttributeValueEvidenceVector br_av, List<Integer> w) {
		
		OpenBitSet bs = new OpenBitSet(num_results);
		OpenBitSet br_bs = br_av.toOpenBitSet();
		
		for (int i= 0; i< w.size(); i++) {
			if (br_bs.get(w.get(i)))
				bs.set(i);
		}
		
		return bs;
		
	}
	
	public static OpenBitSet computeAV(int num_results, AttributeValueEvidenceVector br_av, List<Integer> w) {
		
		OpenBitSet bs = new OpenBitSet(num_results);
		OpenBitSet br_bs = br_av.toOpenBitSet();
		
		for (int i= 0; i< w.size(); i++) {
			if (br_bs.get(w.get(i)))
				bs.set(i);
		}
		
		return bs;
		
	}
	
	public int addTuple(List<Integer> t) {
		
		int id = this.generateID();
		this.tuples.put(id, t);
		
		for (int j=0; j< this.num_attrs;j++) {
			if (indices.get(j).containsKey(t.get(j)))
				indices.get(j).get(t.get(j)).add(id); 
			else {
				Set<Integer> si = new HashSet<Integer>();
				si.add(id);
				indices.get(j).put(t.get(j), si);
			}
		}
		return id;
		
	}
	
    public int generateID() {
    	this.tuple_ids.add(tuple_ID);
    	return this.tuple_ID++;
    }

    
	public void displayQueryResults() {
		
		System.out.println("Query results ");
		
		for (int i = 0; i< this.tuples.size(); i++) {
			List<Integer> tuple = this.getTuples().get(i);
			for (int j=0 ; j< this.num_attrs; j++)
				System.out.print(tuple.get(j));
			System.out.println();
		}
		
	}
	
	public Map<Integer, List<Integer>> getTuples() {
		return tuples;
	}
	
	public void displayAgreesets() {
		
		System.out.println("Query agreesets");
		for (OpenBitSet ag : this.agreesets.keySet())
			System.out.println(BitSetUtils.toString(ag,this.num_attrs));
	}

	public void displayAVEV() {
		
		System.out.println("Display Attribute Evidence Vectors");
		
		for (int i = 0; i< this.AVES.size(); i++) {
			System.out.println("Attribute: "+i);
			
			for (Integer avev: this.AVES.get(i).keySet()) {
				System.out.print("Vector for value: "+avev+"  ");
				System.out.println(BitSetUtils.toString(this.AVES.get(i).get(avev).toOpenBitSet(),this.num_results));
				
			}
			
			
		}
		
	}
	
	protected void InitializeTupleEvidenceVector() {
		
		
		
		int size = this.tuples.size();
		TEV_v1 = new HashMap<Integer,TupleEvidenceVector_v1> ();
		
		TupleEvidenceVector_v1 tev = null;
		List<Integer> tuple = null;
		OpenBitSet bs;
		
		for (Integer key_k : this.getTuples().keySet()) {
			
			tev = new TupleEvidenceVector_v1(this.num_attrs,size);
			tuple = this.getTuples().get(key_k);
			for (int i= 0; i<this.num_attrs; i++) {
				
				bs = this.AVES.get(i).get(tuple.get(i)).toOpenBitSet().clone();
				tev.add(i, bs);
				
			}
			
			this.TEV_v1.put(key_k, tev);

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
	
	public static void main(String[] args) {
		
		
		long startTime, endTime;
		
		 //String file = "resources/datasets/Adult/num_adult.csv";
		 //String result_file = "results/num_adult.csv";
		 
		 //String file = "resources/datasets/Flight/num_flight.csv";
		 //String result_file = "results/flight.csv";
		
		/*
		String relation_example = "resources/datasets/example/example.csv";
		String witness_example = "resources/datasets/example/q_e_1.csv";
		String result_example = "results/example.csv";
		String query_results = "resources/datasets/example/results.csv";
		*/
		
		String relation_example = "resources/datasets/Flight/num_flight.csv";
		String witness_example = "resources/datasets/Flight/q_f_1.csv";
		String result_example = "results/flight.csv";
		String query_results = "resources/datasets/Flight/results.csv";
		
		 
		
		//Preparing query inputs
		List<String> input_relations =  new ArrayList<String>();
		List<List<Integer>> query_attrs = new ArrayList<List<Integer>>();
		List<String> witnesses = new ArrayList<String>();
		
		input_relations.add(relation_example);
		query_attrs.add(null);		
		witnesses.add(witness_example);
		
		Query query = new Query(input_relations,query_attrs,witnesses,query_results);
		
		startTime= System.nanoTime();
		query.processBaseRelations();
		endTime= System.nanoTime();
		System.out.println("Time for processing base relations: "+(endTime - startTime)/1000000+"ms");
		
		query.getWitnessList();
		
		query.determineNumAttrs();
		
		System.out.println("Number of attributes: "+query.num_attrs);
		System.out.println("Number of result tuples: "+query.num_results);
		
		//query.displayQueryResults();
		
		startTime= System.nanoTime();
		query.InitializeAVEvidenceVectors();
		endTime= System.nanoTime();
		System.out.println("Time for computing attribute value evidence vectors for thr query: "+(endTime - startTime)/1000000+"ms");

		
		//query.displayAVEV();
		
		startTime= System.nanoTime();
		query.InitializeTupleEvidenceVector();
		endTime= System.nanoTime();
		System.out.println("Time for initializing tuple evidence vectors for the query: "+(endTime - startTime)/1000000+"ms");

		
		//query.displayAgreesets();
		
		startTime= System.nanoTime();
		query.collect_v1();
		endTime= System.nanoTime();
		System.out.println("Time for agree-sets for the query: "+(endTime - startTime)/1000000+"ms");

		
		//query.displayAgreesets();

		System.out.println("Processing ditrectly query results");
		String query_results_with_headers = "resources/datasets/Flight/results_with_headers.csv";
		Baserelation_AS query_flight = new Baserelation_AS(query_results_with_headers,null);
		startTime= System.nanoTime();
		query_flight.processRelation();
		endTime= System.nanoTime();
		System.out.println("Time for agree-sets for the query results directly: "+(endTime - startTime)/1000000+"ms");

		
	}

}
