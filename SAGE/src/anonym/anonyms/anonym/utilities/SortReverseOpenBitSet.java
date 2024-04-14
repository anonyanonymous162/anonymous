package anonym.anonyms.anonym.utilities;

import java.util.Comparator;

import org.apache.lucene.util.OpenBitSet;

public class SortReverseOpenBitSet implements Comparator<OpenBitSet> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(OpenBitSet o1, OpenBitSet o2) 
    { 
    	return (int)(o2.cardinality() - o1.cardinality());
    } 
} 