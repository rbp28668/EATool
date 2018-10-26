/*
 * EntitySet.java
 * Project: EATool
 * Created on 10-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * EntitySet provides a set of Entities for manipulation by scripting.
 * 
 * @author rbp28668
 */
public class EntitySet {

	// Use a linked hash set to allow sorting of entities to the user when used for reporting.
    private Set<alvahouse.eatool.repository.model.Entity> contents = new LinkedHashSet<alvahouse.eatool.repository.model.Entity>();
    
    /**
     * Create a new empty set. 
     */
    EntitySet() {
        super();
    }
    
    /**
     * Creates an entity set initialised from a collection of Entity.
     * @param entities 
     */
    EntitySet(Collection<alvahouse.eatool.repository.model.Entity> entities){
        contents.addAll(entities);
    }
    
    /**
     * Adds an entity to the set.
     * @param e is the entity to add.
     */
    void add(alvahouse.eatool.repository.model.Entity e){
        contents.add(e);
    }
    
    /**
     * Remove an entity from the set.
     * @param e is the entity to remove.
     */
    void remove(alvahouse.eatool.repository.model.Entity e){
        contents.remove(e);
    }
    
    /**
     * Determines if a given entity is contained in the set.
     * @param e is the entity to check.
     * @return true if the set contains the given entity, false otherwise.
     */
    boolean contains(alvahouse.eatool.repository.model.Entity e){
        return contents.contains(e);
    }

    /**
     * Gets the underlying collection of Entities.  Note that this is modifiable.
     * @return a Collection of Entity.
     */
    public Set<alvahouse.eatool.repository.model.Entity> getContents(){
        return contents;
    }

    /**
     * Adds a list of entities.
     * @param entities are the entities to add.
     */
    void addAll(List<alvahouse.eatool.repository.model.Entity> entities) {
        contents.addAll(entities);
    }
    
    
    /**
     * Sorts the list into alphabetical order.
     */
    public void sort(){
    	TreeSet<alvahouse.eatool.repository.model.Entity> sorted = new TreeSet<alvahouse.eatool.repository.model.Entity>(new alvahouse.eatool.repository.model.Entity.Compare());
    	sorted.addAll(contents);
    	contents.clear();
    	contents.addAll(sorted);
    }
    
    /**
     * Determines if the set is empty.
     * @return true if the set is empty, false otherwise.
     */
    public boolean isEmpty(){
        return contents.isEmpty();
    }
    
    /**
     * Removes and returns the first Entity from the set.  Note that
     * being an unordered set "first" is arbitrary.
     * @return an Entity from the set.
     */
    public EntityProxy removeFirst(){
        Iterator<alvahouse.eatool.repository.model.Entity> iter = contents.iterator();
        alvahouse.eatool.repository.model.Entity e = (alvahouse.eatool.repository.model.Entity)iter.next();
        contents.remove(e);
        return new EntityProxy(e);
    }
    
    /**
     * Copies the EntitySet.  Lightweight operation - iterate through
     * EntitySet by copy and while(!isEmpty) removeFirst().
     * @return
     */
    public EntitySet copy(){
        return new EntitySet(contents);
    }
    
    /**
     * Unions this set with another.  This set is changed the
     * other set is unaltered.
     * @param other is the set to union with.
     */
    public void union(EntitySet other){
        contents.addAll(other.contents);
    }
    
    /**
     * Performs a set intersection with another set.  This set is
     * set to the intersection of the 2 sets, the other set is unchanged.
     * @param other is the set to intersect with.
     */
    public void intersection(EntitySet other){
        contents.retainAll(other.contents);
    }
    
    /**
     * Pefroms a set complement with another set.  This set is 
     * set to the complement of this set with the other in that all
     * the entities in this set which are also in the other set are
     * removed.
     * @param other is the set to complement.
     */
    public void complement(EntitySet other){
        contents.removeAll(other.contents);
    }

    
    /**
     * Applies the filter to the entity set.  After applying the filter
     * only those entities that match the filter will remain in the set.
     * @param filter is the filter to apply.
     */
    public void applyFilter(Filter filter){
        Set<alvahouse.eatool.repository.model.Entity> remaining = new HashSet<alvahouse.eatool.repository.model.Entity>();
        for(alvahouse.eatool.repository.model.Entity e : contents ){
            if(filter.matches(e)){
                remaining.add(e);
            }
        }
        contents = remaining;
    }
    
    
}
