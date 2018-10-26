
package alvahouse.eatool.repository.db.metamodel;

import alvahouse.eatool.repository.version.VersionImpl;

/**
 * Persistent class for storing multiplicity.
 * @author bruce.porteous
 *
 */
public class PersistentMultiplicity extends VersionImpl {

	private int lower;
	private int upper;
	
    /**
     * getLower gets the lower inclusive bound of the multiplicity.  
     * @return Returns the lower.
     */
    public int getLower() {
    	return lower;
    }

    /**
     * setLower sets the lower inclusive bound of the multiplicity.
     * @param lower The lower inclusive bound to set.
     */
    public  void setLower(int lower){
    	assert(lower >= 0);
    	assert(lower <= upper);
    	this.lower = lower;
    }

    /**
     * Get the upper inclusive bound of the multiplicity.  Note that Integer.MAX_VALUE
     * is used to mean "many", i.e. unbounded.
     * @return Returns the upper inclusive bound.
     */
    public  int getUpper(){
    	return upper;
    }

    /**
     * Sets the upper inclusive bound of the multiplicity.  Use the value Integer.MAX_VALUE
     * to mean "many", i.e. unbounded.
     * @param upper The upper inclusive bound to set.
     */
    public void setUpper(int upper){
    	assert(upper >= 0);
    	assert(upper >= lower);
    }

    
}