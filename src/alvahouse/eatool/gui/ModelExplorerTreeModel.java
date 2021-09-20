/*
 * ModelExplorerTreeModel.java
 *
 * Created on 21 February 2002, 08:29
 */

package alvahouse.eatool.gui;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaModelChangeListener;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.ModelChangeEvent;
import alvahouse.eatool.repository.model.ModelChangeListener;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.repository.model.PropertyContainer;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;

/**
 * Tree model for exploring the repository model.
 * @author  rbp28668
 */
public class ModelExplorerTreeModel extends ExplorerTreeModel
    implements ModelChangeListener, MetaModelChangeListener{

    private static final long serialVersionUID = 1L;
    private MetaModel metaModel;
    private Model model;
    
    private MutableTreeNode metaEntitesNode;
    private MutableTreeNode metaRelationshipsNode;


    /** Creates new ModelExplorerTreeModel */
    public ModelExplorerTreeModel(String rootTitle) {
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
    
    /** sets the model used by this explorer 
     * @param m is the model to be used 
     */
    public void setModel(Model m) {
        if(model != null) model.removeChangeListener(this);
        model = m;
        model.addChangeListener(this);
    }

	/**
	 * Method dispose cleans up the tree model by removing itself from
	 * model and metaModel change listeners.
	 */
    public void dispose() {
        if(metaModel != null) metaModel.removeChangeListener(this);
        if(model != null) model.removeChangeListener(this);
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
        Collections.sort(metaEntities, new MetaEntity.Compare());
        for(MetaEntity me : metaEntities) {
            idx = addMetaEntityNode(metn,me,idx);
        }
        
        idx=0;
        List<MetaRelationship> metaRelationships = new LinkedList<>();
        metaRelationships.addAll(metaModel.getMetaRelationships());
        Collections.sort(metaRelationships,new MetaRelationship.CompareByName());
        for(MetaRelationship mr : metaRelationships) {
            idx = addMetaRelationshipNode(mrtn,mr,idx);
        }
    }

    @SuppressWarnings("unused") // debug method
	private void showTree(int depth, DefaultMutableTreeNode node){
        for(int i=0;i<depth;++i){
            System.out.print(" ");
        }
        System.out.println(node.getUserObject().toString());
        Enumeration<?> children = node.children();
        while(children.hasMoreElements()){
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
            showTree(depth+2,child);
        }
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
     * Clears all the nodes from the tree.
     */
    private void clearTree() {
        ((DefaultMutableTreeNode)getRoot()).removeAllChildren();
           ((DefaultMutableTreeNode)getMetaEntitiesNode()).removeAllChildren();
           ((DefaultMutableTreeNode)getMetaRelationshipsNode()).removeAllChildren();
           removeAll();
    }

    /** updates the tree model for a meta-entity
     * @param node is the tree node corresponding to the given meta-entity
     * @param me is the meta-entity for the given tree node
     */
    private void refreshNode(DefaultMutableTreeNode node, MetaEntity me) throws Exception{
        node.removeAllChildren();
        List<Entity> listEntities = model.getEntitiesOfType(me);
        if(!listEntities.isEmpty()) {
            Collections.sort(listEntities,new Entity.Compare());
            setMetaEntityNodeChildren(node, listEntities);
            nodeStructureChanged(node);
        } else { // now an empty node - remove it as well
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            removeNodeFromParent(parent);
        }
    }
    
    /**
     * Gets the model underlying this tree model.
     * @return the Model.
     */
    public Model getModel() {
        return model;
    }
     /** gets the root node for all the meta-entities.  If the node
     * hasn't yet been created it is created with a MetaEntities class
     * @return a tree node that roots the meta-entities
     */
    private MutableTreeNode getMetaEntitiesNode() {
        if(metaEntitesNode == null) {
            metaEntitesNode = new DefaultMutableTreeNode(new Entities());
        }
        return metaEntitesNode;
    }

    /** gets the root node for all the meta-relationships.  If the node
     * hasn't yet been created it is created with a MetaRelationships class
     * @return a tree node that roots the meta-relationships
     */
    private MutableTreeNode getMetaRelationshipsNode() {
        if(metaRelationshipsNode == null) {
            metaRelationshipsNode = new DefaultMutableTreeNode(new Relationships());
        }
        return metaRelationshipsNode;
    }

    
    /** Adds a new tree node for a meta-entity and adds it to the parent node
     * @param parent is the parent node to which the meta-entity's node should be added
     * @param me is the meta-entity to add
     * @idxEntity is where to insert the entity-node into its parent
     * @returns the index for the next entity to be added to
     */
    private int addMetaEntityNode(MutableTreeNode parent, MetaEntity me, int idxEntity) throws Exception {
        List<Entity> listEntities = model.getEntitiesOfType(me);
        if(!listEntities.isEmpty()) {
            //System.out.println("Inserting meta-entity " + me.getName());
            DefaultMutableTreeNode tnEntity = new DefaultMutableTreeNode(me);
            insertNodeInto(tnEntity,parent,idxEntity);
            registerNode(tnEntity,me);
            Collections.sort(listEntities,new Entity.Compare());
            setMetaEntityNodeChildren(tnEntity, listEntities);
            ++idxEntity;
        }
        return idxEntity;
    }
    
    /** Sets the child nodes for a meta-entity
     * @param tnEntity is the tree node for the meta-entity
     * @param listEntities is a list of entities of the the meta-entity
     */
    public void setMetaEntityNodeChildren(MutableTreeNode tnMetaEntity, List<Entity> listEntities) {
        int idx = 0;
        for(Entity e : listEntities) {
            addEntityNode(tnMetaEntity, e, idx++);
        }
    }
    
    /** Adds a new tree node for an entity and adds it to the parent node
     * @param parent is the parent node to which the entity's node should be added
     * @param e is the entity to add
     * @idxEntity is where to insert the entity-node into its parent
     */
    private void addEntityNode(MutableTreeNode parent, Entity e, int idxEntity) {
        //System.out.println("Adding entity " + e.toString());
        DefaultMutableTreeNode tnEntity = new DefaultMutableTreeNode(e);
        insertNodeInto(tnEntity,parent,idxEntity);
        registerNode(tnEntity,e);
        setPropertyContainerNodeChildren(tnEntity, e);
    }
        
    /** Sets the child nodes for an property container
     * @param tnEntity is the tree node for the properties to be added to.
     * @param e is the entity
     */
    private void setPropertyContainerNodeChildren(MutableTreeNode tnParent, PropertyContainer pc) {
        setPropertyCollectionNodeChildren(tnParent, pc.getProperties());
    }
    
    
    /**
     * Adds a collection of properties as children of a given node.
     * @param tnParent is the parent node to add to.
     * @param properties is the collection of properties.
     */
    private void setPropertyCollectionNodeChildren(MutableTreeNode tnParent, Collection<Property> properties) {
        int idx = 0;
        for(Property p : properties) {
            DefaultMutableTreeNode tnProperty = new DefaultMutableTreeNode(p);
            insertNodeInto(tnProperty, tnParent, idx++);
            registerNode(tnProperty, p);
        }
    }

    /** Adds a new tree node for a meta-relationship and adds it to the parent node
     * @param parent is the parent node to which the meta-relationship's node should be added
     * @param mr is the meta-relationship to add
     * @idxRelationship is where to insert the relationship-node into its parent
     * @returns the index for the next relationship to be added to
     */
    private int addMetaRelationshipNode(MutableTreeNode parent, MetaRelationship mr, int idxRelationship)  throws Exception{
        List<Relationship> listRelationships = model.getRelationshipsOfType(mr);
        if(!listRelationships.isEmpty()) {
            DefaultMutableTreeNode tnRelationship = new DefaultMutableTreeNode(mr);
            insertNodeInto(tnRelationship, parent, idxRelationship++);
            registerNode(tnRelationship, mr);
            setMetaRelationshipNodeChildren(tnRelationship, listRelationships);
        }
        return idxRelationship;
    }
    
    /** Sets the child nodes for a meta-relationship
     * @param tnMetaRelationship is the tree node for the meta-relationship
     * @param listRelationships is a list of relationships of the the meta-relaitonship
     */
    public void setMetaRelationshipNodeChildren(MutableTreeNode tnMetaRelationship, List<Relationship> listRelationships)  throws Exception{
        int idx = 0;
        for(Relationship  r : listRelationships) {
            addRelationshipNode(tnMetaRelationship, r, idx++);
        }
    }
    
    /** Adds a new tree node for a relationship and adds it to the parent node
     * @param parent is the parent node to which the relationships's node should be added
     * @param r is the relationship to add
     * @idxRelationship is where to insert the relationship-node into its parent
     */
    private void addRelationshipNode(MutableTreeNode parent, Relationship r, int idxRelationship)  throws Exception{
        DefaultMutableTreeNode tnRelationship = new DefaultMutableTreeNode(r);
        insertNodeInto(tnRelationship, parent, idxRelationship);
        registerNode(tnRelationship,r);
        
        int idx = 0;
        
        MutableTreeNode startRole = new DefaultMutableTreeNode(r.start());
        MutableTreeNode finishRole = new DefaultMutableTreeNode(r.finish());
        insertNodeInto(startRole,tnRelationship,idx++);
        insertNodeInto(finishRole,tnRelationship,idx++);
        addRoleDetail(startRole,r.start());
        addRoleDetail(finishRole,r.finish());
        
        
        Collection<Property> properties = r.getProperties();
        if(!properties.isEmpty()) {
        	MutableTreeNode propertyNode = new DefaultMutableTreeNode("Properties");
        	insertNodeInto(propertyNode,tnRelationship,idx++);
        	setPropertyCollectionNodeChildren(propertyNode, properties);
        }
    }
    
    /**
     * Adds detail for a role as children of that role
     * @param roleNode is the role node to add detail to.
     * @param r is the Role to supply that detail.
     */
    private void addRoleDetail(MutableTreeNode roleNode, Role r) throws Exception{
        int idx = 0;
        insertNodeInto(new DefaultMutableTreeNode("connects to " + r.connectsTo().toString()),roleNode,idx++);
        Collection<Property> properties = r.getProperties();
        if(!properties.isEmpty()) {
        	MutableTreeNode propertyNode = new DefaultMutableTreeNode("Properties");
        	insertNodeInto(propertyNode,roleNode,idx++);
        	setPropertyCollectionNodeChildren(propertyNode, properties);
        }
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
    public void metaRelationshipAdded(MetaModelChangeEvent e) {
        // NOP - wait until relationships added that use this.
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
    public void metaEntityAdded(MetaModelChangeEvent e) {
        // nop as we don't yet have any child entities. - meta-entity node
        // will be created when its first entity is created.
    }
    
    
    /** signals that a meta entity has been changed.
     * Note that if the meta-entity has no associated entities then it 
     * won't appear in the explorer.
     * @ param e is the event that references the object being changed
     */
    public void metaEntityChanged(MetaModelChangeEvent e) throws Exception {
        MetaEntity me = (MetaEntity)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(me);
        if(me != null && tn != null){ 
            refreshNode(tn,me);
        }
    }
    
    /** signals that a meta relationship has been changed
     * @ param e is the event that references the object being changed
     */
    public void metaRelationshipChanged(MetaModelChangeEvent e)  throws Exception{
        refresh(); // likely to spawn major changes.
    }

    /*-------------------------------------------------------------------
     * event handlers for ModelChangeEvents
     *-----------------------------------------------------------------*/
    
    /** signals that a  relationship has been changed
     * @ param e is the event that references the object being changed
     */
    public void RelationshipChanged(ModelChangeEvent e) {
        Relationship r = (Relationship)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(r);
        nodeChanged(tn);
        
        tn = lookupNodeOf(r.start());
        if(tn != null){
            nodeChanged(tn);
        }
        tn = lookupNodeOf(r.finish());
        if(tn != null){
            nodeChanged(tn);
        }

    }
    
    /** signals that an entity has been added
     * @ param e is the event that references the object being changed
     */
    public void EntityAdded(ModelChangeEvent evt) throws Exception {
        Entity e = (Entity)evt.getSource();
        MetaEntity me = e.getMeta();
        
        // find this entitie's meta-entity.  If none then this is the
        // first entity, so create the meta-entity node.
        DefaultMutableTreeNode meNode = lookupNodeOf(me);
        if(meNode == null) {
            MutableTreeNode parent = metaEntitesNode; // MetaEntities
            if(parent != null) {
                int idxEntity = parent.getChildCount();
                addMetaEntityNode(parent, me, idxEntity); // will add the entity as well.
            }
        } else { // meta-entity node exists - add the entity as its child.
            int idxEntity = meNode.getChildCount();
            addEntityNode(meNode, e, idxEntity);
        }
    }
    
    /** signals that a  relationship has been deleted
     * @ param e is the event that references the object being changed
     */
    public void RelationshipDeleted(ModelChangeEvent e) {
        Relationship r = (Relationship)e.getSource();
        
        DefaultMutableTreeNode tn = lookupNodeOf(r);
        // DefaultMutableTreeNode parent = null;
        if(tn != null) {
            //parent = (DefaultMutableTreeNode)tn.getParent();
            //System.out.println("Removing node for " + r);
            removeNodeFromParent(tn);
            removeNodeOf(r);
        }

        // TBD....This fails as system doesn't think parent has a parent
//        if((parent != null) && (parent.getChildCount() == 0)) {
//            //System.out.println("Removing parent node " + parent.getUserObject());
//            removeNodeFromParent(parent);
//            MetaRelationship mr = r.getMeta();
//            removeNodeOf(mr);
//        }
    }
    
    /** signals that a  entity has been deleted
     * @ param e is the event that references the object being changed
     */
    public void EntityDeleted(ModelChangeEvent evt) {
        Entity e = (Entity)evt.getSource();
        
        DefaultMutableTreeNode tn = lookupNodeOf(e);
        //MutableTreeNode parent = (MutableTreeNode)tn.getParent();
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(e);
        }
        
        // TBD....This fails as system doesn't think parent has a parent
//        if(parent.getChildCount() == 0) {
//           removeNodeFromParent(parent);
//            MetaEntity me = e.getMeta();
//            removeNodeOf(me);
//        }
        
    }
   
    
    /** signals that a  relationship has been added
     * @ param e is the event that references the object being changed
     */
    public void RelationshipAdded(ModelChangeEvent e)  throws Exception{
        Relationship r = (Relationship)e.getSource();
        MetaRelationship mr = r.getMeta();
        
        // find this relationship's meta-relationship.  If none then this is the
        // first relationship, so create the meta-relationship node.
        DefaultMutableTreeNode metaNode = lookupNodeOf(mr);
        if(metaNode == null) {
            MutableTreeNode parent = metaRelationshipsNode;
            if(parent != null) {
                int idxRelationship = parent.getChildCount();
                addMetaRelationshipNode(parent, mr, idxRelationship); // will add the relationship as well.
            }
        } else { // ndoe exists - add new child relationship
            int idxRelationship = metaNode.getChildCount();
            addRelationshipNode(metaNode, r, idxRelationship);
        }
    }

    /** signals a major update to the  model
     * @ param e is the event that references the object being changed
     */
    public void modelUpdated(ModelChangeEvent e)  throws Exception{
        refresh();
    }
    
    /** signals that a  entity has been changed
     * @ param evt is the event that references the object being changed
     */
    public void EntityChanged(ModelChangeEvent evt) {
        Entity e = (Entity)evt.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(e);
        nodeChanged(tn);
        for(Property p : e.getProperties()){
            tn = lookupNodeOf(p);
            if(tn != null){
                tn.setUserObject(p);
                nodeChanged(tn);
            }
        }
    }

    /*-----------------------------------------------------------------
     * Inner Classes
     *----------------------------------------------------------------*/
    
    /** inner class to provide a placeholder for the meta-entities.  This 
     * allows popups to be keyed by the "Entities" class name
     */
    private class Entities {
        public String toString() {
            return "Entities";
        }
    }

    /** inner class to provide a placeholder for the meta-relationships.  This 
     * allows popups to be keyed by the "Relationships" class name
     */
    private class Relationships {
        public String toString() {
            return "Relationships";
        }
    }
    
    
}
