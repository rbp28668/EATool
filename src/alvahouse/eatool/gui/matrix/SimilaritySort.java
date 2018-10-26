/*
 * SimilaritySort.java
 * Created on 29-Jul-2005
 *
 */
package alvahouse.eatool.gui.matrix;



/**
 * SimilaritySort
 * 
 * @author rbp28668
 * Created on 29-Jul-2005
 */
public class SimilaritySort implements MatrixSort{

    /**
     * 
     */
    public SimilaritySort() {
        super();
     }

    public int[] sortColumns(MatrixTableModel model){
        int columns = model.getColumnCount();
    
        Entry[] entries = new Entry[columns];
        for(int i=0; i<columns; ++i) {
            BitVector bits = model.getColumn(i);
            Entry entry = new Entry(i, bits);
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
            Entry entry = new Entry(i, bits);
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
        
        // Find the densest bit vector
        int position = 0;
        Entry densest = entries[position];
        int score = densest.bits.getSetCount();
        
        for(int i=1; i<columns; ++i){
            Entry current = entries[i];
            int currentScore = current.bits.getSetCount(); 
            if(currentScore > score){
                score = currentScore;
                position = i;
            }
        }
        // Move it to the start.
        densest = entries[position];
        entries[position] = entries[0];
        entries[0] = densest;
        
        // Now sort, variation of selection sort: given a bit vector in
        // position i, find the best match in the rest of the array.  Move
        // the best match to the i+1th position and then loop with i = i+1.
        for(int i=0; i<columns-1; ++i){
            
            BitVector currentBits = entries[i].bits;
            int best = 0;
            position = i+1;
            for(int j=i+1; j<columns; ++j){
                BitVector otherBits = entries[j].bits;
                int match = currentBits.countAnd(otherBits);
                if(match > best){
                    best = match;
                    position = j;
                }
            }
            // swap i+1th element with position-th element.
            Entry temp = entries[position];
            entries[position] = entries[i+1];
            entries[i+1] = temp;
        }
        
        // Create the position array.
        int[] result = new int[columns];
        for(int i=0; i<columns; ++i) {
            result[i] = entries[i].position;
        }
        return result;
    }

    
    
    private static class Entry {

        int position;
        BitVector bits;
        
        Entry(int position, BitVector bits){
            this.position = position;
            this.bits = bits;
        }
        
        
    }
}
