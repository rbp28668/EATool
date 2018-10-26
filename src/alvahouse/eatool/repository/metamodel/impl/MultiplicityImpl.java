/**
 * Describes the multiplicity (optionality/cardinality) of one direction of a (meta)relationship.
 * @author  rbp28668
 */

package alvahouse.eatool.repository.metamodel.impl;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.metamodel.Multiplicity;

/**
 * Multiplicity describes the multiplicity (optionality/cardinality) 
 * of one direction of a (meta)relationship.
 * @author rbp28668
 */
public class MultiplicityImpl implements Multiplicity {

    /** Name of this multiplicity */
    private String value;
    /** Lower inclusive bound */
    private int lower = 0;
    /** Upper inclusive bound */
    private int upper = Integer.MAX_VALUE;
    /** Map to allow fast lookup by name */
    private static HashMap<String,Multiplicity> valueMap = new HashMap<String,Multiplicity>();
    /** Complete list of multiplicicites known to the system */
    private static LinkedList<Multiplicity> valueList = new LinkedList<Multiplicity>();
    
    
    /**
     * Sets up a named multiplicity.
     * @param val is the name of this multiplicity.
     * @param lower is the lower inclusive bound.
     * @param upper is the upper inclusive bound.
     */
    protected MultiplicityImpl(String val, int lower, int upper) {
        value = val;
        this.lower = lower;
        this.upper = upper;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return value;
    }
    
    //public void setValue(String value) {
    //    m_value = value; // should parse this to validate
    //}
    
    /**
     * Gets an iterator for the set of Multiplicities.
     * @return an iterator.
     */
    public static List<Multiplicity>getValues() {
        return valueList;
    }

    /**
     * Looks up the multiplcity by name.  If no multiplicity with a matching
     * name exists, then a new one is created.
     * @param val is the name to lookup.
     * @return a multiplicity with the given name.
     */
    public static Multiplicity fromString(String val) {
        val = val.toLowerCase();
        Multiplicity m = valueMap.get(val);
        if(m == null) {
            m = new MultiplicityImpl(val,0,Integer.MAX_VALUE);
            add(m);
        }
        return m;
    }
    
    private static void add(Multiplicity m) {
        valueMap.put(m.toString(), m);
        valueList.addLast(m);
    }
    
    static {
        add(new MultiplicityImpl("one",1,1));
        add(new MultiplicityImpl("zero or one",0,1));
        add(new MultiplicityImpl("zero, one or many",0,Integer.MAX_VALUE));
        add(new MultiplicityImpl("many",1,Integer.MAX_VALUE));
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.Multiplicity#getLower()
     */
    public int getLower() {
        return lower;
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.Multiplicity#setLower(int)
     */
    public void setLower(int lower) {
        this.lower = lower;
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.Multiplicity#getUpper()
     */
    public int getUpper() {
        return upper;
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.Multiplicity#setUpper(int)
     */
    public void setUpper(int upper) {
        this.upper = upper;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.Multiplicity#isValid(int)
     */
    public boolean isValid(int number){
        return number >= lower && number <= upper;
    }
}

