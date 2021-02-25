/*
 * ModelChangeListener.java
 *
 * Created on 16 February 2002, 19:06
 */

package alvahouse.eatool.repository.model;

/**
 * Change listener to receive model change events
 * @author  rbp28668
 */
public interface ModelChangeListener {
    /** signals a major update to the  model 
     * @throws Exception 
     * @ param e is the event that references the object being changed
     */
    public void modelUpdated(ModelChangeEvent e) throws Exception;   // major update

    /*======= ENTITIES ================================================*/

    /** signals that an entity has been added
     * @ param e is the event that references the object being changed
     */
    public void EntityAdded(ModelChangeEvent e) throws Exception;

    /** signals that a  entity has been changed
     * @ param e is the event that references the object being changed
     */
    public void EntityChanged(ModelChangeEvent e) throws Exception;

    /** signals that a  entity has been deleted
     * @ param e is the event that references the object being changed
     */
    public void EntityDeleted(ModelChangeEvent e) throws Exception;
    
    /*======= PROPERTIES ==============================================*/
    // Note that there are no property added or deleted events as
    // these are controlled by the meta-model (the meta-entity determines
    // which properties are present in an entity).  PropertyChangedEvent
    // has been removed - only changes to top level items such as 
    // entity, relationship and role fire changes.
    

    
    /*======= RELATIONSHIPS ============================================*/

    /** signals that a  relationship has been added
     * @ param e is the event that references the object being changed
     */
    public void RelationshipAdded(ModelChangeEvent e) throws Exception;

    /** signals that a  relationship has been changed
     * @ param e is the event that references the object being changed
     */
    public void RelationshipChanged(ModelChangeEvent e) throws Exception;

    /** signals that a  relationship has been deleted
     * @ param e is the event that references the object being changed
     */
    public void RelationshipDeleted(ModelChangeEvent e) throws Exception;


}

