/*
 * GraphicalProxy.java
 * Project: EATool
 * Created on 18-Jan-2007
 *
 */
package alvahouse.eatool.repository.graphical;

import alvahouse.eatool.repository.base.KeyedItem;

/**
 * GraphicalProxy is an interface implemented by a graphical object when
 * that graphical object is acting as a visual proxy for an underlying
 * model element.
 * 
 * @author rbp28668
 */
public interface GraphicalProxy {

    /** Gets the user object associated with this component.
	 * @return the associated user object
	 */
	public KeyedItem getItem();
	
	/**
	 * This sets the object that this graphical component represents.
	 * @param o is the object to tie to this component.
	 */
	public abstract void setItem(KeyedItem item);

}
