/*
 * Multiplicity.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.metamodel;

public interface Multiplicity {

    /**
     * getLower gets the lower inclusive bound of the multiplicity.  
     * @return Returns the lower.
     */
    public abstract int getLower();

    /**
     * setLower sets the lower inclusive bound of the multiplicity.
     * @param lower The lower inclusive bound to set.
     */
    public abstract void setLower(int lower);

    /**
     * Get the upper inclusive bound of the multiplicity.  Note that Integer.MAX_VALUE
     * is used to mean "many", i.e. unbounded.
     * @return Returns the upper inclusive bound.
     */
    public abstract int getUpper();

    /**
     * Sets the upper inclusive bound of the multiplicity.  Use the value Integer.MAX_VALUE
     * to mean "many", i.e. unbounded.
     * @param upper The upper inclusive bound to set.
     */
    public abstract void setUpper(int upper);

    /**
     * Determine if a given number of items would be valid for this multiplicity.
     * @param number is the number to test.
     * @return true if value, otherwise false.
     */
    public abstract boolean isValid(int number);

}