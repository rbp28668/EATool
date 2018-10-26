/*
 * ImportMappingTtreeModel.java
 * Project: EATool
 * Created on 03-Dec-2005
 *
 */
package alvahouse.eatool.gui.mappings;

import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.gui.ExplorerTreeModel;
import alvahouse.eatool.repository.mapping.EntityTranslation;
import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.repository.mapping.ImportMappingChangeListener;
import alvahouse.eatool.repository.mapping.ImportMappings;
import alvahouse.eatool.repository.mapping.MappingChangeEvent;
import alvahouse.eatool.repository.mapping.PropertyTranslation;
import alvahouse.eatool.repository.mapping.PropertyTranslationCollection;
import alvahouse.eatool.repository.mapping.RelationshipTranslation;
import alvahouse.eatool.repository.mapping.RoleTranslation;

/**
 * ImportMappingTreeModel provides the TreeModel for the ImportMappingExploer.
 * 
 * @author rbp28668
 */
public class ImportMappingTreeModel extends ExplorerTreeModel 
implements ImportMappingChangeListener{

	private MutableTreeNode root = null;
	private JTree tree = null;
	private ImportMappings mappings;
	private final static int ENTITY_MAPPINGS = 0;
	private final static int RELATIONSHIP_MAPPINGS = 1;

    /**
     * @param rootTitle
     */
    public ImportMappingTreeModel(String rootTitle,ImportMappings mappings) {
        super(rootTitle);
        this.mappings = mappings;
        root = (MutableTreeNode)getRoot();
		root.setUserObject( mappings);
		mappings.addChangeListener(this);
        initModel();
    }

    public void dispose(){
        if(mappings != null){
            mappings.removeChangeListener(this);
        }
    }
    
    public void setMappings(ImportMappings mappings){
        this.mappings = mappings;
        initModel();
    }
	/**
	 * Method initModel builds the tree model from the mappings.
	 */
    private void initModel() {
        int idx = 0;
        for(Iterator iter = mappings.getImportMappings().iterator();iter.hasNext();){
            ImportMapping mapping = (ImportMapping)iter.next();
            addImportMappingNode((MutableTreeNode)getRoot(),mapping,idx++);
        }
    }

    /**
     * @param node
     * @param mapping
     * @param i
     */
    private int addImportMappingNode(MutableTreeNode parent, ImportMapping mapping, int idxMap) {
        DefaultMutableTreeNode tnImportMapping = new DefaultMutableTreeNode(mapping.getName());
        tnImportMapping.setUserObject(mapping);
        insertNodeInto(tnImportMapping,parent,idxMap);
        registerNode(tnImportMapping,mapping);
        setImportMappingNodeChildren(tnImportMapping, mapping);
        return idxMap;
    }

    /**
     * @param tnImportMapping
     * @param mapping
     */
    private void setImportMappingNodeChildren(DefaultMutableTreeNode tnImportMapping, ImportMapping mapping) {

        DefaultMutableTreeNode tnEntities = new DefaultMutableTreeNode("Entity Mappings");
        tnEntities.setUserObject(new EntityTranslations());
        insertNodeInto(tnEntities,tnImportMapping,ENTITY_MAPPINGS);
        setEntityMappings(tnEntities,mapping);
        
        DefaultMutableTreeNode tnRelationships = new DefaultMutableTreeNode("Relationshp Mappings");
        insertNodeInto(tnRelationships,tnImportMapping,RELATIONSHIP_MAPPINGS);
        tnRelationships.setUserObject(new RelationshipTranslations());
        setRelationshipMappings(tnRelationships,mapping);
    }

    /**
     * @param tnRelationships
     * @param mapping
     */
    private void setRelationshipMappings(DefaultMutableTreeNode tnRelationships, ImportMapping mapping) {
        int idx = 0;
        for(Iterator iter = mapping.getRelationshipTranslations().iterator(); iter.hasNext();){
            RelationshipTranslation rt = (RelationshipTranslation)iter.next();
            setRelationship(tnRelationships, rt, idx++);
        }
    }

    /**
     * Adds a relationship to the relationships node.
     * @param tnRelationships is the relationships node.
     * @param rt is the RelationshipTranslation to add.
     * @param idxRelation is the position under tnRelationships to add the new node.
     */
    private void setRelationship(DefaultMutableTreeNode tnRelationships, RelationshipTranslation rt, int idxRelation){
        DefaultMutableTreeNode tnMapping = new DefaultMutableTreeNode();
        tnMapping.setUserObject(rt);
        insertNodeInto(tnMapping, tnRelationships,idxRelation);
        registerNode(tnMapping,rt);
        setRoles(tnMapping,rt);
    }
    
    /**
     * @param tnMapping
     * @param rt
     */
    private void setRoles(DefaultMutableTreeNode tnRelationship, RelationshipTranslation rt) {
        setRole(tnRelationship, rt.getStart(),0);
        setRole(tnRelationship, rt.getFinish(),1);
    }

    /**
     * Adds a role to a parent relationship node.
     * @param tnRelationship is the parent node to add to.
     * @param rt is the RoleTranslation to add.
     * @param idxRole is the index where the role translation should be added to.
     */
    private int setRole(DefaultMutableTreeNode tnRelationship, RoleTranslation rt, int idxRole) {

        DefaultMutableTreeNode tnRole = new DefaultMutableTreeNode();
        tnRole.setUserObject(rt);
        insertNodeInto(tnRole, tnRelationship, idxRole);
        registerNode(tnRole,rt);

        int idx = 0;
        for(Iterator iter = rt.getPropertyTranslations().iterator(); iter.hasNext();){
            PropertyTranslation pt = (PropertyTranslation)iter.next();
            setProperty(tnRole, pt, idx++);
        }
        return idxRole;
    }
    
    /**
     * @param tnEntities
     * @param mapping
     */
    private void setEntityMappings(DefaultMutableTreeNode tnEntities, ImportMapping mapping) {
        int idx = 0;
        for(Iterator iter = mapping.getEntityTranslations().iterator(); iter.hasNext();){
            EntityTranslation et = (EntityTranslation)iter.next();
            setEntity(tnEntities,et,idx++);
        }
    }

    /**
     * Adds an entity to a parent entities node.
     * @param tnEntities is the parent node to add to.
     * @param et is the EntityTranslation to add.
     * @param idxEntity is the index where the entity translation should be added to.
     */
    private int setEntity(DefaultMutableTreeNode tnEntities, EntityTranslation et, int idxEntity) {

        DefaultMutableTreeNode tnEntity = new DefaultMutableTreeNode();
        tnEntity.setUserObject(et);
        insertNodeInto(tnEntity, tnEntities,idxEntity);
        registerNode(tnEntity,et);

        int idx = 0;
        for(Iterator iter = et.getPropertyTranslations().iterator(); iter.hasNext();){
            PropertyTranslation pt = (PropertyTranslation)iter.next();
            setProperty(tnEntity, pt, idx++);
        }
        return idxEntity;
    }

    /**
     * Adds a PropertyTranslation as a child of an entity node.
     * @param tnEntity is the entity node to add to.
     * @param pt is the PropertyTranslation to add.
     * @param idxProperty is the position to add the new node at.
     */
    private int setProperty(DefaultMutableTreeNode tnEntity, PropertyTranslation pt, int idxProperty){
        DefaultMutableTreeNode tnMapping = new DefaultMutableTreeNode();
        tnMapping.setUserObject(pt);
        insertNodeInto(tnMapping, tnEntity,idxProperty);
        registerNode(tnMapping,pt);
        return idxProperty;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#Updated(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void Updated(MappingChangeEvent e) {
        while(root.getChildCount() > 0){
            root.remove(0);
        }
        removeAll();
        reload();
        initModel();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#MappingAdded(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void MappingAdded(MappingChangeEvent e) {
        ImportMapping mapping = (ImportMapping)e.getSource();
        int idx = root.getChildCount();
        addImportMappingNode((MutableTreeNode)getRoot(),mapping,idx);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#MappingEdited(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void MappingEdited(MappingChangeEvent e) {
        ImportMapping mapping = (ImportMapping)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            nodeChanged(tn);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#MappingDeleted(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void MappingDeleted(MappingChangeEvent e) {
        ImportMapping mapping = (ImportMapping)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(mapping);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#EntityMappingAdded(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void EntityMappingAdded(MappingChangeEvent e) {
        ImportMapping mapping = (ImportMapping)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        DefaultMutableTreeNode entities = (DefaultMutableTreeNode)tn.getChildAt(ENTITY_MAPPINGS);

        // Add any entity translations not already registered - i.e. the new one!
        for(Iterator iter = mapping.getEntityTranslations().iterator(); iter.hasNext();){
            EntityTranslation et = (EntityTranslation)iter.next();
            if(lookupNodeOf(et) == null){
                setEntity(entities,et,entities.getChildCount());
            }
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#EntityMappingDeleted(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void EntityMappingDeleted(MappingChangeEvent e) {
        EntityTranslation mapping = (EntityTranslation)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(mapping);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#EntityMappingEdited(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void EntityMappingEdited(MappingChangeEvent e) {
        EntityTranslation mapping = (EntityTranslation)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            nodeChanged(tn);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#PropertyMappingAdded(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void PropertyMappingAdded(MappingChangeEvent e) {
        PropertyTranslationCollection mapping = (PropertyTranslationCollection)e.getSource();
        DefaultMutableTreeNode tnEntity = lookupNodeOf(mapping);
        // Add any entity translations not already registered - i.e. the new one!
        for(Iterator iter = mapping.getPropertyTranslations().iterator(); iter.hasNext();){
            PropertyTranslation pt = (PropertyTranslation)iter.next();
            if(lookupNodeOf(pt) == null){
                setProperty(tnEntity, pt, tnEntity.getChildCount());
            }
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#PropertyMappingEdited(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void PropertyMappingEdited(MappingChangeEvent e) {
        PropertyTranslation mapping = (PropertyTranslation)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            nodeChanged(tn);
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#PropertyMappingDeleted(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void PropertyMappingDeleted(MappingChangeEvent e) {
        PropertyTranslation mapping = (PropertyTranslation)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(mapping);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#RelationshipMappingAdded(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void RelationshipMappingAdded(MappingChangeEvent e) {
        ImportMapping mapping = (ImportMapping)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        DefaultMutableTreeNode relationships = (DefaultMutableTreeNode)tn.getChildAt(RELATIONSHIP_MAPPINGS);

        // Add any entity translations not already registered - i.e. the new one!
        for(Iterator iter = mapping.getRelationshipTranslations().iterator(); iter.hasNext();){
            RelationshipTranslation rt = (RelationshipTranslation)iter.next();
            if(lookupNodeOf(rt) == null){
                setRelationship(relationships,rt,relationships.getChildCount());
            }
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#RelationshipMappingDeleted(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void RelationshipMappingDeleted(MappingChangeEvent e) {
        RelationshipTranslation mapping = (RelationshipTranslation)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(mapping);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#RelationshipMappingEdited(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void RelationshipMappingEdited(MappingChangeEvent e) {
        RelationshipTranslation mapping = (RelationshipTranslation)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            nodeChanged(tn);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ImportMappingChangeListener#RoleMappingEdited(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void RoleMappingEdited(MappingChangeEvent e) {
        RoleTranslation mapping = (RoleTranslation)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            nodeChanged(tn);
        }
    }
    
    
    /**
     * EntityTranslations is a marker class for the list of EntityTranslation.
     * @author rbp28668
     */
    class EntityTranslations{
        
        public String toString() {
            return "Entity Mappings";
        }
    }
    
    /**
     * RelationshipTranslations is a marker class for the list of RelationshipTranslation.
     * @author rbp28668
     */
    class RelationshipTranslations{

        public String toString() {
            return "Relationship Mappings";
        }
        
    }


     
}
