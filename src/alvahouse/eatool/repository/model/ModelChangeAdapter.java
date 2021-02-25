/*
 * ModelChangeAdapter.java
 *
 * Created on 06 March 2002, 22:15
 */

package alvahouse.eatool.repository.model;

/**
 *
 * @author  rbp28668
 */
public class ModelChangeAdapter implements ModelChangeListener {

    /** Creates new ModelChangeAdapter */
    public ModelChangeAdapter() {
    }

    /** signals that a  relationship has been changed
     * @ param e is the event that references the object being changed
     */
    public void RelationshipChanged(ModelChangeEvent e) throws Exception {
    }
    
    /** signals that an entity has been added
     * @ param e is the event that references the object being changed
     */
    public void EntityAdded(ModelChangeEvent e) throws Exception {
    }

	/**
	* signals that a relationship has been added 
	* @ param e is the event that
	* references the object being changed
	*/
    public void RelationshipAdded(ModelChangeEvent e) throws Exception {
    }

    /** signals that a  relationship has been deleted
     * @ param e is the event that references the object being changed
     */
    public void RelationshipDeleted(ModelChangeEvent e) throws Exception {
    }
    
    /** signals that a  entity has been deleted
     * @ param e is the event that references the object being changed
     */
    public void EntityDeleted(ModelChangeEvent e) throws Exception {
    }
    
    
    /** signals a major update to the  model
     * @throws Exception 
     * @ param e is the event that references the object being changed
     */
    public void modelUpdated(ModelChangeEvent e) throws Exception {
    }
    
    /** signals that a  entity has been changed
     * @ param e is the event that references the object being changed
     */
    public void EntityChanged(ModelChangeEvent e) throws Exception{
    }
    
}
