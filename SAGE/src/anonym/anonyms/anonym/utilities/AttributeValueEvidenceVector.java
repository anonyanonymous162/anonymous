package anonym.anonyms.anonym.utilities;

import org.apache.lucene.util.OpenBitSet;

public class AttributeValueEvidenceVector {
	
	int size;
	OpenBitSet vector;
	
	
	public AttributeValueEvidenceVector(OpenBitSet vector){
		this.size = vector.length();
		this.vector = vector;
	}
	
	public OpenBitSet toOpenBitSet() {
		return this.vector;
	}
	
}
