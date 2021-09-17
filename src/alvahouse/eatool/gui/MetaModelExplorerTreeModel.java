/*
 * ExplorerTreeModel.java
 *
 * Created on 20 February 2002, 20:27
 */

package alvahouse.eatool.gui;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaModelChangeListener;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;

/**
 * Tree model that represents the MetaModel as a hierarchy. Used by the
 * meta-model explorer.
 * @author  rbp28668
 */
public class MetaModelExplorerTreeModel extends ExplorerTreeModel
    implements MetaModelChangeListener{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Creates new ExplorerTreeModel */
    public MetaModelExplorerTreeModel(String rootTitle) {
        super(rootTitle);
    }

    /** sets the meta model used by this explorer 
     * @param mm is the meta model to be used 
     */
    public void setMetaModel(MetaModel mm) {
        if(metaModel != null) metaModel.removeChangeListener(this);
        metaModel = mm;
        metaModel.addChangeListener(this);
    }
    
//    /** Sets the tree that is displaying this tree model.  Needed as the
//     * removeAllChildren method doesn't work properly if the node is expanded
//     * hence need to be able to collapse the node before modifying it.
//     * @param tree is the tree that is displaying this tree model
//     */
//    public void setTree(JTree tree) {
//        this.tree = tree;
//    }
    
    public void dispose() {
        if(metaModel != null) metaModel.removeChangeListener(this);
    }

    /** Call when the meta model changes to get the explorer to re-build
     * it's internal representation
     */
    public void refresh()  throws Exception{
       clearTree();
       reload();
       initModel();
    }
    
     /**
     * Deletes all the nodes in the tree.
     */
    private void clearTree() {
        ((DefaultMutableTreeNode)getRoot()).removeAllChildren();
           ((DefaultMutableTreeNode)getMetaEntitiesNode()).removeAllChildren();
           ((DefaultMutableTreeNode)getMetaRelationshipsNode()).removeAllChildren();
           removeAll();
    }

    /** initialises the tree model from the model or meta-model.
     */
    private void initModel()  throws Exception{
        MutableTreeNode metn = getMetaEntitiesNode();
        MutableTreeNode mrtn = getMetaRelationshipsNode();
        insertNodeInto(metn, (DefaultMutableTreeNode)getRoot(),0);
        insertNodeInto(mrtn, (DefaultMutableTreeNode)getRoot(),1);
        
        int idx = 0;
        List<MetaEntity> metaEntities = new LinkedList<>();
        metaEntities.addAll(metaModel.getMetaEntities()); 
        Collections.sort(metaEntities,new MetaEntity.Compare());
        for(MetaEntity me : metaEntities) {
            addMetaEntityNode(metn,me,idx++);
        }
        
        idx=0;
        List<MetaRelationship> metaRelationships = new LinkedList<>();
        metaRelationships.addAll(metaModel.getMetaRelationships());
        Collections.sort(metaRelationships, new MetaRelationship.CompareByName());
        for(MetaRelationship mr : metaRelationships) {
            addMetaRelationshipNode(mrtn,mr,idx++);
        }
    }


    /** updates the tree model for a meta-entity
     * @param node is the tree node corresponding to the given meta-entity
     * @param me is the meta-entity for the given tree node
     */
    private void refreshNode(DefaultMutableTreeNode node, MetaEntity me) throws Exception {
        node.removeAllChildren();
        setMetaEntityNodeChildren(node, me);
        nodeStructureChanged(node);
    }

    /** updates the tree model for a meta-property
     * @param node is the tree node corresponding to the given meta-property
     * @param mp is the meta-property for the given tree node
     */
    private void refreshNode(DefaultMutableTreeNode node, MetaProperty mp) {
        node.removeAllChildren();
        setMetaPropertyNodeChildren(node, mp);
        nodeStructureChanged(node);
    }
    
    /** updates the tree model for a meta-relationship
     * @param node is the tree node corresponding to the given meta-relationship
     * @param mr is the meta-relationship for the given tree node
     */
    private void refreshNode(DefaultMutableTreeNode node, MetaRelationship mr) throws Exception{
        node.removeAllChildren();
        setMetaRelationshipNodeChildren(node, mr);
        nodeStructureChanged(node);
    }
    
    /** updates the tree model for a meta-role
     * @param node is the tree node corresponding to the given meta-role
     * @param mr is the meta-role for the given tree node
     */
    private void refreshNode(DefaultMutableTreeNode node, MetaRole mr) throws Exception{
        node.removeAllChildren();
        setMetaRoleNodeChildren(node, mr);
        nodeStructureChanged(node);
    }

    /*-------------------------------------------------------------------
     * Builder members that help build a tree.  These default to building
     * a meta-model tree.  Over-ride these members to alter how the 
     * tree is built
     *-----------------------------------------------------------------*/

    /** gets the root node for all the meta-entities.  If the node
     * hasn't yet been created it is created with a MetaEntities class
     * @return a tree node that roots the meta-entities
     */
    private MutableTreeNode getMetaEntitiesNode() {
        if(metaEntitesNode == null) {
            metaEntitesNode = new DefaultMutableTreeNode(new MetaEntities());
        }
        return metaEntitesNode;
    }

    /** gets the root node for all the meta-relationships.  If the node
     * hasn't yet been created it is created with a MetaRelationships class
     * @return a tree node that roots the meta-relationships
     */
    private MutableTreeNode getMetaRelationshipsNode() {
        if(metaRelationshipsNode == null) {
            metaRelationshipsNode = new DefaultMutableTreeNode(new MetaRelationships());
        }
        return metaRelationshipsNode;
    }
    
        

    /** Adds a new tree node for a meta-entity and adds it to the parent node
     * @param parent is the parent node to which the meta-entity's node should be added
     * @param me is the meta-entity to add
     * @idxEntity is where to insert the entity-node into its parent
     */
    private void addMetaEntityNode(MutableTreeNode parent, MetaEntity me, int idxEntity) throws Exception {
        DefaultMutableTreeNode tnEntity = new DefaultMutableTreeNode(me.getName());
        insertNodeInto(tnEntity,parent,idxEntity);
        tnEntity.setUserObject(me);
        registerNode(tnEntity,me);
        setMetaEntityNodeChildren(tnEntity, me);
    }
    
    /** Sets the child nodes for a meta-entity
     * @param tnEntity is the tree node for the meta-entity
     * @param me is the meta-entity
     */
    private void setMetaEntityNodeChildren(MutableTreeNode tnEntity, MetaEntity me) throws Exception{
        int idx = 0;
        
        if(me.getDescription() != null)
            insertNodeInto(new DefaultMutableTreeNode("description: " + me.getDescription()), tnEntity, idx++);
        
        if(me.isAbstract()) {
            insertNodeInto(new DefaultMutableTreeNode("abstract: true"), tnEntity, idx++);
        }

        MetaEntity meBase = me.getBase();
        if(meBase != null) {
            DefaultMutableTreeNode tn = new DefaultMutableTreeNode("extends: " + meBase.getName());
            insertNodeInto(tn, tnEntity, idx++);
        }

        idx = addPropertiesNode(tnEntity, me, idx);
    }

	/**
	 * Inserts a Property collection node at the given position index and populates it with the declared
	 * properties of the given meta property container.
	 * @param parent is the parent node that the properties node should be attached to.
	 * @param mpc is the container for the meta properties to be shown
	 * @param positionIndex is where in the parent to insert the properties node.
	 */
	private int addPropertiesNode(MutableTreeNode parent, MetaPropertyContainer mpc, int positionIndex) {
		int idxProperty=0;
        Iterator<MetaProperty> iter = mpc.getDeclaredMetaProperties().iterator();
        if(iter.hasNext()) {
        	DefaultMutableTreeNode tnProperties = new DefaultMutableTreeNode("Properties");
            insertNodeInto(tnProperties, parent, positionIndex++);
	        while(iter.hasNext()) {
	            MetaProperty mp = (MetaProperty)iter.next();
	            addMetaPropertyNode(tnProperties, mp, idxProperty++);
	        }
        }
        return positionIndex;
	}
    
    /** Adds a new tree node for a meta-property and adds it to the parent node
     * @param parent is the parent node to which the meta-property's node should be added
     * @param mp is the meta-property to add
     * @idxProperty is where to insert the property-node into its parent
     */
    private void addMetaPropertyNode(MutableTreeNode parent, MetaProperty mp, int idxProperty) {
        DefaultMutableTreeNode tnProperty = new DefaultMutableTreeNode(mp.getName());
        insertNodeInto(tnProperty,parent,idxProperty);
        tnProperty.setUserObject(mp);
        registerNode(tnProperty,mp);
        setMetaPropertyNodeChildren(tnProperty, mp);
    }
    
    /** Sets the child nodes for a meta-property
     * @param tnProperty is the tree node for the meta-property
     * @param mp is the meta-property
     */
    private void setMetaPropertyNodeChildren(MutableTreeNode tnProperty, MetaProperty mp) {
        int idx = 0;
        if(mp.getDescription() != null)
            insertNodeInto(new DefaultMutableTreeNode("description: " + mp.getDescription()), tnProperty, idx++);
        insertNodeInto(new DefaultMutableTreeNode("type: " + mp.getMetaPropertyType().getTypeName()), tnProperty, idx++);
        if(mp.isMandatory())
            insertNodeInto(new DefaultMutableTreeNode("mandatory: true"), tnProperty, idx++);
        if(mp.isReadOnly())
            insertNodeInto(new DefaultMutableTreeNode("read-only: true"), tnProperty, idx++);
        String defaultValue = mp.getDefaultValue();
        if(defaultValue != null && !defaultValue.isEmpty())
            insertNodeInto(new DefaultMutableTreeNode("default: " + mp.getDefaultValue()), tnProperty, idx++);
    }

    /** Adds a new tree node for a meta-relationship and adds it to the parent node
     * @param parent is the parent node to which the meta-relationship's node should be added
     * @param mr is the meta-relationship to add
     * @idxRelationship is where to insert the relationship-node into its parent
     */
    private void addMetaRelationshipNode(MutableTreeNode parent, MetaRelationship mr, int idxRelationship) throws Exception{
        DefaultMutableTreeNode tnRelationship = new DefaultMutableTreeNode(mr.getName());
        tnRelationship.setUserObject(mr);
        insertNodeInto(tnRelationship,parent,idxRelationship);
        registerNode(tnRelationship,mr);
        setMetaRelationshipNodeChildren(tnRelationship, mr);
    }
        
    /** Sets the child nodes for a meta-relationship
     * @param tnRole is the tree node for the meta-relationship
     * @param is the meta-relationship
     */
    private void setMetaRelationshipNodeChildren(MutableTreeNode tnRelationship, MetaRelationship mr) throws Exception {
        int idx = 0;
        if(mr.getDescription() != null)
            insertNodeInto(new DefaultMutableTreeNode("description: " + mr.getDescription()), tnRelationship, idx++);
        addMetaRoleNode(tnRelationship, mr.start(), idx++);
        addMetaRoleNode(tnRelationship, mr.finish(), idx++);
        idx = addPropertiesNode(tnRelationship, mr, idx);
    }

    /** Adds a new tree node for a meta-role and adds it to the parent node
     * @param parent is the parent node to which the meta-role's node should be added
     * @param mr is the meta-role to add
     * @idxRole is where to insert the role-node into its parent
     */
    private void addMetaRoleNode(MutableTreeNode parent, MetaRole mr, int idxRole) throws Exception {
        DefaultMutableTreeNode tnRole = new DefaultMutableTreeNode("Role: " + mr.getName());
        tnRole.setUserObject(mr);
        insertNodeInto(tnRole, parent, idxRole);
        registerNode(tnRole,mr);
        setMetaRoleNodeChildren(tnRole,mr);
    }

    /** Sets the child nodes for a meta-role
     * @param tnRole is the tree node for the meta-role
     * @param is the meta-role
     */
    private void setMetaRoleNodeChildren(MutableTreeNode tnRole, MetaRole mr) throws Exception{
        int idx = 0;
        if((mr.getDescription() != null) && (mr.getDescription().length() > 0))
            insertNodeInto(new DefaultMutableTreeNode("description: " + mr.getDescription()), tnRole, idx++);
        insertNodeInto(new DefaultMutableTreeNode("multiplicity: " + mr.getMultiplicity().toString()), tnRole, idx++);
        insertNodeInto(new DefaultMutableTreeNode("connects to: " + mr.connectsTo().getName()), tnRole, idx++);
        idx = addPropertiesNode(tnRole, mr, idx);
    }
    
    /*-----------------------------------------------------------------
     * Meta-Model event handlers.
     *---------------------------------------------------------------*/
    
    /** signals that a meta relationship has been deleted
     * @ param e is the event that references the object being changed
     */
    public void metaRelationshipDeleted(MetaModelChangeEvent e) {
        MetaRelationship mr = (MetaRelationship)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mr);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(mr);
        }
    }
    
    /** signals that a meta relationship has been added
     * @ param e is the event that references the object being changed
     */
    public void metaRelationshipAdded(MetaModelChangeEvent e) throws Exception {
        MetaRelationship mr = (MetaRelationship)e.getSource();
        MutableTreeNode parent = metaRelationshipsNode; // MetaRelationships
        if(parent != null) {
            int idxRelationship = parent.getChildCount();
            addMetaRelationshipNode(parent, mr, idxRelationship); 
        }
    }
    
    /** signals a major update to the meta model
     * @ param e is the event that references the object being changed
     */
    public void modelUpdated(MetaModelChangeEvent e)  throws Exception{
        refresh();
    }
    
    /** signals that a meta entity has been deleted
     * @ param e is the event that references the object being changed
     */
    public void metaEntityDeleted(MetaModelChangeEvent e) {
        MetaEntity me = (MetaEntity)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(me);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(me);
        }
    }

    
    /** signals that a meta entity has been added
     * @ param e is the event that references the object being changed
     */
    public void metaEntityAdded(MetaModelChangeEvent e) throws Exception{
        MetaEntity me = (MetaEntity)e.getSource();
        MutableTreeNode parent = metaEntitesNode; // MetaEntities
        if(parent != null) {
            int idxEntity = parent.getChildCount();
            addMetaEntityNode(parent, me, idxEntity); 
        }
    }

    
    /** signals that a meta entity has been changed
     * @ param e is the event that references the object being changed
     */
    public void metaEntityChanged(MetaModelChangeEvent e) throws Exception{
        MetaEntity me = (MetaEntity)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(me);
        if(me != null) {
            refreshNode(tn,me);
        }
    }

    
    /** signals that a meta relationship has been changed
     * @ param e is the event that references the object being changed
     */
    public void metaRelationshipChanged(MetaModelChangeEvent e) throws Exception {
        MetaRelationship mr = (MetaRelationship)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mr);
        if(tn != null) {
            refreshNode(tn,mr);
        }
    }

    

    /*-----------------------------------------------------------------
     * Inner Classes
     *---------------------------------------------------------------*/
   
    /** inner class to provide a placeholder for the meta-entities.  This 
     * allows popups to be keyed by the "MetaEntities" class name
     */
    private class MetaEntities {
        public String toString() {
            return "Meta-Entities";
        }
    }

    /** inner class to provide a placeholder for the meta-relationships.  This 
     * allows popups to be keyed by the "MetaRelationships" class name
     */
    private class MetaRelationships {
        public String toString() {
            return "Meta-Relationships";
        }
    }

    private MetaModel metaModel;
    
    private MutableTreeNode metaEntitesNode;
    private MutableTreeNode metaRelationshipsNode;
     
 
}
