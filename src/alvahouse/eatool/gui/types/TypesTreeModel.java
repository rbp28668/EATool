/*
 * TypesTreeModel.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.gui.types;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.gui.ExplorerTreeModel;
import alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypeList;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypes;
import alvahouse.eatool.repository.metamodel.types.TypeEvent;
import alvahouse.eatool.repository.metamodel.types.TypeEventListener;

/**
 * TypesTreeModel
 * 
 * @author rbp28668
 */
public class TypesTreeModel extends ExplorerTreeModel implements TypeEventListener {

	private static final long serialVersionUID = 1L;
	private ExtensibleTypes types;
    
    /**
     * @param rootTitle
     */
    public TypesTreeModel(ExtensibleTypes types, String rootTitle) throws Exception{
        super(rootTitle);

        this.types = types;
        buildTree();
        types.addListener(this);
    }

    private void buildTree() throws Exception{
        int idx = 0;
        for(ExtensibleTypeList typeList : types.getTypes()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(typeList);
            registerNode(node,typeList);
            insertNodeInto(node, (DefaultMutableTreeNode)getRoot(),idx);
            insertChildrenOf(node,typeList);
            idx++;
        }
    }
    
    private void insertChildrenOf(MutableTreeNode parent, ExtensibleTypeList typeList){
        int idx = 0;
        for(ExtensibleMetaPropertyType type : typeList.getTypes()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(type);
            registerNode(node,type);
            insertNodeInto(node, parent,idx);
            idx++;
        }
    }
    
    public void refresh(){
        
    }
    
    public void dispose(){
        types.removeListener(this);
    }

    /**
     * @return
     */
    public ExtensibleTypes getTypes() {
        return types;
    }

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.types.TypeEventListener#typeAdded(alvahouse.eatool.repository.metamodel.types.TypeEvent)
	 */
	public void typeAdded(TypeEvent event) throws Exception{
	    ExtensibleMetaPropertyType type = event.getType();
	    ExtensibleTypeList parentList = types.lookupList(type);
	    DefaultMutableTreeNode parentNode = lookupNodeOf(parentList);
        MutableTreeNode node = new DefaultMutableTreeNode(type);
        insertNodeInto(node, parentNode, parentNode.getChildCount());
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.types.TypeEventListener#typeChanged(alvahouse.eatool.repository.metamodel.types.TypeEvent)
	 */
	public void typeChanged(TypeEvent event) {
	    ExtensibleMetaPropertyType type = event.getType();
	    DefaultMutableTreeNode typeNode = lookupNodeOf(type);
	    nodeChanged(typeNode);
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.types.TypeEventListener#typeDeleted(alvahouse.eatool.repository.metamodel.types.TypeEvent)
	 */
	public void typeDeleted(TypeEvent event) {
	    ExtensibleMetaPropertyType type = event.getType();
	    DefaultMutableTreeNode typeNode = lookupNodeOf(type);
	    typeNode.removeFromParent();
	    removeNodeOf(type);
	}
    
}
