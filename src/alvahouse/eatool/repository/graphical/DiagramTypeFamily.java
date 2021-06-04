/*
 * DiagramTypeFamily.java
 * Project: EATool
 * Created on 20-Aug-2006
 *
 */
package alvahouse.eatool.repository.graphical;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import alvahouse.eatool.util.UUID;

/**
 * DiagramTypeFamily is a container class for different diagram types of the same class. DiagramTypes
 * describe all the different types for a given family or DiagramTypeFamily.
 * Note that in practice this delegates much of its functionality to the underlying DiagramTypes collection.
 * 
 * @author rbp28668
 */
public abstract class DiagramTypeFamily {

    /** Parent container for all diagram types/diagram type families */
    private DiagramTypes allTypes; 
    
    /** Diagram types of this family */
    //private List<DiagramType> diagramTypes = new LinkedList<DiagramType>();
    
    /** Name of this family */
    private String name;
    
    /** Class to create new diagram types of */
    private Class<? extends DiagramType> createdClass;
    
    /** Unique ID for this diagram type family */
    private UUID key;
    
    
     /**
      * Creates a new DiagramTypeFamily.
     * @param allTypes is the parent DiagramTypes.
     * @param createdClass is the Class of the DiagramType that this family contains.
     * @param name is the user-friendly name for this family.
     */
    protected DiagramTypeFamily(Class<? extends DiagramType> createdClass, UUID key) {
        super();
        
        assert(createdClass != null);
        assert(DiagramType.class.isAssignableFrom(createdClass));
        
        this.createdClass = createdClass;
        this.key = key;
    }

    /**
     * @param allTypes
     */
    public void setParent(DiagramTypes allTypes){
        if(allTypes == null){
            throw new NullPointerException("Can't set null DiagramTypes");
        }
        this.allTypes = allTypes;
    }
    
    /**
     * Creates an instance of the DiagramType appropriate for this family.
     * @return a new DiagramType.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     */
    public DiagramType newDiagramType() throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
        DiagramType created = createdClass.newInstance();
        created.setFamily(this);
        return created;
    }
    
    /**
     * Gets the Class of the child DiagramType.
     * @return Class used to create new DiagramType for this family.
     */
    public Class<? extends DiagramType> getDiagramTypeClass(){
        return createdClass;
    }

    /**
     * Sets the name of the family.
     * @param name is the name to set.
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * Gets the name of the family.
     * @return String containing the family name.
     */
    public String getName(){
        return name;
    }
    
    /**
     * Gets the key value for this family (used for serialising/deserialising)
     * @return the key value.
     */
    public UUID getKey(){
        return key;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }
    
    /**
     * Adds a DiagramType to this family.
     * @param dt is the DiagramType to add.
     */
    public void add(DiagramType dt) throws Exception{
    	if(dt == null) {
    		throw new NullPointerException("Can't add null diagram type to type family");
    	}
    	if(allTypes == null) {
    		throw new IllegalStateException("No underlying DiagramTypes in DiagramTypeFamily");
    	}
    	dt.setFamily(this);
        allTypes.add(dt);
    }

    /**
     * Adds a DiagramType to this family without updating version, firing events etc.
     * @param dt is the DiagramType to add.
     */
    public void _add(DiagramType dt) throws Exception{
    	if(dt == null) {
    		throw new NullPointerException("Can't add null diagram type to type family");
    	}
    	if(allTypes == null) {
    		throw new IllegalStateException("No underlying DiagramTypes in DiagramTypeFamily");
    	}
    	dt.setFamily(this);
        allTypes._add(dt);
    }

    /**
     * removes a diagram type from this family.
     * @param dt is the DiagramType to remove.
     */
    public void delete(DiagramType dt) throws Exception{
    	if(dt == null) {
    		throw new NullPointerException("Can't delete null diagram type from type family");
    	}
    	if(allTypes == null) {
    		throw new IllegalStateException("No underlying DiagramTypes in DiagramTypeFamily");
    	}
        allTypes.delete(dt);
    }
    
    /**
     * Gets the collection of DiagramTypes in this family.
     * @return a read-only Collection of DiagramTypes.
     */
    public Collection<DiagramType> getDiagramTypes() throws Exception{
    	if(allTypes == null) {
    		throw new IllegalStateException("No underlying DiagramTypes in DiagramTypeFamily");
    	}
        return allTypes.getDiagramTypesOfFamily(this);
    }

    /**
     * Removes any child DiagramType. 
     */
    public void deleteContents() throws Exception {
    	if(allTypes == null) {
    		throw new IllegalStateException("No underlying DiagramTypes in DiagramTypeFamily");
    	}
        allTypes.deleteDiagramTypesOfFamily(this);
    }
    
    
}
