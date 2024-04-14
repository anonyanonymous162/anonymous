package anonym.anonyms.anonym.utilities;

import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealMatrixFormat;


public class Toy {

    public static void main(String[] args) {
    	

    	
        int n = 3; // size of matrix
        double[][] values = {{1, 2, 3}, {2, 4, 5}, {3, 5, 6}}; // values of the matrix
        
        RealMatrix matrix  = new Array2DRowRealMatrix(3,3);
        matrix.setEntry(0, 0, 0);
        matrix.setEntry(0, 1, 2);
        matrix.setEntry(0, 2, 3);
        matrix.setEntry(1, 2, 4);
        
        Toy.printMatrix(matrix);


        RealMatrix symMatrix = matrix.add(matrix.transpose());
        
        Toy.printMatrix(symMatrix);
        
    }
    
    private static void printMatrix(RealMatrix matrix) {
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(matrix.getEntry(i, j) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
		
		

	

}
