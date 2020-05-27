/*
 * MetaPropertySet.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.metamodel.MetaProperty;

/**
 * MetaPropertySet is part of the meta-model and holds the MetaProperty 
 * corresponding to a MetaEntity.
 * 
 * @author rbp28668
 */
@Scripted(description="Provides a set of meta-property.")  
public class MetaPropertySet {

    List<MetaProperty> metaProperties = new LinkedList<MetaProperty>();
    /**
     * 
     */
    MetaPropertySet() {
        super();
    }
    
    MetaPropertySet(Collection<MetaProperty> metaProperties){
        this.metaProperties.addAll(metaProperties);
    }

    /**
     * Determine whether the set is empty or not.
     * @return true if the set is empty, false if not.
     */
    @Scripted(description="Determines if the set is empty.")  
    public boolean isEmpty(){
        return metaProperties.isEmpty();
    }
    
    /**
     * Removes the first MetaProperty from the set.
     * @return the first MetaProperty.
     */
    @Scripted(description="Removes the first MetaProperty from the set.")  
    public MetaPropertyProxy removeFirst(){
        MetaProperty mp = (MetaProperty)metaProperties.remove(0);
        return new MetaPropertyProxy(mp);
    }
    
    /**
     * Copies the MetaPropertySet.  Note that the new  set shares
     * the underlying elements of this set.
     * @return a new MetaPropertySet.
     */
    @Scripted(description="Copies the MetaPropertySet.  The new set contains the same elements as the original.")  
    public MetaPropertySet copy(){
        return new MetaPropertySet(metaProperties);
    }

}
