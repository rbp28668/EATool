/*
 * MetaEntitySet.java
 * Project: EATool
 * Created on 10-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import alvahouse.eatool.repository.metamodel.MetaEntity;


/**
 * MetaEntitySet is a set of MetaEntity.  Useful for filtering.
 * 
 * @author rbp28668
 */
public class MetaEntitySet {

    private Set<MetaEntity> contents = new HashSet<MetaEntity> ();

    /**
     * 
     */
    MetaEntitySet() {
        super();
    }
    
    void add(MetaEntity e){
        contents.add(e);
    }
    
    void add(Collection<MetaEntity> all){
        contents.addAll(all);
    }
    
    void remove(MetaEntity e){
        contents.remove(e);
    }
    
    boolean contains(MetaEntity e){
        return contents.contains(e);
    }
    
    public Set<MetaEntity> getContents(){
        return Collections.unmodifiableSet(contents);
    }

    
    /**
     * Determines whether the set is empty.
     * @return true if the set is empty, false if not.
     */
    public boolean isEmpty(){
        return contents.isEmpty();
    }
    
    /**
     * Removes and returns the first entry to the set.
     * @return the first MetaEntity of the set.
     */
    public MetaEntityProxy removeFirst(){
        Iterator<MetaEntity> iter = contents.iterator();
        MetaEntity me = iter.next();
        contents.remove(me);
        return new MetaEntityProxy(me);

    }
    /**
     * Adds any MetaEntities in the other set that do not already exist in this set.
     * @param other contains the MetaEntities to add.
     */
    public void union(MetaEntitySet other){
        contents.addAll(other.contents);
    }
    
    /** 
     * Removes all the MetaEntities from this set that do not exist in the other set.
     * @param other contains all the meta entities that should be
     * retained in this set.
     */
    public void intersection(MetaEntitySet other){
        contents.retainAll(other.contents);
    }
    
    /** 
     * Removes all the MetaEntities from this set that exist in the other set.
     * @param other contains all the meta entities that should be
     * removed.
     */
    public void complement(MetaEntitySet other){
        contents.removeAll(other.contents);
    }
    

    
}
