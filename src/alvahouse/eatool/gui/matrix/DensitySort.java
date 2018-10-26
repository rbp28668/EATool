/*
 * DensitySort.java sorts the table by density - the entries with the most 
 * connections are at the start of the array.
 * Created on 25-Jul-2005
 *
 */
package alvahouse.eatool.gui.matrix;

import java.util.Arrays;

/**
 * DensitySort sorts the table by density - the entries with the most 
 * connections are at the start of the array.
 * 
 * @author rbp28668
 * Created on 25-Jul-2005
 */
public class DensitySort implements MatrixSort {

    /**
     * 
     */
    public DensitySort() {
        super();
    }

    public int[] sortColumns(MatrixTableModel model){
        int columns = model.getColumnCount();
    
        Entry[] entries = new Entry[columns];
        for(int i=0; i<columns; ++i) {
            BitVector bits = model.getColumn(i);
            Entry entry = new Entry(i, bits.getSetCount());
            entries[i] = entry;
       }
        
        int[] result = sortOrder(columns, entries);
        
        return result;
        
    }

    public int[] sortRows(MatrixTableModel model){
        int rows = model.getRowCount();
        
        Entry[] entries = new Entry[rows];
        for(int i=0; i<rows; ++i) {
            BitVector bits = model.getRow(i);
            Entry entry = new Entry(i, bits.getSetCount());
            entries[i] = entry;
        }
        
        int[] result = sortOrder(rows, entries);
        
        return result;
    }

    /**
     * sortOrder
     * @param columns
     * @param entries
     * @return
     */
    private int[] sortOrder(int columns, Entry[] entries) {
        Arrays.sort(entries);
        
        int[] result = new int[columns];
        for(int i=0; i<columns; ++i) {
            result[i] = entries[i].position;
        }
        return result;
    }

    
    
    private static class Entry implements Comparable{

        int score;
        int position;
        
        Entry(int position, int score){
            this.position = position;
            this.score = score;
        }
        
        /* (non-Javadoc)
         * Note - comparison reversed to make highest density  appear
         * first in matrix.
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object arg0) {
            if(!(arg0 instanceof Entry)){
                throw new IllegalArgumentException("Must compare with Entry");
            }
            
            Entry other = (Entry) arg0;
            if( score < other.score) {
                return 1;
            } else if (score > other.score){
                return -1;
            }
            return 0;
        }
        
    }
}
