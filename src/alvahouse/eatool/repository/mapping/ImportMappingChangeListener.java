/*
 * ImportMappingChangeListener.java
 * Project: EATool
 * Created on 10-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

/**
 * ImportMappingChangeListener is an event listener that listens for changes
 * in the import mapping hierarchy. 
 * 
 * @author rbp28668
 */
public interface ImportMappingChangeListener {

    /**
     * Signals a major change to the model.  The event points to the
     * complete ImportMappings
     * @param e is the change event.
     */
    public void Updated(MappingChangeEvent e);
    
    /**
     * Signals that a mapping has been added.  The event points to the
     * new ImportMapping.
     * @param e is the change event.
     */
    public void MappingAdded(MappingChangeEvent e);

    /**
     * Signals that a mapping has been edited.  The event points to the
     * new ImportMapping.
     * @param e is the change event.
     */
    public void MappingEdited(MappingChangeEvent e);
    
    /**
     * Signals that a mapping has been deleted. The event points to the
     * ImportMapping that has been deleted.
     * @param e is the change event.
     */
    public void MappingDeleted(MappingChangeEvent e);
    
    /**
     * Signals that an EntityMapping has been added to an ImportMapping.
     * The event points to the updated ImportMapping.
     * @param e is the change event.
     */
    public void EntityMappingAdded(MappingChangeEvent e);

    /**
     * Signals that an EntityMapping has been deleted from an ImportMapping.
     * The event points to the deleted EntityMapping.
     * @param e is the change event.
     */
    public void EntityMappingDeleted(MappingChangeEvent e);

    /**
     * Signals that an EntityMapping has been changed.
     * The event points to the edited EntityMapping.
     * @param e is the change event.
     */
    public void EntityMappingEdited(MappingChangeEvent e);
   
    /**
     * Signals that a PropertyMapping has been added.
     * The event points to the PropertyTranslationCollection that the PropertyMapping has 
     * been added to.
     * @param e is the change event.
     */
    public void PropertyMappingAdded(MappingChangeEvent e);

    /**
     * Signals that a PropertyMapping has been edited.
     * The event points to the edited PropertyMapping.
     * @param e is the change event.
     */
    public void PropertyMappingEdited(MappingChangeEvent e);
    
    /**
     * Signals that a PropertyMapping has been deleted.
     * The event points to the deleted PropertyMapping.
     * @param e is the change event.
     */
    public void PropertyMappingDeleted(MappingChangeEvent e);
    
    /**
     * Signals that a RelationshipMapping has been added.
     * The event points to the ImportMapping that the RelationshipMapping has 
     * been added to.
     * @param e is the change event.
     */
    public void RelationshipMappingAdded(MappingChangeEvent e);

    /**
     * Signals that a RelationshipMapping has been deleted.
     * The event points to the deleted RelationshipMapping.
     * @param e is the change event.
     */
    public void RelationshipMappingDeleted(MappingChangeEvent e);

    /**
     * Signals that a RelationshipMapping has been edited.
     * The event points to the edited RelationshipMapping.
     * @param e is the change event.
     */
    public void RelationshipMappingEdited(MappingChangeEvent e);

    /**
     * Signals that a RoleMapping has been edited.
     * The event points to the edited RoleMapping.
     * @param e is the change event.
     */
    public void RoleMappingEdited(MappingChangeEvent e);


}
