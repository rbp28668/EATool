 /*
 * MatrixTableModel.java
 * Created on 25-Jul-2005
 *
 */
package alvahouse.eatool.gui.matrix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import alvahouse.eatool.repository.model.Entity;


/**
 * MatrixTableModel provides the model for a MatrixViewer.  This keeps track of the 
 * relationship matrix as well as the entities that form the rows and columns.
 * The TableModel also includes a level of indirection so the apparent order of the
 * rows and columns can be changed easily.
 * 
 * @author rbp28668
 * Created on 28-Jul-2005
 */
class MatrixTableModel extends AbstractTableModel{

	private Vector rowEntities;
	private Vector colEntities;
	private Map rowIndex = new HashMap(); // maps entity to its integer index
	private Map colIndex = new HashMap(); // maps entity to its integer index
	private int[] rowShuffle;
	private int[] colShuffle;
	private int rows;
	private int cols;
	private boolean data[][]; // [rows][cols]
	private List listeners = new LinkedList(); // of MatrixTableModelListener
	private boolean modified = false;
	
	/**
	 * Creates a new table model.
	 * @param rowList is a List of Entities that forms the rows of the matrix.
	 * @param colList is a List of Entities that forms the columns of the matrix.
	 */
	MatrixTableModel(List rowList, List colList){
	    
	    rows = rowList.size();
	    cols = colList.size();
	    
		rowEntities = new Vector(rows);
		colEntities = new Vector(cols);

		rowShuffle = new int[rows];
		colShuffle = new int[cols];
		
		int idx = 0;
		for(Iterator iter = rowList.iterator(); iter.hasNext();){
			Entity entity = (Entity)iter.next();
			rowEntities.add(idx,entity);
			rowIndex.put(entity,new Integer(idx));
			rowShuffle[idx] = idx;	// default 1:1
			++idx;
		}
		idx = 0;
		for(Iterator iter = colList.iterator(); iter.hasNext();){
			Entity entity = (Entity)iter.next();
			colEntities.add(idx,entity);
			colIndex.put(entity,new Integer(idx));
			colShuffle[idx] = idx; // default 1:1
			++idx;
		}
		
		data = new boolean[rows][cols];	
	}
	

	/**
	 * getRowEntity gets the entity corresponding to a given row.
	 * @param index is the integer index of the row.
	 * @return the corresponding entity.
	 */
	Entity getRowEntity(int index){
	    return (Entity) rowEntities.get(rowShuffle[index]);
	}

	/**
	 * getColumnEntity gets the entity corresponding to a given column.
	 * @param index is the integer index of the column.
	 * @return the corresponding entity.
	 */
	Entity getColumnEntity(int index){
	    return (Entity) colEntities.get(colShuffle[index]);
	}

	/**
	 * getUnshuffledRowEntity gets the entity corresponding to a given row ignoring
	 * any current row order.
	 * @param index is the integer index of the row.
	 * @return the corresponding entity.
	 */
	Entity getUnshuffledRowEntity(int index){
	    return (Entity) rowEntities.get(index);
	}

	/**
	 * getUnshuffledColumnEntity gets the entity corresponding to a given column ignoring
	 * andy current column order.
	 * @param index is the integer index of the column.
	 * @return the corresponding entity.
	 */
	Entity getUnshuffledColumnEntity(int index){
	    return (Entity) colEntities.get(index);
	}

	
	/**
	 * setColumnOrder sets a new order for displaying the columns.
	 * @param columnOrder is an array of integer indices.  This array will be
	 * used to map the order of the columns.  For example, if the array
	 * 0,2,1,3 is used, the display order will be 0, 2, 1, 3.
	 */
	void setColumnOrder(int[] columnOrder){
	    if(columnOrder == null) {
	        throw new NullPointerException("Can't set null column order");
	    }
	    if(columnOrder.length != cols){
	        throw new IllegalArgumentException("Invalid size of column order");
	    }
	    colShuffle = (int[])columnOrder.clone(); // shallow
	    
	    fireColumnOrderChanged();
	}


    /**
	 * setRowOrder sets a new order for displaying the rows.
	 * @param rowOrder is an array of integer indices.  This array will be
	 * used to map the order of the rows.  For example, if the array
	 * 0,2,1,3 is used, the display order will be 0, 2, 1, 3.
	 */
	void setRowOrder(int[] rowOrder){
	    if(rowOrder == null) {
	        throw new NullPointerException("Can't set null row order");
	    }
	    if(rowOrder.length != rows){
	        throw new IllegalArgumentException("Invalid size of row order");
	    }
	    rowShuffle = (int[])rowOrder.clone(); // shallow
	    
	    fireRowOrderChanged();

	}
	

    /**
	 * link links 2 entities together in the matrix.  Note that if the
	 * rows and columns are the same set of entities, this only does 
	 * a single direction
	 * @param start is the entity at the start of the relationship.
	 * @param finish is the entity at the end of the relationship.
	 */
	void link(Entity start, Entity finish){
		int row = getInternalRowIndex(start);
		int col = getInternalColumnIndex(finish);
		data[row][col] = true;
	}

	/**
	 * getInternalRowIndex gets the internal (unscrambled) index for the
	 * given entity.
	 * @param e is is the entity to look for.
	 * @return corresponding index.
	 */
	private int getInternalRowIndex(Entity e){
	    Integer i = (Integer)rowIndex.get(e);
	    if(i == null) {
	        throw new IllegalArgumentException("Entity does not match a valid row");
	    }
	    return i.intValue();
	}
	
	/**
	 * getInternalColumnIndex gets the internal (unscrambled) index for the
	 * given entity.
	 * @param e is is the entity to look for.
	 * @return corresponding index.
	 */
	private int getInternalColumnIndex(Entity e){
	    Integer i = (Integer)colIndex.get(e);
	    if(i == null) {
	        throw new IllegalArgumentException("Entity does not match a valid column");
	    }
	    return i.intValue();
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return cols;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return rows;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
	    int row = rowShuffle[rowIndex];
	    int col = colShuffle[columnIndex];
		return new Boolean(data[row][col]);
	}
	
	public boolean booleanValueAt(int rowIndex, int columnIndex) {
	    int row = rowShuffle[rowIndex];
	    int col = colShuffle[columnIndex];
		return data[row][col];
	}

	/**
	 * setValueAt sets a value at a given location in the index.
	 * @param linked is the value to set.
	 * @param rowIndex is the logical row index.
	 * @param columnIndex is the logical columnIndex.
	 */
	public void setValueAt(boolean linked, int rowIndex, int columnIndex) {
	    int row = rowShuffle[rowIndex];
	    int col = colShuffle[columnIndex];
		data[row][col] = linked;
		fireValueChanged(rowIndex, columnIndex, linked);
		//System.out.println("Row " + row + " Col " + col + " --> " + linked);
		//showMatrix(); // DEBUG
	}
	
	/**
	 * setValueAt sets a value at a given location in the index.
	 * @param linked is the value to set.
	 * @param rowIndex is the logical row index.
	 * @param columnIndex is the logical columnIndex.
	 */
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	    setValueAt(((Boolean)value).booleanValue(),rowIndex,columnIndex);
	}
	
//	private void showMatrix(){
//	    StringBuffer buff = new StringBuffer(cols + 2);
//	    for(int row = 0; row <rows; ++row){
//	        buff.delete(0,cols + 2);
//	        buff.append('|');
//	        for(int col = 0; col < cols; ++col){
//	    	    int rs = rowShuffle[row];
//	    	    int cs = colShuffle[col];
//	    		buff.append((data[row][col]) ? 'X' : ' ');
//	        }
//	        buff.append('|');
//	        System.out.println(buff);
//	    }
//	    System.out.println();
//	}

    /* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex){
		return Boolean.class;
	}
	
	/**
	 * isLinkedAt is similar to getValueAt except that it returns
	 * the raw (unshuffled) value and it returns
	 * boolean rather than Boolean objects.
	 * @param rowIndex is the row in the grid.
	 * @param columnIndex is the column in the grid.
	 * @return linkage at the given location.
	 */
	boolean isLinkedAt(int rowIndex, int columnIndex) {
	    return data[rowIndex][columnIndex];
	}
	
	/**
	 * getRow returns a row as a BitVector.
	 * @param rowIndex is the row to retrieve.
	 * @return the row as a BitVector
	 */
	BitVector getRow(int rowIndex){
	    BitVector vector = new BitVector(cols);
	    for(int i=0; i<cols; ++i){
	        vector.setBit(i, isLinkedAt(rowIndex,i));
	    }
	    return vector;
	}

	/**
	 * getColumn returns a column as a BitVector.
	 * @param columnIndex is the column to retrieve.
	 * @return the column as a BitVector
	 */
	BitVector getColumn(int columnIndex){
	    BitVector vector = new BitVector(rows);
	    for(int i=0; i<rows; ++i){
	        vector.setBit(i, isLinkedAt(i, columnIndex));
	    }
	    return vector;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int arg0, int arg1) {
		return true;
	}
	
	/**
	 * Adds a listener to be notified of any changes to the table model.
	 * @param listener
	 */
	public void addListener(MatrixTableModelListener listener){
	    listeners.add(listener);
	}
	
	/** removes a registered table model listener.
	 * @param listener
	 */
	public void removeListener(MatrixTableModelListener listener){
	    listeners.remove(listener);
	}
	
	/**
     * Tells any listeners that the column order has changed.
     */
    private void fireColumnOrderChanged() {
        for(Iterator iter = listeners.iterator(); iter.hasNext();){
            MatrixTableModelListener listener = (MatrixTableModelListener)iter.next();
            listener.columnOrderChanged();
        }
    }

	/**
     * Tells any listeners that the row order has changed.
     */
    private void fireRowOrderChanged() {
        for(Iterator iter = listeners.iterator(); iter.hasNext();){
            MatrixTableModelListener listener = (MatrixTableModelListener)iter.next();
            listener.rowOrderChanged();
        }
    }

	/**
	 * Tells any listeners that a value has changed in the model.
     * @param rowIndex
     * @param columnIndex
     * @param linked
     */
    private void fireValueChanged(int rowIndex, int columnIndex, boolean linked) {
        for(Iterator iter = listeners.iterator(); iter.hasNext();){
            MatrixTableModelListener listener = (MatrixTableModelListener)iter.next();
            listener.valueChanged(rowIndex, columnIndex, linked);
        }
        modified = true;
    }


    /**
     * @return Returns the modified.
     */
    public boolean isModified() {
        return modified;
    }
    /**
     * @param modified The modified to set.
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }
}