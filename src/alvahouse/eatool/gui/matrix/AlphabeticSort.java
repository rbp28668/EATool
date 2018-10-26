/*
 * AlphabeticSort.java
 * Created on 29-Jul-2005
 *
 */
package alvahouse.eatool.gui.matrix;

import java.util.Arrays;

import alvahouse.eatool.repository.model.Entity;


/**
 * AlphabeticSort
 * 
 * @author rbp28668
 * Created on 29-Jul-2005
 */
public class AlphabeticSort implements MatrixSort{

    /**
     * 
     */
    public AlphabeticSort() {
        super();
    }
    
    public int[] sortColumns(MatrixTableModel model){
        int columns = model.getColumnCount();
        Entry[] entries = new Entry[columns];
        for(int i=0; i<columns; ++i) {
            Entry entry = new Entry(i, model.getUnshuffledColumnEntity(i));
            entries[i] = entry;
       }
        
        int[] result = sortOrder(columns, entries);
        
        return result;
        
    }

    public int[] sortRows(MatrixTableModel model){
        int rows = model.getRowCount();
        
        Entry[] entries = new Entry[rows];
        for(int i=0; i<rows; ++i) {
            Entry entry = new Entry(i, model.getUnshuffledRowEntity(i));
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

        Entity entity;
        int position;
        
        Entry(int position, Entity entity){
            this.position = position;
            this.entity = entity;
        }
        
        /* (non-Javadoc)
         * Comparison on string values of entities.
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object arg0) {
            if(!(arg0 instanceof Entry)){
                throw new IllegalArgumentException("Must compare with Entry");
            }
            
            Entry other = (Entry) arg0;
            return entity.toString().compareTo(other.entity.toString());
        }
        
    }

}
