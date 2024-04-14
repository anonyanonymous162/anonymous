package anonym.anonyms.anonym.algo;

import anonym.anonyms.anonym.utilities.RelationEvidenceVector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.util.OpenBitSet;

import au.com.bytecode.opencsv.CSVReader;

public class Join2Query {
	
	
	
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
	
	public static List<List<OpenBitSet>> Run(RelationEvidenceVector rev, List<Integer> witness_list) {
		
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
		
		String relation_1 = "resources/PTE/single_tables/pte_active_num.csv";
		String witness_1 = "resources/PTE/queries/q_active_drug/w.csv";
		//String result_example = "results/flight.csv";
		//String query_results = "resources/datasets/Flight/q_f_2_results.csv";
		String query_results_wh = "resources/PTE/queries/q_active_drug/results_num.csv";
		
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
		List<Integer> witness_list = Join2Query.getWitnessList(witness_1);
		endTime = System.nanoTime();
		System.out.println("Getting witness list: "+(endTime - startTime)/1000000+"ms");

		
		startTime= System.nanoTime();
		List<List<OpenBitSet>> q_r_v_c = Join2Query.Run_clone(br1.rev, witness_list);
		endTime = System.nanoTime();
		System.out.println("Generating the Query (clone) Result Evidence Vector: "+(endTime - startTime)/1000000+"ms");

		
		startTime= System.nanoTime();
		List<List<OpenBitSet>> q_r_v = Join2Query.Run(br1.rev, witness_list);
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
	

		
		//RelationEvidenceVector.print(q_r_v, br.numAtts);
		
		

	}

}
