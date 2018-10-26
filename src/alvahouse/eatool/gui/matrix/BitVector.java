/*
 * BitVector.java
 * Created on 25-Jul-2005
 *
 */
package alvahouse.eatool.gui.matrix;

/**
 * BitVector is a fixed length vector of bits to allow efficient comparisons for
 * similarity sorting.
 * 
 * @author rbp28668
 * Created on 25-Jul-2005
 */
public class BitVector {

    /** Number of bits in the vector */
    private int size;
    
    /** Bit storage as array of bytes */
    private byte bits[];
    
    /** number of bits per byte */
    private static final int BITS_PER_LUMP=8;
    
    /** lookup table of number of bits in each byte */
    private static final byte[] BIT_COUNT = {
            0,1,1,2,1,2,2,3,1,2,2,3,2,3,3,4,
            1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
            1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
            2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
            1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
            2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
            2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
            3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,
            1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
            2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
            2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
            3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,
            2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
            3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,
            3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,
            4,5,5,6,5,6,6,7,5,6,6,7,6,7,7,8,
           };
    
    /**
     * Creates a bit vector of a given size.
     * @param size is the number of bits to store.
     */
    public BitVector(int size) {
        super();
        if(size < 0) {
            throw new IllegalArgumentException("Cannot have a bit vector with negative size");
        }
        
        bits = new byte[(size + BITS_PER_LUMP - 1)/BITS_PER_LUMP];
        this.size = size;
    }
    
    /**
     * getSize gets the number of bits stored in the bit vector.
     * @return
     */
    public int getSize(){
        return size;
    }
    
    /**
     * getSetCount gets the number of set bits in the vector.
     * @return bit count.
     */
    public int getSetCount(){
        int count = 0;
        for(int i=0; i<bits.length; ++i){
            int idx = (256 | bits[i]) & 255;
            count += BIT_COUNT[idx]; // convert signed byte -128 to 127 to unsigned 0 to 255.
        }
        
        return count;
    }
    
    /**
     * setBit sets a given bit in the vector.
     * @param i is the index of the bit to set [0 <=i < size]
     * @param bit is the new state of the bit.
     */
    public void setBit(int i, boolean bit){
        if(i<0 || i >= size) {
            throw new IllegalArgumentException("Bit array index out of range");
        }
        
        int index = i/BITS_PER_LUMP;
        int offset = i % BITS_PER_LUMP;
        int mask = 1 << offset;
        
        bits[index] &= ~mask;
        if(bit) {
            bits[index] |= mask;
        }
    }
    
    /**
     * getBit gets an individual bit
     * @param i is the index of the bit to get [0 <=i < size]
     * @return the value of the i-th bit.
     */
    public boolean getBit(int i){
        if(i<0 || i >= size) {
            throw new IllegalArgumentException("Bit array index out of range");
        }
        
        int index = i/BITS_PER_LUMP;
        int offset = i % BITS_PER_LUMP;
        int mask = 1 << offset;
        
        boolean value = (bits[index] & mask) != 0;
        return value;
    }
    
    /**
     * countAnd gives a count of the number of bits that are set in the same
     * place in 2 bit vectors.
     * @param other is the BitVector to compare with
     * @return count of bits.
     */
    public int countAnd(BitVector other){
        if(other == null){
            throw new NullPointerException("Can't process null bit vector");
        }
        
        if(other.size != size) {
            throw new IllegalArgumentException("Can only compare bit vectors of same size");
        }

        int count = 0;
        for(int i=0; i<bits.length; ++i){
            byte inBoth = (byte)(bits[i] & other.bits[i]);
            int idx = (256 | inBoth) & 255;
            count += BIT_COUNT[idx]; // -128 to 127 range to 0..255
        }
        return count;
        
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        StringBuffer buff = new StringBuffer(size);
        for(int i=0; i<size; ++i){
            buff.append((getBit(i)) ? '1' : '0');
        }
        return buff.toString();
    }
    
// Used to generate the bit count array.    
//    public static void main(String args[]) {
//        for(int i=0; i<256; ++i){
//            int count = 0;
//            int value = i;
//            for(int j=0; j<8; ++j){
//                if((value & 1) != 0) {
//                    ++count;
//                }
//                value >>= 1;
//            }
//            System.out.print(count + ",");
//            if( (i+1) % 16 == 0){
//                System.out.println();
//            }
//        }
//    }
    
}
