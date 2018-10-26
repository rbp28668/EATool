/*
 * MatrixTableModelListener.java
 * Project: EATool
 * Created on 14-Sep-2005
 *
 */
package alvahouse.eatool.gui.matrix;

/**
 * MatrixTableModelListener receives change events when a MatrixTableModel changes.
 * 
 * @author rbp28668
 */
public interface MatrixTableModelListener {

	/**
     * 
     */
    public void columnOrderChanged() ;

	/**
     * 
     */
    public void rowOrderChanged() ;

	/**
     * @param rowIndex
     * @param columnIndex
     * @param linked
     */
    public void valueChanged(int rowIndex, int columnIndex, boolean linked);

}
