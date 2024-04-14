package anonym.anonyms.anonym.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class convertCSV {

	public static void main(String[] args) throws IOException {
		
		String in_file = "Resources/PTC/queries/q_atom_molecule/ptc_atom_molecule.csv";
		String out_file =  "Resources/PTC/queries/q_atom_molecule/ptc_atom_molecule_num.csv";

		//String in_file = "resources/tpch_001/queries/q2/sample/results.csv";
		//String out_file =  "resources/tpch_001/queries/q2/sample/results_num.csv";
		
		//String in_file = "resources/tpch_001/queries/q3/tpch_q3_without_projection.csv";
		//String out_file = "resources/tpch_001/queries/q3/tpch_q3_without_projection_num.csv";
		//String in_file = "resources/protein/results.csv";
		//String out_file = "resources/protein/results_num.csv";
	    // Read the CSV file
	    BufferedReader reader = new BufferedReader(new FileReader(in_file));

	    // Create a map to associate cell contents with integer values
	    Map<String, Integer> cellToIntMap = new HashMap<>();
	    int nextInt = 1; // Start the integer values at 1

	    // Read the file line by line
	    String line;
	    
	    boolean isHeader = true;
	    
	    while ((line = reader.readLine()) != null) {
	     
	    if (isHeader) {	
	    	isHeader = false;
	    }
	    else {
	    // Split the line into cells
	      //line = line.replace(",","");	
	     //System.out.println("line: "+line);	
	      String[] cells = line.split(",");
	      if (cells.length > 33) {
	    	  System.out.println("line: "+line);
	      	  System.out.println("cells");
	      	  for (String c: cells)
	      		  System.out.println(c);
	      	  break;
	      }
	      //System.out.println("cells: "+Arrays.toString(cells));	

	      // Iterate through the cells and add them to the map if they don't already exist
	      for (String cell : cells) {
	        if (!cellToIntMap.containsKey(cell)) {
	          cellToIntMap.put(cell, nextInt);
	          nextInt++;
	        }
	      }
	    }
	    }
	    reader.close();

	    // Write the modified CSV file
	    BufferedWriter writer = new BufferedWriter(new FileWriter(out_file));

	    isHeader = true;
	    
	    // Read the file again and replace the cell contents with integer values
	    reader = new BufferedReader(new FileReader(in_file));
	    while ((line = reader.readLine()) != null) {
		    if (isHeader) {	
		    	writer.write(line);
		    	writer.newLine();
		    	isHeader = false;
		    }	
		    else {	
		    	//line = line.replace(",","");
		    	//String[] cells = line.replace(","," ").split(",");
		    	String[] cells = line.split(",");
		    	
		    	/*
		    	if (cells.length > 4) {
		    		System.out.println(Arrays.toString(cells));
		    		System.out.println("The 5th element is "+cells[4]);
		    	}
		    	*/
		    	for (int i = 0; i < cells.length; i++) {
		    		cells[i] = String.valueOf(cellToIntMap.get(cells[i]));
		    	}
		    	writer.write(String.join(",", cells));
		    	writer.newLine();
		    }
	    }
	    reader.close();
	    writer.close();
	  }
	
}
