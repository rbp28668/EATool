/*
 * MetaModelChangeListener.java
 *
 * Created on 14 February 2002, 21:47
 */

package alvahouse.eatool.repository.metamodel;

/**
 * Event listener interface to listen for meta model change events
 * @author  rbp28668
 */
public interface MetaModelChangeListener {
    /** signals a major update to the meta model 
     * @ param e is the event that references the object being changed
     */
    public void modelUpdated(MetaModelChangeEvent e)  throws Exception;   // major update

    /*======= ENTITIES ================================================*/

    /** signals that a meta entity has been added
     * @ param e is the event that references the object being changed
     */
    public void metaEntityAdded(MetaModelChangeEvent e) throws Exception;

    /** signals that a meta entity has been changed
     * @ param e is the event that references the object being changed
     */
    public void metaEntityChanged(MetaModelChangeEvent e) throws Exception;

    /** signals that a meta entity has been deleted
     * @ param e is the event that references the object being changed
     */
    public void metaEntityDeleted(MetaModelChangeEvent e) throws Exception;
    
    /*======= PROPERTIES ==============================================*/
    // Note - Properties no longer fired individually as they are treated
    // as contained objects - if a meta property is changed then the appropriate
    // event on it's container should be fired.
    
    /*======= RELATIONSHIPS ============================================*/

    /** signals that a meta relationship has been added
     * @ param e is the event that references the object being changed
     */
    public void metaRelationshipAdded(MetaModelChangeEvent e) throws Exception;

    /** signals that a meta relationship has been changed (including one of its roles)
     * @ param e is the event that references the object being changed
     */
    public void metaRelationshipChanged(MetaModelChangeEvent e) throws Exception;

    /** signals that a meta relationship has been deleted
     * @ param e is the event that references the object being changed
     */
    public void metaRelationshipDeleted(MetaModelChangeEvent e) throws Exception;

 }

