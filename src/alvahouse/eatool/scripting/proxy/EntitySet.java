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

import alvahouse.eatool.repository.model.Entity;


/**
 * EntitySet provides a set of Entities for manipulation by scripting.  Note that
 * whilst a set this does have implied ordering.
 * 
 * @author rbp28668
 */
@Scripted(description="A set of entities.  The set is lightweight and can be copied easily."
		+ " Whilst a set, there is ordering of contents so it can be sorted etc.")
public class EntitySet {

	// Use a linked hash set to allow sorting of entities to the user when used for reporting.
    private final Set<Entity> contents = new LinkedHashSet<>();
    
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
    EntitySet(Collection<Entity> entities){
        contents.addAll(entities);
    }
    
    /**
     * Adds an entity to the set.
     * @param e is the entity to add.
     */
    void add(Entity e){
        contents.add(e);
    }
    
    /**
     * Remove an entity from the set.
     * @param e is the entity to remove.
     */
     void remove(Entity e){
        contents.remove(e);
    }
    
    /**
     * Determines if a given entity is contained in the set.
     * @param e is the entity to check.
     * @return true if the set contains the given entity, false otherwise.
     */
    boolean contains(Entity e){
        return contents.contains(e);
    }

    /**
     * Gets the underlying collection of Entities.  Note that this is modifiable.
     * @return a Collection of Entity.
     */
    public Set<Entity> getContents(){
        return contents;
    }

    /**
     * Adds a list of entities.
     * @param entities are the entities to add.
     */
    void addAll(List<Entity> entities) {
        contents.addAll(entities);
    }

    /**
     * Determines if a given entity is contained in the set.
     * @param e is the entity proxy to check.
     * @return true if the set contains the given entity, false otherwise.
     */
    @Scripted(description="Determines if the entity set contains the given entity.")
    public boolean contains(EntityProxy e){
        return contents.contains(e.get());
    }

    
    /**
     * Sorts the list into alphabetical order.
     */
    @Scripted(description="Sorts the list into alphabetical order.")
    public void sort(){
    	TreeSet<Entity> sorted = new TreeSet<Entity>(new Entity.Compare());
    	sorted.addAll(contents);
    	contents.clear();
    	contents.addAll(sorted);
    }
    
    /**
     * Determines if the set is empty.
     * @return true if the set is empty, false otherwise.
     */
    @Scripted(description="Determines if the set is empty.")
    public boolean isEmpty(){
        return contents.isEmpty();
    }
    
    /**
     * Removes and returns the first Entity from the set.  
     * @return an Entity from the set.
     */
    @Scripted(description="Removes and returns the first Entity from the set.")
    public EntityProxy removeFirst(){
        Iterator<Entity> iter = contents.iterator();
        Entity e = iter.next();
        contents.remove(e);
        return new EntityProxy(e);
    }
    
    /**
     * Copies the EntitySet.  Lightweight operation - iterate through
     * EntitySet by copy and while(!isEmpty) removeFirst().
     * @return
     */
    @Scripted(description="Copies the EntitySet.  Lightweight operation - iterate through" + 
    		" EntitySet by copy followed by while(!isEmpty) and removeFirst().")
    public EntitySet copy(){
        return new EntitySet(contents);
    }
    
    /**
     * Unions this set with another.  This set is changed the
     * other set is unaltered.
     * @param other is the set to union with.
     */
    @Scripted(description="Unions this set with another. "
    		+ " This set is changed the other set is unaltered.")
    public void union(EntitySet other){
        contents.addAll(other.contents);
    }
    
    /**
     * Performs a set intersection with another set.  This set is
     * set to the intersection of the 2 sets, the other set is unchanged.
     * @param other is the set to intersect with.
     */
    @Scripted(description="Performs a set intersection with another set."
    		+ " This set is set to the intersection of the 2 sets,"
    		+ " the other set is unchanged.")
    public void intersection(EntitySet other){
        contents.retainAll(other.contents);
    }
    
    /**
     * Performs a set complement with another set.  This set is 
     * set to the complement of this set with the other. 
     * All the entities in this set which are also in the other set are
     * removed.
     * @param other is the set to complement.
     */
    @Scripted(description="Performs a set complement with another set."
    		+ " This set is set to the complement of this set with the other." + 
    		" All the entities in this set which are also in the other set are" + 
    		" removed.")
    public void complement(EntitySet other){
        contents.removeAll(other.contents);
    }

    
    /**
     * Applies the filter to the entity set.  After applying the filter
     * only those entities that match the filter will remain in the set.
     * @param filter is the filter to apply.
     */
    @Scripted(description="Applies the filter to the entity set."
    		+ " After applying the filter only those entities that match the"
    		+ " filter will remain in the set.")
    public void applyFilter(Filter filter){
        Set<Entity> remaining = new HashSet<Entity>();
        for(Entity e : contents ){
            if(filter.matches(e)){
                remaining.add(e);
            }
        }
        contents.clear();
        contents.addAll(remaining);
    }
    
    
}
