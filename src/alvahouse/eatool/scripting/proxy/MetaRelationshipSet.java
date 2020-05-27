/*
 * MetaRelationshipSet.java
 * Project: EATool
 * Created on 10-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import alvahouse.eatool.repository.metamodel.MetaRelationship;


/**
 * MetaRelationshipSet is part of the meta-model and holds a collection of
 * MetaRelationship.
 * 
 * @author rbp28668
 */
@Scripted(description="A lightweight set of meta-relationships.  Changes to this set do not change the underlying metamodel.")
public class MetaRelationshipSet {


    private Set<MetaRelationship> contents = new LinkedHashSet<MetaRelationship>();
    /**
     * 
     */
    MetaRelationshipSet() {
        super();
    }
    
    MetaRelationshipSet(Collection<MetaRelationship> set){
        contents.addAll(set);
    }
    
    void add(MetaRelationship r){
        contents.add(r);
    }
    
    void remove(MetaRelationship r){
        contents.remove(r);
    }
    
    boolean contains(MetaRelationship r){
        return contents.contains(r);
    }

    public Set<MetaRelationship> getContents(){
        return Collections.unmodifiableSet(contents);
    }

    /**
     * Determines if the set is empty.
     * @return true if empty, false if not.
     */
    @Scripted(description="Determines if the set is empty.")
    public boolean isEmpty(){
        return contents.isEmpty();
    }
    
    /**
     * Removes and returns the first MetaRelationship from the set. 
     * @return a MetaRelationshipy from the set.
     */
    @Scripted(description="Removes and returns the first meta-relationship from the set.")
    public MetaRelationshipProxy removeFirst(){
        Iterator<MetaRelationship> iter = contents.iterator();
        MetaRelationship mr = (MetaRelationship)iter.next();
        contents.remove(mr);
        return new MetaRelationshipProxy(mr);
    }
    
    /**
     * Copies the MetaRelationshipSet.  Lightweight operation - iterate through
     * MetaRelationshipSet by copy and while(!isEmpty) removeFirst().
     * @return
     */
    @Scripted(description="Copies the MetaRelationshipSet.  Lightweight operation - iterate through" + 
    		" MetaRelationshipSet by copy, while(!isEmpty) and removeFirst().")
    public MetaRelationshipSet copy(){
        return new MetaRelationshipSet(contents);
    }

    /**
     * Unions this set with another.  This set is populated with any
     * MetaRelationship that originated in either set.
     * @param other is the set to union this set with.
     */
    @Scripted(description="Unions this set with another.  This set is populated with any" + 
    		" MetaRelationship that originated in either set.")
    public void union(MetaRelationshipSet other){
        contents.addAll(other.contents);
    }
    
    /**
     * Intersects this set with another.  This set is populated with any
     * MetaRelationships that originally were present in both sets.
     * @param other is the set to intersect with.
     */
    @Scripted(description="Intersects this set with another.  This set is populated with any" + 
    		" MetaRelationships that originally were present in both sets.")
    public void intersection(MetaRelationshipSet other){
        contents.retainAll(other.contents);
    }
    
    /**
     * Complements this set with another.  This set is populated with any
     * MetaRelationships that were originally in this set,but not in the other.
     * @param other is the set to complement with.
     */
    @Scripted(description="Complements this set with another.  This set is populated with any" + 
    		" MetaRelationships that were originally in this set,but not in the other.")
    public void complement(MetaRelationshipSet other){
        contents.removeAll(other.contents);
    }
    

    
}
