/*
 * MatrixSort.java
 * Created on 29-Jul-2005
 *
 */
package alvahouse.eatool.gui.matrix;


/**
 * MatrixSort provides the interface for any sort objects that can sort
 * a MatrixTableModel.
 * 
 * @author rbp28668
 * Created on 29-Jul-2005
 */
public interface MatrixSort {

    /**
     * sortColumns should sort the colums of the matrix table model.
     * @param model is the table model to sort.
     * @return an array of integer with the new column order of the matrix.
     */
    int[] sortColumns(MatrixTableModel model);
    
    /**
     * sortRows should sort the rows of the matrix table model.
     * @param model is the table model to sort.
     * @return an array of integer with the new row order of the matrix.
     */
    int[] sortRows(MatrixTableModel model);
}
