/*
 * RelationshipSet.java
 * Project: EATool
 * Created on 10-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import alvahouse.eatool.repository.model.Relationship;

/**
 * RelationshipSet is a set of Relationship.
 * 
 * @author rbp28668
 */
@Scripted(description="A set of relationships.")
public class RelationshipSet {

    private Set<Relationship> contents = new LinkedHashSet<>();
    
    /**
     * Create an empty relationship set. 
     */
    RelationshipSet() {
        super();
    }
    
    /**
     * Create a relationship set from the given collection of Relationship.
     * @param relationships
     */
    RelationshipSet(Collection<Relationship> relationships){
        contents.addAll(relationships);
    }
    
    /**
     * Adds a Relationship to the set.
     * @param r is the Relationship to add.
     */
    void add(Relationship r){
        contents.add(r);
    }
    
    /**
     * Removes a given relationship from the set.
     * @param r is the Relationship to remove.
     */
    void remove(Relationship r){
        contents.remove(r);
    }
    
    /**
     * Determines whether the set contains the given Relationship.
     * @param r is the Relationship to check.
     * @return true if r is in the set, false otherwise.
     */
    boolean contains(Relationship r){
        return contents.contains(r);
    }
 
    /**
     * Get the set contents as an unmodifiable Set.
     * @return a Set of Relationship.
     */
    Set<Relationship> getContents(){
        return Collections.unmodifiableSet(contents);
    }

    /**
     * Adds a list of Relationships to this set.
     * @param relationships is the List to add.
     */
    void addAll(List<Relationship> relationships) {
        contents.addAll(relationships);
    }

    /**
     * Determines whether the set contains the given Relationship.
     * @param r is the Relationship to check.
     * @return true if r is in the set, false otherwise.
     */
    boolean contains(RelationshipProxy r){
        return contents.contains(r.get());
    }
 

    /**
     * Determines whether this set is empty or not.
     * @return true if the set is empty, false otherwise.
     */
    @Scripted(description="Determines whether the set is empty")
    public boolean isEmpty(){
        return contents.isEmpty();
    }
    
    /**
     * Adds all the Relationships from the other set, that do not
     * already exist in this set, to this set.
     * @param other contains the other Relationships to add.
     */
    @Scripted(description="Adds all the Relationships from the other set,"
    		+ " that do not already exist in this set, to this set.")
    public void union(RelationshipSet other){
        contents.addAll(other.contents);
    }
    
    /**
     * Removes any Relationship from this set that does not exist
     * in the other set.
     * @param other is the set to use for the intersection.
     */
    @Scripted(description="Removes any Relationship from this set that does not exist in the other set.")
    public void intersection(RelationshipSet other){
        contents.retainAll(other.contents);
    }
    
    /**
     * Removes any Relationships from this set that exist in
     * the other set.
     * @param other contains the Relationships to remove.
     */
    @Scripted(description="Removes any Relationships from this set that exist in the other set.")
    public void complement(RelationshipSet other){
        contents.removeAll(other.contents);
    }
    
 }
