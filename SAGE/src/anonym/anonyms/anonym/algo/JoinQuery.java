package anonym.anonyms.anonym.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.lucene.util.OpenBitSet;

public class JoinQuery {
	

	long startTime, endTime;
	
	  public void concat(List<OpenBitSet> list1, List<OpenBitSet> list2) {
	    
	    
		startTime= System.nanoTime();
	    // Create a new array list to hold the merged lists
	    ArrayList<OpenBitSet> mergedList = new ArrayList<OpenBitSet>();
	    
	    // Iterate over the elements of the lists
	    for (int i = 0; i < list1.size(); i++) {
	      // Concatenate the elements at the same position in the two lists
	      OpenBitSet bs = list1.get(i);
	      bs.union(list2.get(i));
	      
	      // Add the concatenated element to the merged list
	      mergedList.add(bs);
	    }
		endTime = System.nanoTime();
		System.out.println("Processing time without parallelzation "+(endTime - startTime)/1000000+"ms");

	    
	    // Print the merged list
	    //System.out.println(mergedList); // Output: [appleorange, bananagrape, cherrylemon]
	  
	    
	}

	  
	  public void concat_n(List<OpenBitSet> list1, List<OpenBitSet> list2) {
		    
		    
		startTime= System.nanoTime();
	    // Create a new array list to hold the merged lists
	    ArrayList<OpenBitSet> mergedList = new ArrayList<OpenBitSet>();
	    
	    // Iterate over the elements of the lists
	    for (int i = 0; i < list1.size(); i++) {
	      // Concatenate the elements at the same position in the two lists
	      OpenBitSet bs = list1.get(i).clone();
	      bs.union(list2.get(i));
	      
	      // Add the concatenated element to the merged list
	      mergedList.add(bs);
	    }
		endTime = System.nanoTime();
		System.out.println("Processing time without parallelzation "+(endTime - startTime)/1000000+"ms");

	    
	    // Print the merged list
	    //System.out.println(mergedList); // Output: [appleorange, bananagrape, cherrylemon]
	  
	    
	}
	  
	public void concat_parallel(List<OpenBitSet> list1, List<OpenBitSet> list2) {
		
		    
			startTime= System.nanoTime();
			
		    // Create a new array list to hold the merged lists
		    ArrayList<OpenBitSet> mergedList = new ArrayList<OpenBitSet>();
		    
		    // Iterate over the elements of the lists in parallel
		    IntStream.range(0, list1.size()).parallel().forEach(i -> {
		      // Concatenate the elements at the same position in the two lists
		      
		    	OpenBitSet bs = list1.get(i);
		    	bs.union(list2.get(i));
		      
		    	
		    	synchronized (mergedList) {
		    		// Add the concatenated element to the merged list
		    		mergedList.add(bs);
		    	}
		    });
		    
			endTime = System.nanoTime();
			System.out.println("Processing time with parallelzation "+(endTime - startTime)/1000000+"ms");

			
		    // Print the merged list
		    //System.out.println(mergedList); // Output: [appleorange, bananagrape, cherrylemon]
		  }
		
	public void concat_parallel_n(List<OpenBitSet> list1, List<OpenBitSet> list2) {
		
	    
		startTime= System.nanoTime();
		
	    // Create a new array list to hold the merged lists
	    ArrayList<OpenBitSet> mergedList = new ArrayList<OpenBitSet>();
	    
	    // Iterate over the elements of the lists in parallel
	    IntStream.range(0, list1.size()).parallel().forEach(i -> {
	      // Concatenate the elements at the same position in the two lists
	      
	    	OpenBitSet bs = list1.get(i).clone();
	    	bs.union(list2.get(i));
	      
	    	
	    	synchronized (mergedList) {
	    		// Add the concatenated element to the merged list
	    		mergedList.add(bs);
	    	}
	    });
	    
		endTime = System.nanoTime();
		System.out.println("Processing time with parallelzation "+(endTime - startTime)/1000000+"ms");

		
	    // Print the merged list
	    //System.out.println(mergedList); // Output: [appleorange, bananagrape, cherrylemon]
	  }

	  public ArrayList<OpenBitSet> createBitSetList(int num, int bs_size) {
		    // Create a new array list
		    ArrayList<OpenBitSet> list = new ArrayList<>();
		    
		    // Create a random number generator
		    Random random = new Random();
		    
		    // Generate 1000 randomly created OpenBitSets of size 5
		    for (int i = 0; i < num; i++) {
		      OpenBitSet bitSet = new OpenBitSet(bs_size);
		      for (int j = 0; j < bs_size; j++) {
		        if (random.nextBoolean()) {
		          bitSet.set(j);
		        }
		      }
		      list.add(bitSet);
		    }
		    
		    // Return the array list
		    return list;
		  }

	

	public static void main(String[] args) {
		
		
		JoinQuery q = new JoinQuery();
		
		int num = 300000;
		int bs_size = 10; 
		
		List<OpenBitSet> list1 = q.createBitSetList(num, bs_size);
		List<OpenBitSet> list2 = q.createBitSetList(num, bs_size);

		
		System.out.println("Start traement");
		q.concat_n(list1, list2);
		q.concat(list1, list2);	
		
		//q.concat_parallel_n(list1, list2);


	}

}
